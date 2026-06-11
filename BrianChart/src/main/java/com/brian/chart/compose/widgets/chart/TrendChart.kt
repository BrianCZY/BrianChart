package com.brian.chart.compose.widgets.chart

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import com.brian.chart.compose.widgets.model.*
import com.brian.chart.compose.widgets.renderer.AxisRenderer
import com.brian.chart.compose.widgets.util.PathBuilder
import com.brian.chart.compose.widgets.util.ViewportCalculator
import kotlin.math.sqrt

private const val TAG = "TrendChart"

/**
 * A versatile line/trend chart composable supporting multiple Y-axes,
 * zoom, touch interaction, and extensive customization.
 */
@SuppressLint("UnrememberedMutableState")
@Composable
fun TrendChart(
    modifier: Modifier = Modifier,
    config: TrendChartConfig? = null,
) {
    var zoom by remember { mutableStateOf(1f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }

    val seriesList by derivedStateOf { config?.series }
    val xAxis by derivedStateOf { config?.xAxis ?: ChartAxis() }
    val yPrimary by derivedStateOf { config?.yPrimaryAxis }
    val ySecondary by derivedStateOf { config?.ySecondaryAxis }
    val yRight by derivedStateOf { config?.yRightAxis }
    val autoFit by derivedStateOf { config?.autoFit == true }
    val scrollEnabled by derivedStateOf { config?.scrollEnabled }
    val viewportPadding by derivedStateOf { config?.viewportPadding }
    val thresholdLayer by derivedStateOf { config?.thresholdLayer }
    val onTouch = config?.onTouch

    // Auto-fit axis ranges to data
    remember(seriesList, xAxis, yPrimary, ySecondary, yRight, autoFit) {
        if (autoFit) {
            autoFitAxes(xAxis, yPrimary, ySecondary, yRight, seriesList)
        }
    }

    BoxWithConstraints(modifier = modifier) {
        val textMeasurer: TextMeasurer = rememberTextMeasurer()
        val density = LocalDensity.current

        val canvasSize by remember {
            derivedStateOf {
                with(density) { IntSize(maxWidth.roundToPx(), maxHeight.roundToPx()) }
            }
        }

        // Compute viewport bounds
        val viewport by remember(canvasSize, viewportPadding, xAxis, yPrimary, ySecondary, yRight) {
            derivedStateOf {
                ViewportCalculator.computeBounds(
                    xAxis, yPrimary, ySecondary, yRight,
                    viewportPadding, density, textMeasurer, canvasSize
                )
            }
        }

        // Compute curve paths
        var prevRenderCache by remember { mutableStateOf<Map<AxisOrientation, MutableList<RenderCache>>>(emptyMap()) }
        val renderCaches by remember(seriesList, viewport, zoom, xAxis.upperBound, xAxis.lowerBound,
            xAxis.origin, yPrimary?.upperBound, yPrimary?.lowerBound,
            ySecondary?.upperBound, ySecondary?.lowerBound,
            yRight?.upperBound, yRight?.lowerBound) {
            derivedStateOf {
                buildRenderCaches(seriesList, viewport, xAxis, yPrimary, ySecondary, yRight, zoom, prevRenderCache)
            }
        }
        prevRenderCache = renderCaches

        // Transform modifier for zoom
        val transformModifier = if (scrollEnabled == true) {
            val state = rememberTransformableState { zoomChange, _, _ ->
                zoom *= zoomChange
            }
            Modifier.graphicsLayer().transformable(state)
        } else Modifier

        // Touch handling
        val touchModifier = if (onTouch != null) {
            Modifier.pointerInput(viewport, zoom, xAxis.lowerBound, xAxis.upperBound,
                yPrimary?.lowerBound, yPrimary?.upperBound,
                ySecondary?.lowerBound, ySecondary?.upperBound,
                yRight?.lowerBound, yRight?.upperBound, seriesList) {
                awaitPointerEventScope {
                    val down = awaitFirstDown()

                    val downX = pixelToDataX(down.position.x, viewport, xAxis.lowerBound, xAxis.upperBound, zoom)
                    val downY = pixelToDataY(down.position.y, viewport, yPrimary, ySecondary, yRight)
                    val downNearest = findClosestPoint(down.position.x, down.position.y, seriesList ?: emptyList(),
                        viewport, xAxis.lowerBound, xAxis.upperBound,
                        yPrimary?.lowerBound ?: ySecondary?.lowerBound ?: yRight?.lowerBound ?: 0f,
                        yPrimary?.upperBound ?: ySecondary?.upperBound ?: yRight?.upperBound ?: 0f, zoom)

                    onTouch(TouchPayload(downX, downY, down.position.x, down.position.y,
                        closestPoint = downNearest, gesture = GestureEvent.PRESS))

                    var moved = false
                    val touchSlop = 4f
                    var lastX = down.position.x
                    var lastY = down.position.y

                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull() ?: continue

                        if (change.changedToUp()) {
                            val pos = change.position
                            val upX = pixelToDataX(pos.x, viewport, xAxis.lowerBound, xAxis.upperBound, zoom)
                            val upY = pixelToDataY(pos.y, viewport, yPrimary, ySecondary, yRight)
                            val upNearest = findClosestPoint(pos.x, pos.y, seriesList ?: emptyList(),
                                viewport, xAxis.lowerBound, xAxis.upperBound,
                                yPrimary?.lowerBound ?: ySecondary?.lowerBound ?: yRight?.lowerBound ?: 0f,
                                yPrimary?.upperBound ?: ySecondary?.upperBound ?: yRight?.upperBound ?: 0f, zoom)
                            onTouch(TouchPayload(upX, upY, pos.x, pos.y, closestPoint = upNearest,
                                gesture = if (moved) GestureEvent.RELEASE else GestureEvent.TAP))
                            event.changes.forEach { it.consume() }
                            break
                        }

                        if (change.positionChanged()) {
                            val pos = change.position
                            val dx = pos.x - lastX
                            val dy = pos.y - lastY
                            moved = true

                            val mvX = pixelToDataX(pos.x, viewport, xAxis.lowerBound, xAxis.upperBound, zoom)
                            val mvY = pixelToDataY(pos.y, viewport, yPrimary, ySecondary, yRight)
                            val mvNearest = if (dx * dx + dy * dy >= touchSlop * touchSlop) {
                                lastX = pos.x; lastY = pos.y
                                findClosestPoint(pos.x, pos.y, seriesList ?: emptyList(),
                                    viewport, xAxis.lowerBound, xAxis.upperBound,
                                    yPrimary?.lowerBound ?: ySecondary?.lowerBound ?: yRight?.lowerBound ?: 0f,
                                    yPrimary?.upperBound ?: ySecondary?.upperBound ?: yRight?.upperBound ?: 0f, zoom)
                            } else null

                            onTouch(TouchPayload(mvX, mvY, pos.x, pos.y, closestPoint = mvNearest,
                                gesture = GestureEvent.DRAG))
                            change.consume()
                        }
                    }
                }
            }
        } else Modifier

        Canvas(
            modifier = transformModifier
                .then(touchModifier)
                .fillMaxSize()
                .drawWithCache {
                    onDrawBehind {
                        clipRect {
                            // 1. Colored regions (background)
                            AxisRenderer.drawColoredRegions(this, xAxis, yPrimary, ySecondary, yRight, viewport)

                            // 2. Grid
                            AxisRenderer.drawGrid(this, xAxis, yPrimary, ySecondary, yRight, viewport, zoom)

                            // 3. Threshold lines (background layer)
                            if (thresholdLayer == LimitLineLayer.BACKGROUND) {
                                AxisRenderer.drawThresholds(this, xAxis, yPrimary, ySecondary, yRight, viewport, zoom)
                            }

                            // 4. Axes
                            AxisRenderer.drawAxes(this, xAxis, yPrimary, ySecondary, yRight, viewport)

                            // 5. Labels & ticks
                            AxisRenderer.drawLabels(this, xAxis, yPrimary, ySecondary, yRight, viewport, zoom)

                            // 6. Axis names
                            AxisRenderer.drawAxisNames(this, xAxis, yPrimary, ySecondary, yRight, viewport, zoom)

                            // 7. Data series (curves/points)
                            drawDataSeries(this, renderCaches)

                            // 8. Threshold lines (foreground layer)
                            if (thresholdLayer != LimitLineLayer.BACKGROUND) {
                                AxisRenderer.drawThresholds(this, xAxis, yPrimary, ySecondary, yRight, viewport, zoom)
                            }
                        }
                    }
                }
        ) { }
    }
}

