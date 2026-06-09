package com.brian.chart.compose.view.chart

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.ui.input.pointer.*
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import com.brian.view.chart.AxisPadding
import com.brian.view.chart.AxisPoints
import java.math.BigDecimal

private val TAG = "LineChart"

/**
 * @author Brian
 * @Description: 线性 图表
 * TODO : 优化缩放、添加滑动看数据
 */

@SuppressLint("UnrememberedMutableState")
@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    data: LineChartData? = null,
    // 注意: 不再通过 dynamicLimitLines 合并限制线，调用方应直接更新 data.xAxis.limitLineList
) {
    //用来记录缩放大小
    var scale by remember { mutableStateOf(1f) }//缩放
    var rotation by remember { mutableStateOf(0f) } //旋转
    var offset by remember { mutableStateOf(Offset.Zero) }//移动
// 提取字段，高效且自动跳过无效重组
    val lineList by derivedStateOf { data?.lineList }
    val xAxisRaw by derivedStateOf { data?.xAxis ?: Axis() }
    val yLeftInsideAxis by derivedStateOf { data?.yLeftInsideAxis }
    val yLeftAxis by derivedStateOf { data?.yLeftAxis }
    val yRightAxis by derivedStateOf { data?.yRightAxis }
    val isSelfAdaptation by derivedStateOf { data?.isSelfAdaptation == true }
    val isScroll by derivedStateOf { data?.isScroll }
    val axisPadding by derivedStateOf { data?.axisPadding }
    val limitLinePosition by derivedStateOf { data?.limitLinePosition }
    val onTouch = data?.onTouch


    // 使用来自 data 的 xAxis（调用方负责就地更新 xAxis.limitLineList 来避免重建）
    val xAxis by derivedStateOf { xAxisRaw }

    remember(lineList, xAxis, yLeftAxis, yLeftInsideAxis, yRightAxis, isSelfAdaptation) {
        if (isSelfAdaptation) {
            selfAdaptation(xAxis, yLeftAxis, yLeftInsideAxis, yRightAxis, lineList)

        }
        ""
    }

    // 使用 BoxWithConstraints 获取实际尺寸
    BoxWithConstraints(modifier = modifier) {
        val textMeasurer: TextMeasurer = rememberTextMeasurer()
        val currentDensity = LocalDensity.current

        // 将约束转换为实际像素尺寸
        val canvasSize by remember {
            derivedStateOf {
                with(currentDensity) {
                    IntSize(
                        width = maxWidth.roundToPx(),
                        height = maxHeight.roundToPx()
                    )
                }
            }
        }

        // 在 drawWithCache 外部计算 axisPoints
        val axisPoints by remember(
            canvasSize,
            axisPadding,
            xAxis,
            yLeftAxis,
            yLeftInsideAxis,
            yRightAxis
        ) {


            // 在这里捕获 textMeasurer 和 currentDensity
            val measurer = textMeasurer
            val density = currentDensity

            derivedStateOf {
                Log.i(TAG, "remember getAxisPoints")
                getAxisPoints(
                    xAxis,
                    yLeftAxis,
                    yLeftInsideAxis,
                    yRightAxis,
                    axisPadding = axisPadding,
                    density = density,
                    textMeasurer = measurer,
                    size = canvasSize
                )
            }
        }

        var previousListCurvePathOrPoints by remember {
            mutableStateOf<Map<AxisType, MutableList<PathAndPoints>>>(emptyMap())
        }
        // 在 drawWithCache 外部计算曲线路径
        val listCurvePathOrPoints by remember(
            lineList,
            axisPoints,
            scale,
            xAxis.max,
            xAxis.min,
            xAxis.position,
            yLeftInsideAxis?.max,
            yLeftInsideAxis?.min,
            yLeftAxis?.max,
            yLeftAxis?.min,
            yRightAxis?.max,
            yRightAxis?.min
        ) {
            derivedStateOf {
                mutableMapOf<AxisType, MutableList<PathAndPoints>>().apply {
                    lineList?.let { lines ->
                        yLeftInsideAxis?.let { yAxis ->
                            val lineListNew = lines.filter { it.axisType == AxisType.LEFT_INSIDE }
                            if (lineListNew.isNotEmpty()) {
                                createCurvePathOrPoints(
                                    lineList = lineListNew.toMutableList(),
                                    axisPoints = axisPoints,
                                    xAxisMax = xAxis.max,
                                    xAxisMin = xAxis.min,
                                    yAxisMax = yAxis.max,
                                    yAxisMin = yAxis.min,
                                    scale = scale,
                                    xAxisPosition = xAxis.position ?: 0f,
                                    pathAndPointsList = previousListCurvePathOrPoints.get(AxisType.LEFT_INSIDE)
                                ).let {
                                    put(AxisType.LEFT_INSIDE, it)
                                }
                            }
                        }

                        yLeftAxis?.let { yAxis ->
                            val lineListNew = lines.filter { it.axisType == AxisType.LEFT }
                            if (lineListNew.isNotEmpty()) {
                                createCurvePathOrPoints(
                                    lineList = lineListNew.toMutableList(),
                                    axisPoints = axisPoints,
                                    xAxisMin = xAxis.min,
                                    xAxisMax = xAxis.max,
                                    yAxisMin = yAxis.min,
                                    yAxisMax = yAxis.max,
                                    scale = scale,
                                    xAxisPosition = xAxis.position ?: 0f,
                                    pathAndPointsList = previousListCurvePathOrPoints.get(AxisType.LEFT)
                                ).let {
                                    put(AxisType.LEFT, it)
                                }
                            }
                        }

                        yRightAxis?.let { yAxis ->
                            val lineListNew = lines.filter { it.axisType == AxisType.RIGHT }
                            if (lineListNew.isNotEmpty()) {
                                createCurvePathOrPoints(
                                    lineList = lineListNew.toMutableList(),
                                    axisPoints = axisPoints,
                                    xAxisMin = xAxis.min,
                                    xAxisMax = xAxis.max,
                                    yAxisMin = yAxis.min,
                                    yAxisMax = yAxis.max,
                                    scale = scale,
                                    xAxisPosition = xAxis.position ?: 0f,
                                    pathAndPointsList = previousListCurvePathOrPoints.get(AxisType.RIGHT)
                                ).let {
                                    put(AxisType.RIGHT, it)
                                }
                            }
                        }
                    }
                }
            }

        }

        previousListCurvePathOrPoints = listCurvePathOrPoints
        val transformModifier = if (isScroll == true) {
            val state = rememberTransformableState { zoomChange, panChange, rotationChange ->
                scale = scale * zoomChange
            }
            //监听手势缩放
            Modifier
                .graphicsLayer()
                .transformable(state)
        } else {
            Modifier
        }

        // 触摸处理 Modifier
        val touchModifier = if (onTouch != null) {
            Modifier.pointerInput(
                axisPoints,
                scale,
                xAxis.min,
                xAxis.max,
                yLeftInsideAxis?.min,
                yLeftInsideAxis?.max,
                yLeftAxis?.min,
                yLeftAxis?.max,
                yRightAxis?.min,
                yRightAxis?.max,
                lineList
            ) {
                // 支持拖动（MOVE）与按下/抬起事件（DOWN/UP/TAP）的通用处理
                forEachGesture {
                    awaitPointerEventScope {
                        val down = awaitFirstDown()
                        // 处理 DOWN 事件
                        val downDataX = convertPixelToDataX(
                            pixelX = down.position.x,
                            axisPoints = axisPoints,
                            xAxisMin = xAxis.min,
                            xAxisMax = xAxis.max,
                            scale = scale
                        )
                        val (downDataYLeftInside, downDataYLeft, downDataYRight) = convertPixelToAllDataY(
                            pixelY = down.position.y,
                            axisPoints = axisPoints,
                            yLeftInsideAxis = yLeftInsideAxis,
                            yLeftAxis = yLeftAxis,
                            yRightAxis = yRightAxis
                        )
                        val downDataY = downDataYLeftInside ?: downDataYLeft ?: downDataYRight ?: 0f
                        val downNearest = findNearestDataPoint(
                            touchX = down.position.x,
                            touchY = down.position.y,
                            lineList = lineList ?: emptyList(),
                            axisPoints = axisPoints,
                            xAxisMin = xAxis.min,
                            xAxisMax = xAxis.max,
                            yAxisMin = yLeftInsideAxis?.min ?: yLeftAxis?.min ?: yRightAxis?.min
                            ?: 0f,
                            yAxisMax = yLeftInsideAxis?.max ?: yLeftAxis?.max ?: yRightAxis?.max
                            ?: 0f,
                            scale = scale
                        )
                        Log.d(
                            TAG,
                            "onTouch DOWN dataX=$downDataX dataY=$downDataY pixel=(${down.position.x},${down.position.y})"
                        )
                        onTouch.invoke(
                            TouchEventData(
                                dataX = downDataX,
                                dataY = downDataY,
                                pixelX = down.position.x,
                                pixelY = down.position.y,
                                nearestPoint = downNearest,
                                dataYLeftInside = downDataYLeftInside,
                                dataYLeft = downDataYLeft,
                                dataYRight = downDataYRight,
                                eventType = TouchEventType.DOWN
                            )
                        )

                        var moved = false

                        // 缓存常用值，避免在高频 MOVE 中重复计算
                        val oneDataXPx =
                            (axisPoints.point1.x - axisPoints.point0.x) / (xAxis.max - xAxis.min)
                        // 选择一个主要的 Y 轴用于快速 dataY 计算（优先左内轴）
                        val primaryYAxis = yLeftInsideAxis ?: yLeftAxis ?: yRightAxis
                        val oneDataYPx =
                            primaryYAxis?.let { (axisPoints.point0.y - axisPoints.point3.y) / (it.max - it.min) }
                        val offsetXPx = xAxis.min * oneDataXPx
                        val offsetYPx = primaryYAxis?.let { it.min * (oneDataYPx ?: 0f) } ?: 0f

                        // 最后发送的像素位置（用于节流）
                        var lastSentX = down.position.x
                        var lastSentY = down.position.y
                        val touchSlop = 4f // 像素阈值，减少过多事件

                        // 监听后续事件（move / up）
                        while (true) {
                            val event = awaitPointerEvent()
                            val change = event.changes.firstOrNull() ?: continue

                            if (change.changedToUp()) {
                                // 抬起事件 - UP 或 TAP（如果没有移动则视为 TAP）
                                val upPos = change.position
                                // 最终位置的 dataX/dataY 直接用公式计算，成本小
                                // 使用统一的转换函数（包含边界裁剪）以保证 dataX/dataY 在 axis.min..axis.max 之内
                                val upDataX = convertPixelToDataX(
                                    pixelX = upPos.x,
                                    axisPoints = axisPoints,
                                    xAxisMin = xAxis.min,
                                    xAxisMax = xAxis.max,
                                    scale = scale
                                )
                                val upDataY = convertPixelToDataY(
                                    pixelY = upPos.y,
                                    axisPoints = axisPoints,
                                    yLeftInsideAxis = yLeftInsideAxis,
                                    yLeftAxis = yLeftAxis,
                                    yRightAxis = yRightAxis
                                )

                                // 对于最终 nearestPoint，我们需要更准确的查找（较耗时），但只在 UP/TAP 时执行
                                val upNearest = findNearestDataPoint(
                                    touchX = upPos.x,
                                    touchY = upPos.y,
                                    lineList = lineList ?: emptyList(),
                                    axisPoints = axisPoints,
                                    xAxisMin = xAxis.min,
                                    xAxisMax = xAxis.max,
                                    yAxisMin = yLeftInsideAxis?.min ?: yLeftAxis?.min
                                    ?: yRightAxis?.min ?: 0f,
                                    yAxisMax = yLeftInsideAxis?.max ?: yLeftAxis?.max
                                    ?: yRightAxis?.max ?: 0f,
                                    scale = scale
                                )

                                Log.d(
                                    TAG,
                                    "onTouch UP dataX=$upDataX dataY=$upDataY pixel=(${upPos.x},${upPos.y}) eventType=${if (moved) TouchEventType.UP else TouchEventType.TAP}"
                                )
                                onTouch.invoke(
                                    TouchEventData(
                                        dataX = upDataX,
                                        dataY = upDataY,
                                        pixelX = upPos.x,
                                        pixelY = upPos.y,
                                        nearestPoint = upNearest,
                                        dataYLeftInside = null,
                                        dataYLeft = null,
                                        dataYRight = null,
                                        eventType = if (moved) TouchEventType.UP else TouchEventType.TAP
                                    )
                                )

                                // 标记为已消费位移变化，仅消费位置变化以便系统还能处理其他手势
                                event.changes.forEach { it.consume() }
                                break
                            }

                            if (change.positionChanged()) {
                                val mvPos = change.position
                                val dx = mvPos.x - lastSentX
                                val dy = mvPos.y - lastSentY
                                // 只有超过阈值时才更新 nearestPoint（节流），但仍发送位置更新以保持流畅
                                val distanceSq = dx * dx + dy * dy
                                val shouldComputeNearest = distanceSq >= touchSlop * touchSlop
                                moved = true

                                // 使用统一的转换函数以保持一致性并裁剪到轴范围内
                                val mvDataX = convertPixelToDataX(
                                    pixelX = mvPos.x,
                                    axisPoints = axisPoints,
                                    xAxisMin = xAxis.min,
                                    xAxisMax = xAxis.max,
                                    scale = scale
                                )
                                val mvDataY = convertPixelToDataY(
                                    pixelY = mvPos.y,
                                    axisPoints = axisPoints,
                                    yLeftInsideAxis = yLeftInsideAxis,
                                    yLeftAxis = yLeftAxis,
                                    yRightAxis = yRightAxis
                                )

                                val mvNearest = if (shouldComputeNearest) {
                                    lastSentX = mvPos.x
                                    lastSentY = mvPos.y
                                    findNearestDataPoint(
                                        touchX = mvPos.x,
                                        touchY = mvPos.y,
                                        lineList = lineList ?: emptyList(),
                                        axisPoints = axisPoints,
                                        xAxisMin = xAxis.min,
                                        xAxisMax = xAxis.max,
                                        yAxisMin = yLeftInsideAxis?.min ?: yLeftAxis?.min
                                        ?: yRightAxis?.min ?: 0f,
                                        yAxisMax = yLeftInsideAxis?.max ?: yLeftAxis?.max
                                        ?: yRightAxis?.max ?: 0f,
                                        scale = scale
                                    )
                                } else null

                                Log.d(
                                    TAG,
                                    "onTouch MOVE dataX=$mvDataX dataY=$mvDataY pixel=(${mvPos.x},${mvPos.y}) nearest=${mvNearest != null}"
                                )
                                onTouch.invoke(
                                    TouchEventData(
                                        dataX = mvDataX,
                                        dataY = mvDataY,
                                        pixelX = mvPos.x,
                                        pixelY = mvPos.y,
                                        nearestPoint = mvNearest,
                                        dataYLeftInside = null,
                                        dataYLeft = null,
                                        dataYRight = null,
                                        eventType = TouchEventType.MOVE
                                    )
                                )

                                // 只消费位置变化，让系统继续处理其他必要事件
                                change.consume()
                            }
                        }
                    }
                }
            }
        } else {
            Modifier
        }


        Canvas(
            modifier = transformModifier
                .then(touchModifier)
                .fillMaxSize()
                .drawWithCache {
                    onDrawBehind {
                        clipRect {
                            /**画Gride 网格*/
                            drawGrideLine(
                                this,
                                xAxis,
                                yLeftInsideAxis,
                                yLeftAxis,
                                yRightAxis,
                                axisPoints = axisPoints,
                                scale = scale
                            )
                            /**画chunk 块内容*/
                            drawChunk(
                                this,
                                xAxis,
                                yLeftInsideAxis,
                                yLeftAxis,
                                yRightAxis,
                                axisPoints = axisPoints,
                            )
                            /**画xy轴*/
                            drawXYAxis(
                                this,
                                xAxis,
                                yLeftInsideAxis,
                                yLeftAxis,
                                yRightAxis,
                                axisPoints = axisPoints,
                            )
                            /**刻度 label*/
                            drawLable(
                                this,
                                xAxis,
                                yLeftInsideAxis,
                                yLeftAxis,
                                yRightAxis,
                                axisPoints = axisPoints,
                                scale = scale
                            )

                            /**坐标轴名称*/
                            drawAxisName(
                                this,
                                xAxis,
                                yLeftInsideAxis,
                                yLeftAxis,
                                yRightAxis,
                                axisPoints = axisPoints,
                                scale = scale
                            )

                            val isLimitLineBelow = limitLinePosition == LimitLinePosition.BELOW

                            /**先画限制线（如果它在下面）*/
                            if (isLimitLineBelow) {
                                drawLimitLine(
                                    this,
                                    xAxis,
                                    yLeftInsideAxis,
                                    yLeftAxis,
                                    yRightAxis,
                                    axisPoints,
                                    scale
                                )
                            }
                            /**画曲线/散点*/
                            drawCurveSplashes(this, listCurvePathOrPoints)
                            /**后画限制线（如果它不在下面，即在上面）*/
                            if (!isLimitLineBelow) {
                                drawLimitLine(
                                    this,
                                    xAxis,
                                    yLeftInsideAxis,
                                    yLeftAxis,
                                    yRightAxis,
                                    axisPoints,
                                    scale
                                )
                            }

                        }
                    }
                }
        ) {
            // Canvas content
        }

    }
}


