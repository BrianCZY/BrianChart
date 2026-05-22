package com.brian.chart.compose.view.chart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

data class Line(
    var pointList: List<Point> = emptyList(),
    var color: Color = Color.Blue,
    var isDrawCubic: Boolean = false, // 是否启用贝塞尔曲线)
    var axisType: AxisType = AxisType.LEFT,
    var tag: String = "",
    var width: Dp = 1.dp,
    var isDrawPath: Boolean = true,//画曲线
    var isFill: Boolean = false, // 是否启用填充
    var isDrawArea: Boolean = false, //是否面积，到x轴的面积填充，默认false
    var drawAreaBrush: Brush? = null,//绘制面积的样式
    var isDashes: Boolean = false, // 是否启用虚线
    var isPoints: Boolean = false, // 是否启用散点
    var pathEffect: PathEffect? = null,// 虚线样式（自定义），null：则使用默认的样式
    var renderer: ((drawScope: DrawScope, line: Line?, offsetList: List<Offset>?) -> Unit)? = Renderer::emptyRenderer,//渲染器,自定义绘制
    var code: String = "${AxisType.LEFT.name}_${System.nanoTime()}_${Random.nextInt(100000)}",//创建Line时，生成一个code,用于区分Line

)