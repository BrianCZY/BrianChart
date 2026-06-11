package com.brian.chart.compose.widgets.renderer

import android.graphics.Paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.brian.chart.compose.widgets.model.ChartAxis
import com.brian.chart.compose.widgets.model.ColoredRegion
import com.brian.chart.compose.widgets.model.ThresholdLine
import com.brian.chart.compose.widgets.model.ViewportBounds
import java.math.BigDecimal

/**
 * Renders axis lines, labels, ticks, grid lines, colored regions, and threshold lines
 */
object AxisRenderer {

    fun drawGrid(
        drawScope: DrawScope,
        xAxis: ChartAxis,
        yPrimary: ChartAxis?,
        ySecondary: ChartAxis?,
        yRight: ChartAxis?,
        bounds: ViewportBounds,
        zoom: Float
    ) {
        drawScope.run {
            xAxis.gridStyle?.let { drawXGrid(this, it, bounds, xAxis.lowerBound, xAxis.upperBound, zoom) }
            yPrimary?.gridStyle?.let { drawYGrid(this, it, bounds, yPrimary.lowerBound, yPrimary.upperBound, zoom) }
            ySecondary?.gridStyle?.let { drawYGrid(this, it, bounds, ySecondary.lowerBound, ySecondary.upperBound, zoom) }
            yRight?.gridStyle?.let { drawYGrid(this, it, bounds, yRight.lowerBound, yRight.upperBound, zoom) }
        }
    }

    fun drawColoredRegions(
        drawScope: DrawScope,
        xAxis: ChartAxis,
        yPrimary: ChartAxis?,
        ySecondary: ChartAxis?,
        yRight: ChartAxis?,
        bounds: ViewportBounds,
    ) {
        drawScope.run {
            xAxis.coloredRegions?.let { drawXRegions(this, it, bounds, xAxis.lowerBound, xAxis.upperBound) }
            yPrimary?.coloredRegions?.let { drawYRegions(this, it, bounds, yPrimary.lowerBound, yPrimary.upperBound) }
            ySecondary?.coloredRegions?.let { drawYRegions(this, it, bounds, ySecondary.lowerBound, ySecondary.upperBound) }
            yRight?.coloredRegions?.let { drawYRegions(this, it, bounds, yRight.lowerBound, yRight.upperBound) }
        }
    }

