package com.hxj.chart.compose.view.chart

import android.os.Build
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import java.math.BigDecimal
import com.hxj.chart.compose.view.chart.Axis
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random


/**
 * @author Brian
 * @Description:画颜色块
 */
fun drawXChunk(
    drawScope: DrawScope,
    chunkList: MutableList<Chunk>?,
    point0: Point,
    point1: Point,
    point2: Point,
    point3: Point,
    axisMin: Float,
    axisMax: Float,

    ) {
    drawScope.run {


        val oneDataYPx = (point1.x - point0.x) / (axisMax - axisMin) // X轴上 1f单位数据点对应的px数
        chunkList?.forEachIndexed { index, chunk ->
            val X1 = point0.x + chunk.start * oneDataYPx
            val X2 = point0.x + chunk.end * oneDataYPx
            val Y1 = point0.y  //
            val Y2 = point3.y  //

            drawRect(
                color = chunk.color,
                topLeft = Offset(x = X1, y = Y1),
                size = Size(X2 - X1, Y2 - Y1)
            )
        }


    }


}


fun drawChunk(
    drawScope: DrawScope,
    xAxis: Axis,
    yLeftInsideAxis: Axis? = null,
    yLeftAxis: Axis? = null,
    yRightAxis: Axis? = null,
    point0: Point,
    point1: Point,
    point2: Point,
    point3: Point,
) {
    drawScope.run {
        xAxis.let {
            it.chunkList?.let { chunkList ->
                drawXChunk(
                    drawScope = this,
                    chunkList = chunkList,
                    point0 = point0,
                    point1 = point1,
                    point2 = point2,
                    point3 = point3,
                    axisMax = it.max,
                    axisMin = it.min,
                )
            }
        }
        yLeftInsideAxis?.let {
            it.chunkList?.let { chunkList ->
                drawYChunk(
                    drawScope = this,
                    yChunkList = chunkList,
                    point0 = point0,
                    point1 = point1,
                    point2 = point2,
                    point3 = point3,
                    yAxisMax = it.max,
                    yAxisMin = it.min,
                )
            }
        }
        yLeftAxis?.let {
            it.chunkList?.let { chunkList ->
                drawYChunk(
                    drawScope = this,
                    yChunkList = chunkList,
                    point0 = point0,
                    point1 = point1,
                    point2 = point2,
                    point3 = point3,
                    yAxisMax = it.max,
                    yAxisMin = it.min,
                )
            }


        }
        yRightAxis?.let {
            it.chunkList?.let { chunkList ->
                drawYChunk(
                    drawScope = this,
                    yChunkList = chunkList,
                    point0 = point0,
                    point1 = point1,
                    point2 = point2,
                    point3 = point3,
                    yAxisMax = it.max,
                    yAxisMin = it.min,
                )
            }


        }
    }
}

fun drawXYAxis(
    drawScope: DrawScope,
    xAxis: Axis,
    yLeftInsideAxis: Axis? = null,
    yLeftAxis: Axis? = null,
    yRightAxis: Axis? = null,
    point0: Point,
    point1: Point,
    point2: Point,
    point3: Point
) {
    drawScope.run {

        val oneDataXPx = (point1.x - point0.x) / (xAxis.max - xAxis.min) // X轴上 1f单位数据点对应的px数
        var oneDataYPx = 0f
        var yOffset = 0f
        when {
            yLeftAxis != null -> {
                oneDataYPx =
                    (point0.y - point3.y) / (yLeftAxis.max - yLeftAxis.min) // Y轴上 1f单位数据点对应的px数
                xAxis.position?.let {
                    yOffset = (it - yLeftAxis.min) * oneDataYPx
                }

            }

            yLeftInsideAxis != null -> {
                oneDataYPx =
                    (point0.y - point3.y) / (yLeftInsideAxis.max - yLeftInsideAxis.min) // Y轴上 1f单位数据点对应的px数
                xAxis.position?.let {
                    yOffset = (it - yLeftInsideAxis.min) * oneDataYPx
                }
            }

            yRightAxis != null -> {
                oneDataYPx =
                    (point0.y - point3.y) / (yRightAxis.max - yRightAxis.min) // Y轴上 1f单位数据点对应的px数
                xAxis.position?.let {
                    yOffset = (it - yRightAxis.min) * oneDataYPx
                }
            }
        }


        xAxis.let {
            //绘制 X轴
            if (it.isDrawAxis) {

                drawLine(
                    start = Offset(point0.x, point0.y - yOffset),
                    end = Offset(point1.x, point1.y - yOffset),
                    color = it.color,
                    strokeWidth = it.strokeSize.toPx()
                )
            }
        }
        //绘制 Y轴 左内
        yLeftInsideAxis?.let {
            if (it.isDrawAxis) {

                val xOffset = it.position?.let { (it - xAxis.min) * oneDataXPx } ?: 0f
                drawLine(
                    start = Offset(point0.x + xOffset, point0.y),
                    end = Offset(point3.x + xOffset, point3.y),
                    color = it.color,
                    strokeWidth = it.strokeSize.toPx()
                )
            }
        }
        //绘制 Y轴 左
        yLeftAxis?.let {
            if (it.isDrawAxis) {
                val xOffset = it.position?.let { (it - xAxis.min) * oneDataXPx } ?: 0f
                drawLine(
                    start = Offset(point0.x + xOffset, point0.y),
                    end = Offset(point3.x + xOffset, point3.y),
                    color = it.color,
                    strokeWidth = it.strokeSize.toPx()
                )
            }
        }
        //绘制 Y轴 右
        yRightAxis?.let {
            if (it.isDrawAxis) {
                val xOffset = it.position?.let { (it - xAxis.max) * oneDataXPx } ?: 0f
                drawLine(
                    start = Offset(point1.x + xOffset, point1.y),
                    end = Offset(point2.x + xOffset, point2.y),
                    color = it.color,
                    strokeWidth = it.strokeSize.toPx()
                )
            }
        }
    }

}

fun getScaleLengSize(drawScope: DrawScope, axis: Axis?): Float {
    drawScope.run {
        return if (axis?.scaleInterval != null) {
            axis.scaleLengSize.toPx()
        } else {
            0f
        }//左边刻度的长度
    }

}