/**
 *@author Brian
 *@Description: 调整xy轴以适应实际的数据
 */
private fun selfAdaptation(
    xAxis: Axis,
    yLeftAxis: Axis?,
    yLeftInsideAxis: Axis?,
    yRightAxis: Axis?,
    lineList: List<Line>?
) {
    reSetXMax(xAxis, lineList)
    reSetXMin(xAxis, lineList)
    if (yLeftAxis != null) {
        val lineListNew = lineList?.filter { it.axisType == AxisType.LEFT }
        reSetYMax(yLeftAxis, lineListNew)
        reSetYMin(yLeftAxis, lineListNew)
    }
    if (yLeftInsideAxis != null) {
        val lineListNew = lineList?.filter { it.axisType == AxisType.LEFT_INSIDE }
        reSetYMax(yLeftInsideAxis, lineListNew)
        reSetYMin(yLeftInsideAxis, lineListNew)
    }
    if (yRightAxis != null) {
        val lineListNew = lineList?.filter { it.axisType == AxisType.RIGHT }
        reSetYMax(yRightAxis, lineListNew)
        reSetYMin(yRightAxis, lineListNew)
    }
}

/**
 *@author Brian
 *@Description: 调整xy轴以适应实际的数据
 */
private fun getAxisPoints(
    xAxis: Axis,
    yLeftAxis: Axis?,
    yLeftInsideAxis: Axis?,
    yRightAxis: Axis?,
    axisPadding: AxisPadding?,
    density: Density,
    textMeasurer: TextMeasurer,
    size: IntSize,
): AxisPoints {

    var startPx: Float? = null
    var endPx: Float? = null
    var topPx: Float? = null
    var bottomPx: Float? = null
    axisPadding?.apply {
        with(density) {
            startPx = start?.toPx()
            endPx = end?.toPx()
            topPx = top?.toPx()
            bottomPx = bottom?.toPx()
        }
    }
    var lablePaddingLeft =
        startPx ?: getYAxisPaddingLeft(
            yLeftAxis,
            xAxis,
            textMeasurer,
            density
        )
    var lablePaddingRight =
        endPx ?: getAxisPaddingRight(
            yRightAxis,
            xAxis,
            textMeasurer,
            density
        )
    var lablePaddingTop =
        topPx ?: getXAxisPaddingTop(
            yLeftAxis,
            yRightAxis,
            textMeasurer,
            density
        )
    var lablePaddingBootom =
        bottomPx ?: getXAxisPaddingBottom(xAxis, textMeasurer, density)

    /* Log.d(
         "LineChart", """padding:
                         |lablePaddingLeft = ${lablePaddingLeft}
                         |lablePaddingRight = ${lablePaddingRight}
                         |lablePaddingTop = ${lablePaddingTop}
                         |lablePaddingBootom = ${lablePaddingBootom}
                         |""".trimMargin()
     )*/
    //确定四个绘图点 (去除标签、刻度线、padding占据的位置)
    val point0 = Point(
        0f + lablePaddingLeft,
        size.height - lablePaddingBootom
    ) //左下角（原点）

    val point1 = Point(


        size.width - lablePaddingRight,
        size.height - lablePaddingBootom
    )//右下角点

    val point2 = Point(
        size.width - lablePaddingRight, 0f + lablePaddingTop
    )//右上角点

    val point3 = Point(
        0f + lablePaddingLeft, 0f + lablePaddingTop
    )//左上角点


    return AxisPoints(point0, point1, point2, point3)
}

