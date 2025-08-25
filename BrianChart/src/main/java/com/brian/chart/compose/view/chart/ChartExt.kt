package com.brian.chart.compose.view.chart

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.brian.chart.R

import java.math.BigDecimal
import kotlin.random.Random


/**
 * @author Brian
 * @Description:画颜色块
 */
fun drawXChunk(
    drawScope: DrawScope,
    chunkList: MutableList<Chunk>?,
    axisPoints: AxisPoints,
    axisMin: Float,
    axisMax: Float,

    ) {
    drawScope.run {


        val oneDataYPx =
            (axisPoints.point1.x - axisPoints.point0.x) / (axisMax - axisMin) // X轴上 1f单位数据点对应的px数
        chunkList?.forEachIndexed { index, chunk ->
            val X1 = axisPoints.point0.x + chunk.start * oneDataYPx
            val X2 = axisPoints.point0.x + chunk.end * oneDataYPx
            val Y1 = axisPoints.point0.y  //
            val Y2 = axisPoints.point3.y  //

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
    axisPoints: AxisPoints,
) {
    drawScope.run {
        xAxis.let {
            it.chunkList?.let { chunkList ->
                drawXChunk(
                    drawScope = this,
                    chunkList = chunkList,
                    axisPoints = axisPoints,
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
                    axisPoints = axisPoints,
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
                    axisPoints = axisPoints,
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
                    axisPoints = axisPoints,
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
    axisPoints: AxisPoints,
) {
    drawScope.run {

        val oneDataXPx =
            (axisPoints.point1.x - axisPoints.point0.x) / (xAxis.max - xAxis.min) // X轴上 1f单位数据点对应的px数
        var oneDataYPx = 0f
        var yOffset = 0f
        when {
            yLeftAxis != null -> {
                oneDataYPx =
                    (axisPoints.point0.y - axisPoints.point3.y) / (yLeftAxis.max - yLeftAxis.min) // Y轴上 1f单位数据点对应的px数
                xAxis.position?.let {
                    yOffset = (it - yLeftAxis.min) * oneDataYPx
                }

            }

            yLeftInsideAxis != null -> {
                oneDataYPx =
                    (axisPoints.point0.y - axisPoints.point3.y) / (yLeftInsideAxis.max - yLeftInsideAxis.min) // Y轴上 1f单位数据点对应的px数
                xAxis.position?.let {
                    yOffset = (it - yLeftInsideAxis.min) * oneDataYPx
                }
            }

            yRightAxis != null -> {
                oneDataYPx =
                    (axisPoints.point0.y - axisPoints.point3.y) / (yRightAxis.max - yRightAxis.min) // Y轴上 1f单位数据点对应的px数
                xAxis.position?.let {
                    yOffset = (it - yRightAxis.min) * oneDataYPx
                }
            }
        }


        xAxis.let {
            //绘制 X轴
            if (it.isDrawAxis) {

                drawLine(
                    start = Offset(axisPoints.point0.x, axisPoints.point0.y - yOffset),
                    end = Offset(axisPoints.point1.x, axisPoints.point1.y - yOffset),
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
                    start = Offset(axisPoints.point0.x + xOffset, axisPoints.point0.y),
                    end = Offset(axisPoints.point3.x + xOffset, axisPoints.point3.y),
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
                    start = Offset(axisPoints.point0.x + xOffset, axisPoints.point0.y),
                    end = Offset(axisPoints.point3.x + xOffset, axisPoints.point3.y),
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
                    start = Offset(axisPoints.point1.x + xOffset, axisPoints.point1.y),
                    end = Offset(axisPoints.point2.x + xOffset, axisPoints.point2.y),
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

@Composable
fun getScaleLengSize(boxScope: BoxScope, axis: Axis?): Float {
    boxScope.run {

        return if (axis?.scaleInterval != null) {
            with(LocalDensity.current) {
                axis.scaleLengSize.toPx()
            }

        } else {
            0f
        }//左边刻度的长度
    }

}

fun getScaleLengSize(axis: Axis?, currentDensity: Density): Float {

    return if (axis?.scaleInterval != null) {
        with(currentDensity) {
            axis.scaleLengSize.toPx()
        }

    } else {
        0f
    }//左边刻度的长度

}

fun drawLable(
    drawScope: DrawScope,
    xAxis: Axis,
    yLeftInsideAxis: Axis? = null,
    yLeftAxis: Axis? = null,
    yRightAxis: Axis? = null,
    axisPoints: AxisPoints,
    scale: Float = 1f
) {
    drawScope.run {
        val oneDataXPx =
            (axisPoints.point1.x - axisPoints.point0.x) / (xAxis.max - xAxis.min) // X轴上 1f单位数据点对应的px数
        var oneDataYPx = 0f
        var yOffset = 0f
        when {
            yLeftAxis != null -> {
                oneDataYPx =
                    (axisPoints.point0.y - axisPoints.point3.y) / (yLeftAxis.max - yLeftAxis.min) // Y轴上 1f单位数据点对应的px数
                xAxis.position?.let {
                    yOffset = (it - yLeftAxis.min) * oneDataYPx
                }

            }

            yLeftInsideAxis != null -> {
                oneDataYPx =
                    (axisPoints.point0.y - axisPoints.point3.y) / (yLeftInsideAxis.max - yLeftInsideAxis.min) // Y轴上 1f单位数据点对应的px数
                xAxis.position?.let {
                    yOffset = (it - yLeftInsideAxis.min) * oneDataYPx
                }
            }

            yRightAxis != null -> {
                oneDataYPx =
                    (axisPoints.point0.y - axisPoints.point3.y) / (yRightAxis.max - yRightAxis.min) // Y轴上 1f单位数据点对应的px数
                xAxis.position?.let {
                    yOffset = (it - yRightAxis.min) * oneDataYPx
                }
            }
        }
        xAxis.let {
            it.scaleInterval?.let { scaleInterval ->
                drawXaxisBottomScale(
                    drawScope = this,
                    axisPoints = axisPoints,
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
                        axisPoints = axisPoints,
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
                    axisPoints = axisPoints,
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
                        axisPoints = axisPoints,
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
                    axisPoints = axisPoints,
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
                        axisPoints = axisPoints,
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
                    axisPoints = axisPoints,
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
                        axisPoints = axisPoints,
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
    axisPoints: AxisPoints,
    scale: Float
) {
    drawScope.run {
        xAxis.let {
            it.limitLineList?.let { limitLineList ->
                drawXLimitLine(
                    drawScope = this,
                    xLimitLineList = limitLineList,
                    axisPoints = axisPoints,
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
                    axisPoints = axisPoints,
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
                    axisPoints = axisPoints,
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
                    axisPoints = axisPoints,
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
    axisPoints: AxisPoints,
    scale: Float
) {
    drawScope.run {
        xAxis.let {
            it.name?.let { name ->
                drawXaxisBottomName(
                    drawScope = this,
                    name = name,
                    axisPoints = axisPoints,
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
                    axisPoints = axisPoints,

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
                    axisPoints = axisPoints,
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
                    axisPoints = axisPoints,

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
    yChunkList.add(
        Chunk(
            800f,
            1000f,
            color = Color(0X222FF4E87)
        )
    )
    return yChunkList
}

fun getTestChunkList3(): MutableList<Chunk> {
    val yChunkList: MutableList<Chunk> = mutableListOf()
    yChunkList.add(
        Chunk(
            1500f,
            1800f,
            color = Color(0X22058BF6)
        )
    )
    return yChunkList
}

fun getTestLimitLineList(): MutableList<LimitLine> {
    val limitLineList: MutableList<LimitLine> = mutableListOf()
    limitLineList.add(
        LimitLine(
            50f,
            true,
            width = 2.dp,
            color = Color.Gray,
            text = "测试"
        )
    )
    limitLineList.add(LimitLine(15f))
    return limitLineList
}

fun getTestPlusOrMinusLimitLineList(): MutableList<LimitLine> {
    val limitLineList: MutableList<LimitLine> = mutableListOf()
    limitLineList.add(
        LimitLine(
            50f,
            true,
            width = 2.dp,
            color = Color.Gray,
            text = "测试"
        )
    )
    limitLineList.add(LimitLine(-25f))
    return limitLineList
}

fun getTestXLimitLineList(): MutableList<LimitLine> {
    val limitLineList: MutableList<LimitLine> = mutableListOf()
    limitLineList.add(
        LimitLine(
            100f,
            true,
            width = 2.dp,
            color = Color.Gray,
            text = "测试"
        )
    )
    limitLineList.add(LimitLine(20f))
    return limitLineList
}

fun getTestXLimitLineList1(): MutableList<LimitLine> {
    val limitLineList: MutableList<LimitLine> = mutableListOf()
    limitLineList.add(
        LimitLine(
            10f,
            true,
            width = 2.dp,
            color = Color.Gray,
            text = "测试"
        )
    )
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
        Point(10f, 10f),
        Point(50f, 100f),
        Point(100f, 30f),
        Point(150f, 200f),
        Point(200f, 120f),
        Point(250f, 10f),
        Point(300f, 280f),
        Point(350f, 100f),
        Point(400f, 10f),
        Point(450f, 100f),
        Point(500f, 200f)
    )
    val point1 = mutableListOf(
        Point(10f, 210f),
        Point(50f, 150f),
        Point(100f, 130f),
        Point(150f, 200f),
        Point(200f, 80f),
        Point(250f, 240f),
        Point(300f, 20f),
        Point(350f, 150f),
        Point(400f, 50f),
        Point(450f, 240f),
        Point(500f, 140f)
    )
    linList.add(
        Line(
            point,
            color = Color(0xff50E3C2),
            isDashes = true,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 12f), 2f)
        )
    )
    linList.add(
        Line(
            point1,
            color = Color(0xff4A90E2),
            isDrawCubic = true,
            isDrawArea = true,
            drawAreaBrush = Brush.linearGradient(
                colors = listOf(Color(0xff4A90E2), Color(0x204A90E2)),
                start = Offset(0f, 0f),
                end = Offset(0f, Float.POSITIVE_INFINITY)
            )
        )
    )
    return linList
}


/**
 * @author Brian
 * @Description: test 测试数据，测试UI效果使用
 */
fun getTestLineListSelfDefined(context: Context): MutableList<Line> {
    val linList: MutableList<Line> = mutableListOf()
    val point = mutableListOf(
        Point(
            100f,
            50f,
            selfDefinedValue = { drawScope, offset ->
                drawSelfDefinedTextAndShape(
                    drawScope = drawScope,
                    offset = offset,
                    x = 100f,
                    y = 50f,
                    color = Color.Green
                )

            }),
        Point(
            200f,
            120f,
            selfDefinedValue = { drawScope, offset ->
                drawSelfDefinedTextAndShape(
                    drawScope = drawScope,
                    offset = offset,
                    x = 200f,
                    y = 120f,
                    color = Color.Red
                )

            }),
        Point(
            300f,
            220f,
            selfDefinedValue = { drawScope, offset ->
                drawSelfDefinedText(
                    drawScope = drawScope,
                    offset = offset,
                    x = 300f,
                    y = 220f,
                    color = Color.Black
                )

            }),
        Point(
            400f,
            80f,
            selfDefinedValue = { drawScope, offset ->

                val bitmap = BitmapFactory.decodeResource(
                    context.resources,  // 需要 Context
                    R.mipmap.icon_safelp_2
                )
                val scaledBmp = Bitmap.createScaledBitmap(
                    bitmap,
                    40,
                    40,
                    true   // 是否使用双线性滤波
                )
                drawSelfDefinedBitmap(
                    drawScope = drawScope,
                    bitmap = scaledBmp.asImageBitmap(),
                    offset = offset,
                )
            }),
        Point(
            500f,
            200f,
            selfDefinedValue = { drawScope, offset ->
                val bitmap = BitmapFactory.decodeResource(
                    context.resources,  // 需要 Context
                    R.mipmap.icon_safelp_2
                ).asImageBitmap()
                drawSelfDefinedBitmap(
                    drawScope = drawScope,
                    bitmap = bitmap,
                    offset = offset,
                )

            })
    )
    val point1 = mutableListOf(
        Point(10f, 210f),
        Point(50f, 150f),
        Point(100f, 130f),
        Point(150f, 200f),
        Point(200f, 80f),
        Point(250f, 240f),
        Point(300f, 20f),
        Point(350f, 150f),
        Point(400f, 50f),
        Point(450f, 240f),
        Point(500f, 140f)
    )
    linList.add(
        Line(
            point,
            color = Color(0xff50E3C2),
            isDashes = true,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 12f), 2f),
            renderer = { drawScope, line, offsetList ->
                line?.pointList?.forEachIndexed { index, point ->
                    offsetList?.getOrNull(index)
                        ?.let { point.selfDefinedValue?.invoke(drawScope, it) }
                }
            }
        )
    )
    /*linList.add(
        Line(
            point1,
            color = Color(0xff4A90E2),
            isDrawCubic = true,
            isDrawArea = true,
            drawAreaBrush = Brush.linearGradient(
                colors = listOf(Color(0xff4A90E2), Color(0x204A90E2)),
                start = Offset(0f, 0f),
                end = Offset(0f, Float.POSITIVE_INFINITY)
            ),
            renderer = { drawScope, line, offsetList ->
                line?.pointList?.forEachIndexed { index, point ->
                    offsetList?.getOrNull(index)
                        ?.let { point.selfDefinedValue?.invoke(drawScope, it) }
                }
            }
        )
    )*/
    return linList
}

/**
 *@author Brian
 *@Description:自定义样式，示例
 */
fun drawSelfDefinedBitmap(
    drawScope: DrawScope,
    bitmap: ImageBitmap,
    offset: Offset,
) {
    drawScope.run {

        drawImage(
            image = bitmap,
            topLeft = Offset(
                offset.x - bitmap.width / 2,
                offset.y - bitmap.height / 2
            ) // Example position adjustment
        )

    }

}

/**
 *@author Brian
 *@Description:自定义样式，示例
 */
fun drawSelfDefinedTextAndShape(
    drawScope: DrawScope,
    offset: Offset,
    x: Float,
    y: Float,
    color: Color
) {
    drawScope.run {
        val textSize = 12.sp
        drawRoundRect(
            color = color,
            topLeft = Offset(offset.x - 10f, offset.y - 10f),
            size = Size(20f, 20f),
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
            cornerRadius = CornerRadius(2f, 2f)
        )
        drawContext.canvas.nativeCanvas.apply {
            val nativePaint = Paint().let {
                it.apply {
                    this.textSize = textSize.toPx()
                    this.color = color.toArgb()
                    this.isAntiAlias = true//抗锯齿
                }
            }
            drawText(
                "(${x},${y})",
                offset.x - 80f,
                offset.y - textSize.toPx() / 2,
                nativePaint
            )
        }
    }
}

/**
 *@author Brian
 *@Description:自定义样式，示例
 */
fun drawSelfDefinedText(
    drawScope: DrawScope,
    offset: Offset,
    x: Float,
    y: Float,
    color: Color
) {
    drawScope.run {
        val textSize = 12.sp
        drawContext.canvas.nativeCanvas.apply {
            val nativePaint = Paint().let {
                it.apply {
                    this.textSize = textSize.toPx()
                    this.color = color.toArgb()
                    this.isAntiAlias = true//抗锯齿
                }
            }
            drawText(
                "${y.toInt()}次",
                offset.x - 40f,
                offset.y - textSize.toPx() / 2,
                nativePaint
            )
        }
    }
}

fun getTestLineList2(): MutableList<Line> {
    val linList: MutableList<Line> = mutableListOf()
    val point = mutableListOf(
        Point(0f, 10f),
        Point(5f, 100f),
        Point(10f, 30f),
        Point(15f, 200f),
        Point(20f, 120f),
        Point(25f, 10f),
        Point(30f, 180f),
        Point(35f, 100f),
        Point(40f, 10f),

        )
    val point1 = mutableListOf(
        Point(0f, 1000f),
        Point(5f, 1000f),
        Point(10f, 2000f),
        Point(15f, 120f),
        Point(20f, 1120f),
        Point(25f, 1000f),
        Point(30f, 180f),
        Point(35f, 100f),
        Point(40f, 1000f),
    )
    val point2 = mutableListOf(
        Point(0f, 1200f),
        Point(5f, 100f),
        Point(10f, 2200f),
        Point(15f, 600f),
        Point(20f, 120f),
        Point(25f, 1500f),
        Point(30f, 680f),
        Point(35f, 200f),
        Point(40f, 1500f),
    )
    linList.add(
        Line(
            point,
            color = Color(0XFF18D276),
            axisType = AxisType.LEFT_INSIDE
        )
    )
    linList.add(
        Line(
            point1,
            color = Color(0XFFFF4E87),
            isDrawCubic = true,
            isDashes = true
        )
    )
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

fun getPointLineList(context: Context): MutableList<Line> {
    val lineList: MutableList<Line> = mutableListOf()
    // 散点+标签
    val point = mutableListOf(
        Point(
            10f,
            10f,
            label = "标签label1",
            labelColor = Color(0XFFFF4E87),
            labelTextSize = 12.sp
        ),
        Point(
            50f,
            100f,
            label = "标签label2",
            labelColor = Color(0XFFFF4E87),
            labelTextSize = 15.sp
        ),
        Point(
            100f,
            30f,
            label = "标签label3",
            labelColor = Color(0XFFFF4E87),
            labelTextSize = 12.sp
        ),
        Point(
            150f,
            200f,
            label = "标签label4",
            labelColor = Color(0XFFFF4E87),
            labelTextSize = 15.sp
        ),
        Point(
            200f,
            120f,
            label = "标签label5",
            labelColor = Color(0XFFFF4E87),
            labelTextSize = 12.sp
        ),
        Point(
            250f,
            10f,
            label = "标签label6",
            labelColor = Color(0XFFFF4E87),
            labelTextSize = 15.sp
        ),
        Point(
            300f,
            280f,
            label = "标签label7",
            labelColor = Color(0XFFFF4E87),
            labelTextSize = 12.sp
        ),
        Point(
            350f,
            100f,
            label = "标签label8",
            labelColor = Color(0XFFFF4E87),
            labelTextSize = 15.sp
        ),
        Point(
            400f,
            10f,
            label = "标签label9",
            labelColor = Color(0XFFFF4E87),
            labelTextSize = 12.sp
        ),
        Point(
            450f,
            100f,
            label = "标签label10",
            labelColor = Color(0XFFFF4E87),
            labelTextSize = 15.sp
        ),
        Point(
            500f,
            200f,
            label = "标签label11",
            labelColor = Color(0XFFFF4E87),
            labelTextSize = 12.sp
        )
    )
    // 圆点
    val pointCircle = mutableListOf(
        Point(
            10f,
            210f,
            radius = 5f,
            style = Stroke(width = 1.5f)
        ),
        Point(
            50f,
            150f,
            radius = 10f,
            style = Stroke(width = 1.5f)
        ),
        Point(
            100f,
            130f,
            radius = 5f,
            style = Stroke(width = 1.5f)
        ),
        Point(
            150f,
            200f,
            radius = 10f,
            style = Stroke(width = 1.5f)
        ),
        Point(
            200f,
            80f,
            radius = 5f,
            style = Stroke(width = 1.5f)
        ),
        Point(
            250f,
            240f,
            radius = 10f,
            style = Stroke(width = 1.5f)
        ),
        Point(
            300f,
            20f,
            radius = 5f,
            style = Stroke(width = 1.5f)
        ),
        Point(
            350f,
            150f,
            radius = 10f,
            style = Stroke(width = 1.5f)
        ),
        Point(
            400f,
            50f,
            radius = 5f,
            style = Stroke(width = 1.5f)
        ),
        Point(
            450f,
            240f,
            radius = 10f,
            style = Stroke(width = 1.5f)
        ),
        Point(
            500f,
            140f,
            radius = 5f,
            style = Stroke(width = 1.5f)
        )
    )
    // 图标
    val image = drawableToBitmap(ContextCompat.getDrawable(context, R.mipmap.icon_safelp_2))
    val pointImage = mutableListOf(
        Point(0f, 1200f, image = image),
        Point(5f, 100f, image = image),
        Point(10f, 2200f, image = image),
        Point(15f, 600f, image = image),
        Point(20f, 120f, image = image),
        Point(25f, 1500f, image = image),
        Point(30f, 680f, image = image),
        Point(35f, 200f, image = image),
        Point(40f, 1500f, image = image),
    )
    // 填充区域
    val pointArea = mutableListOf(
        Point(10f, 10f),
        Point(50f, 100f),
        Point(100f, 30f),
    )
    lineList.add(
        Line(
            pointList = point,
            color = Color(0xff000000),
            isPoints = true,
            isDrawPath = false,
            isDrawLabel = true,
            width = 5.dp
        )
    )
    lineList.add(
        Line(
            pointList = pointCircle,
            color = Color(0xffff0000),
            isCircle = true,
            isDrawPath = false,
            width = 5.dp
        )
    )
    lineList.add(
        Line(
            pointList = pointImage,
            isDrawDrawable = true,
            isDrawPath = false
        )
    )
    lineList.add(
        Line(
            pointList = pointArea,
            color = Color(0XFF058BF6),
            isFill = true
        )
    )
    return lineList
}

fun drawableToBitmap(drawable: Drawable? = null): ImageBitmap? {
    if (drawable == null) {
        return null
    }
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap.asImageBitmap()
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
    linList.add(
        Line(
            point1,
            color = Color(0xff4A90E2),
            isDrawCubic = true
        )
    )
    return linList
}

/**
 * @author Brian
 * @Description: test 测试数据，测试UI效果使用
 */
fun getTestPointLineList(): MutableList<Line> {
    val linList: MutableList<Line> = mutableListOf()
    val point = mutableListOf(
        Point(10f, 210f),
        Point(50f, 150f),
        Point(100f, 130f),
        Point(150f, 200f),
        Point(200f, 80f),
        Point(250f, 240f),
        Point(300f, 20f),
        Point(350f, 150f),
        Point(400f, 50f),
        Point(450f, 240f),
        Point(500f, 140f)
    )
    linList.add(
        Line(
            point,
            width = 2.dp,
            color = Color(0xff4A90E2),
            isDrawCubic = true,
            isPoints = true,
            isDrawPath = false,
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
    linList.add(
        Line(
            point,
            width = 2.dp,
            color = Color(0xff4A90E2),
            isDrawCubic = true
        )
    )
    return linList
}

fun drawXaxisBottomScale(
    drawScope: DrawScope,
    axisPoints: AxisPoints,
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
        val scaleIntervalSize = (axisPoints.point1.x - axisPoints.point0.x) / scaleNum//刻度间隔，换算成px
        for (i in 0..scaleNum.toInt()) {
            val x = (axisPoints.point0.x + i * scaleIntervalSize)
            drawLine(
                start = Offset(x * scale, axisPoints.point0.y - yOffset),
                end = Offset(
                    x * scale,
                    axisPoints.point0.y + scaleLengSize - yOffset
                ),
                color = axisColor,
                strokeWidth = axisStrokeSize
            )
        }

    }
}

fun drawYAxisLeftInsideScale(
    drawScope: DrawScope,
    axisPoints: AxisPoints,
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
        val scaleIntervalSize = (axisPoints.point0.y - axisPoints.point3.y) / scaleNum//刻度间隔，换算成px
        for (i in 0..scaleNum.toInt()) {
            val y = (axisPoints.point0.y - i * scaleIntervalSize)
            drawLine(
                start = Offset(axisPoints.point0.x + xOffset, y),
                end = Offset(
                    axisPoints.point0.x + scaleLengSize * scale + xOffset,
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
    axisPoints: AxisPoints,
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
        val scaleIntervalSize = (axisPoints.point0.y - axisPoints.point3.y) / scaleNum//刻度间隔，换算成px
        for (i in 0..scaleNum.toInt()) {
            val y = (axisPoints.point0.y - i * scaleIntervalSize)
            drawLine(
                start = Offset(axisPoints.point0.x + xOffset, y),
                end = Offset(
                    axisPoints.point0.x - scaleLengSize * scale + xOffset,
                    y
                ),
                color = axisColor,
                strokeWidth = axisStrokeSize
            )
        }

    }
}

fun drawYAxisRightScale(
    drawScope: DrawScope,
    axisPoints: AxisPoints,
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
        val scaleIntervalSize = (axisPoints.point0.y - axisPoints.point3.y) / scaleNum//刻度间隔，换算成px
        for (i in 0..scaleNum.toInt()) {
            val y = (axisPoints.point0.y - i * scaleIntervalSize)
            drawLine(
                start = Offset(axisPoints.point1.x + xOffset, y),
                end = Offset(
                    axisPoints.point1.x + scaleLengSize * scale + xOffset,
                    y
                ),
                color = axisColor,
                strokeWidth = axisStrokeSize
            )
        }

    }
}


fun drawXaxisBottomLabel(
    drawScope: DrawScope,
    axisPoints: AxisPoints,
    defaultXAxisMin: Float,
    defaultXAxisMax: Float,
    labelColor: Color,
    labelInterval: Float,
    labelTextSizePx: Float = 24f,
    scale: Float,
    settingLabelValue: ((value: Float) -> String)?,
    yOffset: Float,
) {
    with(drawScope) {
        val scaleNum = (defaultXAxisMax - defaultXAxisMin) / labelInterval
        val scaleIntervalSize = (axisPoints.point1.x - axisPoints.point0.x) / scaleNum

        // Create paint once
        val nativePaint = android.graphics.Paint().apply {
            textSize = labelTextSizePx
            color = labelColor.toArgb()
            isAntiAlias = true
        }

        val baseY = axisPoints.point0.y + labelTextSizePx + 4.dp.toPx() - yOffset

        // Precompute all text positions and labels
        val textEntries = (0..scaleNum.toInt()).map { i ->
            val labelValue =
                BigDecimal(defaultXAxisMin.toString()).add(
                    BigDecimal(labelInterval.toString()).multiply(
                        BigDecimal(i)
                    )
                ).toFloat()
            val labelText = settingLabelValue?.invoke(labelValue) ?: run {
                if (labelValue.isInteger()) labelValue.toInt().toString()
                else labelValue.toString()
            }
            val x =
                (axisPoints.point0.x + i * scaleIntervalSize - labelText.length * labelTextSizePx / 2 * 0.6f) * scale
            Pair(labelText, x)
        }

        // Draw all texts in one operation
        drawContext.canvas.nativeCanvas.apply {
            textEntries.forEach { (text, x) ->
                drawText(text, x, baseY, nativePaint)
            }
        }
    }
}

private fun Float.isInteger(): Boolean = this == toInt().toFloat()

fun drawXaxisBottomName(
    drawScope: DrawScope,
    name: String,
    axisPoints: AxisPoints,
    defaultXAxisMax: Float,
    labelColor: Color,
    labelTextSizePx: Float = 24f,
    scale: Float
) {
    drawScope.run {
        val nativePaint = Paint().let {
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

        val x = axisPoints.point1.x + offset
        var y = axisPoints.point0.y + labelTextSizePx + 4.dp.toPx()
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
    axisPoints: AxisPoints,
    labelColor: Color,
    labelTextSizePx: Float = 24f,
    scale: Float
) {

    drawScope.run {
        val nativePaint = Paint().let {
            it.apply {
                textSize = labelTextSizePx
                color = labelColor.toArgb()
                isAntiAlias = true//抗锯齿
            }
        }


        val x = axisPoints.point0.x + 8.dp.toPx()
        var y = axisPoints.point3.y - labelTextSizePx
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
    axisPoints: AxisPoints,
    labelColor: Color,
    labelTextSizePx: Float = 24f,
    scale: Float
) {

    drawScope.run {
        val nativePaint = Paint().let {
            it.apply {
                textSize = labelTextSizePx
                color = labelColor.toArgb()
                isAntiAlias = true//抗锯齿
            }
        }


        val x = 0f + 2.dp.toPx()
        var y = axisPoints.point3.y - labelTextSizePx
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
    axisPoints: AxisPoints,
    labelColor: Color,
    labelTextSizePx: Float = 24f,
    scale: Float
) {

    drawScope.run {
        val nativePaint = Paint().let {
            it.apply {
                textSize = labelTextSizePx
                color = labelColor.toArgb()
                isAntiAlias = true//抗锯齿
            }
        }
        val x = axisPoints.point2.x + 2.dp.toPx()
        var y = axisPoints.point2.y - labelTextSizePx
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
    axisPoints: AxisPoints,
    defaultYAxisMin: Float,
    defaultYAxisMax: Float,
    labelColor: Color,
    labelInterval: Float,
    labelTextSizePx: Float = 24f,
    scale: Float,
    settingLabelValue: ((value: Float) -> String)?,
    xOffset: Float,
) {
    with(drawScope) {
        val scaleNum = (defaultYAxisMax - defaultYAxisMin) / labelInterval
        val scaleIntervalSize = (axisPoints.point0.y - axisPoints.point3.y) / scaleNum

        // 创建并配置Paint对象（只创建一次）
        val textPaint = android.graphics.Paint().apply {
            textSize = labelTextSizePx
            color = labelColor.toArgb()
            isAntiAlias = true
        }

        // 预计算所有标签位置
        (0..scaleNum.toInt()).map { i ->
            val labelValue =
                BigDecimal(defaultYAxisMin.toString()).add(
                    BigDecimal(labelInterval.toString()).multiply(
                        BigDecimal(i)
                    )
                ).toFloat()
            val labelText = settingLabelValue?.invoke(labelValue) ?: formatLabel(labelValue)
            val x = axisPoints.point0.x + xOffset + 8.dp.toPx()
            val y = axisPoints.point0.y - i * scaleIntervalSize + labelTextSizePx / 4
            labelText to Point(x, y)
        }.let {
            drawContext.canvas.nativeCanvas.apply {
                it.forEach { (text, pos) ->
                    drawText(text, pos.x, pos.y, textPaint)
                }
            }
        }
    }
}

private fun formatLabel(value: Float): String {


    return if (value.isInteger()) value.toInt().toString() else value.toString()
}

fun drawYAxisLeftLabel(
    drawScope: DrawScope,
    axisPoints: AxisPoints,
    defaultYAxisMin: Float,
    defaultYAxisMax: Float,
    labelColor: Color,
    labelInterval: Float,
    labelTextSizePx: Float = 24f,
    scale: Float,
    settingLabelValue: ((value: Float) -> String)?,
    xOffset: Float,
) {
    with(drawScope) {
        val scaleNum = (defaultYAxisMax - defaultYAxisMin) / labelInterval
        val scaleIntervalSize = (axisPoints.point0.y - axisPoints.point3.y) / scaleNum

        val textPaint = android.graphics.Paint().apply {
            textSize = labelTextSizePx
            color = labelColor.toArgb()
            isAntiAlias = true
        }

        (0..scaleNum.toInt()).map { i ->
            val labelValue =
                BigDecimal(defaultYAxisMin.toString()).add(
                    BigDecimal(labelInterval.toString()).multiply(
                        BigDecimal(i)
                    )
                ).toFloat()
            val labelText = settingLabelValue?.invoke(labelValue) ?: formatLabel(
                labelValue
            )
            val textWidth = labelText.length * labelTextSizePx
            val x = axisPoints.point0.x + xOffset - 8.dp.toPx() - textWidth / 2
            val y = axisPoints.point0.y - i * scaleIntervalSize + labelTextSizePx * 0.3f
            labelText to Point(x, y)
        }.let {
            drawContext.canvas.nativeCanvas.apply {
                it.forEach { (text, pos) ->
                    drawText(text, pos.x, pos.y, textPaint)
                }
            }
        }


    }
}

fun drawYAxisRightLabel(
    drawScope: DrawScope,
    axisPoints: AxisPoints,
    defaultYAxisMin: Float,
    defaultYAxisMax: Float,
    labelColor: Color,
    labelInterval: Float,
    labelTextSizePx: Float = 24f,
    scale: Float,
    settingLabelValue: ((value: Float) -> String)?,
    xOffset: Float,
) {
    with(drawScope) {
        val scaleNum = (defaultYAxisMax - defaultYAxisMin) / labelInterval
        val scaleIntervalSize = (axisPoints.point1.y - axisPoints.point2.y) / scaleNum

        val textPaint = android.graphics.Paint().apply {
            textSize = labelTextSizePx
            color = labelColor.toArgb()
            isAntiAlias = true
        }

        (0..scaleNum.toInt()).map { i ->
            val labelValue =
                BigDecimal(defaultYAxisMin.toString()).add(
                    BigDecimal(labelInterval.toString()).multiply(
                        BigDecimal(i)
                    )
                ).toFloat()
            val labelText = settingLabelValue?.invoke(labelValue) ?: formatLabel(
                labelValue
            )
            val x = axisPoints.point1.x + xOffset + 8.dp.toPx()
            val y = axisPoints.point1.y - i * scaleIntervalSize + labelTextSizePx * 0.3f
            labelText to Point(x, y)
        }.let {
            drawContext.canvas.nativeCanvas.apply {
                it.forEach { (text, pos) ->
                    drawText(text, pos.x, pos.y, textPaint)
                }
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
    axisPoints: AxisPoints,
    yAxisMin: Float,
    yAxisMax: Float,

    ) {
    drawScope.run {


        val oneDataYPx =
            (axisPoints.point0.y - axisPoints.point3.y) / (yAxisMax - yAxisMin) // X轴上 1f单位数据点对应的px数
        yChunkList?.forEachIndexed { index, chunk ->
            val X1 = axisPoints.point0.x
            val X2 = axisPoints.point1.x
            val Y1 = axisPoints.point0.y - chunk.start * oneDataYPx //
            val Y2 = axisPoints.point0.y - chunk.end * oneDataYPx //

            drawRect(
                color = chunk.color,
                topLeft = Offset(x = axisPoints.point0.x, y = Y1),
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
    axisPoints: AxisPoints,
    axisMin: Float,
    axisMax: Float,
    scale: Float

) {
    drawScope.run {

        val oneDataXPx =
            (axisPoints.point1.x - axisPoints.point0.x) / (axisMax - axisMin) // X轴上 1f单位数据点对应的px数

        xLimitLineList?.forEachIndexed { index, limitLine ->
            val X1 = axisPoints.point0.x + (limitLine.value - axisMin) * oneDataXPx //转换为对应的X Px
            val Y1 = axisPoints.point3.y
            val Y2 = axisPoints.point0.y
            val widthPx = limitLine.width.toPx()
            val dashPathEffect = if (limitLine.isDashes) {
                PathEffect.dashPathEffect(floatArrayOf(10f, 4f), 4f)
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
            val nativePaint = Paint().let {
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
    axisPoints: AxisPoints,
    axisMin: Float,
    axisMax: Float,
    scale: Float
) {
    drawScope.run {

        val oneDataYPx =
            (axisPoints.point0.y - axisPoints.point3.y) / (axisMax - axisMin) // X轴上 1f单位数据点对应的px数

        yLimitLineList?.forEachIndexed { index, limitLine ->
            val X1 = axisPoints.point0.x
            val X2 = axisPoints.point1.x
            val Y1 = axisPoints.point0.y - (limitLine.value - axisMin) * oneDataYPx //转换为对应的Y Px

            //直线
            val dashPathEffect = if (limitLine.isDashes) {
                PathEffect.dashPathEffect(floatArrayOf(10f, 4f), 4f)
            } else {
                null
            }
            val widthPx = limitLine.width.toPx()
            val linY = Y1 - widthPx / 4
            drawLine(
                start = Offset(x = X1, y = linY),
                end = Offset(x = X2, y = linY),
                color = limitLine.color,
                pathEffect = dashPathEffect,
                strokeWidth = widthPx


            )
            //文字
            var textSizePx = limitLine.textSize.toPx()
            val nativePaint = Paint().let {
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
    axisPoints: AxisPoints,
    xAxisMin: Float,
    xAxisMax: Float,
    scale: Float

) {
    drawScope.run {

        val oneDataXPx =
            (axisPoints.point1.x - axisPoints.point0.x) / (xAxisMax - xAxisMin) // X轴上 1f单位数据点对应的px数
        gridLine?.let {
            val gridNum = ((xAxisMax - xAxisMin) / it.interval).toInt()
            it.interval * oneDataXPx
            for (i in 0..gridNum) {
                val X1 = axisPoints.point0.x + i * it.interval * oneDataXPx //转换为对应的X Px
                val Y1 = axisPoints.point3.y
                val Y2 = axisPoints.point0.y
                val widthPx = it.width.toPx()
                val dashPathEffect = if (it.isDashes) {
                    PathEffect.dashPathEffect(floatArrayOf(10f, 4f), 4f)
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


            }

        }


    }


}

fun drawYGridLine(
    drawScope: DrawScope,
    gridLine: GridLine? = null,//Y轴上画线
    axisPoints: AxisPoints,
    xAxisMin: Float,
    xAxisMax: Float,
    scale: Float
) {
    drawScope.run {

        val oneDataYPx =
            (axisPoints.point0.y - axisPoints.point3.y) / (xAxisMax - xAxisMin) // X轴上 1f单位数据点对应的px数
        gridLine?.let {
            val gridNum = ((xAxisMax - xAxisMin) / it.interval).toInt()
            for (i in 0..gridNum) {
                val X1 = axisPoints.point0.x
                val X2 = axisPoints.point1.x
                val Y1 = axisPoints.point0.y - i * it.interval * oneDataYPx //转换为对应的Y Px

                //直线
                val dashPathEffect = if (it.isDashes) {
                    PathEffect.dashPathEffect(floatArrayOf(10f, 4f), 4f)
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
    axisPoints: AxisPoints,
    scale: Float
) {
    drawScope.run {
        xAxis.let {
            it.gridLine?.let { gridLine ->
                drawXGridLine(
                    drawScope = this,
                    gridLine = gridLine,
                    axisPoints = axisPoints,
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
                    axisPoints = axisPoints,
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
                    axisPoints = axisPoints,
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
                    axisPoints = axisPoints,
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