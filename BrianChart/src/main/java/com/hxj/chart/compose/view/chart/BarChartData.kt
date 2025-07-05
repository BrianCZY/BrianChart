package com.hxj.chart.compose.view.chart

import com.hxj.chart.compose.view.chart.Axis


data class BarChartData(
    var barData: BarData? = null,
    var xAxis: Axis = Axis(),
    var yLeftAxis: Axis = Axis(),
    var isScroll: Boolean = false
)
