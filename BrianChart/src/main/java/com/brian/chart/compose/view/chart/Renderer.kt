package com.brian.chart.compose.view.chart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope

object Renderer {
    fun baseRenderer(drawScope: DrawScope, line: Line?, offsetList: List<Offset>?): Unit {
        line?.pointList?.forEachIndexed { index, point ->
            offsetList?.getOrNull(index)
                ?.let { point.selfDefinedValue?.invoke(drawScope, it) }
        }
    }

    fun emptyRenderer(drawScope: DrawScope, line: Line?, offsetList: List<Offset>?) = null
}