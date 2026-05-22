package com.brian.chart.compose.view.chart

import com.brian.view.chart.AxisPadding


data class LineChartData(
    val lineList: List<Line>? = null,
    val xAxis: Axis = Axis(), //x
    val yLeftInsideAxis: Axis? = null,//y左内
    val yLeftAxis: Axis? = null, //y左外
    val yRightAxis: Axis? = null,//y右
    /**自适配宽高,这个计算占用cpu较高,如果是要求性能较好的图表,建议设置为false,宽高自行计算或固定值*/
    val isSelfAdaptation: Boolean = false,
    val isScroll: Boolean = false,
    val axisPadding: AxisPadding? = null,

)
