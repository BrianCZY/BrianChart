package com.brian.view.chart

import com.brian.chart.compose.view.chart.Point

data class AxisPoints(
    val point0: Point = Point(),//左下角
    val point1: Point = Point(),//右下角
    val point2: Point = Point(),//右上角
    val point3: Point = Point(),//左上角
)