fun drawLable(
    drawScope: DrawScope,
    xAxis: Axis,
    yLeftInsideAxis: Axis? = null,
    yLeftAxis: Axis? = null,
    yRightAxis: Axis? = null,
    point0: Point,
    point1: Point,
    point2: Point,
    point3: Point,
    scale: Float = 1f
) {
    drawScope.run {
        val oneDataXPx = (point1.x - point0.x) / (xAxis.max - xAxis.min) // X轴上 1f单位数据点对应的px数
        var oneDataYPx = 0f
        var yOffset = 0f
        when {
            yLeftAxis != null -> {
                oneDataYPx =
                    (point0.y - point3.y) / (yLeftAxis.max - yLeftAxis.min) // Y轴上 1f单位数据点对应的px数
                xAxis.position?.let {
                    yOffset = (it - yLeftAxis.min) * oneDataYPx
                }

            }

            yLeftInsideAxis != null -> {
                oneDataYPx =
                    (point0.y - point3.y) / (yLeftInsideAxis.max - yLeftInsideAxis.min) // Y轴上 1f单位数据点对应的px数
                xAxis.position?.let {
                    yOffset = (it - yLeftInsideAxis.min) * oneDataYPx
                }
            }

            yRightAxis != null -> {
                oneDataYPx =
                    (point0.y - point3.y) / (yRightAxis.max - yRightAxis.min) // Y轴上 1f单位数据点对应的px数
                xAxis.position?.let {
                    yOffset = (it - yRightAxis.min) * oneDataYPx
                }
            }
        }
        xAxis.let {
            it.scaleInterval?.let { scaleInterval ->
                drawXaxisBottomScale(
                    drawScope = this,
                    point0 = point0,
                    point1 = point1,
                    point2 = point2,
                    point3 = point3,
                    defaultXAxisMax = it.max,
                    defaultXAxisMin = it.min,
                    axisColor = it.color,
                    scaleLengSize = it.scaleLengSize.toPx(),
                    axisStrokeSize = it.strokeSize.toPx(),
                    scaleInterval = scaleInterval,
                    scale = scale,
                    yOffset = yOffset
                )

            }
            if (it.isDrawLabel) {
                it.labelInterval?.let { labelInterval ->

                    drawXaxisBottomLabel(
                        drawScope = this,
                        point0 = point0,
                        point1 = point1,
                        point2 = point2,
                        point3 = point3,
                        defaultXAxisMax = it.max,
                        defaultXAxisMin = it.min,
                        labelColor = it.color,
                        labelInterval = labelInterval,
                        labelTextSizePx = it.labelTextSize.toPx(),
                        scale = scale,
                        settingLabelValue = it.settingLabelValue,
                        yOffset = yOffset
                    )
                }
            }

        }
        yLeftInsideAxis?.let {
            val xOffset = it.position?.let { (it - xAxis.min) * oneDataXPx } ?: 0f
            it.scaleInterval?.let { scaleInterval ->
                drawYAxisLeftInsideScale(
                    drawScope = this,
                    point0 = point0,
                    point1 = point1,
                    point2 = point2,
                    point3 = point3,
                    defaultXAxisMax = it.max,
                    defaultXAxisMin = it.min,
                    axisColor = it.color,
                    scaleLengSize = it.scaleLengSize.toPx(),
                    axisStrokeSize = it.strokeSize.toPx(),
                    scaleInterval = scaleInterval,
                    scale = scale,
                    xOffset = xOffset
                )
            }
            if (it.isDrawLabel) {
                it.labelInterval?.let { labelInterval ->
                    drawYAxisLeftInsideLabel(
                        drawScope = this,
                        point0 = point0,
                        point1 = point1,
                        point2 = point2,
                        point3 = point3,
                        defaultYAxisMax = it.max,
                        defaultYAxisMin = it.min,
                        labelColor = it.color,
                        labelInterval = labelInterval,
                        labelTextSizePx = it.labelTextSize.toPx(),
                        scale = scale,
                        settingLabelValue = it.settingLabelValue,
                        xOffset = xOffset
                    )
                }
            }
        }
        yLeftAxis?.let {
            val xOffset = it.position?.let { (it - xAxis.min) * oneDataXPx } ?: 0f
            it.scaleInterval?.let { scaleInterval ->
                drawYAxisLeftScale(
                    drawScope = this,
                    point0 = point0,
                    point1 = point1,
                    point2 = point2,
                    point3 = point3,
                    defaultXAxisMax = it.max,
                    defaultXAxisMin = it.min,
                    axisColor = it.color,
                    scaleLengSize = it.scaleLengSize.toPx(),
                    axisStrokeSize = it.strokeSize.toPx(),
                    scaleInterval = scaleInterval,
                    scale = scale,
                    xOffset = xOffset
                )
            }
            if (it.isDrawLabel) {
                it.labelInterval?.let { labelInterval ->
                    drawYAxisLeftLabel(
                        drawScope = this,
                        point0 = point0,
                        point1 = point1,
                        point2 = point2,
                        point3 = point3,
                        defaultYAxisMax = it.max,
                        defaultYAxisMin = it.min,
                        labelColor = it.color,
                        labelInterval = labelInterval,
                        labelTextSizePx = it.labelTextSize.toPx(),
                        scale = scale,
                        settingLabelValue = it.settingLabelValue,
                        xOffset = xOffset
                    )
                }
            }
        }
        yRightAxis?.let {
            val xOffset = it.position?.let { (it - xAxis.max) * oneDataXPx } ?: 0f
            it.scaleInterval?.let { scaleInterval ->
                drawYAxisRightScale(
                    drawScope = this,
                    point0 = point0,
                    point1 = point1,
                    point2 = point2,
                    point3 = point3,
                    defaultXAxisMax = it.max,
                    defaultXAxisMin = it.min,
                    axisColor = it.color,
                    scaleLengSize = it.scaleLengSize.toPx(),
                    axisStrokeSize = it.strokeSize.toPx(),
                    scaleInterval = scaleInterval,
                    scale = scale,
                    xOffset = xOffset
                )
            }
            if (it.isDrawLabel) {
                it.labelInterval?.let { labelInterval ->
                    drawYAxisRightLabel(
                        drawScope = this,
                        point0 = point0,
                        point1 = point1,
                        point2 = point2,
                        point3 = point3,
                        defaultYAxisMax = it.max,
                        defaultYAxisMin = it.min,
                        labelColor = it.color,
                        labelInterval = labelInterval,
                        labelTextSizePx = it.labelTextSize.toPx(),
                        scale = scale,
                        settingLabelValue = it.settingLabelValue,
                        xOffset = xOffset
                    )
                }
            }

        }
    }


}

fun drawLimitLine(
    drawScope: DrawScope,
    xAxis: Axis,
    yLeftInsideAxis: Axis? = null,
    yLeftAxis: Axis? = null,
    yRightAxis: Axis? = null,
    point0: Point,
    point1: Point,
    point2: Point,
    point3: Point,
    scale: Float
) {
    drawScope.run {
        xAxis.let {
            it.limitLineList?.let { limitLineList ->
                drawXLimitLine(
                    drawScope = this,
                    xLimitLineList = limitLineList,
                    point0 = point0,
                    point1 = point1,
                    point2 = point2,
                    point3 = point3,
                    axisMax = it.max,
                    axisMin = it.min,
                    scale = scale,
                )

            }


        }
        yLeftInsideAxis?.let {
            it.limitLineList?.let { limitLineList ->
                drawYLimitLine(
                    drawScope = this,
                    yLimitLineList = limitLineList,
                    point0 = point0,
                    point1 = point1,
                    point2 = point2,
                    point3 = point3,
                    axisMax = it.max,
                    axisMin = it.min,
                    scale = scale,
                )

            }
        }
        yLeftAxis?.let {
            it.limitLineList?.let { limitLineList ->
                drawYLimitLine(
                    drawScope = this,
                    yLimitLineList = limitLineList,
                    point0 = point0,
                    point1 = point1,
                    point2 = point2,
                    point3 = point3,
                    axisMax = it.max,
                    axisMin = it.min,
                    scale = scale,
                )

            }

        }
        yRightAxis?.let {
            it.limitLineList?.let { limitLineList ->
                drawYLimitLine(
                    drawScope = this,
                    yLimitLineList = limitLineList,
                    point0 = point0,
                    point1 = point1,
                    point2 = point2,
                    point3 = point3,
                    axisMax = it.max,
                    axisMin = it.min,
                    scale = scale,
                )

            }
        }
    }

}

