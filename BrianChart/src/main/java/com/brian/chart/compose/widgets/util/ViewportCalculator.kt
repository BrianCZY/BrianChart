package com.brian.chart.compose.widgets.util

import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import com.brian.chart.compose.widgets.model.ChartAxis
import com.brian.chart.compose.widgets.model.Coordinate
import com.brian.chart.compose.widgets.model.ViewportBounds
import com.brian.chart.compose.widgets.model.ViewportPadding
import java.math.BigDecimal

/**
 * Calculates chart viewport bounds and padding requirements
 */
object ViewportCalculator {

    /**
     * Computes the four corner points of the drawing area
     */
    fun computeBounds(
        xAxis: ChartAxis,
        yPrimary: ChartAxis?,
        ySecondary: ChartAxis?,
        yRight: ChartAxis?,
        padding: ViewportPadding?,
        density: Density,
        textMeasurer: TextMeasurer,
        canvasSize: IntSize,
    ): ViewportBounds {
        var leftPad: Float? = null
        var rightPad: Float? = null
        var topPad: Float? = null
        var bottomPad: Float? = null
        padding?.apply {
            with(density) {
                leftPad = left?.toPx()
                rightPad = right?.toPx()
                topPad = top?.toPx()
                bottomPad = bottom?.toPx()
            }
        }

        val leftPadding = leftPad ?: computeLeftPadding(yPrimary, xAxis, textMeasurer, density)
        val rightPadding = rightPad ?: computeRightPadding(yRight, xAxis, textMeasurer, density)
        val topPadding = topPad ?: computeTopPadding(yPrimary, yRight, textMeasurer, density)
        val bottomPadding = bottomPad ?: computeBottomPadding(xAxis, textMeasurer, density)

        return ViewportBounds(
            bottomLeft = Coordinate(0f + leftPadding, canvasSize.height - bottomPadding),
            bottomRight = Coordinate(canvasSize.width - rightPadding, canvasSize.height - bottomPadding),
            topRight = Coordinate(canvasSize.width - rightPadding, 0f + topPadding),
            topLeft = Coordinate(0f + leftPadding, 0f + topPadding),
        )
    }

    private fun computeLeftPadding(
        yAxis: ChartAxis?,
        xAxis: ChartAxis,
        measurer: TextMeasurer,
        density: Density
    ): Float {
        var paddingY = 0f
        var paddingX = 0f
        var tickSize = 0f

        yAxis?.let { axis ->
            val maxLabel = resolveMaxLabel(axis)
            val minLabel = resolveMinLabel(axis)
            val maxLayout = measurer.measure(
                text = maxLabel,
                style = TextStyle(fontSize = axis.labelFontSize)
            )
            val minLayout = measurer.measure(
                text = minLabel,
                style = TextStyle(fontSize = axis.labelFontSize)
            )
            val nameLayout = measurer.measure(
                text = axis.label ?: "",
                style = TextStyle(fontSize = axis.labelFontSize)
            )
            tickSize = if (axis.tickStep != null) with(density) { axis.tickLength.toPx() } else 0f

            val widths = listOf(
                if (axis.showLabels) maxLayout.size.width else 0,
                if (axis.showLabels) minLayout.size.width else 0,
                nameLayout.size.width
            )
            paddingY = widths.maxOrNull()?.toFloat() ?: 0f
        }

        val xLabelLayout = measurer.measure(
            text = xAxis.lowerBound.toString(),
            style = TextStyle(fontSize = xAxis.labelFontSize)
        )
        paddingX = if (xAxis.showLabels) xLabelLayout.size.width / 4f else 0f

        val padding = maxOf(paddingY, paddingX)
        return if (padding == 0f) padding + tickSize else padding + tickSize + 8f
    }