/**
 * Configuration data for TrendChart
 */
data class TrendChartConfig(
    val series: List<DataSeries>? = null,
    val xAxis: ChartAxis = ChartAxis(),
    val yPrimaryAxis: ChartAxis? = null,
    val ySecondaryAxis: ChartAxis? = null,
    val yRightAxis: ChartAxis? = null,
    val autoFit: Boolean = false,
    val scrollEnabled: Boolean = false,
    val viewportPadding: ViewportPadding? = null,
    val thresholdLayer: LimitLineLayer = LimitLineLayer.BACKGROUND,
    val onTouch: ((TouchPayload) -> Unit)? = null,
)

// --- Internal implementation ---

private fun autoFitAxes(
    xAxis: ChartAxis,
    yPrimary: ChartAxis?,
    ySecondary: ChartAxis?,
    yRight: ChartAxis?,
    series: List<DataSeries>?
) {
    adjustXUpperBound(xAxis, series)
    adjustXLowerBound(xAxis, series)
    yPrimary?.let { adjustYUpperBound(it, series?.filter { s -> s.axisTarget == AxisOrientation.PRIMARY_LEFT }) }
    yPrimary?.let { adjustYLowerBound(it, series?.filter { s -> s.axisTarget == AxisOrientation.PRIMARY_LEFT }) }
    ySecondary?.let { adjustYUpperBound(it, series?.filter { s -> s.axisTarget == AxisOrientation.SECONDARY_LEFT }) }
    ySecondary?.let { adjustYLowerBound(it, series?.filter { s -> s.axisTarget == AxisOrientation.SECONDARY_LEFT }) }
    yRight?.let { adjustYUpperBound(it, series?.filter { s -> s.axisTarget == AxisOrientation.RIGHT }) }
    yRight?.let { adjustYLowerBound(it, series?.filter { s -> s.axisTarget == AxisOrientation.RIGHT }) }
}

