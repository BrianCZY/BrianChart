package com.brian.chart.compose.view.chart


data class LineChartData(
    var lineList: List<Line>? = null,
    var xAxis: Axis = Axis(), //x
    var yLeftInsideAxis: Axis? = null,//y左内
    var yLeftAxis: Axis? = null, //y左外
    var yRightAxis: Axis? = null,//y右
    var isSelfAdaptation: Boolean = false, //自适配高度
    var isScroll: Boolean = false,
    var axisPadding: AxisPadding? = null
)