    private fun computeRightPadding(
        yAxis: ChartAxis?,
        xAxis: ChartAxis,
        measurer: TextMeasurer,
        density: Density
    ): Float {
        var paddingY = 0f
        var paddingX = 0f
        var tickSize = 0f

        yAxis?.let { axis ->
            val maxLabel = resolveMaxLabel(axis)
            val minLabel = resolveMinLabel(axis)
            val maxLayout = measurer.measure(
                text = maxLabel,
                style = TextStyle(fontSize = axis.labelFontSize)
            )
            val minLayout = measurer.measure(
                text = minLabel,
                style = TextStyle(fontSize = axis.labelFontSize)
            )
            val nameLayout = measurer.measure(
                text = axis.label ?: "",
                style = TextStyle(fontSize = axis.labelFontSize)
            )
            tickSize = if (axis.tickStep != null) with(density) { axis.tickLength.toPx() } else 0f

            val widths = listOf(
                if (axis.showLabels) maxLayout.size.width else 0,
                if (axis.showLabels) minLayout.size.width else 0,
                nameLayout.size.width
            )
            paddingY = widths.maxOrNull()?.toFloat() ?: 0f
        }

        val maxLayout = measurer.measure(
            text = xAxis.upperBound.toString(),
            style = TextStyle(fontSize = xAxis.labelFontSize)
        )
        val nameLayout = measurer.measure(
            text = xAxis.label ?: "",
            style = TextStyle(fontSize = xAxis.labelFontSize)
        )
        paddingX = maxOf(
            if (xAxis.showLabels) maxLayout.size.width / 4f else 0f,
            nameLayout.size.width.toFloat()
        )

        val padding = maxOf(paddingY, paddingX)
        return if (padding == 0f) padding + tickSize else padding + tickSize + 8f
    }

    private fun computeTopPadding(
        yLeft: ChartAxis?,
        yRight: ChartAxis?,
        measurer: TextMeasurer,
        density: Density
    ): Float {
        var padding = 0f
        val leftNameLayout = measurer.measure(
            text = yLeft?.label ?: "",
            style = TextStyle(fontSize = yLeft?.labelFontSize ?: 12.sp)
        )
        val rightNameLayout = measurer.measure(
            text = yRight?.label ?: "",
            style = TextStyle(fontSize = yRight?.labelFontSize ?: 12.sp)
        )
        val heights = listOf(
            if (yLeft?.label != null) leftNameLayout.size.height else 0,
            if (yRight?.label != null) rightNameLayout.size.height else 0
        )
        padding = heights.maxOrNull()?.toFloat() ?: 0f
        return if (padding == 0f) padding else padding + 8f
    }

    private fun computeBottomPadding(
        axis: ChartAxis?,
        measurer: TextMeasurer,
        density: Density
    ): Float {
        var padding = 0f
        var tickSize = 0f
        axis?.let {
            val nameLayout = measurer.measure(
                text = it.label ?: "",
                style = TextStyle(fontSize = it.labelFontSize)
            )
            val maxLayout = measurer.measure(
                text = it.upperBound.toString(),
                style = TextStyle(fontSize = it.labelFontSize)
            )
            val nameH = if (it.label != null) nameLayout.size.height else 0
            val labelH = if (it.showLabels) maxLayout.size.height else 0
            tickSize = if (it.tickStep != null) with(density) { it.tickLength.toPx() } else 0f
            padding = maxOf(nameH, labelH).toFloat()
        }
        return if (padding == 0f) padding + tickSize else padding + tickSize + 8f
    }

    private fun resolveMaxLabel(axis: ChartAxis): String {
        val interval = axis.labelStep ?: return axis.upperBound.toString()
        if (interval <= 0) return axis.upperBound.toString()
        val num = ((axis.upperBound - axis.lowerBound) / interval).toInt()
        return if (num > 0) {
            BigDecimal(axis.lowerBound.toString())
                .add(BigDecimal(interval.toString()).multiply(BigDecimal(num - 1)))
                .toString()
        } else axis.upperBound.toString()
    }

    private fun resolveMinLabel(axis: ChartAxis): String {
        val interval = axis.labelStep ?: return axis.lowerBound.toString()
        if (interval <= 0) return axis.lowerBound.toString()
        return BigDecimal(axis.lowerBound.toString())
            .add(BigDecimal(interval.toString()).multiply(BigDecimal(0)))
            .toString()
    }
}
