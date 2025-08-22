package com.brian.chart.compose.view.chart

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

open class Axis(
    var min: Float = 00f,//默认最小值
    var max: Float = 10f,//默认最大值
    var position: Float? = null, //默认的位置0f，position：如果是X轴的Axis，position则是相对于Y轴上的位置
    var labelInterval: Float? = null,//X轴标签的间隔
    var scaleInterval: Float? = null,//X轴刻度的间隔
    var labelTextSize: TextUnit = 12.sp,//标签的字体大小
    var scaleLengSize: Dp = 4.dp,//刻度的长度
    var color: Color = Color.Gray,
    var strokeSize: Dp = 1.dp,
    var name: String? = null,
    var chunkList: MutableList<Chunk>? = null,//画块 范围
    var limitLineList: MutableList<LimitLine>? = null,//画限制线
    var settingLabelValue: ((value: Float) -> String)? = null,//定制轴的标签内容
    var gridLine: GridLine? = null, //网格线
    var isDrawLabel: Boolean = true,//是否绘制标签
    var isDrawAxis: Boolean = true,//是否绘制轴
)