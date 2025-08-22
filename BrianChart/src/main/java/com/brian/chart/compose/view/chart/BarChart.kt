package com.brian.chart.compose.view.chart

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brian.chart.TimeUtil
import com.brian.chart.formatDigitOrNull
import kotlin.math.abs

/**
 * @author Brian
 * @Description: 线性 图表
 * TODO : 优化缩放、添加滑动看数据
 */
@Composable
fun BarChart(
    modifier: Modifier = Modifier, data: BarChartData? = null
) {
    val barData: BarData? = data?.barData
    val xAxis: Axis = data?.xAxis ?: Axis()
    val yLeftAxis: Axis = data?.yLeftAxis ?: Axis()
    val isScroll: Boolean = data?.isScroll ?: false
    //用来记录缩放大小
    var scale by remember { mutableStateOf(1f) }//缩放
    var rotation by remember { mutableStateOf(0f) } //旋转
    var offset by remember { mutableStateOf(Offset.Zero) }//移动

    reSetBarXMax(xAxis, barData)
    reSetBarYMax(yLeftAxis, barData)

    val state = rememberTransformableState { zoomChange, panChange, rotationChange ->
        scale = scale * zoomChange
        Log.d("LineChart", "scale = ${scale}  zoomChange = ${zoomChange} panChange = ${panChange}")

    }
    Box(modifier = modifier) {

        val textMeasurer: TextMeasurer = rememberTextMeasurer()
        val currentDensity = LocalDensity.current

        var lablePaddingLeft = getBarYAxisPadding(this, yLeftAxis)
        var lablePaddingRight = getBarAxisPaddingRight(this, xAxis)
        var lablePaddingTop = getBarXAxisPaddingTop(this, yLeftAxis)
        var lablePaddingBootom = getBarXAxisPaddingBottom(this, xAxis)

        val modifier = if (isScroll) {
            //监听手势缩放
            Modifier
                .graphicsLayer()
                .transformable(state)
        } else {
            Modifier
        }

        Canvas(
            modifier = modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(4.dp)

        ) {

            val yLeftScaleLengSize =
                getScaleLengSize(this, yLeftAxis)//左边刻度的长度
            val yRightScaleLengSize =
                getScaleLengSize(this, yLeftAxis)//右边刻度的长度
            val xBottomScaleLengSize =
                getScaleLengSize(this, xAxis)//低边刻度的长度
            //确定四个绘图点
            val point0 = Point(
                0f + lablePaddingLeft + yLeftScaleLengSize,
                size.height - xBottomScaleLengSize - lablePaddingBootom
            ) //左下角（原点）

            val point1 = Point(
                size.width - lablePaddingRight - yRightScaleLengSize,
                size.height - xBottomScaleLengSize - lablePaddingBootom
            )//右下角点

            val point2 = Point(
                size.width - lablePaddingRight - yRightScaleLengSize, 0f + lablePaddingTop
            )//右上角点

            val point3 = Point(
                0f + lablePaddingLeft + yLeftScaleLengSize, 0f + lablePaddingTop
            )//左上角点

            val axisPoints = AxisPoints(point0, point1, point2, point3)
            /**画chunk 块内容*/
            drawChunk(
                this, xAxis = xAxis, yLeftAxis = yLeftAxis, axisPoints = axisPoints
            )

            /**画xy轴*/
            drawXYAxis(
                this, xAxis = xAxis, yLeftAxis = yLeftAxis, axisPoints = axisPoints
            )
            /**刻度 label*/
            drawLable(
                this,
                xAxis = xAxis,
                yLeftAxis = yLeftAxis,
                axisPoints = axisPoints,
                scale = scale
            )
            /**划限制线*/
            drawLimitLine(
                this,
                xAxis = xAxis,
                yLeftAxis = yLeftAxis,
                axisPoints = axisPoints,
                scale = scale
            )
            /**坐标轴名称*/
            drawAxisName(
                this,
                xAxis = xAxis,
                yLeftAxis = yLeftAxis,
                axisPoints = axisPoints,
                scale = scale
            )
            /**画柱状图*/
            drawBar(
                this,
                barData,
                xAxis = xAxis,
                yLeftAxis = yLeftAxis,
                point0 = point0,
                point1 = point1,
                point2 = point2,
                point3 = point3,
                scale = scale
            )
        }
    }
}

