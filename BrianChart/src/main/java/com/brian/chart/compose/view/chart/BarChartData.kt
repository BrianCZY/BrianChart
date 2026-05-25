package com.brian.chart.compose.view.chart

import com.brian.view.chart.AxisPadding


data class BarChartData(
    val barData: BarData? = null,
    val xAxis: Axis = Axis(),
    val yLeftAxis: Axis = Axis(),
    val isScroll: Boolean = false,
    /**自适配宽高,这个计算占用cpu较高,如果是要求性能较好的图表,建议设置为false,宽高自行计算或固定值*/
    val isSelfAdaptation: Boolean = false,
    val axisPadding: AxisPadding? = null,
    val limitLinePosition: LimitLinePosition = LimitLinePosition.BELOW, //限制线位置
)
