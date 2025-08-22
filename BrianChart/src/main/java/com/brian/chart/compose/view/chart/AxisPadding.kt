package com.brian.chart.compose.view.chart

import androidx.compose.ui.unit.Dp

/**
 *@author Brian
 *@Description:Axis绘图区域的padding
 * 这个padding空间通常是label scale
 */
class AxisPadding(
    var start: Dp? = null,
    var top: Dp? = null,
    var end: Dp? = null,
    var bottom: Dp? = null,
) {

    fun padding(all: Dp) = AxisPadding(
        start = all,
        top = all,
        end = all,
        bottom = all
    )

    fun padding(start: Dp?, top: Dp?, end: Dp?, bottom: Dp?) = AxisPadding(
        start = start,
        top = top,
        end = end,
        bottom = bottom
    )
}