fun drawAxisName(
    drawScope: DrawScope,
    xAxis: Axis,
    yLeftInsideAxis: Axis? = null,
    yLeftAxis: Axis? = null,
    yRightAxis: Axis? = null,
    point0: Point,
    point1: Point,
    point2: Point,
    point3: Point,
    scale: Float
) {
    drawScope.run {
        xAxis.let {
            it.name?.let { name ->
                drawXaxisBottomName(
                    drawScope = this,
                    name = name,
                    point0 = point0,
                    point1 = point1,
                    point2 = point2,
                    point3 = point3,
                    defaultXAxisMax = it.max,
                    labelColor = it.color,
                    labelTextSizePx = it.labelTextSize.toPx(),
                    scale = scale,
                )

            }


        }
        yLeftInsideAxis?.let {
            it.name?.let { name ->
                drawYAxisLeftInsideName(
                    drawScope = this,
                    name = name,
                    point0 = point0,
                    point1 = point1,
                    point2 = point2,
                    point3 = point3,

                    labelColor = it.color,
                    labelTextSizePx = it.labelTextSize.toPx(),
                    scale = scale,
                )

            }
        }
        yLeftAxis?.let {
            it.name?.let { name ->
                drawYAxisLeftName(
                    drawScope = this,
                    name = name,
                    point0 = point0,
                    point1 = point1,
                    point2 = point2,
                    point3 = point3,

                    labelColor = it.color,
                    labelTextSizePx = it.labelTextSize.toPx(),
                    scale = scale,
                )

            }
        }
        yRightAxis?.let {
            it.name?.let { name ->
                drawYAxisRightName(
                    drawScope = this,
                    name = name,
                    point0 = point0,
                    point1 = point1,
                    point2 = point2,
                    point3 = point3,

                    labelColor = it.color,
                    labelTextSizePx = it.labelTextSize.toPx(),
                    scale = scale,
                )

            }

        }
    }
}

fun getTestChunkList(): MutableList<Chunk> {
    val yChunkList: MutableList<Chunk> = mutableListOf()
    yChunkList.add(Chunk(40f, 60f))
    yChunkList.add(Chunk(10f, 20f))
    return yChunkList
}

fun getTestXChunkList(): MutableList<Chunk> {
    val yChunkList: MutableList<Chunk> = mutableListOf()
    yChunkList.add(Chunk(20f, 25f))
    return yChunkList
}

fun getTestChunkList1(): MutableList<Chunk> {
    val yChunkList: MutableList<Chunk> = mutableListOf()
    yChunkList.add(Chunk(25f, 50f, color = Color(0X2218D276)))
    return yChunkList
}

fun getTestChunkList2(): MutableList<Chunk> {
    val yChunkList: MutableList<Chunk> = mutableListOf()
    yChunkList.add(Chunk(800f, 1000f, color = Color(0X222FF4E87)))
    return yChunkList
}

fun getTestChunkList3(): MutableList<Chunk> {
    val yChunkList: MutableList<Chunk> = mutableListOf()
    yChunkList.add(Chunk(1500f, 1800f, color = Color(0X22058BF6)))
    return yChunkList
}

fun getTestLimitLineList(): MutableList<LimitLine> {
    val limitLineList: MutableList<LimitLine> = mutableListOf()
    limitLineList.add(LimitLine(50f, true, width = 2.dp, color = Color.Gray, text = "测试"))
    limitLineList.add(LimitLine(15f))
    return limitLineList
}

fun getTestPlusOrMinusLimitLineList(): MutableList<LimitLine> {
    val limitLineList: MutableList<LimitLine> = mutableListOf()
    limitLineList.add(LimitLine(50f, true, width = 2.dp, color = Color.Gray, text = "测试"))
    limitLineList.add(LimitLine(-25f))
    return limitLineList
}

fun getTestXLimitLineList(): MutableList<LimitLine> {
    val limitLineList: MutableList<LimitLine> = mutableListOf()
    limitLineList.add(LimitLine(100f, true, width = 2.dp, color = Color.Gray, text = "测试"))
    limitLineList.add(LimitLine(20f))
    return limitLineList
}

fun getTestXLimitLineList1(): MutableList<LimitLine> {
    val limitLineList: MutableList<LimitLine> = mutableListOf()
    limitLineList.add(LimitLine(10f, true, width = 2.dp, color = Color.Gray, text = "测试"))
    limitLineList.add(LimitLine(20f))
    return limitLineList
}

fun getTestYLimitLineList1(): MutableList<LimitLine> {
    val limitLineList: MutableList<LimitLine> = mutableListOf()
    limitLineList.add(
        LimitLine(
            10f,
            true,
            width = 2.dp,
            color = Color(0XFF18D276),
            text = "限制线"
        )
    )
    return limitLineList
}

fun getTestYLimitLineList2(): MutableList<LimitLine> {
    val limitLineList: MutableList<LimitLine> = mutableListOf()
    limitLineList.add(
        LimitLine(
            600f,
            true,
            width = 2.dp,
            color = Color(0XFFFF4E87),
            text = "限制线"
        )
    )
    return limitLineList
}

fun getTestYLimitLineList3(): MutableList<LimitLine> {
    val limitLineList: MutableList<LimitLine> = mutableListOf()
    limitLineList.add(
        LimitLine(
            1200f,
            true,
            width = 2.dp,
            color = Color(0XFF058BF6),
            text = "限制线"
        )
    )
    return limitLineList
}

/**
 * @author Brian
 * @Description: test 测试数据，测试UI效果使用
 */
fun getTestLineList(): MutableList<Line> {
    val linList: MutableList<Line> = mutableListOf()
    val point = mutableListOf(
        Point(10f, 10f), Point(50f, 100f), Point(100f, 30f),
        Point(150f, 200f), Point(200f, 120f), Point(250f, 10f),
        Point(300f, 280f), Point(350f, 100f), Point(400f, 10f),
        Point(450f, 100f), Point(500f, 200f)
    )
    val point1 = mutableListOf(
        Point(10f, 210f), Point(50f, 150f), Point(100f, 130f),
        Point(150f, 200f), Point(200f, 80f), Point(250f, 240f),
        Point(300f, 20f), Point(350f, 150f), Point(400f, 50f),
        Point(450f, 240f), Point(500f, 140f)
    )
    linList.add(
        Line(
            point,
            color = Color(0xff50E3C2),
            isDashes = true,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 12f), 2f)
        )
    )
    linList.add(Line(point1, color = Color(0xff4A90E2), isDrawCubic = true))
    return linList
}

