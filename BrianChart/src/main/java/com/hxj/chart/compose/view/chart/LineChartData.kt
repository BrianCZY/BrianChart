package com.hxj.chart.compose.view.chart

import com.hxj.chart.compose.view.chart.Axis


data class LineChartData(
    var lineList: MutableList<Line>? = null,
    var xAxis: Axis = Axis(), //x
    var yLeftInsideAxis: Axis? = null,//y左内
    var yLeftAxis: Axis? = null, //y左外
    var yRightAxis: Axis? = null,//y右
    var isSelfAdaptation: Boolean = false, //自适配高度
    var isScroll: Boolean = false
)
