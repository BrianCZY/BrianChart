package com.brian.chart.compose.view.chart

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
*@author Brian
*@Description: 网格线
*/
class GridLine(
    var interval: Float = 1f,//间隔
    var isDashes: Boolean = false, //是否虚线
    var color: Color = Color.LightGray,//颜色
    var width: Dp = 1.dp,//线条宽度

    )