private fun adjustXUpperBound(axis: ChartAxis, series: List<DataSeries>?) {
    val maxData = series?.maxOfOrNull { it.dataPoints.maxByOrNull { pt -> pt.x }?.x ?: 0f } ?: axis.upperBound
    val maxThreshold = axis.thresholdLines?.maxByOrNull { it.value }?.value
    val maxRegion = axis.coloredRegions?.maxByOrNull { it.endValue }?.endValue
    val max = listOfNotNull(maxData, maxThreshold, maxRegion).maxOrNull() ?: axis.upperBound
    val interval = axis.labelStep ?: 1f
    if (max > axis.upperBound) axis.upperBound = ((max / interval).toInt() + 1) * interval
}

private fun adjustXLowerBound(axis: ChartAxis, series: List<DataSeries>?) {
    val minData = series?.minOfOrNull { it.dataPoints.minByOrNull { pt -> pt.x }?.x ?: 0f } ?: axis.lowerBound
    val minThreshold = axis.thresholdLines?.minByOrNull { it.value }?.value
    val minRegion = axis.coloredRegions?.minByOrNull { it.startValue }?.startValue
    val min = listOfNotNull(minData, minThreshold, minRegion).minOrNull() ?: axis.lowerBound
    val interval = axis.labelStep ?: 1f
    if (min < axis.lowerBound) axis.lowerBound = ((min / interval).toInt() - 1) * interval
}

private fun adjustYUpperBound(axis: ChartAxis, series: List<DataSeries>?) {
    val maxData = series?.maxOfOrNull { it.dataPoints.maxByOrNull { pt -> pt.y }?.y ?: 0f } ?: axis.upperBound
    val maxThreshold = axis.thresholdLines?.maxByOrNull { it.value }?.value
    val maxRegion = axis.coloredRegions?.maxByOrNull { it.endValue }?.endValue
    val max = listOfNotNull(maxData, maxThreshold, maxRegion).maxOrNull() ?: axis.upperBound
    val interval = axis.labelStep ?: 1f
    if (max > axis.upperBound) axis.upperBound = ((max / interval).toInt() + 1) * interval
}

private fun adjustYLowerBound(axis: ChartAxis, series: List<DataSeries>?) {
    val minData = series?.minOfOrNull { it.dataPoints.minByOrNull { pt -> pt.y }?.y ?: 0f } ?: axis.lowerBound
    val minThreshold = axis.thresholdLines?.minByOrNull { it.value }?.value
    val minRegion = axis.coloredRegions?.minByOrNull { it.startValue }?.startValue
    val min = listOfNotNull(minData, minThreshold, minRegion).minOrNull() ?: axis.lowerBound
    val interval = axis.labelStep ?: 1f
    if (min < axis.lowerBound) axis.lowerBound = ((min / interval).toInt() - 1) * interval
}

