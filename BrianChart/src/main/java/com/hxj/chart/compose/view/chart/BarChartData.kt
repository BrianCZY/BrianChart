package com.hxj.chart.compose.view.chart


data class BarChartData(
    val barData: BarData? = null,
    val xAxis: Axis = Axis(),
    val yLeftAxis: Axis = Axis(),
    val isScroll: Boolean = false
)
