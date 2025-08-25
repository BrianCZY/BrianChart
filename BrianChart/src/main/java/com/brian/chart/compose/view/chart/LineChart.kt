package com.brian.chart.compose.view.chart

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import java.math.BigDecimal
import kotlin.math.sin
import kotlin.text.forEach

/**
 * @author Brian
 * @Description: 线性 图表
 * TODO : 优化缩放、添加滑动看数据
 * TODO:性能优化，重组优化
 */
@Composable
fun LineChart(
    modifier: Modifier = Modifier, data: LineChartData? = null
) {
    //用来记录缩放大小
    var scale by remember { mutableStateOf(1f) }//缩放
    var rotation by remember { mutableStateOf(0f) } //旋转
    var offset by remember { mutableStateOf(Offset.Zero) }//移动
    val lineList: List<Line>? = data?.lineList
    val xAxis: Axis = data?.xAxis ?: Axis() //x
    val yLeftInsideAxis: Axis? = data?.yLeftInsideAxis//y左内
    val yLeftAxis: Axis? = data?.yLeftAxis //y左外
    val yRightAxis: Axis? = data?.yRightAxis//y右
    val isSelfAdaptation = data?.isSelfAdaptation == true
    val isScroll: Boolean = false
    if (isSelfAdaptation) {
        selfAdaptation(xAxis, yLeftAxis, yLeftInsideAxis, yRightAxis, lineList)
    }
    val state = rememberTransformableState { zoomChange, panChange, rotationChange ->
        scale = scale * zoomChange
//        Log.d("LineChart", "scale = ${scale}  zoomChange = ${zoomChange} panChange = ${panChange}")

    }
    var boxSize by remember { mutableStateOf(IntSize.Zero) }
    Box(modifier = modifier.onGloballyPositioned {
        boxSize = it.size
    }) {
        val textMeasurer: TextMeasurer = rememberTextMeasurer()
        val currentDensity = LocalDensity.current

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
                .fillMaxSize()
                .drawWithCache {
                    val axisPoints = getAxisPoints(
                        xAxis,
                        yLeftAxis,
                        yLeftInsideAxis,
                        yRightAxis,
                        axisPadding = data?.axisPadding,
                        density = currentDensity,
                        textMeasurer = textMeasurer,
                        size = size
                    )

                    /**画gride 网格线*/
                    val listCurvePathOrPoints = mutableMapOf<String, MutableList<PathAndPoints>>()
                    lineList?.let {
                        yLeftInsideAxis?.let {
                            val lineListNew =
                                lineList.filter { it.axisType == AxisType.LEFT_INSIDE }
                            createCurvePathOrPoints(
                                lineList = lineListNew.toMutableList(),
                                axisPoints = axisPoints,
                                xAxisMax = xAxis.max,
                                xAxisMin = xAxis.min,
                                yAxisMax = it.max,
                                yAxisMin = it.min,
                                scale = scale,
                                xAxisPosition = xAxis.position ?: 0f
                            ).let {
                                listCurvePathOrPoints.put(AxisType.LEFT_INSIDE.toString(), it)
                            }
                        }
                        yLeftAxis?.let {
                            val lineListNew = lineList.filter { it.axisType == AxisType.LEFT }
                            createCurvePathOrPoints(
                                lineList = lineListNew.toMutableList(),
                                axisPoints = axisPoints,
                                xAxisMin = xAxis.min,
                                xAxisMax = xAxis.max,
                                yAxisMin = it.min,
                                yAxisMax = it.max,
                                scale = scale,
                                xAxisPosition = xAxis.position ?: 0f,

                                ).let {
                                listCurvePathOrPoints.put(AxisType.LEFT.toString(), it)
                            }
                        }
                        yRightAxis?.let {
                            val lineListNew = lineList.filter { it.axisType == AxisType.RIGHT }
                            createCurvePathOrPoints(
                                lineList = lineListNew.toMutableList(),
                                axisPoints = axisPoints,
                                xAxisMin = xAxis.min,
                                xAxisMax = xAxis.max,
                                yAxisMin = it.min,
                                yAxisMax = it.max,
                                scale = scale,
                                xAxisPosition = xAxis.position ?: 0f,

                                ).let {
                                listCurvePathOrPoints.put(AxisType.RIGHT.toString(), it)
                            }
                        }
                    }

                    onDrawBehind {
                        clipRect {
                            /**画gride 网格线*/
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

    if (yLeftAxis != null) {
        val lineListNew = lineList?.filter { it.axisType == AxisType.LEFT }
        reSetYMax(yLeftAxis, lineListNew)
    }
    if (yLeftInsideAxis != null) {
        val lineListNew = lineList?.filter { it.axisType == AxisType.LEFT_INSIDE }
        reSetYMax(yLeftInsideAxis, lineListNew)
    }
    if (yRightAxis != null) {
        val lineListNew = lineList?.filter { it.axisType == AxisType.RIGHT }
        reSetYMax(yRightAxis, lineListNew)
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
    size: Size,
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

    Log.d(
        "LineChart", """padding:
                        |lablePaddingLeft = ${lablePaddingLeft}
                        |lablePaddingRight = ${lablePaddingRight}
                        |lablePaddingTop = ${lablePaddingTop}
                        |lablePaddingBootom = ${lablePaddingBootom}
                        |""".trimMargin()
    )
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
    listCurvePathOrPoints: MutableMap<String, MutableList<PathAndPoints>>,
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
                val dashPathEffect = if (line.isDashes) {
                    //启用虚线，则绘制虚线样式。
                    //若有自定义的虚线样式，则使用自定义样式；无，则使用默认样式
                    line.pathEffect ?: PathEffect.dashPathEffect(floatArrayOf(10f, 4f), 4f)
                } else {
                    null
                }
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

    ): MutableList<PathAndPoints> {


    val oneDataXPx =
        (axisPoints.point1.x - axisPoints.point0.x) / (xAxisMax - xAxisMin) // X轴上 1f单位数据点对应的px数
    val oneDataYPx =
        (axisPoints.point0.y - axisPoints.point3.y) / (yAxisMax - yAxisMin) // X轴上 1f单位数据点对应的px数
    val offsetXPx = xAxisMin * oneDataXPx
    val offsetYPx = yAxisMin * oneDataYPx
    val pathAndPointsList = mutableListOf<PathAndPoints>()



    lineList.forEach {
        val pathAndPoints = PathAndPoints(line = it)
        val pointList = if (it.isDrawArea) {
            buildList {
                it.pointList.firstOrNull()?.let { add(
                    Point(
                        it.x,
                        xAxisPosition
                    )
                ) }
                addAll(it.pointList)
                it.pointList.lastOrNull()?.let { add(
                    Point(
                        it.x,
                        xAxisPosition
                    )
                ) }
            }
        } else {
            it.pointList
        }.asSequence()/*数据量大的时候asSequence比list的性能更高*/
            .filter { it.x in xAxisMin..xAxisMax } /*过滤掉不在范围内的点*/.toList()

        if (it.isPoints || it.renderer != null) { //散点
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

        if (it.isDrawPath) {//曲线
            pathAndPoints.path = if (it.isDrawCubic) {
                //平滑
                getCubicPath(
                    pointList,
                    axisPoints.point0.x,
                    axisPoints.point0.y,
                    oneDataXPx,
                    oneDataYPx,
                    offsetXPx,
                    offsetYPx,
                    scale,
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

                    )
            }
        }
        pathAndPointsList.add(pathAndPoints)

    }

    return pathAndPointsList

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

fun drawCurves(
    drawScope: DrawScope,
    lineList: List<Line>,
    xAxis: Axis,
    yLeftInsideAxis: Axis?,
    yLeftAxis: Axis?,
    yRightAxis: Axis?,
    point0: Point,
    point1: Point,
    point2: Point,
    point3: Point,
    scale: Float
) {

    drawScope.run {
        yLeftInsideAxis?.let {
            val lineListNew = lineList.filter { it.axisType == AxisType.LEFT_INSIDE }
            drawCurve(
                drawScope = this,
                lineList = lineListNew.toMutableList(),
                point0 = point0,
                point1 = point1,
                point2 = point2,
                point3 = point3,
                xAxisMax = xAxis.max,
                xAxisMin = xAxis.min,
                yAxisMax = it.max,
                yAxisMin = it.min,
                scale = scale,
                xAxisPosition = xAxis.position ?: 0f,
            )
        }
        yLeftAxis?.let {
            val lineListNew = lineList.filter { it.axisType == AxisType.LEFT }
            drawCurve(
                drawScope = this,
                lineList = lineListNew.toMutableList(),
                point0 = point0,
                point1 = point1,
                point2 = point2,
                point3 = point3,
                xAxisMax = xAxis.max,
                xAxisMin = xAxis.min,
                yAxisMax = it.max,
                yAxisMin = it.min,
                scale = scale,
                xAxisPosition = xAxis.position ?: 0f,
            )
        }
        yRightAxis?.let {
            val lineListNew = lineList.filter { it.axisType == AxisType.RIGHT }
            drawCurve(
                drawScope = this,
                lineList = lineListNew.toMutableList(),
                point0 = point0,
                point1 = point1,
                point2 = point2,
                point3 = point3,
                xAxisMax = xAxis.max,
                xAxisMin = xAxis.min,
                yAxisMax = it.max,
                yAxisMin = it.min,
                scale = scale,
                xAxisPosition = xAxis.position ?: 0f,
            )
        }
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
                getScaleLengSize(
                    it,
                    currentDensity
                )
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
                getScaleLengSize(
                    it,
                    currentDensity
                )
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
                scale = getScaleLengSize(
                    it,
                    currentDensity
                )
            }
        }
        mutableListOf(

            nameHeight, labelHeight
        ).maxOrNull()?.let {
            padding += it
        }
//            Log.d(
//                "LineChart",
//                "getXAxisPaddingBottom  name = ${it.name}"
//            )
//            Log.d(
//                "LineChart",
//                "getXAxisPaddingBottom  labelHeight = ${labelHeight}   nameHeight = ${nameTextLayoutResult.size.height}"
//            )
    }


//    Log.d("LineChart", "getXAxisPaddingBottom  padding = ${padding}")
    return if (padding == 0f) padding + scale else padding + scale + 8f
}

fun drawCurve(
    drawScope: DrawScope,
    lineList: MutableList<Line>,
    point0: Point,
    point1: Point,
    point2: Point,
    point3: Point,
    xAxisMin: Float,
    xAxisMax: Float,
    yAxisMin: Float,
    yAxisMax: Float,
    scale: Float,
    xAxisPosition: Float = 0f,
) {
    drawScope.run {

        val oneDataXPx = (point1.x - point0.x) / (xAxisMax - xAxisMin) // X轴上 1f单位数据点对应的px数
        val oneDataYPx = (point0.y - point3.y) / (yAxisMax - yAxisMin) // X轴上 1f单位数据点对应的px数
        val offsetXPx = xAxisMin * oneDataXPx
        val offsetYPx = yAxisMin * oneDataYPx


        lineList.forEach {
            val pointList =
                if (it.isDrawArea) {
                    buildList {
                        it.pointList.firstOrNull()?.let { add(
                            Point(
                                it.x,
                                xAxisPosition
                            )
                        ) }
                        addAll(it.pointList)
                        it.pointList.lastOrNull()?.let { add(
                            Point(
                                it.x,
                                xAxisPosition
                            )
                        ) }
                    }
                } else {
                    it.pointList
                }
//                    .asSequence()/*数据量大的时候asSequence比list的性能更高*/
//                    .filter { it.x in xAxisMin..xAxisMax } /*过滤掉不在范围内的点*/
//                    .toList()
            val path = if (it.isDrawCubic) {
                getCubicPath(
                    pointList,
                    point0.x,
                    point0.y,
                    oneDataXPx,
                    oneDataYPx,
                    offsetXPx,
                    offsetYPx,
                    scale
                )
            } else {
                getPath(
                    pointList,
                    point0.x,
                    point0.y,
                    oneDataXPx,
                    oneDataYPx,
                    offsetXPx,
                    offsetYPx,
                    scale
                )
            }


            val dashPathEffect = if (it.isDashes) {
                //启用虚线，则绘制虚线样式。
                //若有自定义的虚线样式，则使用自定义样式；无，则使用默认样式
                it.pathEffect ?: PathEffect.dashPathEffect(floatArrayOf(10f, 4f), 4f)
            } else {
                null
            }
            val color = it.color
            if (it.isPoints) {
                drawPoints(
                    points = getPoints(
                        /* it.pointList.filter { point -> point.x in xAxisMin..xAxisMax },*/
                        pointList,
                        point0.x,
                        point0.y,
                        oneDataXPx,
                        oneDataYPx,
                        offsetXPx,
                        offsetYPx,
                        scale
                    ),
                    pointMode = PointMode.Points,
                    color = color,
                    strokeWidth = it.width.toPx(),
                    cap = StrokeCap.Round
                )

            } else if (it.isFill || it.isDrawArea) {
                //绘制path
                it.drawAreaBrush?.let {
                    drawPath(
                        path = path,
                        brush = it,
                        style = Fill,
                    )
                } ?: drawPath(
                    path = path,
                    color = color,
                    style = Fill,
                )

            } else {
                drawPath(
                    path = path,
                    color = color,
                    style = Stroke(
                        width = it.width.toPx(),
                        pathEffect = dashPathEffect
                    )
                )
            }

        }

    }

}


/**
 * @author Brian
 * @Description: 获取曲线的路径
 */
fun getPath(
    pointList: List<Point>? = null,
    originX: Float = 1f,
    originY: Float,
    oneDataXPx: Float,
    oneDataYPx: Float,
    offsetXPx: Float,
    offsetYPx: Float,
    scale: Float = 1f
): Path {

    val path = Path()
    pointList?.let {

        for ((index, item) in it.withIndex()) {
            val X = originX + item.x * oneDataXPx - offsetXPx //转换为对应的X Px
            val Y = originY - item.y * oneDataYPx + offsetYPx //转换为对应的Y Px
            if (index == 0) {
                path.moveTo(X * scale, Y)
            } else {
                //画曲线
                path.lineTo(X * scale, Y)
            }
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
    originX: Float = 1f,
    originY: Float,
    oneDataXPx: Float,
    oneDataYPx: Float,
    offsetXPx: Float,
    offsetYPx: Float,
    scale: Float = 1f
): MutableList<Offset> {
    val points: MutableList<Offset> = arrayListOf()
    pointList?.let {
        for ((_, item) in it.withIndex()) {
            val x = originX + item.x * oneDataXPx - offsetXPx //转换为对应的X Px
            val y = originY - item.y * oneDataYPx + offsetYPx //转换为对应的Y Px
            points.add(Offset(x * scale, y))
        }
    }
    return points
}

/**
 * @author Brian
 * @Description: 获取曲线的路径 贝塞尔曲线路径，TODO 颜色值
 */
fun getCubicPath(
    pointList: List<Point>? = null,
    originX: Float = 1f,
    originY: Float,
    oneDataXPx: Float,
    oneDataYPx: Float,
    offsetXPx: Float,
    offsetYPx: Float,
    scale: Float
): Path {
    val path = Path()
    pointList?.let {

        var lastItem: Point? = null
        for ((index, item) in it.withIndex()) {
            val X = originX + item.x * oneDataXPx - offsetXPx //转换为对应的X Px
            val Y = originY - item.y * oneDataYPx + offsetYPx //转换为对应的Y Px

            val lastX = originX + (lastItem?.x ?: 0f) * oneDataXPx - offsetXPx//转换为对应的X Px
            val lastY = originY - (lastItem?.y ?: 0f) * oneDataYPx + offsetYPx //转换为对应的Y Px

            if (index == 0) {
                path.moveTo(X * scale, Y)

            } else {
                lastItem?.let { lastItem ->


                    val firstControlPoint = Point(
                        x = lastX + (X - lastX) / 2F,
                        y = lastY,
                    )
                    val secondControlPoint = Point(
                        x = lastX + (X - lastX) / 2F,
                        y = Y,
                    )
                    path.cubicTo(
                        x1 = firstControlPoint.x * scale,
                        y1 = firstControlPoint.y,
                        x2 = secondControlPoint.x * scale,
                        y2 = secondControlPoint.y,
                        x3 = X * scale,
                        y3 = Y,
                    )

                }
            }

            lastItem = item

        }


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
            val limitLineList =
                getTestLimitLineList()
            val xLimitLineList1 =
                getTestXLimitLineList1()
            val yLimitLineList1 =
                getTestYLimitLineList1()
            val yLimitLineList2 =
                getTestYLimitLineList2()
            val yLimitLineList3 =
                getTestYLimitLineList3()
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
            val limitLineList =
                getTestLimitLineList()
            val xLimitLineList =
                getTestXLimitLineList()
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
            val limitLineList =
                getTestLimitLineList()
            val xLimitLineList =
                getTestXLimitLineList()
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

            val list =
                getTestPlusOrMinusLineList()
            val limitLineList =
                getTestPlusOrMinusLimitLineList()
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
fun LineChartPoint() {
    val context = LocalContext.current
    MaterialTheme {
        Surface {
            LineChart(
                data = LineChartData(
                    lineList = getPointLineList(
                        context
                    ),
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
                    max = 10000f,
                    scaleInterval = 1000f,
                    labelInterval = 1000f,
                ),
                yLeftAxis = Axis(
                    max = 500f,
                    min = -500f,
                    scaleInterval = 100f,
                    labelInterval = 100f,
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
                delay(10) // 10ms间隔
            }
        }
    }

    // 启动/停止控制
    var isRunning by remember { mutableStateOf(false) }
    var job by remember { mutableStateOf<Job?>(null) }

    Box {
        Button(
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = {
                isRunning = !isRunning
                if (isRunning) {
                    // 清理数据
                    lineData = lineData.copy(
                        lineList = lineData.lineList?.map { line ->
                            line.copy(pointList = mutableListOf())
                        } as MutableList?
                    )

                    // 启动数据生成
                    job = scope.launch {
                        val amplitude = 200.0
                        val frequency = 0.2

                        timerFlow
                            .take(1000) // 限制生成1000次
                            .collect { i ->
                                val newPoints = (0 until 10).map { j ->
                                    val x = (i * 10 + j).toDouble()
                                    val y = amplitude * sin(2 * Math.PI * frequency * x / 100.0)
                                    Point(
                                        x.toFloat(),
                                        y.toFloat()
                                    )
                                }

                                lineData = lineData.copy(
                                    lineList = lineData.lineList?.map { line ->
                                        line.copy(pointList = (line.pointList + newPoints) as MutableList<Point>)
                                    } as MutableList<Line>?
                                )
                            }
                    }
                } else {
                    job?.cancel() // 停止生成
                }
            }
        ) {
            Text(if (isRunning) "Stop" else "Start")
        }

        // 显示当前点数
        Text(
            "Points: ${lineData.lineList?.first()?.pointList?.size}",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 80.dp),
            color = Color.White
        )

        LineChart(data = lineData)
    }
}


@Composable
@Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun LineChartPreviewSelfDefined() {
    MaterialTheme {
        Surface {
            val context = LocalContext.current
            val list =
                getTestLineListSelfDefined(
                    context
                )
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
                )
            )
        }
    }
}