fun getTestLineList2(): MutableList<Line> {
    val linList: MutableList<Line> = mutableListOf()
    val point = mutableListOf(
        Point(0f, 10f), Point(5f, 100f), Point(10f, 30f),
        Point(15f, 200f), Point(20f, 120f), Point(25f, 10f),
        Point(30f, 180f), Point(35f, 100f), Point(40f, 10f),

        )
    val point1 = mutableListOf(
        Point(0f, 1000f), Point(5f, 1000f), Point(10f, 2000f),
        Point(15f, 120f), Point(20f, 1120f), Point(25f, 1000f),
        Point(30f, 180f), Point(35f, 100f), Point(40f, 1000f),
    )
    val point2 = mutableListOf(
        Point(0f, 1200f), Point(5f, 100f), Point(10f, 2200f),
        Point(15f, 600f), Point(20f, 120f), Point(25f, 1500f),
        Point(30f, 680f), Point(35f, 200f), Point(40f, 1500f),
    )
    linList.add(Line(point, color = Color(0XFF18D276), axisType = AxisType.LEFT_INSIDE))
    linList.add(Line(point1, color = Color(0XFFFF4E87), isDrawCubic = true, isDashes = true))
    linList.add(
        Line(
            point2,
            color = Color(0XFF058BF6),
            axisType = AxisType.RIGHT,
            isDrawCubic = true
        )
    )
    return linList
}

/**
 * @author Brian
 * @Description: test 有正 有负 测试数据，测试UI效果使用
 */
fun getTestPlusOrMinusLineList(): MutableList<Line> {
    val linList: MutableList<Line> = mutableListOf()
    val point = mutableListOf<Point>().apply {
        for (i in 0..20) {
            val x = Random.nextInt(-500, 500).toFloat()
            val y = Random.nextInt(-200, 200).toFloat()
            add(Point(x, y))
        }
    }
    val point1 = mutableListOf<Point>().apply {
        for (i in -500..500 step 50) {
            val y = Random.nextInt(-200, 200).toFloat()
            add(Point(i.toFloat(), y))
        }
    }


    linList.add(
        Line(
            point,
            color = Color(0xff50E3C2),
            isDrawCubic = true
        )
    )
    linList.add(Line(point1, color = Color(0xff4A90E2), isDrawCubic = true))
    return linList
}

/**
 * @author Brian
 * @Description: test 测试数据，测试UI效果使用
 */
fun getTestPointLineList(): MutableList<Line> {
    val linList: MutableList<Line> = mutableListOf()
    val point = mutableListOf(
        Point(10f, 210f), Point(50f, 150f), Point(100f, 130f),
        Point(150f, 200f), Point(200f, 80f), Point(250f, 240f),
        Point(300f, 20f), Point(350f, 150f), Point(400f, 50f),
        Point(450f, 240f), Point(500f, 140f)
    )
    linList.add(
        Line(
            point,
            width = 2.dp,
            color = Color(0xff4A90E2),
            isDrawCubic = true,
            isPoints = true
        )
    )
    return linList
}

/**
 * @author Brian
 * @Description: test 测试数据，测试UI效果使用
 */
fun getTestLine(): MutableList<Line> {
    val linList: MutableList<Line> = mutableListOf()
    val point = mutableListOf(
        Point(10f, 0.1f),
        Point(50f, 0.2f),
        Point(100f, 0.1f),
        Point(150f, 0.2f),
    )
    linList.add(Line(point, width = 2.dp, color = Color(0xff4A90E2), isDrawCubic = true))
    return linList
}

fun drawXaxisBottomScale(
    drawScope: DrawScope,
    point0: Point,
    point1: Point,
    point2: Point,
    point3: Point,
    defaultXAxisMin: Float,
    defaultXAxisMax: Float,
    axisColor: Color,
    scaleLengSize: Float,
    axisStrokeSize: Float,
    scaleInterval: Float,
    scale: Float,
    yOffset: Float
) {
    drawScope.run {
//        val scaleInterval = 2f //刻度之间的间隔 实际数据间隔
        val scaleNum = (defaultXAxisMax - defaultXAxisMin) / scaleInterval//刻度个数
        val scaleIntervalSize = (point1.x - point0.x) / scaleNum//刻度间隔，换算成px
        for (i in 0..scaleNum.toInt()) {
            val x = (point0.x + i * scaleIntervalSize)
            drawLine(
                start = Offset(x * scale, point0.y - yOffset),
                end = Offset(
                    x * scale,
                    point0.y + scaleLengSize - yOffset
                ),
                color = axisColor,
                strokeWidth = axisStrokeSize
            )
        }

    }
}

fun drawYAxisLeftInsideScale(
    drawScope: DrawScope,
    point0: Point,
    point1: Point,
    point2: Point,
    point3: Point,
    defaultXAxisMin: Float,
    defaultXAxisMax: Float,
    axisColor: Color,
    scaleLengSize: Float,
    axisStrokeSize: Float,
    scaleInterval: Float,
    scale: Float,
    xOffset: Float
) {
    // 计算刻度数量和间隔
    val scaleNum = (defaultXAxisMax - defaultXAxisMin) / scaleInterval
    val scaleIntervalSize = (point0.y - point3.y) / scaleNum

    // 计算线条的起点和终点X坐标
    val startX = point0.x + xOffset
    val endX = startX + scaleLengSize * scale

    // 使用Compose的Path构建所有刻度线
    val path = Path().apply {
        for (i in 0..scaleNum.toInt()) {
            val y = point0.y - i * scaleIntervalSize
            moveTo(startX, y)
            lineTo(endX, y)
        }
    }

    // 使用Compose的drawPath一次性绘制所有刻度线
    drawScope.drawPath(
        path = path,
        color = axisColor,
        style = Stroke(width = axisStrokeSize)
    )
}
fun drawYAxisLeftScale1(
    drawScope: DrawScope,
    point0: Point,
    point1: Point,
    point2: Point,
    point3: Point,
    defaultXAxisMin: Float,
    defaultXAxisMax: Float,
    axisColor: Color,
    scaleLengSize: Float,
    axisStrokeSize: Float,
    scaleInterval: Float,
    scale: Float,
    xOffset: Float
) {
    drawScope.run {
//        val scaleInterval = 2f //刻度之间的间隔 实际数据间隔
        val scaleNum = (defaultXAxisMax - defaultXAxisMin) / scaleInterval//刻度个数
        val scaleIntervalSize = (point0.y - point3.y) / scaleNum//刻度间隔，换算成px
        for (i in 0..scaleNum.toInt()) {
            val y = (point0.y - i * scaleIntervalSize)
            drawLine(
                start = Offset(point0.x + xOffset, y),
                end = Offset(
                    point0.x - scaleLengSize * scale + xOffset,
                    y
                ),
                color = axisColor,
                strokeWidth = axisStrokeSize
            )
        }

    }
}
fun drawYAxisLeftScale(
    drawScope: DrawScope,
    point0: Point,
    point1: Point,
    point2: Point,
    point3: Point,
    defaultXAxisMin: Float,
    defaultXAxisMax: Float,
    axisColor: Color,
    scaleLengSize: Float,
    axisStrokeSize: Float,
    scaleInterval: Float,
    scale: Float,
    xOffset: Float
) {

    val scaleNum = (defaultXAxisMax - defaultXAxisMin) / scaleInterval
    if (scaleNum <= 0f) return

    // 计算刻度间隔和坐标
    val scaleIntervalSize = (point0.y - point3.y) / scaleNum
    val startX = point0.x + xOffset
    val endX = startX - scaleLengSize * scale  // 向左绘制，所以用减法

    // 使用while循环构建Path（比for循环更高效）
    val path = Path().apply {
        var y = point0.y
        val stopY = point0.y - scaleNum * scaleIntervalSize - 0.1f  // 包含浮点误差补偿
        while (y >= stopY) {
            moveTo(startX, y)
            lineTo(endX, y)
            y -= scaleIntervalSize
        }
    }

    // 一次性绘制所有刻度线
    drawScope.drawPath(
        path = path,
        color = axisColor,
        style = Stroke(width = axisStrokeSize)
    )
}
fun drawYAxisRightScale(
    drawScope: DrawScope,
    point0: Point,
    point1: Point,
    point2: Point,
    point3: Point,
    defaultXAxisMin: Float,
    defaultXAxisMax: Float,
    axisColor: Color,
    scaleLengSize: Float,
    axisStrokeSize: Float,
    scaleInterval: Float,
    scale: Float,
    xOffset: Float
) {
    // 参数校验
    val scaleNum = (defaultXAxisMax - defaultXAxisMin) / scaleInterval
    if (scaleNum <= 0f) return

    // 计算刻度间隔和坐标
    val scaleIntervalSize = (point0.y - point3.y) / scaleNum
    val startX = point1.x + xOffset  // 使用point1作为右轴起点
    val endX = startX + scaleLengSize * scale  // 向右绘制，所以用加法

    // 使用while循环构建Path
    val path = Path().apply {
        var y = point0.y
        val stopY = point0.y - scaleNum * scaleIntervalSize - 0.1f  // 浮点误差补偿
        while (y >= stopY) {
            moveTo(startX, y)
            lineTo(endX, y)
            y -= scaleIntervalSize
        }
    }

    // 一次性绘制所有刻度线
    drawScope.drawPath(
        path = path,
        color = axisColor,
        style = Stroke(width = axisStrokeSize)
    )
}


