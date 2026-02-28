package com.hxj.chart.compose.view.chart

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hxj.view.chart.AxisPadding
import com.hxj.view.chart.AxisPoints
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.math.BigDecimal
import kotlin.math.sin

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
    data: LineChartData? = null
) {
    //用来记录缩放大小
    var scale by remember { mutableStateOf(1f) }//缩放
    var rotation by remember { mutableStateOf(0f) } //旋转
    var offset by remember { mutableStateOf(Offset.Zero) }//移动
// 提取字段，高效且自动跳过无效重组
    val lineList by derivedStateOf { data?.lineList }
    val xAxis by derivedStateOf { data?.xAxis ?: Axis() }
    val yLeftInsideAxis by derivedStateOf { data?.yLeftInsideAxis }
    val yLeftAxis by derivedStateOf { data?.yLeftAxis }
    val yRightAxis by derivedStateOf { data?.yRightAxis }
    val isSelfAdaptation by derivedStateOf { data?.isSelfAdaptation == true }
    val isScroll by derivedStateOf { data?.isScroll }
    val axisPadding by derivedStateOf { data?.axisPadding }

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

//            // 重点关注这些参数
//            Log.i(TAG, "=== axisPoints Recomposition ===")
//
//            // 1. canvasSize 是否频繁变化
//            val canvasSizeHash = canvasSize.hashCode()
//            Log.i(TAG, "canvasSize: $canvasSizeHash")
//
//            // 2. Axis 对象是否每次都创建新实例
//            Log.i(TAG, "xAxis identity: ${System.identityHashCode(xAxis)}")
//            Log.i(TAG, "yLeftAxis identity: ${System.identityHashCode(yLeftAxis)}")
//            Log.i(TAG, "yLeftInsideAxis identity: ${System.identityHashCode(yLeftInsideAxis)}")
//            Log.i(TAG, "yRightAxis identity: ${System.identityHashCode(yRightAxis)}")
//
//            // 3. Axis 对象的内容是否变化
//            Log.i(TAG, "xAxis name: ${xAxis.name}")
//            Log.i(TAG, "xAxis min: ${xAxis.min}, max: ${xAxis.max}")

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


        Canvas(
            modifier = transformModifier
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
                            /**划限制线*/
                            drawLimitLine(
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
                            /**画曲线或散点图*/
                            listCurvePathOrPoints.let {
                                drawCurveSplashes(this, it)
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
fun selfAdaptation(
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

                if (line.isCircle) {
                    it.offsetList?.forEachIndexed { index, offset ->
                        val point = line.pointList.getOrNull(index)
                        drawCircle(
                            center = offset,
                            color = color,
                            radius = point?.radius ?: 1f,
                            style = point?.style ?: Fill,
                        )
                    }
                }

                if (line.isDrawDrawable) {
                    it.offsetList?.forEachIndexed { index, offset ->
                        val point = line.pointList.getOrNull(index)
                        if (point?.image != null) {
                            val x = offset.x - 40
                            val y = offset.y - 40
                            drawImage(
                                image = point.image ?: ImageBitmap(100, 100),
                                topLeft = Offset(
                                    x = x,
                                    y = y
                                )
                            )
                        }
                    }
                }

                if (line.isDrawPath) {

                    //绘制path
                    if (line.isDrawArea || line.isFill) {
                        //绘制面

                        it.path?.let { path ->
                            //绘制path
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
                    } else {
                        it.path?.let { path ->
                            val dashPathEffect = if (line.isDashes) {
                                //启用虚线，则绘制虚线样式。
                                //若有自定义的虚线样式，则使用自定义样式；无，则使用默认样式
                                line.pathEffect ?: PathEffect.dashPathEffect(
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
                                    width = line.width.toPx(),
                                    pathEffect = dashPathEffect
                                )
                            )

                        }
                    }


                }

                if (line.isDrawLabel) {
                    it.offsetList?.forEachIndexed { index, offset ->
                        val point = line.pointList.getOrNull(index)
                        if (point == null || point.label.isNullOrBlank()) {
                            return
                        }
                        var chineseCount = 0 // 中文数量
                        var otherCount = 0 // 其他数量
                        point.label?.forEach { char ->
                            when {
                                char in '\u4E00'..'\u9FFF' -> chineseCount++
                                else -> otherCount++
                            }
                        }
                        val labelWidth =
                            chineseCount * (point.labelTextSize
                                ?: 12.sp).toPx() + otherCount * (point.labelTextSize
                                ?: 12.sp).toPx() / 2
                        val x = offset.x - labelWidth / 2
                        val y = if (offset.y > (point.labelTextSize ?: 12.sp).toPx())
                            offset.y - (point.labelTextSize ?: 12.sp).toPx()
                        else
                            offset.y + (point.labelTextSize ?: 12.sp).toPx()
                        drawContext.canvas.nativeCanvas.drawText(
                            point.label ?: "",
                            x,
                            y,
                            android.graphics.Paint().let { paint ->
                                paint.apply {
                                    this.textSize = (point.labelTextSize ?: 12.sp).toPx()
                                    this.color = (point.labelColor ?: Color.Black).toArgb()
                                    this.isAntiAlias = true
                                }
                            }
                        )
                    }
                }

                line.renderer?.invoke(drawScope, it.line, it.offsetList)
            }
        }


    }

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
        val pointList = if (line.isDrawArea) {
            buildList {
                line.pointList.firstOrNull()?.let { add(Point(it.x, xAxisPosition)) }
                addAll(line.pointList)
                line.pointList.lastOrNull()?.let { add(Point(it.x, xAxisPosition)) }
            }
        } else {
            line.pointList
        }

        if (line.isPoints || line.isDrawDrawable || line.isCircle || line.isDrawLabel || line.renderer != null) { //散点
            pathAndPoints.offsetList =
                getPoints(
                    pointList,
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
            pathAndPoints.path = if (line.isDrawCubic) {
                //平滑
                getCubicPathCatmullRom(
                    pointList,
                    axisPoints.point0.x,
                    axisPoints.point0.y,
                    oneDataXPx,
                    oneDataYPx,
                    offsetXPx,
                    offsetYPx,
                    scale,
                    pathCache = pathAndPoints.path,
                    excludeHeadAndTrail = if (line.isDrawArea == true) 2 else 1
                )
            } else {
                //折线
                getPath(
                    pointList,
                    axisPoints.point0.x,
                    axisPoints.point0.y,
                    oneDataXPx,
                    oneDataYPx,
                    offsetXPx,
                    offsetYPx,
                    scale,
                    pathCache = pathAndPoints.path
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

                // Catmull-Rom控制点计算
                val ctrl1X = x1 + (x2 - x0) * tension * 0.5f
                val ctrl1Y = y1 + (y2 - y0) * tension * 0.5f
                val ctrl2X = x2 - (x3 - x1) * tension * 0.5f
                val ctrl2Y = y2 - (y3 - y1) * tension * 0.5f

                path.cubicTo(ctrl1X, ctrl1Y, ctrl2X, ctrl2Y, x2, y2)
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

                // Catmull-Rom控制点计算
                val ctrl1X = x1 + (x2 - x0) * tension * 0.5f
                val ctrl1Y = y1 + (y2 - y0) * tension * 0.5f
                val ctrl2X = x2 - (x3 - x1) * tension * 0.5f
                val ctrl2Y = y2 - (y3 - y1) * tension * 0.5f

                path.cubicTo(ctrl1X, ctrl1Y, ctrl2X, ctrl2Y, x2, y2)
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

                // Catmull-Rom控制点计算
                val ctrl1X = x1 + (x2 - x0) * tension * 0.5f
                val ctrl1Y = y1 + (y2 - y0) * tension * 0.5f
                val ctrl2X = x2 - (x3 - x1) * tension * 0.5f
                val ctrl2Y = y2 - (y3 - y1) * tension * 0.5f

                path.cubicTo(ctrl1X, ctrl1Y, ctrl2X, ctrl2Y, x2, y2)
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

fun settingLineChartLabelValue(value: Float): String {
    val label = when {
        value.toInt().toFloat() == value -> {//为整数浮点数
            "${value.toInt()}"
        }


        else -> {//为小数浮点数
            "${value}"
        }
    }
    return "${label}T"
}

@Composable
@Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun LineChartPreview() {
    MaterialTheme {
        Surface {
            val list = getTestLineList2()
            val listChunk = getTestChunkList()
            val listChunkX = getTestXChunkList()
            val listChunk1 = getTestChunkList1()
            val listChunk2 = getTestChunkList2()
            val listChunk3 = getTestChunkList3()
            val limitLineList = getTestLimitLineList()
            val xLimitLineList1 = getTestXLimitLineList1()
            val yLimitLineList1 = getTestYLimitLineList1()
            val yLimitLineList2 = getTestYLimitLineList2()
            val yLimitLineList3 = getTestYLimitLineList3()
            LineChart(
                data = LineChartData(
                    lineList = list, xAxis = Axis(
                        min = 10f,
                        max = 40f,
                        scaleInterval = 5f,
                        labelInterval = 10f,
                        limitLineList = xLimitLineList1,
                        chunkList = listChunkX,
                        name = "x轴",
                    ), yLeftInsideAxis = Axis(
                        max = 200f,
                        scaleInterval = 25f,
                        labelInterval = 25f,
                        name = "Load\nW",
                        color = Color(0XFF18D276),
                        chunkList = listChunk1,
                        limitLineList = yLimitLineList1
                    ), yLeftAxis = Axis(
                        max = 2000f,
                        scaleInterval = 100f,
                        labelInterval = 500f,
                        name = "  VO2\nml/min",
                        color = Color(0XFFFF4E87),
                        chunkList = listChunk2,
                        limitLineList = yLimitLineList2
                    ),

                    yRightAxis = Axis(
                        max = 2000f,
                        scaleInterval = 100f,
                        labelInterval = 500f,
                        name = "  VCO2\nml/min",
                        color = Color(0XFF058BF6),
                        chunkList = listChunk3,
                        limitLineList = yLimitLineList3
                    )
                )
            )
        }
    }
}

@Composable
@Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun LineChartPreview2() {
    MaterialTheme {
        Surface {

            val list = getTestLineList()
            val listChunk = getTestChunkList()
            val listChunkX = getTestXChunkList()
            val limitLineList = getTestLimitLineList()
            val xLimitLineList = getTestXLimitLineList()
            LineChart(
                data = LineChartData(
                    lineList = list,
                    xAxis = Axis(
                        max = 500f,
                        scaleInterval = 20f,
                        labelInterval = 100f,

                        limitLineList = xLimitLineList,
                        chunkList = listChunk,
                        name = "",
                    ),

                    yLeftAxis = Axis(
                        max = 300f, scaleInterval = 10f, labelInterval = 50f, name = "",

                        chunkList = listChunk, limitLineList = limitLineList
                    ),

                    )
            )
        }
    }
}

@Composable
@Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun LineChartPreview3() {
    MaterialTheme {
        Surface {

            val list = getTestLineList()
            val listChunk = getTestChunkList()
            val listChunkX = getTestXChunkList()
            val limitLineList = getTestLimitLineList()
            val xLimitLineList = getTestXLimitLineList()
            LineChart(
                data = LineChartData(
                    lineList = list,
                    xAxis = Axis(
                        max = 480f,
                        scaleInterval = 20f,
                        labelInterval = 100f,

                        limitLineList = xLimitLineList,
                        chunkList = listChunk,
                        name = "",
                        settingLabelValue = ::settingLineChartLabelValue,
                        gridLine = GridLine(10f)
                    ),

                    yLeftAxis = Axis(
                        max = 300f,
                        scaleInterval = 10f,
                        labelInterval = 50f,
                        name = "",

                        chunkList = listChunk,
                        limitLineList = limitLineList,
                        settingLabelValue = ::settingLineChartLabelValue,
                        gridLine = GridLine(10f)
                    ),
                )
            )
        }
    }
}

@Composable
@Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun LineChartPreview4() {
    MaterialTheme {
        Surface {

            val list = getTestLineList()
            LineChart(
                modifier = Modifier
                    .padding(2.dp)
                    .background(Color(0xffaabbcc)),
                data = LineChartData(
                    lineList = list,
                    xAxis = Axis(
                        max = 500f,
                        gridLine = GridLine(interval = 10f, width = 0.5.dp),
                        isDrawLabel = false,
                        isDrawAxis = false
                    ),

                    yLeftAxis = Axis(
                        max = 300f,
                        gridLine = GridLine(interval = 10f, width = 0.5.dp),
                        isDrawLabel = false,
                        isDrawAxis = false
                    ),
                )
            )
        }
    }
}


@Composable
@Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun LineChartPreview5() {
    MaterialTheme {
        Surface {

            val list = getTestPlusOrMinusLineList()
            val limitLineList = getTestPlusOrMinusLimitLineList()
            LineChart(
                data = LineChartData(
                    lineList = list,
                    xAxis = Axis(
                        max = 800f,
                        min = -400f,
                        position = 0f,
                        scaleInterval = 100f,
                        labelInterval = 100f,
                        name = "",
                        limitLineList = limitLineList
                    ),

                    yLeftAxis = Axis(
                        max = 200f,
                        scaleInterval = 50f,
                        labelInterval = 50f,
                        position = 0f,
                        name = "",
                        min = -300f,
                        limitLineList = limitLineList
                    ),

                    )
            )
        }
    }
}

@Composable
@Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun LineChartPreview6() {
    MaterialTheme {
        Surface {
            val list = getTestPointLineList()
            LineChart(
                data = LineChartData(
                    lineList = list,
                    xAxis = Axis(
                        max = 800f,
                        min = -400f,
                        position = 0f,
                        scaleInterval = 100f,
                        labelInterval = 100f,
                        name = "",
                    ),

                    yLeftAxis = Axis(
                        max = 200f,
                        scaleInterval = 50f,
                        labelInterval = 50f,
                        position = 0f,
                        name = "",
                        min = -300f,
                    )
                )
            )
        }
    }
}

@Composable
@Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun LineChartPreview7() {
    MaterialTheme {
        Surface {
            LineChart(
                modifier = Modifier.padding(2.dp),
                data = LineChartData(
                    xAxis = Axis(
                        max = 0.83f,
                        min = -0f,
                        position = 0f,
                        scaleInterval = 0.1f,
                        labelInterval = 0.1f,
                        name = "",
                    ),

                    yLeftAxis = Axis(
                        max = 0.2f,
                        scaleInterval = 0.03f,
                        labelInterval = 0.03f,
                        position = 0f,
                        name = "",
                        min = 0f,
                    )
                )
            )
        }
    }
}

@Composable
@Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun LineChartPreview8() {
    MaterialTheme {
        Surface {
            LineChart(
                modifier = Modifier.padding(2.dp),
                data = LineChartData(
                    xAxis = Axis(
                        max = 8f,
                        min = -0f,
                        position = 0f,
                        scaleInterval = 1f,
                        labelInterval = 1f,
                        name = "",
                    ),

                    yLeftAxis = Axis(
                        max = 0f,
                        scaleInterval = 10f,
                        labelInterval = 10f,
                        position = 0f,
                        name = "",
                        min = -80f,
                    )
                )
            )
        }
    }
}

@Composable
@Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun LineChartSelfAdaptationPreview() {
    MaterialTheme {
        Surface {

            val list = getTestPlusOrMinusLineList()

            LineChart(
                data = LineChartData(
                    lineList = list,
                    xAxis = Axis(
                        position = 0f,
                        scaleInterval = 100f,
                        labelInterval = 100f,
                        name = "",
                    ),

                    yLeftAxis = Axis(
                        scaleInterval = 50f,
                        labelInterval = 50f,
                        position = 0f,
                        name = "",
                    ),
                    isSelfAdaptation = true
                ),

                )
        }
    }
}

@Composable
@Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun LineChartPoint() {
    val context = LocalContext.current
    MaterialTheme {
        Surface {
            LineChart(
                data = LineChartData(
                    lineList = getPointLineList(context),
                    xAxis = Axis(
                        max = 500f,
                        scaleInterval = 20f,
                        labelInterval = 100f,
                        gridLine = GridLine(10f)
                    ),

                    yLeftAxis = Axis(
                        max = 300f,
                        scaleInterval = 10f,
                        labelInterval = 50f,
                        gridLine = GridLine(10f)
                    ),
                )
            )
        }
    }
}

@Composable
@Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun LineChartPadingPreview() {
    val context = LocalContext.current
    MaterialTheme {
        Surface {
            Row(modifier = Modifier.padding(8.dp)) {

                LineChart(
                    data = LineChartData(
                        lineList = null,
                        xAxis = Axis(
                            max = 6f,
                            min = 0f,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            scaleInterval = 1f,
                            limitLineList = null,
                            chunkList = null,
                            labelTextSize = 14.sp,
                            isDrawLabel = false
                        ),

                        yLeftAxis = Axis(
                            max = 4.0f,
                            min = -4f,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            scaleInterval = 2f,
                            labelInterval = 2f,
                            chunkList = null,
                            limitLineList = null,
                            labelTextSize = 14.sp,
                            position = 3f,
                        ),


                        ),
                    modifier = Modifier.weight(1f)
                )
                LineChart(
                    data = LineChartData(
                        lineList = null,
                        xAxis = Axis(
                            max = 6f,
                            min = 0f,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            scaleInterval = 1f,
                            limitLineList = null,
                            chunkList = null,
                            labelTextSize = 14.sp,
                            isDrawLabel = false
                        ),

                        yLeftAxis = Axis(
                            max = 4.0f,
                            min = -4f,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            scaleInterval = 2f,
                            labelInterval = 2f,
                            chunkList = null,
                            limitLineList = null,
                            labelTextSize = 14.sp,
                            position = 3f,
                        ),


                        ),
                    modifier = Modifier.weight(1f)
                )

            }
        }
    }
}

@Composable
@Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun LineChartPadingSelfDefinePreview() {
    val context = LocalContext.current
    MaterialTheme {
        Surface {
            Row(modifier = Modifier.padding(8.dp)) {

                LineChart(
                    data = LineChartData(
                        lineList = null,
                        xAxis = Axis(
                            max = 6f,
                            min = 0f,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            scaleInterval = 1f,
                            labelInterval = 1f,
                            limitLineList = null,
                            chunkList = null,
                            labelTextSize = 14.sp,
                        ),

                        yLeftAxis = Axis(
                            max = 4.0f,
                            min = -4f,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            scaleInterval = 2f,
                            labelInterval = 2f,
                            chunkList = null,
                            limitLineList = null,
                            labelTextSize = 14.sp,
                        ),
                        axisPadding = AxisPadding().padding(40.dp)

                    ),
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0x10000000))
                )


            }
        }
    }
}

@Composable
@Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun LineChartWithTimer() {
    // 使用不可变数据结构
    val max = 5000f
    var lineData by remember {

        mutableStateOf(
            LineChartData(
                lineList = listOf(
                    Line(
                        pointList = mutableListOf(),
                        color = Color(0xff50E3C2)
                    )
                ) as MutableList<Line>?,
                xAxis = Axis(
                    max = max,
                    scaleInterval = max / 10,
                    labelInterval = max / 10,
                    gridLine = GridLine(max / 10, width = 0.5.dp)
                ),
                yLeftAxis = Axis(
                    max = 500f,
                    min = -500f,
                    scaleInterval = 100f,
                    labelInterval = 100f,
                    gridLine = GridLine(100f, width = 0.5.dp)
                ),
            )
        )
    }

    // 使用协程作用域
    val scope = rememberCoroutineScope()

    // 定时器Flow
    val timerFlow = remember {
        flow {
            var i = 0
            while (true) {
                emit(i++)
                delay(1) // 10ms间隔
            }
        }
    }

    // 启动/停止控制
    var time by remember { mutableStateOf(0L) }
    var isRunning by remember { mutableStateOf(false) }
    var job by remember { mutableStateOf<Job?>(null) }

    Box {

        LineChart(data = lineData)
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 20.dp, end = 20.dp)
        ) {
            // 显示当前点数
            Text(
                "Time:${time}s",
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                color = Color.Gray
            )
            Text(
                "Points: ${lineData.lineList?.first()?.pointList?.size}",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .width(100.dp)
                    .padding(start = 10.dp),
                color = Color.Gray
            )
            Button(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(start = 10.dp),
                onClick = {
                    isRunning = !isRunning
                    if (isRunning) {
                        // 清理数据
                        lineData = lineData.copy(
                            lineList = lineData.lineList?.map { line ->
                                line.copy(pointList = mutableListOf())
                            } as MutableList?
                        )
                        val startTime = System.currentTimeMillis()
                        CoroutineScope(Dispatchers.Default).launch {
                            // 启动数据生成，正弦波数据
                            job = scope.launch {
                                val amplitude = 200.0
                                val frequency = 0.2

                                timerFlow
                                    .take(max.toInt()) // 限制生成max次
                                    .collect { i ->
                                        val newPoints = (0 until 10).map { j ->
                                            val x = (i * 1 + j).toDouble()
                                            val y =
                                                amplitude * sin(2 * Math.PI * frequency * x / 100.0)
                                            Point(x.toFloat(), y.toFloat())
                                        }

                                        lineData = lineData.copy(
                                            lineList = lineData.lineList?.map { line ->
                                                line.copy(pointList = (line.pointList + newPoints) as MutableList<Point>)
                                            } as MutableList<Line>?
                                        )
                                        val now = System.currentTimeMillis()
                                        time = (now - startTime) / 1_000
                                    }
                            }
                            job?.join()
                            isRunning = false
                            Log.d("LineChartWithTimer", "job?.join() end")
                        }

                    } else {
                        job?.cancel() // 停止生成
                    }
                }
            ) {
                Text(if (isRunning) "Stop" else "Start")
            }


        }


    }
}


@Composable
@Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun LineChartPreviewSelfDefined() {
    MaterialTheme {
        Surface {
            val context = LocalContext.current
            val list = getTestLineListSelfDefined(context)
            LineChart(
                data = LineChartData(
                    lineList = list,
                    xAxis = Axis(
                        max = 500f,
                        scaleInterval = 20f,
                        labelInterval = 100f,
                        name = "",
                        gridLine = GridLine(10f, width = 0.5.dp)
                    ),

                    yLeftAxis = Axis(
                        max = 300f,
                        scaleInterval = 10f,
                        labelInterval = 50f,
                        name = "",
                        gridLine = GridLine(10f, width = 0.5.dp)
                    ),
                    isScroll = true
                )
            )
        }
    }
}