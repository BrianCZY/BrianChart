package com.brian.chart.compose.view.chart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.unit.TextUnit

data class Point(
    val x: Float = 0f,
    val y: Float = 0f,

    /** 可自定义绘制内容，结合renderer使用,必须设置renderer,否则无效*/
    var selfDefinedValue: ((drawScope: DrawScope, offset: Offset) -> Unit)? = null,


)