    fun drawAxes(
        drawScope: DrawScope,
        xAxis: ChartAxis,
        yPrimary: ChartAxis?,
        ySecondary: ChartAxis?,
        yRight: ChartAxis?,
        bounds: ViewportBounds,
    ) {
        drawScope.run {
            val xScale = (bounds.bottomRight.x - bounds.bottomLeft.x) / (xAxis.upperBound - xAxis.lowerBound)
            var yScale = 0f
            var yOrigin = 0f

            when {
                yPrimary != null -> {
                    yScale = (bounds.bottomLeft.y - bounds.topLeft.y) / (yPrimary.upperBound - yPrimary.lowerBound)
                    xAxis.origin?.let { yOrigin = (it - yPrimary.lowerBound) * yScale }
                }
                ySecondary != null -> {
                    yScale = (bounds.bottomLeft.y - bounds.topLeft.y) / (ySecondary.upperBound - ySecondary.lowerBound)
                    xAxis.origin?.let { yOrigin = (it - ySecondary.lowerBound) * yScale }
                }
                yRight != null -> {
                    yScale = (bounds.bottomLeft.y - bounds.topLeft.y) / (yRight.upperBound - yRight.lowerBound)
                    xAxis.origin?.let { yOrigin = (it - yRight.lowerBound) * yScale }
                }
            }

            // X axis
            if (xAxis.showAxis) {
                drawLine(
                    start = Offset(bounds.bottomLeft.x, bounds.bottomLeft.y - yOrigin),
                    end = Offset(bounds.bottomRight.x, bounds.bottomRight.y - yOrigin),
                    color = xAxis.strokeColor,
                    strokeWidth = xAxis.strokeThickness.toPx()
                )
            }

            // Y Primary (left)
            yPrimary?.let { axis ->
                if (axis.showAxis) {
                    val xOff = axis.origin?.let { (it - xAxis.lowerBound) * xScale } ?: 0f
                    drawLine(
                        start = Offset(bounds.bottomLeft.x + xOff, bounds.bottomLeft.y),
                        end = Offset(bounds.topLeft.x + xOff, bounds.topLeft.y),
                        color = axis.strokeColor,
                        strokeWidth = axis.strokeThickness.toPx()
                    )
                }
            }

            // Y Secondary (left inside)
            ySecondary?.let { axis ->
                if (axis.showAxis) {
                    val xOff = axis.origin?.let { (it - xAxis.lowerBound) * xScale } ?: 0f
                    drawLine(
                        start = Offset(bounds.bottomLeft.x + xOff, bounds.bottomLeft.y),
                        end = Offset(bounds.topLeft.x + xOff, bounds.topLeft.y),
                        color = axis.strokeColor,
                        strokeWidth = axis.strokeThickness.toPx()
                    )
                }
            }

            // Y Right
            yRight?.let { axis ->
                if (axis.showAxis) {
                    val xOff = axis.origin?.let { (it - xAxis.upperBound) * xScale } ?: 0f
                    drawLine(
                        start = Offset(bounds.bottomRight.x + xOff, bounds.bottomRight.y),
                        end = Offset(bounds.topRight.x + xOff, bounds.topRight.y),
                        color = axis.strokeColor,
                        strokeWidth = axis.strokeThickness.toPx()
                    )
                }
            }
        }
    }

    fun drawLabels(
        drawScope: DrawScope,
        xAxis: ChartAxis,
        yPrimary: ChartAxis?,
        ySecondary: ChartAxis?,
        yRight: ChartAxis?,
        bounds: ViewportBounds,
        zoom: Float
    ) {
        drawScope.run {
            val xScale = (bounds.bottomRight.x - bounds.bottomLeft.x) / (xAxis.upperBound - xAxis.lowerBound)
            var yScale = 0f
            var yOrigin = 0f

            when {
                yPrimary != null -> {
                    yScale = (bounds.bottomLeft.y - bounds.topLeft.y) / (yPrimary.upperBound - yPrimary.lowerBound)
                    xAxis.origin?.let { yOrigin = (it - yPrimary.lowerBound) * yScale }
                }
                ySecondary != null -> {
                    yScale = (bounds.bottomLeft.y - bounds.topLeft.y) / (ySecondary.upperBound - ySecondary.lowerBound)
                    xAxis.origin?.let { yOrigin = (it - ySecondary.lowerBound) * yScale }
                }
                yRight != null -> {
                    yScale = (bounds.bottomLeft.y - bounds.topLeft.y) / (yRight.upperBound - yRight.lowerBound)
                    xAxis.origin?.let { yOrigin = (it - yRight.lowerBound) * yScale }
                }
            }

            // X axis ticks and labels
            xAxis.tickStep?.let { step ->
                drawXTicks(this, bounds, xAxis.lowerBound, xAxis.upperBound, step, xAxis.strokeColor,
                    xAxis.strokeThickness.toPx(), xAxis.tickLength.toPx(), zoom, yOrigin)
            }
            if (xAxis.showLabels) {
                xAxis.labelStep?.let { step ->
                    drawXLabels(this, bounds, xAxis.lowerBound, xAxis.upperBound, step, xAxis.strokeColor,
                        xAxis.labelFontSize.toPx(), zoom, xAxis.labelFormatter, yOrigin)
                }
            }

            // Y Primary labels
            yPrimary?.let { axis ->
                val xOff = axis.origin?.let { (it - xAxis.lowerBound) * xScale } ?: 0f
                axis.tickStep?.let { step ->
                    drawYLeftTicks(this, bounds, axis.lowerBound, axis.upperBound, step, axis.strokeColor,
                        axis.strokeThickness.toPx(), axis.tickLength.toPx(), zoom, xOff)
                }
                if (axis.showLabels) {
                    axis.labelStep?.let { step ->
                        drawYLeftLabels(this, bounds, axis.lowerBound, axis.upperBound, step, axis.strokeColor,
                            axis.labelFontSize.toPx(), zoom, axis.labelFormatter, xOff)
                    }
                }
            }

            // Y Secondary labels
            ySecondary?.let { axis ->
                val xOff = axis.origin?.let { (it - xAxis.lowerBound) * xScale } ?: 0f
                axis.tickStep?.let { step ->
                    drawYLeftTicks(this, bounds, axis.lowerBound, axis.upperBound, step, axis.strokeColor,
                        axis.strokeThickness.toPx(), axis.tickLength.toPx(), zoom, xOff)
                }
                if (axis.showLabels) {
                    axis.labelStep?.let { step ->
                        drawYLeftInsideLabels(this, bounds, axis.lowerBound, axis.upperBound, step, axis.strokeColor,
                            axis.labelFontSize.toPx(), zoom, axis.labelFormatter, xOff)
                    }
                }
            }

            // Y Right labels
            yRight?.let { axis ->
                val xOff = axis.origin?.let { (it - xAxis.upperBound) * xScale } ?: 0f
                axis.tickStep?.let { step ->
                    drawYRightTicks(this, bounds, axis.lowerBound, axis.upperBound, step, axis.strokeColor,
                        axis.strokeThickness.toPx(), axis.tickLength.toPx(), zoom, xOff)
                }
                if (axis.showLabels) {
                    axis.labelStep?.let { step ->
                        drawYRightLabels(this, bounds, axis.lowerBound, axis.upperBound, step, axis.strokeColor,
                            axis.labelFontSize.toPx(), zoom, axis.labelFormatter, xOff)
                    }
                }
            }
        }
    }