fun reSetBarXMax(
    axis: Axis,
    barData: BarData?,

    ) {
    //动态调整最大值
    val list = barData?.barDataSetList?.map { it.barEntryList?.maxByOrNull { it.x }?.x ?: 0f }
    val maxListData = list?.maxOrNull() ?: axis.max
    val maxLimitLine = axis.limitLineList?.maxByOrNull { it.value }?.value
    val maxChunk = axis.chunkList?.maxByOrNull { it.end }?.end
    var max = 0f
    mutableListOf(maxListData, maxLimitLine, maxChunk).maxByOrNull { it ?: 0f }?.let {
        max = it
    }

    val labelInterval = (axis.labelInterval ?: 1f)
    if (max > axis.max) {
        axis.max = ((max / labelInterval).toInt() + 1) * labelInterval
    }

}

fun reSetBarYMax(axis: Axis, barData: BarData?) {
    //动态调整最大值
    val list = barData?.barDataSetList?.map { it.barEntryList?.maxByOrNull { it.y }?.y ?: 0f }
    val maxListData = list?.maxOrNull() ?: axis.max
    val maxLimitLine = axis.limitLineList?.maxByOrNull { it.value }?.value
    val maxChunk = axis.chunkList?.maxByOrNull { it.end }?.end
    var max = 0f
    mutableListOf(maxListData, maxLimitLine, maxChunk).maxByOrNull { it ?: 0f }?.let {
        max = it
    }
    val labelInterval = axis.labelInterval ?: 1f
    if (max > axis.max) {
        axis.max = ((max / labelInterval).toInt() + 1) * labelInterval
    }

}

fun drawBar(
    drawScope: DrawScope,
    barData: BarData? = null,
    xAxis: Axis,
    yLeftAxis: Axis,
    point0: Point,
    point1: Point,
    point2: Point,
    point3: Point,
    scale: Float
) {
    drawScope.run {
        barData?.let { barData ->
            val width = point1.x - point0.x
            val heigt = point0.y - point3.y
            val sizeList = barData.barDataSetList?.map { it.barEntryList?.size }?.toMutableList()
            val maxSize = sizeList?.maxBy { it ?: 0 } ?: 1
            val oneDataXPx = width / (xAxis.max - xAxis.min)
            val oneDataYPx = heigt / (yLeftAxis.max - yLeftAxis.min)
            val oneDataXUsePx = oneDataXPx * barData.weight //一个单位可用的宽度
            val defaultPadding = barData.dataSetPadding.toPx() //
            val barDataSetCount = barData.barDataSetList?.size ?: 0
            val oneBardataMaxWidth = oneDataXUsePx / barDataSetCount
            val oneBardataMaxWidthNoPadding = oneBardataMaxWidth - defaultPadding * 2
            barData.barDataSetList?.forEachIndexed { indexDataSet, barDataSet ->
                barDataSet.barEntryList?.forEachIndexed { index, barEntry ->

                    //柱状图的宽度
                    val barDataWidth = barData.width?.toPx()?.let {
                        if (oneBardataMaxWidthNoPadding < it) {
                            oneBardataMaxWidthNoPadding
                        } else {
                            it
                        }
                    } ?: oneBardataMaxWidthNoPadding

                    val barDataHeight = -barEntry.y * oneDataYPx //柱状图高度
//                    val offsetX = point0.x + oneDataXPx * barEntry.x
                    val offsetX =
                        point0.x + (oneDataXPx * barEntry.x) - (oneDataXUsePx / 2) + (indexDataSet * oneBardataMaxWidth) + (oneBardataMaxWidth - barDataWidth) / 2
                    val offsetY = point0.y

                    val offset = Offset(x = offsetX, y = offsetY)
                    val size = Size(
                        width = barDataWidth, height = barDataHeight
                    )

                    //画柱状形状-------------
                    if (barDataSet.background == null) { //采用默认背景
                        drawRoundRect(
                            color = barDataSet.color,
                            topLeft = offset,
                            size = size,
                            style = Stroke(width = 1.dp.toPx(), cap = StrokeCap.Butt),
                            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                        )
                    } else {//自定义背景 ，可绘画
                        barDataSet.background?.let { background ->
                            background(
                                this, barDataSet.color, offset, size
                            )
                        }
                    }
                    //画数值 ----------------
                    val valueTextSizePx = barDataSet.valueTextSize.toPx()
                    val nativePaint = android.graphics.Paint().let {
                        it.apply {
                            textSize = valueTextSizePx
                            color = barDataSet.color.toArgb()
                            isAntiAlias = true//抗锯齿
                        }
                    }
                    val label = barDataSet.settingValueText?.let { it(barDataSet.name, barEntry.y) }
                        ?: "${barEntry.y}"
//                    val label = "${barDataSet.name}${barEntry.y}"
                    val labelWidth = label.length * valueTextSizePx
                    val offsetText = labelWidth / 2

                    val x = offsetX + barDataWidth / 2 - offsetText / 2 + defaultPadding / 4

                    var y = offsetY + barDataHeight - valueTextSizePx - 2.dp.toPx()
                    if (label.contains("\n")) {
                        var list = label.split("\n").reversed()
                        drawContext.canvas.nativeCanvas.drawText(
                            list[1], x, y - valueTextSizePx, nativePaint
                        )
                        drawContext.canvas.nativeCanvas.drawText(
                            list[0], x, y, nativePaint
                        )
                    } else {
                        drawContext.canvas.nativeCanvas.drawText(
                            label, x, y, nativePaint
                        )
                    }

                }
            }
        }
    }

}