fun drawXaxisBottomLabel(
    drawScope: DrawScope,
    point0: Point,
    point1: Point,
    point2: Point,
    point3: Point,
    defaultXAxisMin: Float,
    defaultXAxisMax: Float,
    labelColor: Color,
    labelInterval: Float,
    labelTextSizePx: Float = 24f,
    scale: Float,
    settingLabelValue: ((value: Float) -> String)?,
    yOffset: Float,
) {
    drawScope.run {
//        val scaleInterval = 2f //刻度之间的间隔 实际数据间隔
        val scaleNum = (defaultXAxisMax - defaultXAxisMin) / labelInterval//刻度个数
        val scaleIntervalSize = (point1.x - point0.x) / scaleNum//刻度间隔，换算成px
        for (i in 0..scaleNum.toInt()) {
            val nativePaint = android.graphics.Paint().let {
                it.apply {
                    textSize = labelTextSizePx
                    color = labelColor.toArgb()
                    isAntiAlias = true//抗锯齿
                }
            }
            val labelFloat =
                BigDecimal(defaultXAxisMin.toString()).add(
                    BigDecimal(labelInterval.toString()).multiply(
                        BigDecimal(i)
                    )
                ).toFloat()
//            var label = settingLabelValue?.let { it(labelFloat) }
            var label = if (settingLabelValue == null) {
                when {
                    labelFloat.toInt().toFloat() == labelFloat -> {//为整数浮点数
                        "${labelFloat.toInt()}"
                    }

                    else -> {//为小数浮点数
                        "${labelFloat}"
                    }
                }
            } else {
                settingLabelValue(labelFloat)
            }
            label.let {
                //            val label = "${(defaultXAxisMin + labelInterval * i).toInt()}"
                val labelWidth = it.length * labelTextSizePx
                val offset = labelWidth / 2
                val x = point0.x + i * scaleIntervalSize - offset / 2
                val y = point0.y + labelTextSizePx + 4.dp.toPx() - yOffset
                drawContext.canvas.nativeCanvas.drawText(
                    it,
                    x * scale,
                    y,
                    nativePaint
                )
            }


        }

    }
}

fun drawXaxisBottomName(
    drawScope: DrawScope,
    name: String,
    point0: Point,
    point1: Point,
    point2: Point,
    point3: Point,
    defaultXAxisMax: Float,
    labelColor: Color,
    labelTextSizePx: Float = 24f,
    scale: Float
) {
    drawScope.run {
        val nativePaint = android.graphics.Paint().let {
            it.apply {
                textSize = labelTextSizePx
                color = labelColor.toArgb()
                isAntiAlias = true//抗锯齿
            }
        }
        val label = when {
            defaultXAxisMax.toInt().toFloat() == defaultXAxisMax -> {//为整数浮点数
                "${defaultXAxisMax.toInt()}"
            }

            else -> {//为小数浮点数
                "${defaultXAxisMax}"
            }
        }
        val labelWidth = label.length * labelTextSizePx
        val offset = labelWidth / 2

        val x = point1.x + offset
        var y = point0.y + labelTextSizePx + 4.dp.toPx()
        val nameList = name.split("\n")
        nameList.forEach {
            drawContext.canvas.nativeCanvas.drawText(
                it,
                x,
                y,
                nativePaint
            )
            y += labelTextSizePx
        }


    }
}

fun drawYAxisLeftInsideName(
    drawScope: DrawScope,
    name: String,
    point0: Point,
    point1: Point,
    point2: Point,
    point3: Point,
    labelColor: Color,
    labelTextSizePx: Float = 24f,
    scale: Float
) {

    drawScope.run {
        val nativePaint = android.graphics.Paint().let {
            it.apply {
                textSize = labelTextSizePx
                color = labelColor.toArgb()
                isAntiAlias = true//抗锯齿
            }
        }


        val x = point0.x + 8.dp.toPx()
        var y = point3.y - labelTextSizePx
        val nameList = name.split("\n")
        for (i in nameList.size - 1 downTo 0) {
            drawContext.canvas.nativeCanvas.drawText(
                nameList[i],
                x,
                y,
                nativePaint
            )
            y -= labelTextSizePx
        }


    }
}

fun drawYAxisLeftName(
    drawScope: DrawScope,
    name: String,
    point0: Point,
    point1: Point,
    point2: Point,
    point3: Point,
    labelColor: Color,
    labelTextSizePx: Float = 24f,
    scale: Float
) {

    drawScope.run {
        val nativePaint = android.graphics.Paint().let {
            it.apply {
                textSize = labelTextSizePx
                color = labelColor.toArgb()
                isAntiAlias = true//抗锯齿
            }
        }


        val x = 0f + 2.dp.toPx()
        var y = point3.y - labelTextSizePx
        val nameList = name.split("\n")
        for (i in nameList.size - 1 downTo 0) {
            drawContext.canvas.nativeCanvas.drawText(
                nameList[i],
                x,
                y,
                nativePaint
            )
            y -= labelTextSizePx
        }

    }
}

