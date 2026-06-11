package com.brian.chart.compose.widgets.chart

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.brian.chart.compose.widgets.model.*
import com.brian.chart.compose.widgets.renderer.AxisRenderer
import com.brian.chart.compose.widgets.util.ViewportCalculator

/**
 * A grouped/stacked bar chart composable
 */
@Composable
fun GroupedBarChart(
    modifier: Modifier = Modifier,
    config: BarChartConfig? = null,
    xAxis: ChartAxis = ChartAxis(),
    yAxis: ChartAxis = ChartAxis(),
    viewportPadding: ViewportPadding? = null,
) {
    BoxWithConstraints(modifier = modifier) {
        val textMeasurer: TextMeasurer = rememberTextMeasurer()
        val density = LocalDensity.current

        val canvasSize by remember {
            derivedStateOf {
                with(density) { IntSize(maxWidth.roundToPx(), maxHeight.roundToPx()) }
            }
        }

        val bounds by remember(canvasSize, viewportPadding, xAxis, yAxis) {
            derivedStateOf {
                ViewportCalculator.computeBounds(xAxis, yAxis, null, null, viewportPadding, density, textMeasurer, canvasSize)
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            clipRect {
                // Background grid
                AxisRenderer.drawGrid(this, xAxis, yAxis, null, null, bounds, 1f)

                // Axes
                AxisRenderer.drawAxes(this, xAxis, yAxis, null, null, bounds)

                // Labels
                AxisRenderer.drawLabels(this, xAxis, yAxis, null, null, bounds, 1f)

                // Axis names
                AxisRenderer.drawAxisNames(this, xAxis, yAxis, null, null, bounds, 1f)

                // Draw bars
                drawBars(this, config, bounds, xAxis.lowerBound, xAxis.upperBound, yAxis.lowerBound, yAxis.upperBound, density)
            }
        }
    }
}

private fun drawBars(
    scope: DrawScope,
    config: BarChartConfig?,
    bounds: ViewportBounds,
    xMin: Float, xMax: Float,
    yMin: Float, yMax: Float,
    density: androidx.compose.ui.unit.Density
) {
    scope.run {
        val sets = config?.dataSets ?: return
        if (sets.isEmpty()) return

        val xScale = (bounds.bottomRight.x - bounds.bottomLeft.x) / (xMax - xMin)
        val yScale = (bounds.bottomLeft.y - bounds.topLeft.y) / (yMax - yMin)
        val groupSpacing = config.groupSpacing
        val intraSpacing = config.intraGroupSpacing
        val setPadding = with(density) { config.setPadding.toPx() }

        // Calculate bar width
        val totalSets = sets.size
        val totalIntraSpacing = (totalSets - 1) * intraSpacing
        val availableWidth = xScale - groupSpacing - totalIntraSpacing
        val barWidth = with(density) { config.barWidth?.toPx() } ?: (availableWidth / totalSets * config.widthRatio)

        // Group entries by x value
        val grouped = mutableMapOf<Float, MutableList<Pair<Int, BarEntry>>>()
        sets.forEachIndexed { setIdx, set ->
            set.entries?.forEach { entry ->
                grouped.getOrPut(entry.x) { mutableListOf() }.add(setIdx to entry)
            }
        }

        // Draw each group
        grouped.forEach { (xVal, entries) ->
            val baseX = bounds.bottomLeft.x + (xVal - xMin) * xScale
            val groupWidth = totalSets * barWidth + totalIntraSpacing
            val groupStartX = baseX - groupWidth / 2

            entries.forEach { (setIdx, entry) ->
                val barX = groupStartX + setIdx * (barWidth + intraSpacing) + setPadding / 2
                val barW = barWidth - setPadding
                val set = sets[setIdx]

                if (entry.stackValues != null && entry.stackValues.isNotEmpty()) {
                    // Stacked bar
                    drawStackedBar(scope, entry, set, barX, barW, bounds.bottomLeft.y, yScale, yMin, density)
                } else {
                    // Simple bar
                    val barHeight = (entry.y - yMin) * yScale
                    val barY = bounds.bottomLeft.y - barHeight
                    val barTopLeft = Offset(barX, barY)
                    val barSize = Size(barW, barHeight)

                    if (entry.customPainter != null) {
                        entry.customPainter(this, set.color, barTopLeft, barSize, barHeight, set.label, entry.y)
                    } else if (set.backgroundPainter != null) {
                        set.backgroundPainter!!.invoke(this, set.color, barTopLeft, barSize)
                    } else {
                        drawRect(set.color, topLeft = barTopLeft, size = barSize)
                    }

                    // Value label
                    if (set.showValues) {
                        val paint = Paint().apply {
                            textSize = with(density) { set.valueFontSize.toPx() }
                            isAntiAlias = true
                        }
                        paint.color = (set.valueColor ?: Color.Black).toArgb()
                        val text = set.valueFormatter?.invoke(set.label, entry.y) ?: entry.y.toString()
                        val textX = barX + barW / 2 - paint.measureText(text) / 2
                        val textY = barY - with(density) { 4.dp.toPx() }
                        drawContext.canvas.nativeCanvas.drawText(text, textX, textY, paint)
                    }
                }
            }
        }
    }
}

private fun drawStackedBar(
    scope: DrawScope,
    entry: BarEntry,
    set: BarSet,
    barX: Float,
    barW: Float,
    baseY: Float,
    yScale: Float,
    yMin: Float,
    density: androidx.compose.ui.unit.Density
) {
    scope.run {
        val stacks = entry.stackValues ?: return
        var cumulative = 0f

        stacks.forEachIndexed { idx, value ->
            val stackHeight = value * yScale
            val stackY = baseY - cumulative - stackHeight
            val topLeft = Offset(barX, stackY)
            val size = Size(barW, stackHeight)

            val color = set.stackColors?.getOrNull(idx) ?: set.color
            val bgPainter = set.stackBackgrounds?.getOrNull(idx)
            val valueColor = set.stackValueColors?.getOrNull(idx) ?: set.valueColor ?: Color.Black

            if (entry.stackPainter != null) {
                entry.stackPainter(this, color, topLeft, size, stackHeight, set.label, value, idx)
            } else if (bgPainter != null) {
                bgPainter!!.invoke(this, color, topLeft, size)
            } else {
                drawRect(color, topLeft = topLeft, size = size)
            }

            // Value label
            if (set.showValues) {
                val paint = Paint().apply {
                    textSize = with(density) { set.valueFontSize.toPx() }
                    isAntiAlias = true
                }
                paint.color = valueColor.toArgb()
                val text = set.valueFormatter?.invoke(set.label, value) ?: value.toString()
                val textX = barX + barW / 2 - paint.measureText(text) / 2
                val textY = stackY + stackHeight / 2 + paint.textSize / 3
                drawContext.canvas.nativeCanvas.drawText(text, textX, textY, paint)
            }

            cumulative += stackHeight
        }
    }
}