@Composable
fun getBarYAxisPadding(boxScope: BoxScope, axis: Axis?): Float {
    var padding = 0f
    boxScope.run {
        axis?.let {
            val maxTextLayoutResult = rememberTextMeasurer().measure(
                text = axis.max.toString(),
                style = TextStyle(color = Color.Black, fontSize = axis.labelTextSize)
            )
            val nameTextLayoutResult = rememberTextMeasurer().measure(
                text = axis.name.toString(),
                style = TextStyle(color = Color.Black, fontSize = axis.labelTextSize)
            )
            mutableListOf(
                maxTextLayoutResult.size.width, nameTextLayoutResult.size.width
            ).maxOrNull()?.let {
                padding += it
            }

        }
    }
    Log.d("LinearChart2", "getYAxisPadding  padding = ${padding}")
    return padding + 8f
}

@Composable
fun getBarAxisPaddingRight(boxScope: BoxScope, xAxis: Axis): Float {
    var padding = 0f

    boxScope.run {

        xAxis.let {
            val maxTextLayoutResult = rememberTextMeasurer().measure(
                text = it.max.toString(),
                style = TextStyle(color = Color.Black, fontSize = it.labelTextSize)
            )
            val nameTextLayoutResult = rememberTextMeasurer().measure(
                text = it.name.toString(),
                style = TextStyle(color = Color.Black, fontSize = it.labelTextSize)
            )
            mutableListOf(
                maxTextLayoutResult.size.width / 4, nameTextLayoutResult.size.width
            ).maxOrNull()?.let {
                padding += it
            }

        }

    }

    return padding + 8f
}

@Composable
fun getBarXAxisPaddingTop(drawScope: BoxScope, yLeftAxis: Axis?): Float {
    var paddingTop = 0f
    drawScope.run {


        val yLeftNameTextLayoutResult = rememberTextMeasurer().measure(
            text = yLeftAxis?.name ?: "",
            style = TextStyle(color = Color.Black, fontSize = yLeftAxis?.labelTextSize ?: 12.sp)
        )

        paddingTop = yLeftNameTextLayoutResult.size.height.toFloat()


    }
    Log.d("LinearChart2", "getXAxisPaddingTop  paddingTop = ${paddingTop}")
    return paddingTop + 8f
}

