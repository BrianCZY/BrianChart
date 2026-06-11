package com.brian.chart.compose.widgets.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Axis orientation identifier
 */
enum class AxisOrientation {
    PRIMARY_LEFT,
    SECONDARY_LEFT,
    RIGHT
}

/**
 * Defines where limit lines are rendered relative to chart data
 */
enum class LimitLineLayer {
    FOREGROUND,
    BACKGROUND
}

/**
 * Touch interaction event types
 */
enum class GestureEvent {
    PRESS,
    DRAG,
    RELEASE,
    TAP
}

/**
 * Represents a 2D coordinate point
 */
data class Coordinate(
    val x: Float = 0f,
    val y: Float = 0f,
    val customRenderer: ((DrawScope, Offset) -> Unit)? = null
)

/**
 * Axis configuration for chart
 */
data class ChartAxis(
    var lowerBound: Float = 0f,
    var upperBound: Float = 10f,
    var origin: Float? = null,
    var labelStep: Float? = null,
    var tickStep: Float? = null,
    var labelFontSize: TextUnit = 12.sp,
    var tickLength: Dp = 4.dp,
    var strokeColor: Color = Color.Gray,
    var strokeThickness: Dp = 1.dp,
    var label: String? = null,
    var coloredRegions: MutableList<ColoredRegion>? = null,
    var thresholdLines: MutableList<ThresholdLine>? = null,
    var labelFormatter: ((Float) -> String)? = null,
    var gridStyle: GridStyle? = null,
    var showLabels: Boolean = true,
    var showAxis: Boolean = true,
)

/**
 * Colored background region between two values on an axis
 */
data class ColoredRegion(
    val startValue: Float,
    val endValue: Float,
    val fillColor: Color = Color(0xffEAF1FA)
)

/**
 * Threshold/reference line on a chart
 */
data class ThresholdLine(
    val value: Float,
    val dashed: Boolean = false,
    val lineColor: Color = Color(0xffF36464),
    val lineWidth: Dp = 1.dp,
    val caption: String = "",
    val captionSize: TextUnit = 12.sp,
    val customPainter: ((DrawScope, Offset, Offset, ThresholdLine) -> Unit)? = null,
)

/**
 * Grid line configuration
 */
data class GridStyle(
    val spacing: Float = 1f,
    val dashed: Boolean = false,
    val lineColor: Color = Color.LightGray,
    val lineWidth: Dp = 1.dp,
)

/**
 * Data series configuration for line charts
 */
data class DataSeries(
    val dataPoints: List<Coordinate> = emptyList(),
    val seriesColor: Color = Color.Blue,
    val smoothCurve: Boolean = false,
    val axisTarget: AxisOrientation = AxisOrientation.PRIMARY_LEFT,
    val seriesTag: String = "",
    val strokeWidth: Dp = 1.dp,
    val showPath: Boolean = true,
    val fillEnabled: Boolean = false,
    val areaFillEnabled: Boolean = false,
    val areaGradient: androidx.compose.ui.graphics.Brush? = null,
    val dashedLine: Boolean = false,
    val showPoints: Boolean = false,
    val dashPattern: PathEffect? = null,
    val customPainter: ((DrawScope, DataSeries?, List<Offset>?) -> Unit)? = null,
    val identifier: String = "${AxisOrientation.PRIMARY_LEFT.name}_${System.nanoTime()}_${kotlin.random.Random.nextInt(100000)}",
)

/**
 * Cached path and point data for rendering optimization
 */
data class RenderCache(
    val series: DataSeries? = null,
    var curvePath: androidx.compose.ui.graphics.Path? = null,
    var fillPath: androidx.compose.ui.graphics.Path? = null,
    var pixelOffsets: List<Offset>? = null
)

/**
 * Touch interaction data passed to callbacks
 */
data class TouchPayload(
    val dataX: Float,
    val dataY: Float,
    val pixelX: Float,
    val pixelY: Float,
    val closestPoint: DataPointInfo? = null,
    val yPrimary: Float? = null,
    val ySecondary: Float? = null,
    val yRight: Float? = null,
    val gesture: GestureEvent = GestureEvent.TAP
) {
    fun allYValues(): Map<String, Float> = buildMap {
        yPrimary?.let { put("Primary", it) }
        ySecondary?.let { put("Secondary", it) }
        yRight?.let { put("Right", it) }
    }
}

/**
 * Information about the nearest data point to a touch
 */
data class DataPointInfo(
    val point: Coordinate,
    val series: DataSeries,
    val pixelDistance: Float
)

/**
 * Padding configuration for chart drawing area
 */
class ViewportPadding(
    var left: Dp? = null,
    var top: Dp? = null,
    var right: Dp? = null,
    var bottom: Dp? = null,
) {
    fun uniform(value: Dp) = ViewportPadding(left = value, top = value, right = value, bottom = value)
    fun asymmetric(start: Dp?, top: Dp?, end: Dp?, bottom: Dp?) =
        ViewportPadding(left = start, top = top, right = end, bottom = bottom)
}

/**
 * Defines the four corners of the chart drawing area in pixels
 */
data class ViewportBounds(
    val bottomLeft: Coordinate = Coordinate(),
    val bottomRight: Coordinate = Coordinate(),
    val topRight: Coordinate = Coordinate(),
    val topLeft: Coordinate = Coordinate(),
)
