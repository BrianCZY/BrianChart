package com.brian.chart.compose.widgets.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Container for bar chart data configuration
 */
data class BarChartConfig(
    var dataSets: MutableList<BarSet>? = null,
    var groupSpacing: Float = 10f,
    var intraGroupSpacing: Float = 0f,
    var barWidth: Dp? = null,
    var widthRatio: Float = 0.8f,
    var setPadding: Dp = 2.dp
)

/**
 * A single group/set of bars in a bar chart
 */
data class BarSet(
    var entries: MutableList<BarEntry>? = null,
    var color: Color = Color.Cyan,
    var backgroundPainter: ((DrawScope, Color, Offset, Size) -> Unit)? = null,
    var label: String = "",
    var valueFontSize: TextUnit = 8.sp,
    var valueColor: Color? = null,
    var valueFormatter: ((String, Float) -> String)? = null,
    var showValues: Boolean = true,
    var stackColors: List<Color>? = null,
    var stackBackgrounds: List<((DrawScope, Color, Offset, Size) -> Unit)>? = null,
    var stackValueColors: List<Color>? = null
)

/**
 * A single bar entry, optionally with stacked values
 */
data class BarEntry(
    val x: Float,
    val y: Float,
    val stackValues: List<Float>? = null,
    val customPainter: ((DrawScope, Color, Offset, Size, Float, String, Float) -> Unit)? = null,
    val stackPainter: ((DrawScope, Color, Offset, Size, Float, String, Float, Int) -> Unit)? = null
)

object TextAlignment {
    const val TOP = "TOP"
    const val BOTTOM = "BOTTOM"
}
