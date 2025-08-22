package com.brian.chart.compose.view.chart


data class BarChartData(
    var barData: BarData? = null,
    var xAxis: Axis = Axis(),
    var yLeftAxis: Axis = Axis(),
    var isScroll: Boolean = false
)