private fun buildRenderCaches(
    series: List<DataSeries>?,
    bounds: ViewportBounds,
    xAxis: ChartAxis,
    yPrimary: ChartAxis?,
    ySecondary: ChartAxis?,
    yRight: ChartAxis?,
    zoom: Float,
    prevCache: Map<AxisOrientation, MutableList<RenderCache>>
): Map<AxisOrientation, MutableList<RenderCache>> {
    val result = mutableMapOf<AxisOrientation, MutableList<RenderCache>>()

    series?.let { lines ->
        yPrimary?.let { yAxis ->
            val filtered = lines.filter { it.axisTarget == AxisOrientation.PRIMARY_LEFT }
            if (filtered.isNotEmpty()) {
                result[AxisOrientation.PRIMARY_LEFT] = computePaths(
                    filtered.toMutableList(), bounds, xAxis.upperBound, xAxis.lowerBound,
                    yAxis.upperBound, yAxis.lowerBound, zoom, xAxis.origin ?: 0f,
                    prevCache[AxisOrientation.PRIMARY_LEFT]
                )
            }
        }
        ySecondary?.let { yAxis ->
            val filtered = lines.filter { it.axisTarget == AxisOrientation.SECONDARY_LEFT }
            if (filtered.isNotEmpty()) {
                result[AxisOrientation.SECONDARY_LEFT] = computePaths(
                    filtered.toMutableList(), bounds, xAxis.upperBound, xAxis.lowerBound,
                    yAxis.upperBound, yAxis.lowerBound, zoom, xAxis.origin ?: 0f,
                    prevCache[AxisOrientation.SECONDARY_LEFT]
                )
            }
        }
        yRight?.let { yAxis ->
            val filtered = lines.filter { it.axisTarget == AxisOrientation.RIGHT }
            if (filtered.isNotEmpty()) {
                result[AxisOrientation.RIGHT] = computePaths(
                    filtered.toMutableList(), bounds, xAxis.upperBound, xAxis.lowerBound,
                    yAxis.upperBound, yAxis.lowerBound, zoom, xAxis.origin ?: 0f,
                    prevCache[AxisOrientation.RIGHT]
                )
            }
        }
    }
    return result
}

private fun computePaths(
    seriesList: MutableList<DataSeries>,
    bounds: ViewportBounds,
    xMax: Float, xMin: Float,
    yMax: Float, yMin: Float,
    zoom: Float,
    xAxisPos: Float,
    prevCache: MutableList<RenderCache>?
): MutableList<RenderCache> {
    val scaleX = (bounds.bottomRight.x - bounds.bottomLeft.x) / (xMax - xMin)
    val scaleY = (bounds.bottomLeft.y - bounds.topLeft.y) / (yMax - yMin)
    val offsetX = xMin * scaleX
    val offsetY = yMin * scaleY

    val cache = prevCache ?: mutableListOf()
    if (cache.size > seriesList.size) cache.clear()

    seriesList.forEach { series ->
        var entry = cache.find { it.series?.identifier == series.identifier }
        if (entry == null) {
            entry = RenderCache(series = series)
            cache.add(entry)
        }

        if (series.showPoints || series.customPainter != null) {
            entry.pixelOffsets = PathBuilder.toPixelOffsets(
                series.dataPoints, bounds.bottomLeft.x, bounds.bottomLeft.y,
                scaleX, scaleY, offsetX, offsetY, zoom
            )
        }

        if (series.showPath) {
            entry.curvePath = buildSeriesPath(series, bounds.bottomLeft.x, bounds.bottomLeft.y,
                scaleX, scaleY, offsetX, offsetY, zoom, entry.curvePath)

            if (series.areaFillEnabled) {
                val areaPoints = buildList {
                    series.dataPoints.firstOrNull()?.let { add(Coordinate(it.x, xAxisPos)) }
                    addAll(series.dataPoints)
                    series.dataPoints.lastOrNull()?.let { add(Coordinate(it.x, xAxisPos)) }
                }
                entry.fillPath = buildSeriesPathWithPoints(areaPoints, series, bounds.bottomLeft.x, bounds.bottomLeft.y,
                    scaleX, scaleY, offsetX, offsetY, zoom, entry.fillPath)
            }
        }
    }

    return if (cache.size > seriesList.size) {
        cache.filter { entry -> seriesList.any { it.identifier == entry.series?.identifier } }.toMutableList()
    } else cache
}

private fun buildSeriesPath(
    series: DataSeries,
    ox: Float, oy: Float,
    sx: Float, sy: Float,
    offX: Float, offY: Float,
    zoom: Float,
    cached: androidx.compose.ui.graphics.Path?
): androidx.compose.ui.graphics.Path {
    return if (series.smoothCurve) {
        PathBuilder.buildCatmullRomPath(series.dataPoints, ox, oy, sx, sy, offX, offY, zoom, cached)
    } else {
        PathBuilder.buildLinearPath(series.dataPoints, ox, oy, sx, sy, offX, offY, zoom, cached)
    }
}