/**
 *@author Brian
 *@Description:绘制曲线或散点
 */
fun drawCurveSplashes(
    drawScope: DrawScope,
    listCurvePathOrPoints: Map<AxisType, MutableList<PathAndPoints>>,
) {
    listCurvePathOrPoints.forEach {
        it.value.let {
            drawCurveSplashes(drawScope, it)
        }
    }
}

/**
 *@author Brian
 *@Description:绘制曲线或散点
 */
fun drawCurveSplashes(
    drawScope: DrawScope,
    pathAndPointsList: MutableList<PathAndPoints>,
) {
    drawScope.run {

        pathAndPointsList.forEach {
            it.line?.let { line ->

                val color = line.color

                if (line.isPoints) {
                    it.offsetList?.let { points ->
                        drawPoints(
                            points = points,
                            pointMode = PointMode.Points,
                            color = color,
                            strokeWidth = line.width.toPx()
                        )
                    }
                }


                if (line.isDrawPath) {

                    it.path?.let { path ->
                        drawPathWithDashEffect(
                            path = path,
                            color = color,
                            lineWidth = line.width,
                            isDashes = line.isDashes,
                            pathEffect = line.pathEffect
                        )

                    }


                }

                if (line.isFill) {
                    //绘制面
                    it.path?.let { path ->
                        //绘制填充
                        line.drawAreaBrush?.let { brush ->
                            drawPath(
                                path = path,
                                brush = brush,
                                style = Fill,
                            )

                        } ?: drawPath(
                            path = path,
                            color = color,
                            style = Fill,
                        )

                    }

                }

                if (line.isDrawArea) {
                    it.areaPath?.let { path ->
                        //绘制填充
                        line.drawAreaBrush?.let { brush ->
                            drawPath(
                                path = path,
                                brush = brush,
                                style = Fill,
                            )

                        } ?: drawPath(
                            path = path,
                            color = color,
                            style = Fill,
                        )
                    }

                }



                line.renderer?.invoke(drawScope, it.line, it.offsetList)
            }
        }


    }

}