    fun drawAxisNames(
        drawScope: DrawScope,
        xAxis: ChartAxis,
        yPrimary: ChartAxis?,
        ySecondary: ChartAxis?,
        yRight: ChartAxis?,
        bounds: ViewportBounds,
        zoom: Float
    ) {
        drawScope.run {
            xAxis.label?.let { drawXName(this, it, bounds, xAxis.upperBound, xAxis.strokeColor, xAxis.labelFontSize.toPx(), zoom) }
            yPrimary?.label?.let { drawYLeftName(this, it, bounds, yPrimary.strokeColor, yPrimary.labelFontSize.toPx(), zoom) }
            ySecondary?.label?.let { drawYLeftInsideName(this, it, bounds, ySecondary.strokeColor, ySecondary.labelFontSize.toPx(), zoom) }
            yRight?.label?.let { drawYRightName(this, it, bounds, yRight.strokeColor, yRight.labelFontSize.toPx(), zoom) }
        }
    }

    fun drawThresholds(
        drawScope: DrawScope,
        xAxis: ChartAxis,
        yPrimary: ChartAxis?,
        ySecondary: ChartAxis?,
        yRight: ChartAxis?,
        bounds: ViewportBounds,
        zoom: Float
    ) {
        drawScope.run {
            xAxis.thresholdLines?.let { drawXThresholds(this, it, bounds, xAxis.lowerBound, xAxis.upperBound, zoom) }
            yPrimary?.thresholdLines?.let { drawYThresholds(this, it, bounds, yPrimary.lowerBound, yPrimary.upperBound, zoom) }
            ySecondary?.thresholdLines?.let { drawYThresholds(this, it, bounds, ySecondary.lowerBound, ySecondary.upperBound, zoom) }
            yRight?.thresholdLines?.let { drawYThresholds(this, it, bounds, yRight.lowerBound, yRight.upperBound, zoom) }
        }
    }

    // --- Private drawing helpers ---