private fun buildSeriesPathWithPoints(
    points: List<Coordinate>,
    series: DataSeries,
    ox: Float, oy: Float,
    sx: Float, sy: Float,
    offX: Float, offY: Float,
    zoom: Float,
    cached: androidx.compose.ui.graphics.Path?
): androidx.compose.ui.graphics.Path {
    return if (series.smoothCurve) {
        PathBuilder.buildCatmullRomPath(points, ox, oy, sx, sy, offX, offY, zoom, cached, skipEdges = 2)
    } else {
        PathBuilder.buildLinearPath(points, ox, oy, sx, sy, offX, offY, zoom, cached)
    }
}

private fun drawDataSeries(
    scope: DrawScope,
    caches: Map<AxisOrientation, MutableList<RenderCache>>
) {
    caches.forEach { (_, list) -> drawSeriesList(scope, list) }
}

private fun drawSeriesList(scope: DrawScope, caches: MutableList<RenderCache>) {
    scope.run {
        caches.forEach { cache ->
            val series = cache.series ?: return@forEach
            val color = series.seriesColor

            // Draw points
            if (series.showPoints) {
                cache.pixelOffsets?.let { pts ->
                    drawPoints(pts, PointMode.Points, color, series.strokeWidth.toPx())
                }
            }

            // Draw path
            if (series.showPath) {
                cache.curvePath?.let { path ->
                    val dash = if (series.dashedLine) {
                        series.dashPattern ?: PathEffect.dashPathEffect(floatArrayOf(10f, 4f), 4f)
                    } else null
                    drawPath(path, color, style = Stroke(width = series.strokeWidth.toPx(), pathEffect = dash))
                }

                // Fill
                if (series.fillEnabled) {
                    cache.curvePath?.let { path ->
                        if (series.areaGradient != null) {
                            drawPath(path, brush = series.areaGradient, style = Fill)
                        } else {
                            drawPath(path, color = color, style = Fill)
                        }
                    }
                }

                // Area fill
                if (series.areaFillEnabled) {
                    cache.fillPath?.let { path ->
                        if (series.areaGradient != null) {
                            drawPath(path, brush = series.areaGradient, style = Fill)
                        } else {
                            drawPath(path, color = color, style = Fill)
                        }
                    }
                }
            }

            // Custom renderer
            series.customPainter?.invoke(scope, series, cache.pixelOffsets)
        }
    }
}

private fun pixelToDataX(px: Float, bounds: ViewportBounds, min: Float, max: Float, zoom: Float): Float {
    val scale = (bounds.bottomRight.x - bounds.bottomLeft.x) / (max - min)
    val off = min * scale
    return ((px - bounds.bottomLeft.x + off) / (scale * zoom)).coerceIn(min, max)
}

private fun pixelToDataY(px: Float, bounds: ViewportBounds, vararg axes: ChartAxis?): Float {
    val axis = axes.firstOrNull { it != null } ?: return 0f
    val scale = (bounds.bottomLeft.y - bounds.topLeft.y) / (axis!!.upperBound - axis.lowerBound)
    val off = axis.lowerBound * scale
    return ((bounds.bottomLeft.y - px + off) / scale).coerceIn(axis.lowerBound, axis.upperBound)
}

private fun findClosestPoint(
    tx: Float, ty: Float,
    series: List<DataSeries>,
    bounds: ViewportBounds,
    xMin: Float, xMax: Float,
    yMin: Float, yMax: Float,
    zoom: Float
): DataPointInfo? {
    if (series.isEmpty()) return null
    var best: DataPointInfo? = null
    var bestDist = Float.MAX_VALUE

    series.forEach { s ->
        s.dataPoints.forEach { pt ->
            val sx = (bounds.bottomRight.x - bounds.bottomLeft.x) / (xMax - xMin)
            val sy = (bounds.bottomLeft.y - bounds.topLeft.y) / (yMax - yMin)
            val px = bounds.bottomLeft.x + (pt.x - xMin) * sx * zoom
            val py = bounds.bottomLeft.y - (pt.y - yMin) * sy
            val dist = sqrt((px - tx) * (px - tx) + (py - ty) * (py - ty))
            if (dist < bestDist) {
                bestDist = dist
                best = DataPointInfo(pt, s, dist)
            }
        }
    }
    return best
}
