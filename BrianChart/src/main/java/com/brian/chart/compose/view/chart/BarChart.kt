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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brian.view.chart.AxisPoints
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

            val yLeftScaleLengSize = getScaleLengSize(this, yLeftAxis)//左边刻度的长度
            val yRightScaleLengSize = getScaleLengSize(this, yLeftAxis)//右边刻度的长度
            val xBottomScaleLengSize = getScaleLengSize(this, xAxis)//低边刻度的长度
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
                this, xAxis = xAxis, yLeftAxis = yLeftAxis,axisPoints = axisPoints
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
            
            // 计算X轴在画布中的Y位置（作为柱状图的起点）
            val xAxisPositionValue = xAxis.position ?: 0f
            val xAxisYPosition = point0.y - (xAxisPositionValue - yLeftAxis.min) * oneDataYPx
            
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

                    val offsetX =
                        point0.x + (oneDataXPx * barEntry.x) - (oneDataXUsePx / 2) + (indexDataSet * oneBardataMaxWidth) + (oneBardataMaxWidth - barDataWidth) / 2
                    
                    // 检查是否为堆积图
                    if (barEntry.stackValues != null && barEntry.stackValues!!.isNotEmpty()) {
                        // 堆积图模式：在单个 Entry 内堆叠
                        drawStackedEntry(
                            barEntry = barEntry,
                            barDataSet = barDataSet,
                            offsetX = offsetX,
                            barDataWidth = barDataWidth,
                            xAxisYPosition = xAxisYPosition,
                            xAxisPositionValue = xAxisPositionValue,
                            oneDataYPx = oneDataYPx,
                            defaultPadding = defaultPadding
                        )
                    } else {
                        // 普通模式
                        drawSingleBar(
                            barEntry = barEntry,
                            barDataSet = barDataSet,
                            offsetX = offsetX,
                            barDataWidth = barDataWidth,
                            xAxisYPosition = xAxisYPosition,
                            xAxisPositionValue = xAxisPositionValue,
                            oneDataYPx = oneDataYPx,
                            defaultPadding = defaultPadding
                        )
                    }
                }
            }
        }
    }
}

/**
 * 绘制单个普通柱子
 */
private fun DrawScope.drawSingleBar(
    barEntry: BarEntry,
    barDataSet: BarDataSet,
    offsetX: Float,
    barDataWidth: Float,
    xAxisYPosition: Float,
    xAxisPositionValue: Float,
    oneDataYPx: Float,
    defaultPadding: Float
) {
    // 计算数据点相对于X轴的值
    val valueRelativeToXAxis = barEntry.y - xAxisPositionValue
    
    // 柱状图高度（取绝对值）
    val barDataHeight = abs(valueRelativeToXAxis) * oneDataYPx
    
    // 根据值相对于X轴的正负确定柱状图的起始Y位置
    val offsetY = if (valueRelativeToXAxis >= 0) {
        xAxisYPosition - barDataHeight // 正值：从X轴向上绘制
    } else {
        xAxisYPosition // 负值：从X轴向下绘制
    }

    val offset = Offset(x = offsetX, y = offsetY)
    val size = Size(width = barDataWidth, height = barDataHeight)

    drawBarContent(
        barEntry = barEntry,
        barDataSet = barDataSet,
        offset = offset,
        size = size,
        valueRelativeToXAxis = valueRelativeToXAxis,
        offsetX = offsetX,
        barDataWidth = barDataWidth,
        defaultPadding = defaultPadding,
        stackIndex = -1
    )
}

/**
 * 绘制堆积图的单个 Entry
 */
private fun DrawScope.drawStackedEntry(
    barEntry: BarEntry,
    barDataSet: BarDataSet,
    offsetX: Float,
    barDataWidth: Float,
    xAxisYPosition: Float,
    xAxisPositionValue: Float,
    oneDataYPx: Float,
    defaultPadding: Float
) {
    val stackValues = barEntry.stackValues!!
    var currentY = xAxisYPosition
    
    // 遍历堆积值数组，逐个绘制
    stackValues.forEachIndexed { stackIndex, stackValue ->
        val valueRelativeToXAxis = stackValue - xAxisPositionValue
        val barHeight = abs(valueRelativeToXAxis) * oneDataYPx
        
        // 确定柱子的 Y 起始位置
        val offsetY = if (valueRelativeToXAxis >= 0) {
            currentY - barHeight // 正值向上
        } else {
            currentY // 负值向下
        }
        
        val offset = Offset(x = offsetX, y = offsetY)
        val size = Size(width = barDataWidth, height = barHeight)
        
        // 从 BarDataSet 获取当前层的颜色（优先使用 stackColors，否则使用 BarDataSet.color）
        val layerColor = barDataSet.stackColors?.getOrNull(stackIndex) ?: barDataSet.color
        
        // 从 BarDataSet 获取当前层的背景绘制函数（优先使用 stackBackgrounds，否则使用 BarDataSet.background）
        val layerBackground = barDataSet.stackBackgrounds?.getOrNull(stackIndex) ?: barDataSet.background
        
        drawBarContentWithLayerStyle(
            barEntry = barEntry,
            barDataSet = barDataSet,
            offset = offset,
            size = size,
            valueRelativeToXAxis = valueRelativeToXAxis,
            offsetX = offsetX,
            barDataWidth = barDataWidth,
            defaultPadding = defaultPadding,
            stackIndex = stackIndex,
            layerColor = layerColor,
            layerBackground = layerBackground
        )
        
        // 更新下一个段的起始位置
        currentY = if (valueRelativeToXAxis >= 0) {
            offsetY // 正值的顶部
        } else {
            offsetY + barHeight // 负值的底部
        }
    }
}

