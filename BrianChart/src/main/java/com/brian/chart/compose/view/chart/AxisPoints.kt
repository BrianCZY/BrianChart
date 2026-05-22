package com.brian.view.chart

import com.brian.chart.compose.view.chart.Point

data class AxisPoints(
    val point0: Point = Point(), //左上
    val point1: Point = Point(), //右上
    val point2: Point = Point(), //左下
    val point3: Point = Point(), //右下
)
