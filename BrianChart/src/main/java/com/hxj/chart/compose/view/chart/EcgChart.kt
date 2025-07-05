package com.hxj.chart.compose.view.chart

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * @author Brian
 * @Description: 心电 图表
 *
 */
//一个格子(正方形) x = 0.2s  y =0.5mv
val ONE_POINT_HEIGHT = 0.1f //0.1mv  背景1小点（格子）的高 0.1mv
val ONE_POINT_WIDTH = 0.04f //0.04s 背景1小点（格子）的宽 0.04s
val ONE_CELL_POINT_NUM = 5 //一个格子的点个数
private val TAG = "EcgChart"

@Composable
fun EcgChart(
    modifier: Modifier = Modifier, data: EcgChartData
) {
    // 分离静态背景和动态内容
    Box(modifier = modifier.defaultMinSize(10.dp)) {

        // 静态背景（网格和点） - 只在尺寸变化时重绘
        EcgBackground(data = data, modifier = Modifier.matchParentSize())

        // 动态曲线 - 需要频繁更新的部分
        Canvas(modifier = Modifier.matchParentSize()) {
            clipRect {
                if (size.width > 0 && size.height > 0) {
                    drawEcgCurve(this, data, size)
                }
            }

        }
    }
}

@Composable
private fun EcgBackground(
    data: EcgChartData,
    modifier: Modifier = Modifier,
) {
    Log.d(TAG, "EcgBackground: 绘制背景")
    // 使用 remember 缓存背景绘制
    LocalDensity.current
    var sizeState by remember { mutableStateOf(Size.Zero) }
    var ecgPoint by remember {
        mutableStateOf<MutableList<Offset>?>(null)
    }
    val scope = rememberCoroutineScope()
    LaunchedEffect(ecgPoint?.size) {
        // 当尺寸变化时，重新计算圆点集合
        scope.launch {
            ecgPoint = getEcgPoint(
                size = sizeState,
                lineDataSet = data.lineDataSet
            )
        }

    }

    Canvas(modifier = modifier.onSizeChanged { size ->
        sizeState = Size(size.width.toFloat(), size.height.toFloat())
    }) {
        if (sizeState.width > 0 && sizeState.height > 0) {
            // 只在尺寸变化时重绘背景、或在lineDataSet、grideDataSet、pointDataSet变化时重绘
            drawEcgGrid(
                this,
                sizeState,
                lineDataSet = data.lineDataSet,
                grideDataSet = data.grideDataSet
            )
            if (data.pointDataSet.isDraw) {
                ecgPoint?.let {
//                    drawEcgPoint(this, pointDataSet = data.pointDataSet, points = it)
                    drawEcgPointsByLine(this, pointDataSet = data.pointDataSet, points = it)
                }
            }
        }
    }
}

/**
 *@author Brian
 *@Description:
 */
fun drawEcgGrid(
    drawScope: DrawScope,
    size: Size,
    lineDataSet: LineDataSet = LineDataSet(),
    grideDataSet: GrideDataSet = GrideDataSet()
) {
    drawScope.run {
        //y轴上的网格
        val oneCellMv = ONE_POINT_HEIGHT * ONE_CELL_POINT_NUM //一个格子的mv
        val mvSum = lineDataSet.yCellNum * 0.5f
        val yGridNum = (mvSum / oneCellMv)

        val oneGridePx = size.height / yGridNum
        val y0Px = size.height //y的0点位置  这里 y分为正负

        val grideOffset = 0f

//            //画最大值的gride


        //画Y轴上的网
//            val yMinCoefficient = if (lineDataSet.maxMv > 0) 1 else -1
        if (yGridNum > 1) {
            for (i in 0..yGridNum.toInt()) {
                val maxY = y0Px - (i * oneGridePx + grideOffset)
//                    Log.d("EcgChart", "i  = ${i}  maxY =${maxY}")
                drawLine(
                    start = Offset(x = 0f, y = maxY),
                    end = Offset(x = size.width, y = maxY),
                    color = grideDataSet.color,
                    strokeWidth = grideDataSet.width.value
                )
            }
        }

        //x轴上的网格
        val xGrideNum = (size.width / oneGridePx).toInt()
//            Log.d("EcgChart", "drawEcgGrid  xGrideNum = ${xGrideNum}")
        if (xGrideNum > 1) {
            for (i in 0..xGrideNum) {
                val x = i * oneGridePx - grideOffset
//                    Log.d("EcgChart", "xGride x  = ${x}")
                drawLine(
                    start = Offset(x = x, y = 0f),
                    end = Offset(
                        x = x,
                        y = size.height
                    ),
                    color = grideDataSet.color,
                    strokeWidth = grideDataSet.width.value
                )
            }
        }
    }

}