/**
 * 获取堆积图中指定索引的值
 */
private fun stackValue(barEntry: BarEntry, stackIndex: Int): Float {
    return if (stackIndex >= 0 && barEntry.stackValues != null && stackIndex < barEntry.stackValues!!.size) {
        barEntry.stackValues!![stackIndex]
    } else {
        barEntry.y
    }
}

/**
 * 绘制柱子内容和数值（支持每层自定义样式）
 */
private fun DrawScope.drawBarContentWithLayerStyle(
    barEntry: BarEntry,
    barDataSet: BarDataSet,
    offset: Offset,
    size: Size,
    valueRelativeToXAxis: Float,
    offsetX: Float,
    barDataWidth: Float,
    defaultPadding: Float,
    stackIndex: Int,
    layerColor: Color,
    layerBackground: ((drawScope: DrawScope, color: Color, offset: Offset, size: Size) -> Unit)?
) {
    // 绘制柱状形状
    if (barEntry.renderer != null) {
        // 使用 BarEntry 级别的自定义渲染器
        barEntry.renderer?.invoke(this, layerColor, offset, size, stackValue(barEntry, stackIndex), barDataSet.name, valueRelativeToXAxis, stackIndex)
    } else {
        // 使用默认背景或 BarDataSet.background
        val backgroundToUse = layerBackground ?: barDataSet.background
        
        if (backgroundToUse == null) {
            drawRoundRect(
                color = layerColor,
                topLeft = offset,
                size = size,
                style = Stroke(width = 1.dp.toPx(), cap = StrokeCap.Butt),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
        } else {
            backgroundToUse.invoke(this, layerColor, offset, size)
        }
        
        // 绘制数值
        if (barDataSet.showValue) {
            val valueTextSizePx = barDataSet.valueTextSize.toPx()
            
            // 确定数值文字颜色：堆积图优先使用 stackValueColors，否则使用 valueColor 或 color；非堆积图使用 valueColor 或 color
            val textColor = if (stackIndex >= 0) {
                barDataSet.stackValueColors?.getOrNull(stackIndex) ?: barDataSet.valueColor ?: barDataSet.color
            } else {
                barDataSet.valueColor ?: barDataSet.color
            }
            
            val nativePaint = android.graphics.Paint().let {
                it.apply {
                    textSize = valueTextSizePx
                    color = textColor.toArgb()
                    isAntiAlias = true
                }
            }
            val label = barDataSet.settingValueText?.let { it(barDataSet.name, valueRelativeToXAxis) }
                ?: "${valueRelativeToXAxis}"
            val labelWidth = label.length * valueTextSizePx
            val offsetText = labelWidth / 2

            val x = offsetX + barDataWidth / 2 - offsetText / 2 + defaultPadding / 4

            // 如果是堆积图，文字显示在柱子内部；否则显示在外部
            var y = if (stackIndex >= 0) {
                // 堆积图：文字显示在当前段的中心位置
                offset.y + size.height / 2 + valueTextSizePx / 3
            } else {
                // 非堆积图：根据值相对于X轴的正负调整数值文本的Y位置
                if (valueRelativeToXAxis >= 0) {
                    // 正值：显示在柱子顶部上方
                    offset.y - valueTextSizePx - 2.dp.toPx()
                } else {
                    // 负值：显示在柱子底部下方
                    offset.y + size.height + valueTextSizePx + 2.dp.toPx()
                }
            }
            
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

/**
 * 绘制柱子内容和数值
 */
private fun DrawScope.drawBarContent(
    barEntry: BarEntry,
    barDataSet: BarDataSet,
    offset: Offset,
    size: Size,
    valueRelativeToXAxis: Float,
    offsetX: Float,
    barDataWidth: Float,
    defaultPadding: Float,
    stackIndex: Int
) {
    // 绘制柱状形状
    if (barEntry.renderer != null) {
        // 使用 BarEntry 级别的自定义渲染器
        barEntry.renderer?.invoke(this, barDataSet.color, offset, size, stackValue(barEntry, stackIndex), barDataSet.name, valueRelativeToXAxis, stackIndex)
    } else {
        // 使用 BarDataSet 的背景配置或默认背景
        if (barDataSet.background == null) {
            drawRoundRect(
                color = barDataSet.color,
                topLeft = offset,
                size = size,
                style = Stroke(width = 1.dp.toPx(), cap = StrokeCap.Butt),
                cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
            )
        } else {
            barDataSet.background?.invoke(this, barDataSet.color, offset, size)
        }
        
        // 绘制数值
        if (barDataSet.showValue) {
            val valueTextSizePx = barDataSet.valueTextSize.toPx()
            // valueColor 为空时使用 color（柱子颜色）
            val textColor = barDataSet.valueColor ?: barDataSet.color
            val nativePaint = android.graphics.Paint().let {
                it.apply {
                    textSize = valueTextSizePx
                    color = textColor.toArgb()
                    isAntiAlias = true
                }
            }
            val label = barDataSet.settingValueText?.let { it(barDataSet.name, valueRelativeToXAxis) }
                ?: "${valueRelativeToXAxis}"
            val labelWidth = label.length * valueTextSizePx
            val offsetText = labelWidth / 2

            val x = offsetX + barDataWidth / 2 - offsetText / 2 + defaultPadding / 4

            // 根据值相对于X轴的正负调整数值文本的Y位置
            var y = if (valueRelativeToXAxis >= 0) {
                // 正值：显示在柱子顶部上方
                offset.y - valueTextSizePx - 2.dp.toPx()
            } else {
                // 负值：显示在柱子底部下方
                offset.y + size.height + valueTextSizePx + 2.dp.toPx()
            }
            
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
    var barDataSetList: MutableList<BarDataSet>? = null, 
    var groupPadding: Float = 10f,//组之间间隔
    var onGroupPadding: Float = 0f,//组内间隔
    var width: Dp? = null, 
    var weight: Float = 0.8f, //一个单位可用的宽度 比例
    var dataSetPadding: Dp = 2.dp
)

data class BarDataSet(
    var barEntryList: MutableList<BarEntry>? = null,
    var color: Color = Color.Blue,
    var background: ((drawScope: DrawScope, color: Color, offset: Offset, size: Size) -> Unit)? = null,//TODO 可自由定制
    var name: String = "",
    var valueTextSize: TextUnit = 8.sp,
    var valueColor: Color? = null, // 数值文字颜色，null 时使用 color
    var settingValueText: ((name: String, value: Float) -> String)? = null,//定制顶部的值显示
    var showValue: Boolean = true, //是否显示value数据
    /**
     * 堆积图中每一层的颜色数组（可选）
     * 如果设置了，将覆盖 BarDataSet 的 color
     * 长度应与 stackValues 一致
     */
    var stackColors: List<Color>? = null,
    /**
     * 堆积图中每一层的背景绘制函数数组（可选）
     * 如果设置了，将覆盖 BarDataSet 的 background
     * 长度应与 stackValues 一致
     */
    var stackBackgrounds: List<((drawScope: DrawScope, color: Color, offset: Offset, size: Size) -> Unit)>? = null,
    /**
     * 堆积图中每一层的数值文字颜色数组（可选）
     * 如果设置了，将覆盖 BarDataSet 的 valueColor
     * 长度应与 stackValues 一致
     */
    var stackValueColors: List<Color>? = null
)

data class BarEntry(
    val x: Float,
    val y: Float,
    
    /** 
     * 堆积图的值数组。如果设置了此参数，则该 Entry 为堆积模式
     * 例如：listOf(50f, 30f, 20f) 表示三个堆叠段
     */
    val stackValues: List<Float>? = null,
    
    /** 
     * 自定义渲染器，同时负责柱状图和数值的绘制
     * 如果设置了此参数，将完全接管该数据点的绘制逻辑（包括柱子和数值）
     * 参数说明：
     * - drawScope: 绘制作用域
     * - color: 数据集颜色或当前层的颜色
     * - offset: 柱子左上角坐标
     * - size: 柱子尺寸
     * - value: 数据值
     * - name: 数据集名称
     * - valueRelativeToXAxis: 相对于X轴的值（用于判断正负）
     * - stackIndex: 当前堆积段的索引（如果是堆积图），非堆积图为 -1
     */
    val renderer: ((drawScope: DrawScope, color: Color, offset: Offset, size: Size, value: Float, name: String, valueRelativeToXAxis: Float, stackIndex: Int) -> Unit)? = null
)

object NameAglin {
    const val TOP = "TOP"
    const val BOTTOM = "BOTTOM"
}