fun drawYAxisRightName(
    drawScope: DrawScope,
    name: String,
    point0: Point,
    point1: Point,
    point2: Point,
    point3: Point,
    labelColor: Color,
    labelTextSizePx: Float = 24f,
    scale: Float
) {

    drawScope.run {
        val nativePaint = android.graphics.Paint().let {
            it.apply {
                textSize = labelTextSizePx
                color = labelColor.toArgb()
                isAntiAlias = true//抗锯齿
            }
        }
        val x = point2.x + 2.dp.toPx()
        var y = point2.y - labelTextSizePx
        val nameList = name.split("\n")
        for (i in nameList.size - 1 downTo 0) {
            drawContext.canvas.nativeCanvas.drawText(
                nameList[i],
                x,
                y,
                nativePaint
            )
            y -= labelTextSizePx
        }


    }
}

fun drawYAxisLeftInsideLabel(
    drawScope: DrawScope,
    point0: Point,
    point1: Point,
    point2: Point,
    point3: Point,
    defaultYAxisMin: Float,
    defaultYAxisMax: Float,
    labelColor: Color,
    labelInterval: Float,
    labelTextSizePx: Float = 24f,
    scale: Float,
    settingLabelValue: ((value: Float) -> String)?,
    xOffset: Float,
) {
    drawScope.run {
//        val scaleInterval = 2f //刻度之间的间隔 实际数据间隔
        val scaleNum = (defaultYAxisMax - defaultYAxisMin) / labelInterval//刻度个数
        val scaleIntervalSize = (point0.y - point3.y) / scaleNum //刻度间隔，换算成px
        for (i in scaleNum.toInt() downTo 0) {
            val nativePaint = android.graphics.Paint().let {
                it.apply {
                    textSize = labelTextSizePx
                    color = labelColor.toArgb()
                    isAntiAlias = true//抗锯齿
                }
            }

            val labelFloat =
                BigDecimal(defaultYAxisMin.toString()).add(
                    BigDecimal(labelInterval.toString()).multiply(
                        BigDecimal(i)
                    )
                ).toFloat()
            var label = if (settingLabelValue == null) {
                when {
                    labelFloat.toInt().toFloat() == labelFloat -> {//为整数浮点数
                        "${labelFloat.toInt()}"
                    }

                    else -> {//为小数浮点数
                        "${labelFloat}"
                    }
                }
            } else {
                settingLabelValue(labelFloat)
            }

            label.let {
                //            val label = "${(defaultYAxisMin + labelInterval * i).toInt()}"
                val labelWidth = it.length * labelTextSizePx
                val x = point0.x + xOffset + 8.dp.toPx()
                val y = point0.y - i * scaleIntervalSize + labelTextSizePx / 4
                drawContext.canvas.nativeCanvas.drawText(
                    label,
                    x,
                    y,
                    nativePaint
                )
            }
        }
    }
}

fun drawYAxisLeftLabel(
    drawScope: DrawScope,
    point0: Point,
    point1: Point,
    point2: Point,
    point3: Point,
    defaultYAxisMin: Float,
    defaultYAxisMax: Float,
    labelColor: Color,
    labelInterval: Float,
    labelTextSizePx: Float = 24f,
    scale: Float,
    settingLabelValue: ((value: Float) -> String)?,
    xOffset: Float,
) {
    drawScope.run {
//        val scaleInterval = 2f //刻度之间的间隔 实际数据间隔
        val scaleNum = (defaultYAxisMax - defaultYAxisMin) / labelInterval//刻度个数
        val scaleIntervalSize = (point0.y - point3.y) / scaleNum //刻度间隔，换算成px
        for (i in scaleNum.toInt() downTo 0) {
            val nativePaint = android.graphics.Paint().let {
                it.apply {
                    textSize = labelTextSizePx
                    color = labelColor.toArgb()
                    isAntiAlias = true//抗锯齿
                }
            }

            val labelFloat =
                BigDecimal(defaultYAxisMin.toString()).add(
                    BigDecimal(labelInterval.toString()).multiply(
                        BigDecimal(i)
                    )
                ).toFloat()

            var label = if (settingLabelValue == null) {
                when {
                    labelFloat.toInt().toFloat() == labelFloat -> {//为整数浮点数
                        "${labelFloat.toInt()}"
                    }

                    else -> {//为小数浮点数
                        "${labelFloat}"
                    }
                }
            } else {
                settingLabelValue(labelFloat)
            }
            label.let {

                //            val label = "${(defaultYAxisMin + labelInterval * i).toInt()}"
                val labelWidth = label.length * labelTextSizePx
                val x = point0.x + xOffset - labelWidth / 2 - 8.dp.toPx()
                val y = point0.y - i * scaleIntervalSize + labelTextSizePx / 4
                drawContext.canvas.nativeCanvas.drawText(
                    label,
                    x,
                    y,
                    nativePaint
                )
            }

        }
    }
}

fun drawYAxisRightLabel(
    drawScope: DrawScope,
    point0: Point,
    point1: Point,
    point2: Point,
    point3: Point,
    defaultYAxisMin: Float,
    defaultYAxisMax: Float,
    labelColor: Color,
    labelInterval: Float,
    labelTextSizePx: Float = 24f,
    scale: Float,
    settingLabelValue: ((value: Float) -> String)?,
    xOffset: Float,
) {
    drawScope.run {
//        val scaleInterval = 2f //刻度之间的间隔 实际数据间隔
        val scaleNum = (defaultYAxisMax - defaultYAxisMin) / labelInterval//刻度个数
        val scaleIntervalSize = (point1.y - point2.y) / scaleNum //刻度间隔，换算成px
        for (i in scaleNum.toInt() downTo 0) {
            val nativePaint = android.graphics.Paint().let {
                it.apply {
                    textSize = labelTextSizePx
                    color = labelColor.toArgb()
                    isAntiAlias = true//抗锯齿
                }
            }

            val labelFloat =
                BigDecimal(defaultYAxisMin.toString()).add(
                    BigDecimal(labelInterval.toString()).multiply(
                        BigDecimal(i)
                    )
                ).toFloat()
            var label = if (settingLabelValue == null) {
                when {
                    labelFloat.toInt().toFloat() == labelFloat -> {//为整数浮点数
                        "${labelFloat.toInt()}"
                    }

                    else -> {//为小数浮点数
                        "${labelFloat}"
                    }
                }
            } else {
                settingLabelValue(labelFloat)
            }
            label.let {

                //            val label = "${(defaultYAxisMin + labelInterval * i).toInt()}"
                val labelWidth = label.length * labelTextSizePx
                val x = point1.x + xOffset + 8.dp.toPx()
                val y = point1.y - i * scaleIntervalSize + labelTextSizePx / 4
                drawContext.canvas.nativeCanvas.drawText(
                    label,
                    x,
                    y,
                    nativePaint
                )
            }
        }
    }
}