/**
 * 绘制带虚线效果的路径
 */
private fun DrawScope.drawPathWithDashEffect(
    path: Path,
    color: Color,
    lineWidth: Dp,
    isDashes: Boolean,
    pathEffect: PathEffect?
) {
    val dashPathEffect = if (isDashes) {
        //启用虚线，则绘制虚线样式。
        //若有自定义的虚线样式，则使用自定义样式；无，则使用默认样式
        pathEffect ?: PathEffect.dashPathEffect(
            floatArrayOf(10f, 4f),
            4f
        )
    } else {
        null
    }
    drawPath(
        path = path,
        color = color,
        style = Stroke(
            width = lineWidth.toPx(),
            pathEffect = dashPathEffect
        )
    )
}

fun createCurvePathOrPoints(
    lineList: MutableList<Line>,
    axisPoints: AxisPoints,
    xAxisMin: Float,
    xAxisMax: Float,
    yAxisMin: Float,
    yAxisMax: Float,
    scale: Float,
    xAxisPosition: Float = 0f,
    pathAndPointsList: MutableList<PathAndPoints>? = null,

    ): MutableList<PathAndPoints> {

    val oneDataXPx =
        (axisPoints.point1.x - axisPoints.point0.x) / (xAxisMax - xAxisMin) // X轴上 1f单位数据点对应的px数
    val oneDataYPx =
        (axisPoints.point0.y - axisPoints.point3.y) / (yAxisMax - yAxisMin) // X轴上 1f单位数据点对应的px数
    val offsetXPx = xAxisMin * oneDataXPx
    val offsetYPx = yAxisMin * oneDataYPx
    val tmpPathAndPointsList = pathAndPointsList ?: mutableListOf<PathAndPoints>()

    if (tmpPathAndPointsList.size > lineList.size) {
        //清理的过时缓存数据
        tmpPathAndPointsList.clear()
    }


    lineList.forEach { line ->
        var pathAndPoints = tmpPathAndPointsList.find { it.line?.code == line.code }
        if (pathAndPoints == null) {
            pathAndPoints = PathAndPoints(line = line)
            tmpPathAndPointsList.add(pathAndPoints)
        }

//        val pathAndPoints = PathAndPoints(line = line)
//        pathAndPointsList.add(pathAndPoints)
//        val pointList = if (line.isDrawArea) {
//            buildList {
//                line.pointList.firstOrNull()?.let { add(Point(it.x, xAxisPosition)) }
//                addAll(line.pointList)
//                line.pointList.lastOrNull()?.let { add(Point(it.x, xAxisPosition)) }
//            }
//        } else {
//            line.pointList
//        }

        if (line.isPoints || line.renderer != null) { //散点
            pathAndPoints.offsetList =
                getPoints(
                    line.pointList,
                    axisPoints.point0.x,
                    axisPoints.point0.y,
                    oneDataXPx,
                    oneDataYPx,
                    offsetXPx,
                    offsetYPx,
                    scale
                )
        }

        if (line.isDrawPath) {//曲线

            pathAndPoints.path =   createPath(
                line.pointList,
                axisPoints.point0.x,
                axisPoints.point0.y,
                oneDataXPx,
                oneDataYPx,
                offsetXPx,
                offsetYPx,
                scale,
                pathCache = pathAndPoints.path,
                isDrawCubic = line.isDrawCubic,
            )

            if (line.isDrawArea) {
                val pointList = buildList {
                    line.pointList.firstOrNull()?.let { add(Point(it.x, xAxisPosition)) }
                    addAll(line.pointList)
                    line.pointList.lastOrNull()?.let { add(Point(it.x, xAxisPosition)) }
                }
                pathAndPoints.areaPath =   createPath(
                    pointList,
                    axisPoints.point0.x,
                    axisPoints.point0.y,
                    oneDataXPx,
                    oneDataYPx,
                    offsetXPx,
                    offsetYPx,
                    scale,
                    pathCache = pathAndPoints.areaPath,
                    isDrawCubic = line.isDrawCubic,
                    isDrawArea = line.isDrawArea
                )
            }
        }
//        pathAndPointsList.add(pathAndPoints)

    }

    val currentPathAndPointsList = if (tmpPathAndPointsList.size > lineList.size) {
        val newPathAndPointsList = mutableListOf<PathAndPoints>()
        //清理多余的line
        tmpPathAndPointsList.forEach { pathAndPonits ->
            var line = lineList.find { it?.code == pathAndPonits.line?.code }
            if (line != null) {
                newPathAndPointsList.add(pathAndPonits)
            }
        }
        newPathAndPointsList
    } else {
        tmpPathAndPointsList
    }
    return currentPathAndPointsList

}

