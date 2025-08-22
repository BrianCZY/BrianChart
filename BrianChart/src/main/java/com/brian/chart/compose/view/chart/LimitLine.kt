package com.brian.chart.compose.view.chart

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class LimitLine(
    var value: Float,
    var isDashes: Boolean = false,
    var color: Color = Color(0xffF36464),
    var width: Dp = 1.dp,
    var text: String = "",
    var textSize: TextUnit = 12.sp,

    )