@Composable
fun getBarXAxisPaddingBottom(drawScope: BoxScope, axis: Axis?): Float {
    var paddingTop = 0f
    drawScope.run {

        axis?.let {
            val nameTextLayoutResult = rememberTextMeasurer().measure(
                text = it.name ?: "",
                style = TextStyle(color = Color.Black, fontSize = it.labelTextSize)
            )
            val maxTextLayoutResult = rememberTextMeasurer().measure(
                text = it.max.toString(),
                style = TextStyle(color = Color.Black, fontSize = it.labelTextSize)
            )

            mutableListOf(

                nameTextLayoutResult.size.height, maxTextLayoutResult.size.height
            ).maxOrNull()?.let {
                paddingTop += it
            }
        }
    }
    Log.d("LinearChart2", "getXAxisPaddingTop  paddingTop = ${paddingTop}")
    return paddingTop + 8f
}

data class BarData(
    var barDataSetList: MutableList<BarDataSet>? = null, var groupPadding: Float = 10f,//组之间间隔
    var onGroupPadding: Float = 0f,//组内间隔
    var width: Dp? = null, var weight: Float = 0.8f, //一个单位可用的宽度 比例
    var dataSetPadding: Dp = 2.dp
)

data class BarDataSet(
    var barEntryList: MutableList<BarEntry>? = null,
    var color: Color = Color.Blue,
    var background: ((drawScope: DrawScope, color: Color, offset: Offset, size: Size) -> Unit)? = null,//TODO 可自由定制
    var name: String = "",
    var valueTextSize: TextUnit = 8.sp,
    var settingValueText: ((name: String, value: Float) -> String)? = null//定制顶部的值显示
)

data class BarEntry(
    var x: Float, var y: Float
)

object NameAglin {
    const val TOP = "TOP"
    const val BOTTOM = "BOTTOM"
}

fun getTestBarData(): BarData {
    val barData = BarData(width = 80.dp)
    var barDataSetListTemp: MutableList<BarDataSet> = mutableListOf()
    var barEntryList: MutableList<BarEntry> = mutableListOf()
    barEntryList.add(BarEntry(1f, 60f))
    barEntryList.add(BarEntry(2f, 200f))

    barDataSetListTemp.add(
        BarDataSet(
            barEntryList = barEntryList, color = Color.Gray, background = background1
        )
    )

    barData.barDataSetList = barDataSetListTemp
    return barData
}

fun getTestBarData2(): BarData {
    val barData = BarData()
    var barDataSetListTemp: MutableList<BarDataSet> = mutableListOf()
    var barEntryList: MutableList<BarEntry> = mutableListOf()
    barEntryList.add(BarEntry(1f, 60f))
    barEntryList.add(BarEntry(2f, 200f))

    barDataSetListTemp.add(
        BarDataSet(
            name = "CHO",
            barEntryList = barEntryList,
            color = Color.Gray,
            background = background1,
            settingValueText = ::settingValueText2//定制顶部的值显示
        )
    )
    var barEntryList2: MutableList<BarEntry> = mutableListOf()

    barEntryList2.add(BarEntry(1f, 80f))
    barEntryList2.add(BarEntry(2f, 250f))

    barDataSetListTemp.add(
        BarDataSet(
            name = "FAT",
            barEntryList = barEntryList2,
            color = Color.Gray,
            background = background2,
            settingValueText = ::settingValueText2//定制顶部的值显示
        )
    )

    barData.barDataSetList = barDataSetListTemp
    return barData
}

fun getTestBarData3(): BarData {
    val barData = BarData()
    var barDataSetListTemp: MutableList<BarDataSet> = mutableListOf()
    var barEntryList: MutableList<BarEntry> = mutableListOf()
    barEntryList.add(BarEntry(1f, 60f))
    barEntryList.add(BarEntry(2f, 200f))

    barDataSetListTemp.add(
        BarDataSet(
            name = "CHO",
            barEntryList = barEntryList,
            color = Color.Gray,
            background = background1,
            settingValueText = ::settingValueText//定制顶部的值显示
        )
    )
    var barEntryList2: MutableList<BarEntry> = mutableListOf()

    barEntryList2.add(BarEntry(1f, 80f))
    barEntryList2.add(BarEntry(2f, 250f))

    barDataSetListTemp.add(
        BarDataSet(
            name = "FAT",
            barEntryList = barEntryList2,
            color = Color.Gray,
            background = background2,
            settingValueText = ::settingValueText//定制顶部的值显示
        )
    )
    var barEntryList3: MutableList<BarEntry> = mutableListOf()
    barEntryList3.add(BarEntry(1f, 50f))
    barEntryList3.add(BarEntry(2f, 150f))

    barDataSetListTemp.add(
        BarDataSet(
            name = "PRO",
            barEntryList = barEntryList3,
            color = Color.Gray,
            background = background3,
            settingValueText = ::settingValueText//定制顶部的值显示
        )
    )
    barData.barDataSetList = barDataSetListTemp
    return barData
}