    private fun drawXGrid(scope: DrawScope, grid: com.brian.chart.compose.widgets.model.GridStyle, bounds: ViewportBounds,
                          min: Float, max: Float, zoom: Float) {
        scope.run {
            val scale = (bounds.bottomRight.x - bounds.bottomLeft.x) / (max - min)
            val count = ((max - min) / grid.spacing).toInt()
            val stroke = if (grid.dashed) {
                Stroke(width = grid.lineWidth.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 4f), 4f))
            } else {
                Stroke(width = grid.lineWidth.toPx())
            }
            for (i in 0..count) {
                val x = (bounds.bottomLeft.x + i * grid.spacing * scale - grid.lineWidth.toPx() / 4) * zoom
                val path = Path().apply { moveTo(x, bounds.topLeft.y); lineTo(x, bounds.bottomLeft.y) }
                drawPath(path, grid.lineColor, style = stroke)
            }
        }
    }

    private fun drawYGrid(scope: DrawScope, grid: com.brian.chart.compose.widgets.model.GridStyle, bounds: ViewportBounds,
                          min: Float, max: Float, zoom: Float) {
        scope.run {
            val scale = (bounds.bottomLeft.y - bounds.topLeft.y) / (max - min)
            val count = ((max - min) / grid.spacing).toInt()
            val stroke = if (grid.dashed) {
                Stroke(width = grid.lineWidth.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 4f), 4f))
            } else {
                Stroke(width = grid.lineWidth.toPx())
            }
            for (i in 0..count) {
                val y = bounds.bottomLeft.y - i * grid.spacing * scale - grid.lineWidth.toPx() / 4
                val path = Path().apply { moveTo(bounds.bottomLeft.x, y); lineTo(bounds.bottomRight.x, y) }
                drawPath(path, grid.lineColor, style = stroke)
            }
        }
    }

    private fun drawXRegions(scope: DrawScope, regions: List<ColoredRegion>, bounds: ViewportBounds, min: Float, max: Float) {
        scope.run {
            val scale = (bounds.bottomRight.x - bounds.bottomLeft.x) / (max - min)
            regions.forEach { chunk ->
                val x1 = bounds.bottomLeft.x + (chunk.startValue - min) * scale
                val x2 = bounds.bottomLeft.x + (chunk.endValue - min) * scale
                drawRect(chunk.fillColor, topLeft = Offset(x1, bounds.topLeft.y), size = androidx.compose.ui.geometry.Size(x2 - x1, bounds.bottomLeft.y - bounds.topLeft.y))
            }
        }
    }

    private fun drawYRegions(scope: DrawScope, regions: List<ColoredRegion>, bounds: ViewportBounds, min: Float, max: Float) {
        scope.run {
            val scale = (bounds.bottomLeft.y - bounds.topLeft.y) / (max - min)
            regions.forEach { chunk ->
                val y1 = bounds.bottomLeft.y - (chunk.startValue - min) * scale
                val y2 = bounds.bottomLeft.y - (chunk.endValue - min) * scale
                drawRect(chunk.fillColor, topLeft = Offset(bounds.bottomLeft.x, y1), size = androidx.compose.ui.geometry.Size(bounds.bottomRight.x - bounds.bottomLeft.x, y2 - y1))
            }
        }
    }

    private fun drawXTicks(scope: DrawScope, bounds: ViewportBounds, min: Float, max: Float, interval: Float,
                           color: Color, strokeW: Float, tickLen: Float, zoom: Float, yOff: Float) {
        scope.run {
            val count = (max - min) / interval
            val step = (bounds.bottomRight.x - bounds.bottomLeft.x) / count
            for (i in 0..count.toInt()) {
                val x = (bounds.bottomLeft.x + i * step) * zoom
                drawLine(color = color, start = Offset(x, bounds.bottomLeft.y - yOff), end = Offset(x, bounds.bottomLeft.y + tickLen - yOff), strokeWidth = strokeW)
            }
        }
    }

    private fun drawXLabels(scope: DrawScope, bounds: ViewportBounds, min: Float, max: Float, interval: Float,
                            color: Color, textSize: Float, zoom: Float, formatter: ((Float) -> String)?, yOff: Float) {
        scope.run {
            val count = (max - min) / interval
            val step = (bounds.bottomRight.x - bounds.bottomLeft.x) / count
            val paint = Paint().apply { this.textSize = textSize; this.color = color.toArgb(); isAntiAlias = true }
            val baseY = bounds.bottomLeft.y + textSize + 4.dp.toPx() - yOff
            val entries = (0..count.toInt()).map { i ->
                val v = BigDecimal(min.toString()).add(BigDecimal(interval.toString()).multiply(BigDecimal(i))).toFloat()
                val text = formatter?.invoke(v) ?: (if (v == v.toInt().toFloat()) v.toInt().toString() else v.toString())
                val x = (bounds.bottomLeft.x + i * step - text.length * textSize / 2 * 0.6f) * zoom
                text to x
            }
            drawContext.canvas.nativeCanvas.apply { entries.forEach { (t, x) -> drawText(t, x, baseY, paint) } }
        }
    }

    private fun drawXName(scope: DrawScope, name: String, bounds: ViewportBounds, max: Float, color: Color, textSize: Float, zoom: Float) {
        scope.run {
            val paint = Paint().apply { this.textSize = textSize; this.color = color.toArgb(); isAntiAlias = true }
            val label = if (max.toInt().toFloat() == max) max.toInt().toString() else max.toString()
            val x = bounds.bottomRight.x + label.length * textSize / 2
            var y = bounds.bottomLeft.y + textSize + 4.dp.toPx()
            name.split("\n").forEach { line ->
                drawContext.canvas.nativeCanvas.drawText(line, x, y, paint)
                y += textSize
            }
        }
    }

    private fun drawYLeftTicks(scope: DrawScope, bounds: ViewportBounds, min: Float, max: Float, interval: Float,
                               color: Color, strokeW: Float, tickLen: Float, zoom: Float, xOff: Float) {
        scope.run {
            val count = (max - min) / interval
            val step = (bounds.bottomLeft.y - bounds.topLeft.y) / count
            for (i in 0..count.toInt()) {
                val y = bounds.bottomLeft.y - i * step
                drawLine(color = color, start = Offset(bounds.bottomLeft.x + xOff, y), end = Offset(bounds.bottomLeft.x - tickLen * zoom + xOff, y), strokeWidth = strokeW)
            }
        }
    }

    private fun drawYRightTicks(scope: DrawScope, bounds: ViewportBounds, min: Float, max: Float, interval: Float,
                                color: Color, strokeW: Float, tickLen: Float, zoom: Float, xOff: Float) {
        scope.run {
            val count = (max - min) / interval
            val step = (bounds.bottomLeft.y - bounds.topLeft.y) / count
            for (i in 0..count.toInt()) {
                val y = bounds.bottomLeft.y - i * step
                drawLine(color = color, start = Offset(bounds.bottomRight.x + xOff, y), end = Offset(bounds.bottomRight.x + tickLen * zoom + xOff, y), strokeWidth = strokeW)
            }
        }
    }

    private fun drawYLeftLabels(scope: DrawScope, bounds: ViewportBounds, min: Float, max: Float, interval: Float,
                                color: Color, textSize: Float, zoom: Float, formatter: ((Float) -> String)?, xOff: Float) {
        scope.run {
            val count = (max - min) / interval
            val step = (bounds.bottomLeft.y - bounds.topLeft.y) / count
            val paint = Paint().apply { this.textSize = textSize; this.color = color.toArgb(); isAntiAlias = true }
            (0..count.toInt()).map { i ->
                val v = BigDecimal(min.toString()).add(BigDecimal(interval.toString()).multiply(BigDecimal(i))).toFloat()
                val text = formatter?.invoke(v) ?: (if (v == v.toInt().toFloat()) v.toInt().toString() else v.toString())
                val tw = text.length * textSize
                val x = bounds.bottomLeft.x + xOff - 8.dp.toPx() - tw / 2
                val y = bounds.bottomLeft.y - i * step + textSize * 0.3f
                text to androidx.compose.ui.geometry.Offset(x, y)
            }.let { entries ->
                drawContext.canvas.nativeCanvas.apply { entries.forEach { (t, pos) -> drawText(t, pos.x, pos.y, paint) } }
            }
        }
    }

    private fun drawYLeftInsideLabels(scope: DrawScope, bounds: ViewportBounds, min: Float, max: Float, interval: Float,
                                      color: Color, textSize: Float, zoom: Float, formatter: ((Float) -> String)?, xOff: Float) {
        scope.run {
            val count = (max - min) / interval
            val step = (bounds.bottomLeft.y - bounds.topLeft.y) / count
            val paint = Paint().apply { this.textSize = textSize; this.color = color.toArgb(); isAntiAlias = true }
            (0..count.toInt()).map { i ->
                val v = BigDecimal(min.toString()).add(BigDecimal(interval.toString()).multiply(BigDecimal(i))).toFloat()
                val text = formatter?.invoke(v) ?: (if (v == v.toInt().toFloat()) v.toInt().toString() else v.toString())
                val x = bounds.bottomLeft.x + xOff + 8.dp.toPx()
                val y = bounds.bottomLeft.y - i * step + textSize / 4
                text to androidx.compose.ui.geometry.Offset(x, y)
            }.let { entries ->
                drawContext.canvas.nativeCanvas.apply { entries.forEach { (t, pos) -> drawText(t, pos.x, pos.y, paint) } }
            }
        }
    }

    private fun drawYRightLabels(scope: DrawScope, bounds: ViewportBounds, min: Float, max: Float, interval: Float,
                                 color: Color, textSize: Float, zoom: Float, formatter: ((Float) -> String)?, xOff: Float) {
        scope.run {
            val count = (max - min) / interval
            val step = (bounds.bottomRight.y - bounds.topRight.y) / count
            val paint = Paint().apply { this.textSize = textSize; this.color = color.toArgb(); isAntiAlias = true }
            (0..count.toInt()).map { i ->
                val v = BigDecimal(min.toString()).add(BigDecimal(interval.toString()).multiply(BigDecimal(i))).toFloat()
                val text = formatter?.invoke(v) ?: (if (v == v.toInt().toFloat()) v.toInt().toString() else v.toString())
                val x = bounds.bottomRight.x + xOff + 8.dp.toPx()
                val y = bounds.bottomRight.y - i * step + textSize * 0.3f
                text to androidx.compose.ui.geometry.Offset(x, y)
            }.let { entries ->
                drawContext.canvas.nativeCanvas.apply { entries.forEach { (t, pos) -> drawText(t, pos.x, pos.y, paint) } }
            }
        }
    }

    private fun drawYLeftName(scope: DrawScope, name: String, bounds: ViewportBounds, color: Color, textSize: Float, zoom: Float) {
        scope.run {
            val paint = Paint().apply { this.textSize = textSize; this.color = color.toArgb(); isAntiAlias = true }
            val x = 2.dp.toPx()
            var y = bounds.topLeft.y - textSize
            name.split("\n").reversed().forEach { line ->
                drawContext.canvas.nativeCanvas.drawText(line, x, y, paint)
                y -= textSize
            }
        }
    }

    private fun drawYLeftInsideName(scope: DrawScope, name: String, bounds: ViewportBounds, color: Color, textSize: Float, zoom: Float) {
        scope.run {
            val paint = Paint().apply { this.textSize = textSize; this.color = color.toArgb(); isAntiAlias = true }
            val x = bounds.bottomLeft.x + 8.dp.toPx()
            var y = bounds.topLeft.y - textSize
            name.split("\n").reversed().forEach { line ->
                drawContext.canvas.nativeCanvas.drawText(line, x, y, paint)
                y -= textSize
            }
        }
    }

    private fun drawYRightName(scope: DrawScope, name: String, bounds: ViewportBounds, color: Color, textSize: Float, zoom: Float) {
        scope.run {
            val paint = Paint().apply { this.textSize = textSize; this.color = color.toArgb(); isAntiAlias = true }
            val x = bounds.topRight.x + 2.dp.toPx()
            var y = bounds.topRight.y - textSize
            name.split("\n").reversed().forEach { line ->
                drawContext.canvas.nativeCanvas.drawText(line, x, y, paint)
                y -= textSize
            }
        }
    }

    private fun drawXThresholds(scope: DrawScope, lines: List<ThresholdLine>, bounds: ViewportBounds, min: Float, max: Float, zoom: Float) {
        scope.run {
            val scale = (bounds.bottomRight.x - bounds.bottomLeft.x) / (max - min)
            lines.forEach { line ->
                val x = bounds.bottomLeft.x + (line.value - min) * scale
                val w = line.lineWidth.toPx()
                val dash = if (line.dashed) PathEffect.dashPathEffect(floatArrayOf(10f, 4f), 4f) else null
                val lx = (x - w / 4) * zoom
                if (line.customPainter != null) {
                    line.customPainter(this, Offset(lx, bounds.topLeft.y), Offset(lx, bounds.bottomLeft.y), line)
                } else {
                    if (dash != null) {
                        val path = Path().apply { moveTo(lx, bounds.topLeft.y); lineTo(lx, bounds.bottomLeft.y) }
                        drawPath(path, line.lineColor, style = Stroke(width = w, pathEffect = dash))
                    } else {
                        drawLine(color = line.lineColor, start = Offset(lx, bounds.topLeft.y), end = Offset(lx, bounds.bottomLeft.y), strokeWidth = w)
                    }
                    val paint = Paint().apply { textSize = line.captionSize.toPx(); color = line.lineColor.toArgb(); isAntiAlias = true }
                    drawContext.canvas.nativeCanvas.drawText(line.caption, x - (paint.textSize / 2) * line.caption.length - w - 4f, bounds.topLeft.y, paint)
                }
            }
        }
    }

    private fun drawYThresholds(scope: DrawScope, lines: List<ThresholdLine>, bounds: ViewportBounds, min: Float, max: Float, zoom: Float) {
        scope.run {
            val scale = (bounds.bottomLeft.y - bounds.topLeft.y) / (max - min)
            lines.forEach { line ->
                val y = bounds.bottomLeft.y - (line.value - min) * scale
                val w = line.lineWidth.toPx()
                val dash = if (line.dashed) PathEffect.dashPathEffect(floatArrayOf(10f, 4f), 4f) else null
                val ly = y - w / 4
                if (line.customPainter != null) {
                    line.customPainter(this, Offset(bounds.bottomLeft.x, ly), Offset(bounds.bottomRight.x, ly), line)
                } else {
                    if (dash != null) {
                        val path = Path().apply { moveTo(bounds.bottomLeft.x, ly); lineTo(bounds.bottomRight.x, ly) }
                        drawPath(path, line.lineColor, style = Stroke(width = w, pathEffect = dash))
                    } else {
                        drawLine(color = line.lineColor, start = Offset(bounds.bottomLeft.x, ly), end = Offset(bounds.bottomRight.x, ly), strokeWidth = w)
                    }
                    val paint = Paint().apply { textSize = line.captionSize.toPx(); color = line.lineColor.toArgb(); isAntiAlias = true }
                    drawContext.canvas.nativeCanvas.drawText(line.caption, bounds.bottomRight.x - paint.textSize * line.caption.length, ly - w / 4 - paint.textSize / 2, paint)
                }
            }
        }
    }
}