/**
 * @author Brian
 * @Description:画颜色块
 */
fun drawYChunk(
    drawScope: DrawScope,
    yChunkList: MutableList<Chunk>?,
    point0: Point,
    point1: Point,
    point2: Point,
    point3: Point,
    yAxisMin: Float,
    yAxisMax: Float,

    ) {
    drawScope.run {


        val oneDataYPx = (point0.y - point3.y) / (yAxisMax - yAxisMin) // X轴上 1f单位数据点对应的px数
        yChunkList?.forEachIndexed { index, chunk ->
            val X1 = point0.x
            val X2 = point1.x
            val Y1 = point0.y - chunk.start * oneDataYPx //
            val Y2 = point0.y - chunk.end * oneDataYPx //

            drawRect(
                color = chunk.color,
                topLeft = Offset(x = point0.x, y = Y1),
                size = Size(X2 - X1, Y2 - Y1)
            )
        }


    }


}


/**
 * @author Brian
 * @Description:画线
 */
fun drawXLimitLine(
    drawScope: DrawScope,

    xLimitLineList: MutableList<LimitLine>? = null,//Y轴上画线
    point0: Point,
    point1: Point,
    point2: Point,
    point3: Point,
    axisMin: Float,
    axisMax: Float,
    scale: Float

) {
    drawScope.run {

        val oneDataXPx = (point1.x - point0.x) / (axisMax - axisMin) // X轴上 1f单位数据点对应的px数

        xLimitLineList?.forEachIndexed { index, limitLine ->
            val X1 = point0.x + (limitLine.value - axisMin) * oneDataXPx //转换为对应的X Px
            val Y1 = point3.y
            val Y2 = point0.y
            val widthPx = limitLine.width.toPx()
            val dashPathEffect = if (limitLine.isDashes) {
                PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 5f)
            } else {
                null
            }
            val lineX = (X1 - widthPx / 4) * scale
            drawLine(
                start = Offset(x = lineX, y = Y1),
                end = Offset(x = lineX, y = Y2),
                color = limitLine.color,
                pathEffect = dashPathEffect,
                strokeWidth = widthPx
            )
            //文字
            var textSizePx = limitLine.textSize.toPx()
            val nativePaint = android.graphics.Paint().let {
                it.apply {
                    textSize = textSizePx
                    color = limitLine.color.toArgb()
                    isAntiAlias = true//抗锯齿
                }
            }


            drawContext.canvas.nativeCanvas.drawText(
                limitLine.text,
                X1 - (textSizePx / 2) * limitLine.text.length - widthPx - 4f,
                Y1,
                nativePaint
            )
        }


    }


}


fun drawYLimitLine(
    drawScope: DrawScope,
    yLimitLineList: MutableList<LimitLine>? = null,//Y轴上画线
    point0: Point,
    point1: Point,
    point2: Point,
    point3: Point,
    axisMin: Float,
    axisMax: Float,
    scale: Float
) {
    drawScope.run {

        val oneDataYPx = (point0.y - point3.y) / (axisMax - axisMin) // X轴上 1f单位数据点对应的px数

        yLimitLineList?.forEachIndexed { index, limitLine ->
            val X1 = point0.x
            val X2 = point1.x
            val Y1 = point0.y - (limitLine.value - axisMin) * oneDataYPx //转换为对应的Y Px

            //直线
            val dashPathEffect = if (limitLine.isDashes) {
                PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 5f)
            } else {
                null
            }
            val widthPx = limitLine.width.toPx()
            drawLine(
                start = Offset(x = X1, y = Y1 - widthPx / 4),
                end = Offset(x = X2, y = Y1 - widthPx / 4),
                color = limitLine.color,
                pathEffect = dashPathEffect,
                strokeWidth = widthPx
            )
            //文字
            var textSizePx = limitLine.textSize.toPx()
            val nativePaint = android.graphics.Paint().let {
                it.apply {
                    textSize = textSizePx
                    color = limitLine.color.toArgb()
                    isAntiAlias = true//抗锯齿
                }
            }


            drawContext.canvas.nativeCanvas.drawText(
                limitLine.text,
                X2 - textSizePx * limitLine.text.length,
                Y1 - widthPx / 4 - textSizePx / 2,
                nativePaint
            )
        }


    }
}

/**
 * @author Brian
 * @Description:画线
 */
fun drawXGridLine(
    drawScope: DrawScope,
    gridLine: GridLine? = null,//Y轴上画线
    point0: Point,
    point1: Point,
    point2: Point,
    point3: Point,
    xAxisMin: Float,
    xAxisMax: Float,
    scale: Float

) {
    drawScope.run {

        val oneDataXPx = (point1.x - point0.x) / (xAxisMax - xAxisMin) // X轴上 1f单位数据点对应的px数
        gridLine?.let {
            val gridNum = ((xAxisMax - xAxisMin) / it.interval).toInt()
            it.interval * oneDataXPx
            for (i in 0..gridNum) {
                val X1 = point0.x + i * it.interval * oneDataXPx //转换为对应的X Px
                val Y1 = point3.y
                val Y2 = point0.y
                val widthPx = it.width.toPx()
                val dashPathEffect = if (it.isDashes) {
                    it.pathEffect ?: PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 5f)
                } else {
                    null
                }
                val lineX = (X1 - widthPx / 4) * scale
                drawLine(
                    start = Offset(x = lineX, y = Y1),
                    end = Offset(x = lineX, y = Y2),
                    color = it.color,
                    pathEffect = dashPathEffect,
                    strokeWidth = widthPx
                )
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    // 使用原生虚线 低版本的系统，如果启用了硬件加速，drawLine可能不支持虚线
//                    drawLine(
//                        start = Offset(x = lineX, y = Y1),
//                        end = Offset(x = lineX, y = Y2),
//                        color = it.color,
//                        pathEffect = dashPathEffect,
//                        strokeWidth = widthPx
//                    )
//                } else {
//                    // 使用自定义虚线
//                    drawDashedLine(
//                        drawScope = this,
//                        start = Offset(x = lineX, y = Y1),
//                        end = Offset(x = lineX, y = Y2),
//                        color = it.color,
//                        strokeWidth = widthPx,
//                        dashLength = 5f,
//                        gapLength = 5f
//                    )
//                }


            }

        }


    }


}

/**
 *@author Brian
 *@Description:性能太差,慎用,低版本系统,有些机器启用了硬件加速后,
 * drawLine可能不支持虚线,会导致虚线不显示
 *
 */
fun drawDashedLine(
    drawScope: DrawScope,
    start: Offset,
    end: Offset,
    color: Color,
    strokeWidth: Float,
    dashLength: Float = 5f,
    gapLength: Float = 5f
) {
    val path = Path().apply {
        moveTo(start.x, start.y)
        lineTo(end.x, end.y)
    }

    val length = sqrt((end.x - start.x).pow(2) + (end.y - start.y).pow(2))
    val steps = length / (dashLength + gapLength)

    for (i in 0 until steps.toInt()) {
        val startRatio = i * (dashLength + gapLength) / length
        val endRatio = (i * (dashLength + gapLength) + dashLength) / length

        val segmentStart = Offset(
            start.x + (end.x - start.x) * startRatio,
            start.y + (end.y - start.y) * startRatio
        )

        val segmentEnd = Offset(
            start.x + (end.x - start.x) * endRatio,
            start.y + (end.y - start.y) * endRatio
        )

        drawScope.drawLine(
            color = color,
            start = segmentStart,
            end = segmentEnd,
            strokeWidth = strokeWidth
        )
    }
}