/**
 *@author Brian
 *@Description: 优化版，减少循环次数，批量绘制圆点
 */

fun drawEcgPoint(
    drawScope: DrawScope, size: Size, lineDataSet: LineDataSet = LineDataSet(),
    pointDataSet: PointDataSet = PointDataSet()
) {
    drawScope.run {
        val oneCellMv = ONE_POINT_HEIGHT * ONE_CELL_POINT_NUM
        val mvSum = lineDataSet.yCellNum * 0.5f
        val yGridNum = (mvSum / oneCellMv)
        val oneGridePx = size.height / yGridNum
        val oneCirclePx = oneGridePx / ONE_CELL_POINT_NUM
        val y0Px = size.height

        val yCircleNum = (mvSum / ONE_POINT_HEIGHT).toInt()
        val xCircleNum = (size.width / oneCirclePx).toInt()

        // 预分配点集合，批量绘制
        val points = mutableListOf<Offset>()

        for (y in 1 until yCircleNum) {
            if (y % 5 == 0) continue
            for (z in 0..xCircleNum) {
                if (z % 5 == 0) continue
                points.add(
                    Offset(
                        x = z * oneCirclePx,
                        y = y0Px - y * oneCirclePx
                    )
                )
            }
        }
        if (points.isNotEmpty()) {
            drawPoints(
                points = points,
                pointMode = PointMode.Points,
                color = pointDataSet.color,
                strokeWidth = pointDataSet.radius.toPx() * 2
            )
        }
    }
}

/**
 *@author Brian
 *@Description: 批量绘制圆点,经测试3万个点时，性能较差
 */

fun drawEcgPoint(
    drawScope: DrawScope,
    pointDataSet: PointDataSet = PointDataSet(),
    points: MutableList<Offset>
) {
    drawScope.run {
        drawPoints(
            points = points,
            pointMode = PointMode.Points, // 独立点模式
            color = pointDataSet.color,
            strokeWidth = pointDataSet.radius.toPx() * 2, // 点的大小
            cap = StrokeCap.Round // 圆形点
        )
    }
}

/**
*@author Brian
*@Description: 批量绘制圆点，使用线段代替点，性能较高，缺点：画出来的点是正方形，不是圆形
*/
fun drawEcgPointsByLine(
    drawScope: DrawScope,
    points: List<Offset>,
    pointDataSet: PointDataSet
) {
    drawScope.run {
        val radiusPx = pointDataSet.radius.toPx() * 2
        val path = Path().apply {
            points.forEach { point ->
                moveTo(point.x - radiusPx / 2, point.y)
                // 绘制一个极小线段（确保可见）
                lineTo(point.x + radiusPx / 2, point.y)
            }
        }

        drawPath(
            path = path,
            color = pointDataSet.color,
            style = Stroke(width = radiusPx) // 点的大小
        )
    }
}

/**
 *@author Brian
 *@Description: 获取圆点集合
 */