fun getTestBarData4(): BarData {
    val barData = BarData(weight = 1f, dataSetPadding = 40.dp, width = 40.dp)
    var barDataSetListTemp: MutableList<BarDataSet> = mutableListOf()
    var barEntryList: MutableList<BarEntry> = mutableListOf()
    barEntryList.add(BarEntry(1f, 60f))
//    barEntryList.add(BarEntry(2f, 200f))

    barDataSetListTemp.add(
        BarDataSet(
            name = "CHO",
            barEntryList = barEntryList,
            color = Color.Gray,
            background = background1,
            settingValueText = ::settingValueText3//定制顶部的值显示
        )
    )
    var barEntryList2: MutableList<BarEntry> = mutableListOf()

    barEntryList2.add(BarEntry(1f, 80f))
//    barEntryList2.add(BarEntry(2f, 250f))

    barDataSetListTemp.add(
        BarDataSet(
            name = "FAT",
            barEntryList = barEntryList2,
            color = Color.Gray,
            background = background2,
            settingValueText = ::settingValueText3//定制顶部的值显示
        )
    )
    var barEntryList3: MutableList<BarEntry> = mutableListOf()
    barEntryList3.add(BarEntry(1f, 50f))
//    barEntryList3.add(BarEntry(2f, 150f))

    barDataSetListTemp.add(
        BarDataSet(
            name = "PRO",
            barEntryList = barEntryList3,
            color = Color.Gray,
            background = background3,
            settingValueText = ::settingValueText3//定制顶部的值显示
        )
    )
    barData.barDataSetList = barDataSetListTemp
    return barData
}

val background1: ((drawScope: DrawScope, color: Color, offset: Offset, size: Size) -> Unit) =
    { drawScope, color, offset, size ->
        drawScope.run {
            drawRoundRect(
                color = color,
                topLeft = offset,
                size = size,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
                cornerRadius = CornerRadius(2f, 2f)
            )
        }
    }
val background2: ((drawScope: DrawScope, color: Color, offset: Offset, size: Size) -> Unit) =
    { drawScope, color, offset, size ->
        drawScope.run {
            drawRoundRect(
                color = color, topLeft = offset, size = size, cornerRadius = CornerRadius(2f, 2f)
            )
            drawRoundRect(
                color = color,
                topLeft = offset,
                size = size,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
                cornerRadius = CornerRadius(2f, 2f)
            )

        }
    }
val background3: ((drawScope: DrawScope, color: Color, offset: Offset, size: Size) -> Unit) =
    { drawScope, color, offset, size ->
        drawScope.run {
            val width = 2.dp.toPx()
            drawRoundRect(
                color = color,
                topLeft = offset,
                size = size,
                style = Stroke(width = width, cap = StrokeCap.Round),
                cornerRadius = CornerRadius(2f, 2f)
            )
            var heighTemp = size.height
//            val heightAbs = abs(heighTemp).toInt()
            val coefficient = if (heighTemp > 0) 1 else -1
            for (i in 0..abs(heighTemp).toInt() step 20) {
                drawLine(
                    start = Offset(x = offset.x, y = offset.y + i * coefficient),
                    end = Offset(x = offset.x + size.width, y = offset.y + i * coefficient),
                    color = color,
                    strokeWidth = width
                )
            }


        }
    }


fun settingValueText(name: String, value: Float) =
    if (name.isNullOrEmpty()) "$${value}" else "${name}:${value.formatDigitOrNull(1)}"