fun createPath(
    pointList: List<Point>,
    x: Float,
    y: Float,
    oneDataXPx: Float,
    oneDataYPx: Float,
    offsetXPx: Float,
    offsetYPx: Float,
    scale: Float,
    pathCache: Path?,
    isDrawCubic: Boolean = false,
    isDrawArea: Boolean = false
): Path {
    return if (isDrawCubic) {

        //平滑
        getCubicPathCatmullRom(
            pointList,
            x,
            y,
            oneDataXPx,
            oneDataYPx,
            offsetXPx,
            offsetYPx,
            scale,
            pathCache = pathCache,
            excludeHeadAndTrail = if (isDrawArea == true) 2 else 1
        )
    } else {
        //折线
        getPath(
            pointList,
            x,
            y,
            oneDataXPx,
            oneDataYPx,
            offsetXPx,
            offsetYPx,
            scale,
            pathCache = pathCache
        )
    }

}

fun reSetXMax(
    axis: Axis,
    lineList: List<Line>?,

    ) {
    //动态调整最大值
    val maxListData =
        lineList?.maxOfOrNull { it.pointList.maxByOrNull { it.x }?.x ?: 0f } ?: axis.max
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

fun reSetXMin(
    axis: Axis,
    lineList: List<Line>?,

    ) {
    //动态调整最小值
    val minListData =
        lineList?.minOfOrNull { it.pointList.minByOrNull { it.x }?.x ?: 0f } ?: axis.min
    val minLimitLine = axis.limitLineList?.minByOrNull { it.value }?.value
    val minChunk = axis.chunkList?.minByOrNull { it.start }?.start
    var min = 0f
    mutableListOf(minListData, minLimitLine, minChunk).minByOrNull { it ?: 0f }?.let {
        min = it
    }

    val labelInterval = (axis.labelInterval ?: 1f)
    if (min < axis.min) {
        axis.min = ((min / labelInterval).toInt() - 1) * labelInterval
    }

}

fun reSetYMax(axis: Axis, lineList: List<Line>?) {
    //动态调整最大值
    val maxListData =
        lineList?.maxOfOrNull { it.pointList.maxByOrNull { it.y }?.y ?: 0f } ?: axis.max
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

fun reSetYMin(axis: Axis, lineList: List<Line>?) {
    //动态调整最大值
    val minListData =
        lineList?.minOfOrNull { it.pointList.minByOrNull { it.y }?.y ?: 0f } ?: axis.min
    val minLimitLine = axis.limitLineList?.minByOrNull { it.value }?.value
    val minChunk = axis.chunkList?.minByOrNull { it.start }?.start
    var min = 0f
    mutableListOf(minListData, minLimitLine, minChunk).minByOrNull { it ?: 0f }?.let {
        min = it
    }
    val labelInterval = axis.labelInterval ?: 1f
    if (min < axis.min) {
        axis.min = ((min / labelInterval).toInt() - 1) * labelInterval
    }

}


fun getYAxisPaddingLeft(
    yLeftAxis: Axis?,
    xAxis: Axis,
    textMeasurer: TextMeasurer,
    currentDensity: Density
): Float {
    var padding = 0f
    var paddingYLeft = 0f
    var paddingXLeft = 0f
    var scale = 0f

    yLeftAxis?.let {
        var maxLabelValue = yLeftAxis.max.toString()
        var minLabelValue = yLeftAxis.min.toString()

        it.labelInterval?.apply {
            if (this > 0) {
                val num = ((it.max - it.min) / this).toInt()
                if (num > 0) {
                    minLabelValue = BigDecimal(it.min.toString()).add(
                        BigDecimal(this.toString()).multiply(BigDecimal(0))
                    ).toString()
                    maxLabelValue = BigDecimal(it.min.toString()).add(
                        BigDecimal(this.toString()).multiply(BigDecimal(num - 1))
                    ).toString()
//                        BigDecimal(defaultYAxisMin.toString()).add(BigDecimal(labelInterval.toString()) .multiply( BigDecimal(i))).toFloat()
                }
            }
        }

        val maxLabelValueLayoutResult = textMeasurer.measure(
            text = maxLabelValue,
            style = TextStyle(color = Color.Black, fontSize = yLeftAxis.labelTextSize)
        )
        val minLabelValueLayoutResult = textMeasurer.measure(
            text = minLabelValue,
            style = TextStyle(color = Color.Black, fontSize = yLeftAxis.labelTextSize)
        )
        val maxTextLayoutResult = textMeasurer.measure(
            text = yLeftAxis.max.toString(),
            style = TextStyle(color = Color.Black, fontSize = yLeftAxis.labelTextSize)
        )
        val nameTextLayoutResult = textMeasurer.measure(
            text = yLeftAxis.name ?: "",
            style = TextStyle(color = Color.Black, fontSize = yLeftAxis.labelTextSize)
        )
        /*刻度的宽度*/
        val scaleSize = yLeftAxis.let {
            it.scaleInterval?.let { scaleInterval ->
                getScaleLengSize(it, currentDensity)
            }
        }
        var labelWidth = 0
        var maxLabelWidth = 0
        var minLabelWidth = 0
        if (it.position == null || it.position!! == xAxis.min) {
            labelWidth = if (it.isDrawLabel) maxTextLayoutResult.size.width else 0
            maxLabelWidth = if (it.isDrawLabel) maxLabelValueLayoutResult.size.width else 0
            minLabelWidth = if (it.isDrawLabel) minLabelValueLayoutResult.size.width else 0
            scale = scaleSize ?: 0f
        }

        mutableListOf(
            maxLabelWidth, minLabelWidth,
            labelWidth, nameTextLayoutResult.size.width
        ).maxOrNull()?.let {
            paddingYLeft += it
        }
    }


    xAxis.let {
        val minTextLayoutResult = textMeasurer.measure(
            text = it.min.toString(),
            style = TextStyle(color = Color.Black, fontSize = it.labelTextSize)
        )

        val labelWidth = if (it.isDrawLabel) minTextLayoutResult.size.width / 4 else 0

        paddingXLeft += labelWidth
    }
//        Log.d(
//            "LineChart",
//            "getYAxisPaddingLeft  paddingYLeft = ${paddingYLeft}  paddingXLeft = ${paddingXLeft}"
//        )


    mutableListOf(
        paddingYLeft, paddingXLeft
    ).maxOrNull()?.let {
        padding += it
    }

//    Log.d("LineChart", "getYAxisPaddingLeft  padding = ${padding}")
    return if (padding == 0f) padding + scale else padding + scale + 8f
}


fun getAxisPaddingRight(
    yRightAxis: Axis?,
    xAxis: Axis,
    textMeasurer: TextMeasurer,
    currentDensity: Density
): Float {
    var padding = 0f
    var paddingYRight = 0f
    var paddingXRight = 0f
    var scale = 0f
    yRightAxis?.let {
        var maxLabelValue = it.max.toString()
        var minLabelValue = it.min.toString()
        it.labelInterval?.apply {
            if (this > 0) {
                val num = ((it.max - it.min) / this).toInt()
                if (num > 0) {
                    minLabelValue = BigDecimal(it.min.toString()).add(
                        BigDecimal(this.toString()).multiply(BigDecimal(0))
                    ).toString()
                    maxLabelValue = BigDecimal(it.min.toString()).add(
                        BigDecimal(this.toString()).multiply(BigDecimal(num - 1))
                    ).toString()
//                        BigDecimal(defaultYAxisMin.toString()).add(BigDecimal(labelInterval.toString()) .multiply( BigDecimal(i))).toFloat()
                }
            }
        }
        val maxLabelValueLayoutResult = textMeasurer.measure(
            text = maxLabelValue,
            style = TextStyle(color = Color.Black, fontSize = it.labelTextSize)
        )
        val minLabelValueLayoutResult = textMeasurer.measure(
            text = minLabelValue,
            style = TextStyle(color = Color.Black, fontSize = it.labelTextSize)
        )
        val maxTextLayoutResult = textMeasurer.measure(
            text = it.max.toString(),
            style = TextStyle(color = Color.Black, fontSize = it.labelTextSize)
        )
        val nameTextLayoutResult = textMeasurer.measure(
            text = it.name ?: "",
            style = TextStyle(color = Color.Black, fontSize = it.labelTextSize)
        )

        /*刻度的宽度*/
        val scaleSize = yRightAxis?.let {
            it.scaleInterval?.let { scaleInterval ->
                getScaleLengSize(it, currentDensity)
            }
        }
        var labelWidth = 0
        var maxLabelWidth = 0
        var minLabelWidth = 0
        if (it.position == null || it.position!! == yRightAxis.min) {
            maxLabelWidth = if (it.isDrawLabel) maxLabelValueLayoutResult.size.width else 0
            minLabelWidth = if (it.isDrawLabel) minLabelValueLayoutResult.size.width else 0
            labelWidth = if (it.isDrawLabel) maxTextLayoutResult.size.width else 0
            scale = scaleSize ?: 0f
        }
        mutableListOf(
            maxLabelWidth,
            minLabelWidth,
            labelWidth,
            nameTextLayoutResult.size.width
        ).maxOrNull()?.let {
            paddingYRight += it
        }
    }
    xAxis.let {
        val maxTextLayoutResult = textMeasurer.measure(
            text = it.max.toString(),
            style = TextStyle(color = Color.Black, fontSize = it.labelTextSize)
        )
        val nameTextLayoutResult = textMeasurer.measure(
            text = it.name ?: "",
            style = TextStyle(color = Color.Black, fontSize = it.labelTextSize)
        )
        val labelWidth = if (it.isDrawLabel) maxTextLayoutResult.size.width / 4 else 0
        mutableListOf(
            labelWidth, nameTextLayoutResult.size.width
        ).maxOrNull()?.let {
            paddingXRight += it
        }
    }



    mutableListOf(
        paddingYRight, paddingXRight
    ).maxOrNull()?.let {
        padding += it
    }


//    Log.d("LineChart", "getAxisPaddingRight  padding = ${paddingYRight}")
    return if (padding == 0f) padding + scale else padding + scale + 8f
}

fun getXAxisPaddingTop(
    yLeftAxis: Axis?,
    yRightAxis: Axis?,
    textMeasurer: TextMeasurer,
    currentDensity: Density
): Float {
    var padding = 0f
    var scale = 0f


    val yLeftNameTextLayoutResult = textMeasurer.measure(
        text = yLeftAxis?.name ?: "",
        style = TextStyle(color = Color.Black, fontSize = yLeftAxis?.labelTextSize ?: 12.sp)
    )
    val yRightNameTextLayoutResult = textMeasurer.measure(
        text = yRightAxis?.name ?: "",
        style = TextStyle(color = Color.Black, fontSize = yLeftAxis?.labelTextSize ?: 12.sp)
    )

    val yLeftMaxTextLayoutResult = textMeasurer.measure(
        text = yLeftAxis?.max?.toString() ?: "",
        style = TextStyle(color = Color.Black, fontSize = yLeftAxis?.labelTextSize ?: 0.sp)
    )
    val yLeftLabelHeight =
        if (yLeftAxis?.isDrawLabel == true) yLeftMaxTextLayoutResult.size.height else 0

    val yRighMaxTextLayoutResult = textMeasurer.measure(
        text = yLeftAxis?.max?.toString() ?: "",
        style = TextStyle(color = Color.Black, fontSize = yRightAxis?.labelTextSize ?: 0.sp)
    )
    val yRighLabelHeight =
        if (yLeftAxis?.isDrawLabel == true) yRighMaxTextLayoutResult.size.height else 0

    val yLeftAxisHeight =
        if (yLeftAxis?.name == null) 0 else yLeftNameTextLayoutResult.size.height
    val yRightAxisHeight =
        if (yRightAxis?.name == null) 0 else yLeftNameTextLayoutResult.size.height
    mutableListOf(

        yLeftAxisHeight, yRightAxisHeight
    ).maxOrNull()?.let {
        padding += it
    }

    mutableListOf(

        yLeftLabelHeight, yRighLabelHeight
    ).maxOrNull()?.let {
        padding += (it / 2)
    }


//    Log.d("LineChart", "getXAxisPaddingTop  padding = ${padding}")
    return if (padding == 0f) padding else padding + 8f
}


fun getXAxisPaddingBottom(
    axis: Axis?,
    textMeasurer: TextMeasurer,
    currentDensity: Density
): Float {
    var padding = 0f
    var scale = 0f

    axis?.let {
        val nameTextLayoutResult = textMeasurer.measure(
            text = it.name ?: "",
            style = TextStyle(color = Color.Black, fontSize = it.labelTextSize)
        )
        val maxTextLayoutResult = textMeasurer.measure(
            text = it.max.toString(),
            style = TextStyle(color = Color.Black, fontSize = it.labelTextSize)
        )
        val nameHeight = if (it.name != null) nameTextLayoutResult.size.height else 0
        val labelHeight = if (it.isDrawLabel) maxTextLayoutResult.size.height else 0

        /*刻度的宽度*/
        axis?.let {
            it.scaleInterval?.let { scaleInterval ->
                scale = getScaleLengSize(it, currentDensity)
            }
        }
        mutableListOf(

            nameHeight, labelHeight
        ).maxOrNull()?.let {
            padding += it
        }

    }


//    Log.d("LineChart", "getXAxisPaddingBottom  padding = ${padding}")
    return if (padding == 0f) padding + scale else padding + scale + 8f
}


/**
 * @author Brian
 * @Description: 获取曲线的路径
 */
fun getPath(
    pointList: List<Point>? = null,
    originX: Float,
    originY: Float,
    oneDataXPx: Float,
    oneDataYPx: Float,
    offsetXPx: Float,
    offsetYPx: Float,
    scale: Float = 1f,
    pathCache: Path? = null
): Path {
    val path = pathCache ?: Path()
    path.rewind()
    pointList?.let {
        val size = pointList.size
        if (size == 0) return path

        // 预计算所有常量
        val baseX = originX - offsetXPx
        val baseY = originY + offsetYPx
        val scaledOneDataXPx = oneDataXPx * scale

        // 使用数组访问
        val pointsArray = pointList.toTypedArray()

        // 处理第一个点
        val first = pointsArray[0]
        path.moveTo(
            baseX + first.x * scaledOneDataXPx,
            baseY - first.y * oneDataYPx
        )

        // 使用 while 循环，性能最好
        var i = 1
        while (i < size) {
            path.lineTo(
                baseX + pointsArray[i].x * scaledOneDataXPx,
                baseY - pointsArray[i].y * oneDataYPx
            )
            i++
        }

    }
    return path
}

/**
 * @author Brian
 * @Description: 获取曲线的路径，TODO 颜色值
 */
fun getPoints(
    pointList: List<Point>? = null,
    originX: Float,
    originY: Float,
    oneDataXPx: Float,
    oneDataYPx: Float,
    offsetXPx: Float,
    offsetYPx: Float,
    scale: Float = 1f
): List<Offset>? {
    return pointList?.let {
        val size = pointList.size
        if (size == 0) return emptyList()

        // 预计算所有常量
        val baseX = originX - offsetXPx
        val baseY = originY + offsetYPx
        val scaledOneDataXPx = oneDataXPx * scale

        // 预分配列表
        val points = ArrayList<Offset>(size)

        var i = 0
        while (i < size) {
            val item = pointList[i]
            points.add(
                Offset(
                    x = baseX + item.x * scaledOneDataXPx,
                    y = baseY - item.y * oneDataYPx
                )
            )
            i++
        }
        points
    }


}

/**
 * @author Brian
 * @Description: 获取曲线的路径 贝塞尔曲线路径
 */
fun getCubicPath(
    pointList: List<Point>,
    originX: Float,
    originY: Float,
    oneDataXPx: Float,
    oneDataYPx: Float,
    offsetXPx: Float,
    offsetYPx: Float,
    scale: Float,
    pathCache: Path? = null
): Path {
    val path = pathCache ?: Path()
    path.rewind()

    val size = pointList.size
    if (size == 0) return path

    // 一次性预计算所有坐标
    val baseX = originX - offsetXPx
    val baseY = originY + offsetYPx
    val scaledOneDataXPx = oneDataXPx * scale

    // 转换为数组访问
    val pointsArray = pointList.toTypedArray()

    // 处理第一个点
    val first = pointsArray[0]
    val firstX = baseX + first.x * scaledOneDataXPx
    val firstY = baseY - first.y * oneDataYPx
    path.moveTo(firstX, firstY)

    if (size == 1) return path

    // 使用 while 循环，避免迭代器开销
    var i = 1
    var lastX = firstX
    var lastY = firstY

    while (i < size) {
        val point = pointsArray[i]
        val currentX = baseX + point.x * scaledOneDataXPx
        val currentY = baseY - point.y * oneDataYPx

        val midX = (lastX + currentX) * 0.5f  // 更高效的中点计算

        path.cubicTo(midX, lastY, midX, currentY, currentX, currentY)

        lastX = currentX
        lastY = currentY
        i++
    }


    return path
}

/**
 *@author Brian
 *@Description:获取曲线的路径 贝塞尔曲线路径，Catmull-Rom
 */
fun getCubicPathCatmullRom(
    pointList: List<Point>,
    originX: Float,
    originY: Float,
    oneDataXPx: Float,
    oneDataYPx: Float,
    offsetXPx: Float,
    offsetYPx: Float,
    scale: Float,
    pathCache: Path? = null,
    tension: Float = 0.5f,  // 添加张力参数，0.5为标准Catmull-Rom
    excludeHeadAndTrail: Int = 1 //贝赛尔曲线 移除部分
): Path {
    val path = pathCache ?: Path()
    path.rewind()

    val size = pointList.size
    if (size == 0) return path

    // 一次性预计算所有坐标
    val baseX = originX - offsetXPx
    val baseY = originY + offsetYPx
    val scaledOneDataXPx = oneDataXPx * scale

    // 转换为数组访问
    val pointsArray = pointList.toTypedArray()

    // 处理第一个点
    val first = pointsArray[0]
    val firstX = baseX + first.x * scaledOneDataXPx
    val firstY = baseY - first.y * oneDataYPx
    path.moveTo(firstX, firstY)

    if (size == excludeHeadAndTrail) return path

    // 对于少于4个点的情况，使用原来的三次贝塞尔曲线
    if (size < excludeHeadAndTrail + 3) {
        var i = 1
        var lastX = firstX
        var lastY = firstY

        while (i < size) {
            val point = pointsArray[i]
            val currentX = baseX + point.x * scaledOneDataXPx
            val currentY = baseY - point.y * oneDataYPx

            val midX = (lastX + currentX) * 0.5f
            path.cubicTo(midX, lastY, midX, currentY, currentX, currentY)

            lastX = currentX
            lastY = currentY
            i++
        }
        return path
    }

    // 使用Catmull-Rom样条处理4个及以上点的情况
    var i = 0
    var lastX = firstX
    var lastY = firstY

    while (i < size) {
        when (i) {
            // 第一个点已经moveTo了，跳过
            0 -> {
                i++
                continue
            }
            // 第二个点：使用起始虚拟点
            in 1 until excludeHeadAndTrail -> {
                val x = baseX + pointsArray[i].x * scaledOneDataXPx
                val y = baseY - pointsArray[i].y * oneDataYPx
                path.lineTo(x, y)
            }

            excludeHeadAndTrail -> {
                val p0Index = if (excludeHeadAndTrail > 2) excludeHeadAndTrail - 2 else 0
                val p0 = pointsArray[p0Index]  // 虚拟点p-1 = p0
                val p1 = pointsArray[excludeHeadAndTrail - 1]  // p0
                val p2 = pointsArray[excludeHeadAndTrail]  // p1 (当前点)
                val p3 = pointsArray[excludeHeadAndTrail + 1]  // p2

                val x0 = baseX + p0.x * scaledOneDataXPx
                val y0 = baseY - p0.y * oneDataYPx
                val x1 = baseX + p1.x * scaledOneDataXPx
                val y1 = baseY - p1.y * oneDataYPx
                val x2 = baseX + p2.x * scaledOneDataXPx
                val y2 = baseY - p2.y * oneDataYPx
                val x3 = baseX + p3.x * scaledOneDataXPx
                val y3 = baseY - p3.y * oneDataYPx

                // 计算p1和p2的Y范围，用于限制控制点不超出数据范围
                val minY = minOf(y1, y2)
                val maxY = maxOf(y1, y2)

                // Catmull-Rom控制点计算，并限制Y值不超出数据点范围
                val ctrl1X = x1 + (x2 - x0) * tension * 0.5f
                val ctrl1Y = y1 + (y2 - y0) * tension * 0.5f
                val ctrl2X = x2 - (x3 - x1) * tension * 0.5f
                val ctrl2Y = y2 - (y3 - y1) * tension * 0.5f

                // 限制控制点Y值在p1和p2的范围内，防止曲线超出数据范围
                val clampedCtrl1Y = ctrl1Y.coerceIn(minY, maxY)
                val clampedCtrl2Y = ctrl2Y.coerceIn(minY, maxY)

                path.cubicTo(ctrl1X, clampedCtrl1Y, ctrl2X, clampedCtrl2Y, x2, y2)
                lastX = x2
                lastY = y2
            }
            // 中间点：使用正常的四个点
            in excludeHeadAndTrail + 1 until size - excludeHeadAndTrail -> {
                val p0 = pointsArray[i - 2]
                val p1 = pointsArray[i - 1]
                val p2 = pointsArray[i]
                val p3 = pointsArray[i + 1]

                val x0 = baseX + p0.x * scaledOneDataXPx
                val y0 = baseY - p0.y * oneDataYPx
                val x1 = baseX + p1.x * scaledOneDataXPx
                val y1 = baseY - p1.y * oneDataYPx
                val x2 = baseX + p2.x * scaledOneDataXPx
                val y2 = baseY - p2.y * oneDataYPx
                val x3 = baseX + p3.x * scaledOneDataXPx
                val y3 = baseY - p3.y * oneDataYPx

                // 计算p1和p2的Y范围，用于限制控制点不超出数据范围
                val minY = minOf(y1, y2)
                val maxY = maxOf(y1, y2)

                // Catmull-Rom控制点计算，并限制Y值不超出数据点范围
                val ctrl1X = x1 + (x2 - x0) * tension * 0.5f
                val ctrl1Y = y1 + (y2 - y0) * tension * 0.5f
                val ctrl2X = x2 - (x3 - x1) * tension * 0.5f
                val ctrl2Y = y2 - (y3 - y1) * tension * 0.5f

                // 限制控制点Y值在p1和p2的范围内，防止曲线超出数据范围
                val clampedCtrl1Y = ctrl1Y.coerceIn(minY, maxY)
                val clampedCtrl2Y = ctrl2Y.coerceIn(minY, maxY)

                path.cubicTo(ctrl1X, clampedCtrl1Y, ctrl2X, clampedCtrl2Y, x2, y2)
                lastX = x2
                lastY = y2
            }
            // 最后一个平滑点：使用结束虚拟点
            size - excludeHeadAndTrail -> {
                val p0 = pointsArray[i - 2]
                val p1 = pointsArray[i - 1]
                val p2 = pointsArray[i]      // 当前点
                val p3 = pointsArray[i]      // 虚拟点p_{n+1} = p_n

                val x0 = baseX + p0.x * scaledOneDataXPx
                val y0 = baseY - p0.y * oneDataYPx
                val x1 = baseX + p1.x * scaledOneDataXPx
                val y1 = baseY - p1.y * oneDataYPx
                val x2 = baseX + p2.x * scaledOneDataXPx
                val y2 = baseY - p2.y * oneDataYPx
                val x3 = baseX + p3.x * scaledOneDataXPx
                val y3 = baseY - p3.y * oneDataYPx

                // 计算p1和p2的Y范围，用于限制控制点不超出数据范围
                val minY = minOf(y1, y2)
                val maxY = maxOf(y1, y2)

                // Catmull-Rom控制点计算，并限制Y值不超出数据点范围
                val ctrl1X = x1 + (x2 - x0) * tension * 0.5f
                val ctrl1Y = y1 + (y2 - y0) * tension * 0.5f
                val ctrl2X = x2 - (x3 - x1) * tension * 0.5f
                val ctrl2Y = y2 - (y3 - y1) * tension * 0.5f

                // 限制控制点Y值在p1和p2的范围内，防止曲线超出数据范围
                val clampedCtrl1Y = ctrl1Y.coerceIn(minY, maxY)
                val clampedCtrl2Y = ctrl2Y.coerceIn(minY, maxY)

                path.cubicTo(ctrl1X, clampedCtrl1Y, ctrl2X, clampedCtrl2Y, x2, y2)
                lastX = x2
                lastY = y2
            }

            else -> {
                val x = baseX + pointsArray[i].x * scaledOneDataXPx
                val y = baseY - pointsArray[i].y * oneDataYPx
                path.lineTo(x, y)
            }
        }
        i++
    }

    return path
}


/**
 * 将像素Y坐标转换为所有可用Y轴的数据坐标
 * @return Triple<左内轴Y值, 左外轴Y值, 右轴Y值>，不存在的轴为 null
 */
private fun convertPixelToAllDataY(
    pixelY: Float,
    axisPoints: AxisPoints,
    yLeftInsideAxis: Axis?,
    yLeftAxis: Axis?,
    yRightAxis: Axis?
): Triple<Float?, Float?, Float?> {
    val dataYLeftInside = yLeftInsideAxis?.let { axis ->
        val oneDataYPx = (axisPoints.point0.y - axisPoints.point3.y) / (axis.max - axis.min)
        val offsetYPx = axis.min * oneDataYPx
        val raw = (axisPoints.point0.y - pixelY + offsetYPx) / oneDataYPx
        raw.coerceIn(axis.min, axis.max)
    }

    val dataYLeft = yLeftAxis?.let { axis ->
        val oneDataYPx = (axisPoints.point0.y - axisPoints.point3.y) / (axis.max - axis.min)
        val offsetYPx = axis.min * oneDataYPx
        val raw = (axisPoints.point0.y - pixelY + offsetYPx) / oneDataYPx
        raw.coerceIn(axis.min, axis.max)
    }

    val dataYRight = yRightAxis?.let { axis ->
        val oneDataYPx = (axisPoints.point0.y - axisPoints.point3.y) / (axis.max - axis.min)
        val offsetYPx = axis.min * oneDataYPx
        val raw = (axisPoints.point0.y - pixelY + offsetYPx) / oneDataYPx
        raw.coerceIn(axis.min, axis.max)
    }

    return Triple(dataYLeftInside, dataYLeft, dataYRight)
}

/**
 * 查找距离触摸点最近的数据点
 */
private fun findNearestDataPoint(
    touchX: Float,
    touchY: Float,
    lineList: List<Line>,
    axisPoints: AxisPoints,
    xAxisMin: Float,
    xAxisMax: Float,
    yAxisMin: Float,
    yAxisMax: Float,
    scale: Float
): PointData? {
    if (lineList.isEmpty()) return null

    var nearestPoint: PointData? = null
    var minDistance = Float.MAX_VALUE

    lineList.forEach { line ->
        line.pointList.forEach { point ->
            // 计算数据点在屏幕上的像素位置
            val oneDataXPx = (axisPoints.point1.x - axisPoints.point0.x) / (xAxisMax - xAxisMin)
            val oneDataYPx = (axisPoints.point0.y - axisPoints.point3.y) / (yAxisMax - yAxisMin)

            val pixelX = axisPoints.point0.x + (point.x - xAxisMin) * oneDataXPx * scale
            val pixelY = axisPoints.point0.y - (point.y - yAxisMin) * oneDataYPx

            // 计算欧几里得距离
            val distance = kotlin.math.sqrt(
                (pixelX - touchX) * (pixelX - touchX) +
                        (pixelY - touchY) * (pixelY - touchY)
            )

            if (distance < minDistance) {
                minDistance = distance
                nearestPoint = PointData(point, line, distance)
            }
        }
    }

    return nearestPoint
}

