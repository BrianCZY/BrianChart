package com.hxj.chart.compose.view.chart

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

data class Point(
    val x: Float = 0f,
    val y: Float = 0f,

    /** 可自定义绘制内容，结合renderer使用,必须设置renderer,否则无效*/
    var selfDefinedValue: ((drawScope: DrawScope, offset: Offset) -> Unit)? = null,

    @Deprecated(message = "使用selfDefinedValue替换", level = DeprecationLevel.WARNING)
    var image: ImageBitmap? = null,
    @Deprecated(message = "使用selfDefinedValue替换", level = DeprecationLevel.WARNING)
    var radius: Float? = null,
    @Deprecated(message = "使用selfDefinedValue替换", level = DeprecationLevel.WARNING)
    var style: DrawStyle? = null,
    @Deprecated(message = "使用selfDefinedValue替换", level = DeprecationLevel.WARNING)
    var label: String? = null, // 标签
    @Deprecated(message = "使用selfDefinedValue替换", level = DeprecationLevel.WARNING)
    var labelColor: Color? = null, // 标签的颜色
    @Deprecated(message = "使用selfDefinedValue替换", level = DeprecationLevel.WARNING)
    var labelTextSize: TextUnit? = null, // 标签的字体大小

)