package com.hxj.chart.compose.view.chart

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Line(
    var pointList: List<Point> = emptyList<Point>(),
    var color: Color = Color.Blue,
    var isDrawCubic: Boolean = false,//是否启用贝塞尔曲线)
    var axisType: AxisType = AxisType.LEFT,
    var tag :String = "",
    var width: Dp = 1.dp,
    val isDashes: Boolean = false,//是否启用虚线
    val isPoints: Boolean = false,//是否启用散点
    val pathEffect: PathEffect? = null,// 虚线样式（自定义），null：则使用默认的样式
    var isDrawArea: Boolean = false, //是否面积，到x轴的面积填充，默认false
    var areaColor: Color = Color(0xFFB2DFDB), //面积填充颜色，默认浅绿色
)