fun drawYGridLine(
    drawScope: DrawScope,
    gridLine: GridLine? = null,//Y轴上画线
    point0: Point,
    point1: Point,
    point2: Point,
    point3: Point,
    xAxisMin: Float,
    xAxisMax: Float,
    scale: Float
) {
    drawScope.run {

        val oneDataYPx = (point0.y - point3.y) / (xAxisMax - xAxisMin) // X轴上 1f单位数据点对应的px数
        gridLine?.let {
            val gridNum = ((xAxisMax - xAxisMin) / it.interval).toInt()
            for (i in 0..gridNum) {
                val X1 = point0.x
                val X2 = point1.x
                val Y1 = point0.y - i * it.interval * oneDataYPx //转换为对应的Y Px

                //直线
                val dashPathEffect = if (it.isDashes) {
                    it.pathEffect ?: PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 5f)
                } else {
                    null
                }
                val widthPx = it.width.toPx()
                drawLine(
                    start = Offset(x = X1, y = Y1 - widthPx / 4),
                    end = Offset(x = X2, y = Y1 - widthPx / 4),
                    color = it.color,
                    pathEffect = dashPathEffect,
                    strokeWidth = widthPx
                )
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    // 使用原生虚线 低版本的系统，如果启用了硬件加速，drawLine可能不支持虚线
//                    drawLine(
//                        start = Offset(x = X1, y = Y1 - widthPx / 4),
//                        end = Offset(x = X2, y = Y1 - widthPx / 4),
//                        color = it.color,
//                        pathEffect = dashPathEffect,
//                        strokeWidth = widthPx
//                    )
//                } else {
//                    // 使用自定义虚线
//                    drawDashedLine(
//                        drawScope = this,
//                        start = Offset(x = X1, y = Y1 - widthPx / 4),
//                        end = Offset(x = X2, y = Y1 - widthPx / 4),
//                        color = it.color,
//                        strokeWidth = widthPx,
//                        dashLength = 5f,
//                        gapLength = 5f
//                    )
//                }
            }

        }


    }
}


fun drawGrideLine(
    drawScope: DrawScope,
    xAxis: Axis,
    yLeftInsideAxis: Axis? = null,
    yLeftAxis: Axis? = null,
    yRightAxis: Axis? = null,
    point0: Point,
    point1: Point,
    point2: Point,
    point3: Point,
    scale: Float
) {
    drawScope.run {
        xAxis.let {
            it.gridLine?.let { gridLine ->
                drawXGridLine(
                    drawScope = this,
                    gridLine = gridLine,
                    point0 = point0,
                    point1 = point1,
                    point2 = point2,
                    point3 = point3,
                    xAxisMax = it.max,
                    xAxisMin = it.min,
                    scale = scale,
                )

            }


        }
        yLeftInsideAxis?.let {
            it.gridLine?.let { gridLine ->
                drawYGridLine(
                    drawScope = this,
                    gridLine = gridLine,
                    point0 = point0,
                    point1 = point1,
                    point2 = point2,
                    point3 = point3,
                    xAxisMax = it.max,
                    xAxisMin = it.min,
                    scale = scale,
                )

            }
        }
        yLeftAxis?.let {
            it.gridLine?.let { gridLine ->
                drawYGridLine(
                    drawScope = this,
                    gridLine = gridLine,
                    point0 = point0,
                    point1 = point1,
                    point2 = point2,
                    point3 = point3,
                    xAxisMax = it.max,
                    xAxisMin = it.min,
                    scale = scale,
                )

            }

        }
        yRightAxis?.let {
            it.limitLineList?.let { limitLineList ->
                drawYLimitLine(
                    drawScope = this,
                    yLimitLineList = limitLineList,
                    point0 = point0,
                    point1 = point1,
                    point2 = point2,
                    point3 = point3,
                    axisMax = it.max,
                    axisMin = it.min,
                    scale = scale,
                )

            }
        }
    }

}

fun ftChartData(): LineChartData {
    val lineChartData = LineChartData()
    lineChartData.lineList = getFvLineList()
    lineChartData.xAxis.limitLineList = null //x轴限制线
    lineChartData.yLeftAxis = Axis()
    lineChartData.yRightAxis = Axis()
    lineChartData.yLeftAxis?.limitLineList = mutableListOf( // y轴限制线
        LimitLine(
            -10f,
            isDashes = true,
            width = 1.dp,
            color = Color(0xFFFF6203),
            text = "有效吸气流量下限"
        ),
        LimitLine(
            -30f,
            isDashes = true,
            width = 1.dp,
            color = Color(0xFF039DFF),
            text = ""
        ),
        LimitLine(
            -60f,
            isDashes = true,
            width = 1.dp,
            color = Color(0xFFFF6203),
            text = ""
        )
    )
    return lineChartData
}

fun getFvLineList(): MutableList<Line> {
    val linList: MutableList<Line> = mutableListOf()
    val point = mutableListOf(
        Point(0f, 0f),
        Point(1f, -10f),
        Point(2f, -20f),
        Point(3f, -30f),
        Point(4f, -50f),
        Point(5f, -10f),
        Point(6f, -30f),
        Point(7f, -5f),
        Point(8f, 0f),
        Point(9f, -10f),
    )
    linList.add(
        Line(
            point,
            color = Color(0xff000000),
            isDashes = false,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 12f), 2f)
        )
    )
    return linList
}

fun vtChartData(): LineChartData {
    val lineChartData = LineChartData()
    lineChartData.lineList = getVtLineList()
    lineChartData.xAxis.limitLineList = mutableListOf(
        LimitLine(
            2f,
            isDashes = true,
            width = 1.dp,
            color = Color(0xFF444444),
            text = ""
        ),
        LimitLine(
            12f,
            isDashes = true,
            width = 1.dp,
            color = Color(0xFF444444),
            text = ""
        )
    ) //x轴限制线
    lineChartData.yLeftAxis = Axis()
    lineChartData.yRightAxis = Axis()
    lineChartData.yLeftAxis?.limitLineList = mutableListOf() // y轴限制线
    return lineChartData
}

fun getVtLineList(): MutableList<Line> {
    val linList: MutableList<Line> = mutableListOf()
    val point = mutableListOf(
        Point(0f, 0f),
        Point(1f, 2f),
        Point(1.5f, 3f),
        Point(2f, 4f),
        Point(3f, 4f),
        Point(4f, 4f),
        Point(12f, 4f),
        Point(13f, 0f)
    )
    linList.add(
        Line(
            point,
            color = Color(0xff000000),
            isDashes = false,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 12f), 2f)
        )
    )
    return linList
}