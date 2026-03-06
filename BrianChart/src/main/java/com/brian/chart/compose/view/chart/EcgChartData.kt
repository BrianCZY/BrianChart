package com.brian.chart.compose.view.chart

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


/**
 *@author Brian
 *@Description: 心电图的数据模型
 */
data class EcgChartData(

    val ecgWaveLists:List<List<Float>?>? = null,//心电列表
    val lineDataSet: LineDataSet = LineDataSet(), //数据设置
    val grideDataSet: GrideDataSet = GrideDataSet(), //网格设置
    val pointDataSet: PointDataSet = PointDataSet()//背景圆点设置
)

data class GrideDataSet(
    var isDraw: Boolean = true, //是否绘制网格
    var color: Color = Color.Gray, //网格颜色
    var width: Dp = 1.dp//网格线宽度
)

data class PointDataSet(
    var isDraw: Boolean = true,//是否绘制圆点
    var color: Color = Color.Gray,//点颜色
    var radius: Dp = 1.dp//圆点的半径
)

data class LineDataSet(
    var color: Color = Color.Black,//颜色
    var width: Dp = 1.dp,//线宽度
    var onSecondDataNum: Int = 100,//1s的数据个数 根据实际设备设置数据
    @Deprecated("cellNum = leadCellNum * lines.size")
    var yCellNum: Int = 4, //y轴格子个数，一个格子(正方形) x = 0.2s  y =0.5mv
    @Deprecated("cellNum = leadCellNum * lines.size") //cellNum = leadCellNum *导联曲线的条数
    var leadCellNum: Int = 4 //一个导联所占的格子个数，一个格子(正方形) x = 0.2s  y =0.5mv
)
