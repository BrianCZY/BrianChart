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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import com.brian.chart.compose.widgets.model.*

/**
 * ECG trace chart composable for medical waveform display
 */
@Composable
fun EcgTrace(
    modifier: Modifier = Modifier,
    config: EcgConfig? = null,
) {
    val ecgConfig = config ?: EcgConfig()
    val waveforms = ecgConfig.waveforms ?: emptyList()
    val lineStyle = ecgConfig.lineStyle
    val gridStyle = ecgConfig.gridStyle
    val dotStyle = ecgConfig.dotStyle

    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current

        val canvasSize by remember {
            derivedStateOf {
                with(density) { IntSize(maxWidth.roundToPx(), maxHeight.roundToPx()) }
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            clipRect {
                val leadCount = waveforms.size
                if (leadCount == 0) return@clipRect

                val cellWidth = canvasSize.width.toFloat() / (waveforms.maxOfOrNull { it?.size ?: 0 } ?: 1)
                val cellHeight = canvasSize.height.toFloat() / (leadCount * ECG_CELL_SIZE)

                // Draw background grid
                if (gridStyle.visible) {
                    drawGrid(this, cellWidth, cellHeight, leadCount, gridStyle, dotStyle, canvasSize)
                }

                // Draw waveforms
                drawWaveforms(this, waveforms, cellWidth, cellHeight, lineStyle)
            }
        }
    }
}

private fun drawGrid(
    scope: DrawScope,
    cellWidth: Float,
    cellHeight: Float,
    leadCount: Int,
    gridStyle: EcgGridStyle,
    dotStyle: EcgDotStyle,
    canvasSize: IntSize
) {
    scope.run {
        val gridPaint = Paint().apply {
            color = gridStyle.color.toArgb()
            strokeWidth = gridStyle.lineWidth.toPx()
            style = Paint.Style.STROKE
            isAntiAlias = true
        }

        val dotPaint = Paint().apply {
            color = dotStyle.color.toArgb()
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val totalCellsY = leadCount * ECG_CELL_SIZE
        val dotRadius = dotStyle.radius.toPx()

        // Vertical lines and dots
        var col = 0
        while (col * cellWidth <= canvasSize.width) {
            val x = col * cellWidth
            val isMajor = col % ECG_CELL_SIZE == 0

            if (isMajor) {
                gridPaint.strokeWidth = gridStyle.lineWidth.toPx() * 2
            } else {
                gridPaint.strokeWidth = gridStyle.lineWidth.toPx()
            }

            drawContext.canvas.nativeCanvas.drawLine(x, 0f, x, canvasSize.height.toFloat(), gridPaint)

            // Dots at intersections
            if (dotStyle.visible) {
                var row = 0
                while (row <= totalCellsY) {
                    val y = row * cellHeight
                    drawContext.canvas.nativeCanvas.drawCircle(x, y, dotRadius, dotPaint)
                    row++
                }
            }
            col++
        }

        // Horizontal lines
        var row = 0
        while (row <= totalCellsY) {
            val y = row * cellHeight
            val isMajor = row % ECG_CELL_SIZE == 0
            if (isMajor) {
                gridPaint.strokeWidth = gridStyle.lineWidth.toPx() * 2
            } else {
                gridPaint.strokeWidth = gridStyle.lineWidth.toPx()
            }
            drawContext.canvas.nativeCanvas.drawLine(0f, y, canvasSize.width.toFloat(), y, gridPaint)
            row++
        }
    }
}

private fun drawWaveforms(
    scope: DrawScope,
    waveforms: List<List<Float>?>,
    cellWidth: Float,
    cellHeight: Float,
    lineStyle: EcgLineStyle
) {
    scope.run {
        val path = Path()
        val strokeWidth = lineStyle.thickness.toPx()

        waveforms.forEachIndexed { leadIdx, samples ->
            if (samples.isNullOrEmpty()) return@forEachIndexed

            val baseY = leadIdx * ECG_CELL_SIZE * cellHeight + ECG_CELL_SIZE * cellHeight / 2
            path.rewind()

            var first = true
            samples.forEachIndexed { sampleIdx, value ->
                val x = sampleIdx * cellWidth
                val y = baseY - value * cellHeight / ECG_UNIT_HEIGHT

                if (first) {
                    path.moveTo(x, y)
                    first = false
                } else {
                    path.lineTo(x, y)
                }
            }

            drawPath(path, lineStyle.color, style = Stroke(width = strokeWidth))
        }
    }
}
