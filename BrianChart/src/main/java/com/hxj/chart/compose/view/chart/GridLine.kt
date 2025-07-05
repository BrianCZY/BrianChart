package com.hxj.chart.compose.view.chart

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
/**
*@author Brian
*@Description: 网格线
*/
class GridLine(
    var interval: Float = 1f,//间隔
    var isDashes: Boolean = false, //是否虚线
    var color: Color = Color.LightGray,//颜色
    var width: Dp = 1.dp,//线条宽度
//    @RequiresApi(Build.VERSION_CODES.O)
    val pathEffect: PathEffect? = null// 虚线样式（自定义），null：则使用默认的样式 ，系统生效>Build.VERSION_CODES.O
    )