fun settingValueText2(name: String, value: Float) =
    if (name.isNullOrEmpty()) "$${value}" else "${name}=${value.formatDigitOrNull(1)}"

fun settingValueText3(name: String, value: Float) =
    if (name.isNullOrEmpty()) "${value}" else "${name}\n${value.formatDigitOrNull(1)}"


fun settingLabelValue(value: Float): String {
    val label = when {
        value.toInt().toFloat() == value -> {//为整数浮点数
            "${value.toInt()}"
        }

        else -> {//为小数浮点数
            "${value}"
        }
    }
    return label
}

fun settingLabelValue2(value: Float): String {
    val time = System.currentTimeMillis()
    var valueStr = ""
    if (value > 0) {
        valueStr = TimeUtil.getStringByFormat(
            (time + value * 24 * 60 * 60 * 1000).toLong(), TimeUtil.dateFormatYMD
        ).toString()
    }
    return valueStr
}

@Composable
@Preview(showSystemUi = false, showBackground = true, widthDp = 1096, heightDp = 250)
fun BarChartPreview4() {
    MaterialTheme {
        Surface {
            var barData = BarData()
            barData = getTestBarData4()
            val listChunk = getTestChunkList()
            val limitLineList = getTestLimitLineList()
            val xLimitLineList = getTestXLimitLineList()
            BarChart(
                data = BarChartData(
                    barData = barData,

                    xAxis = Axis(
                        max = 3f,
                        scaleInterval = 1f,
                        labelInterval = 1f,
                        name = "",
                        settingLabelValue = ::settingLabelValue2
                    ),

                    yLeftAxis = Axis(
                        max = 300f,
                        scaleInterval = 10f,
                        labelInterval = 50f,
                        name = "",
                        settingLabelValue = ::settingLabelValue
                    ),
                )
            )
        }
    }
}

@Composable
@Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun BarChartPreview3() {
    MaterialTheme {
        Surface {
            var barData = BarData()
            barData = getTestBarData3()
            val listChunk = getTestChunkList()
            val limitLineList = getTestLimitLineList()
            val xLimitLineList = getTestXLimitLineList()
            BarChart(
                data = BarChartData(
                    barData = barData,

                    xAxis = Axis(
                        max = 2.9f,
                        scaleInterval = 1f,
                        labelInterval = 1f,
                        name = "",
                        settingLabelValue = ::settingLabelValue2
                    ),

                    yLeftAxis = Axis(
                        max = 300f,
                        scaleInterval = 10f,
                        labelInterval = 50f,
                        name = "",
                        settingLabelValue = ::settingLabelValue
                    ),
                )
            )
        }
    }
}

@Composable
@Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun BarChartPreview2() {
    MaterialTheme {
        Surface {
            var barData = BarData()
            barData = getTestBarData2()
            val listChunk = getTestChunkList()
            val limitLineList = getTestLimitLineList()
            val xLimitLineList = getTestXLimitLineList()
            BarChart(
                data = BarChartData(
                    barData = barData,
                    xAxis = Axis(
                        max = 5f,
                        name = "",
                    ),

                    yLeftAxis = Axis(
                        max = 300f,
                        scaleInterval = 10f,
                        labelInterval = 50f,
                        name = "",
                    ),

                    )
            )
        }
    }
}

@Composable
@Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun BarChartPreview1() {
    MaterialTheme {
        Surface {
            var barData = BarData()
            barData = getTestBarData()
            val listChunk = getTestChunkList()
            val limitLineList = getTestLimitLineList()
            val xLimitLineList = getTestXLimitLineList()
            BarChart(
                data = BarChartData(
                    barData = barData,
                    xAxis = Axis(
                        max = 5f,
                        scaleInterval = 10f,
                        labelInterval = 10f,
                        chunkList = listChunk,
                        name = "x轴",
                        limitLineList = xLimitLineList,
                        settingLabelValue = ::settingLabelValue
                    ),

                    yLeftAxis = Axis(
                        max = 300f,
                        scaleInterval = 10f,
                        labelInterval = 50f,
                        name = "y轴",
                        chunkList = listChunk,
                        limitLineList = xLimitLineList
                    ),
                )
            )
        }
    }
}

