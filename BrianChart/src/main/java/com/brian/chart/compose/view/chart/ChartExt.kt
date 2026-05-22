package com.brian.chart.compose.view.chart

import android.graphics.Paint
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.brian.view.chart.AxisPoints
import java.math.BigDecimal


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
            val X1 = axisPoints.point0.x + (chunk.start - axisMin) * oneDataYPx
            val X2 = axisPoints.point0.x + (chunk.end - axisMin) * oneDataYPx
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


fun drawTouchIndicator(
    drawScope: DrawScope,
    xAxis: Axis,
    yLeftInsideAxis: Axis? = null,
    yLeftAxis: Axis? = null,
    yRightAxis: Axis? = null,
    axisPoints: AxisPoints,
    offset: Offset,
) {
    drawScope.run {
        drawTouchIndicator(
            drawScope,
            axisPoints = axisPoints,
            axisMin = xAxis.min,
            axisMax = xAxis.max,
            offset = offset
        )
    }
}

/**
 * @author Brian
 * @Description:画颜色块
 */
fun drawTouchIndicator(
    drawScope: DrawScope,
    axisPoints: AxisPoints,
    axisMin: Float,
    axisMax: Float,
    offset: Offset,
) {
    drawScope.run {


        val oneDataYPx =
            (axisPoints.point1.x - axisPoints.point0.x) / (axisMax - axisMin) // X轴上 1f单位数据点对应的px数
        offset?.let { (x, y) ->
            val X1 = axisPoints.point0.x + offset.x
            val X2 = axisPoints.point0.x + offset.y
            val Y1 = axisPoints.point0.y  //
            val Y2 = axisPoints.point3.y  //

            drawRect(
                color = Color.Red,
//                topLeft = offset,
                topLeft = Offset(offset.x, Y1),
                size = Size(1.dp.toPx(), Y2 - Y1)
            )
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
            val Y1 = axisPoints.point0.y - (chunk.start - yAxisMin) * oneDataYPx //
            val Y2 = axisPoints.point0.y - (chunk.end - yAxisMin) * oneDataYPx //

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
    }

}