fun getEcgPoint(
    size: Size, lineDataSet: LineDataSet = LineDataSet(),
): MutableList<Offset> {
    Log.e(TAG, "getEcgPoint: 获取圆点集合")
    val oneCellMv = ONE_POINT_HEIGHT * ONE_CELL_POINT_NUM
    val mvSum = lineDataSet.yCellNum * 0.5f
    val yGridNum = (mvSum / oneCellMv)
    val oneGridePx = size.height / yGridNum
    val oneCirclePx = oneGridePx / ONE_CELL_POINT_NUM
    val y0Px = size.height

    val yCircleNum = (mvSum / ONE_POINT_HEIGHT).toInt()
    val xCircleNum = (size.width / oneCirclePx).toInt()

    // 预分配点集合，批量绘制
    val points = mutableListOf<Offset>()

    for (y in 1 until yCircleNum) {
        if (y % 5 == 0) continue
        for (z in 0..xCircleNum) {
            if (z % 5 == 0) continue
            points.add(
                Offset(
                    x = z * oneCirclePx,
                    y = y0Px - y * oneCirclePx
                )
            )
        }
    }
    return points

}

/**
 *@author Brian
 *@Description:画线
 */
fun drawEcgCurve(drawScope: DrawScope, data: EcgChartData, size: Size) {
//    Log.d(TAG, "drawEcgCurve: 绘制曲线")
    data.apply {
        drawScope.run {
            val oneCellMv = ONE_POINT_HEIGHT * ONE_CELL_POINT_NUM //一个格子的mv
            val oneCellTime = ONE_POINT_WIDTH * ONE_CELL_POINT_NUM //一个格子的mv
            val mvSum = lineDataSet.yCellNum * 0.5f
            val yGridNum = (mvSum / oneCellMv)
            val oneGridePx = size.height / yGridNum
            val oneDataYPx = size.height / mvSum //1mv 对应的px
            val oneDataXPx = (oneDataYPx * oneCellMv) / oneCellTime



            data.ecgWaveLists?.filter { it != null && it.hasNext() }
                ?.forEachIndexed { index, floats ->
                    floats?.let {
                        drawOnePath(
                            drawScope = this,
                            data = this@apply,
                            floats = it,
                            oneDataXPx = oneDataXPx,
                            oneDataYPx = oneDataYPx,
                            y0Px = ((index + 0.5f) * lineDataSet.leadCellNum) * oneGridePx
                        )
                    }

                }


        }
    }
}


private fun buildEcgPath(
    floats: Iterator<Float>,
    lineDataSet: LineDataSet,
    oneDataXPx: Float,
    oneDataYPx: Float,
    y0Px: Float,
    size: Size
): Path {
    val path = Path()
    var index = 0 // 手动跟踪索引
    while (floats.hasNext()) {
        val fl = floats.next()
        val X = index.toFloat() / lineDataSet.onSecondDataNum * oneDataXPx
        val Y = y0Px - fl * oneDataYPx
        if (X < size.width && Y < size.height) {
            if (index == 0) {
                path.moveTo(X, Y)
            } else {
                path.lineTo(X, Y)
            }
        }
        index++ // 手动更新索引
    }
    return path
}

fun drawOnePath(
    drawScope: DrawScope,
    data: EcgChartData,
    floats: Iterator<Float>,
    oneDataXPx: Float,
    oneDataYPx: Float,
    y0Px: Float
) {
    data.apply {
        drawScope.run {
            val path = buildEcgPath(
                floats,
                lineDataSet,
                oneDataXPx,
                oneDataYPx,
                y0Px,
                size
            )
            drawPath(
                path = path,
                color = lineDataSet.color,
                style = Stroke(width = lineDataSet.width.toPx())
            )
        }
    }
}
val colorFFFFC4C3 = Color(0xFFFFC4C3) // 定义颜色常量






@Composable
@Preview(heightDp = 200, widthDp = 825)
fun EcgChartPreview3(
) {
    MaterialTheme {
        Surface {
            EcgChart(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxHeight(),
                data = EcgChartData(
                    lineDataSet = LineDataSet(),
                    grideDataSet = GrideDataSet(color = Color.Red, width = 2.dp)
                )
            )
        }
    }

}

@Composable
@Preview(heightDp = 200, widthDp = 825)
fun EcgChartPreview4(
) {
    MaterialTheme {
        Surface {
            EcgChart(
                modifier = Modifier,
                data = EcgChartData(
                    lineDataSet = LineDataSet(),
                    grideDataSet = GrideDataSet(color = Color.Red, width = 2.dp)
                )
            )
        }
    }

}

