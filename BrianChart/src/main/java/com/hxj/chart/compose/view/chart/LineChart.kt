package com.hxj.chart.compose.view.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val isSelfAdaptation = data?.isSelfAdaptation ?: false
    val isScroll: Boolean = false
    if (isSelfAdaptation) {
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
    val state = rememberTransformableState { zoomChange, panChange, rotationChange ->
        scale = scale * zoomChange
    }


    Box(modifier = modifier) {
        val current = LocalDensity.current
        val textMeasurer: TextMeasurer = rememberTextMeasurer()
        var lablePaddingLeft by remember(
            yLeftAxis, xAxis
        ) {
            mutableStateOf(getYAxisPaddingLeft(this, yLeftAxis, xAxis, textMeasurer))
        }
        var lablePaddingRight by remember(
            yRightAxis, xAxis
        ) {
            mutableStateOf(getAxisPaddingRight(this, yRightAxis, xAxis, textMeasurer))
        }
        var lablePaddingTop by remember(
            yLeftAxis, yRightAxis
        ) {
            mutableStateOf(getXAxisPaddingTop(this, yLeftAxis, yRightAxis, textMeasurer))
        }
        var lablePaddingBootom by remember(
            xAxis
        ) {
            mutableStateOf(getXAxisPaddingBottom(this, xAxis, textMeasurer))
        }

        val yLeftScaleLengSize = with(current) { getScaleLengSize(yLeftAxis).toPx() } //左边刻度的长度
        val yRightScaleLengSize = with(current) { getScaleLengSize(yLeftAxis).toPx() }//右边刻度的长度
        val xBottomScaleLengSize = with(current) { getScaleLengSize(xAxis).toPx() }//低边刻度的长度

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

                    //确定四个绘图点 (去除标签、刻度线、padding占据的位置)
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
                    val listCurvePathOrPoints = mutableMapOf<String, MutableList<PathAndPoints>>()
                    lineList?.let {
                        yLeftInsideAxis?.let {
                            val lineListNew =
                                lineList.filter { it.axisType == AxisType.LEFT_INSIDE }
                            createCurvePathOrPoints(
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
                                xAxisPosition = xAxis.position ?: 0f
                            ).let {
                                listCurvePathOrPoints.put(AxisType.LEFT_INSIDE.toString(), it)
                            }
                        }
                        yLeftAxis?.let {
                            val lineListNew = lineList.filter { it.axisType == AxisType.LEFT }
                            createCurvePathOrPoints(
                                lineList = lineListNew.toMutableList(),
                                point0 = point0,
                                point1 = point1,
                                point2 = point2,
                                point3 = point3,
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
                                point0 = point0,
                                point1 = point1,
                                point2 = point2,
                                point3 = point3,
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
                                point0 = point0,
                                point1 = point1,
                                point2 = point2,
                                point3 = point3,
                                scale = scale
                            )
                            /**画chunk 块内容*/
                            drawChunk(
                                this,
                                xAxis,
                                yLeftInsideAxis,
                                yLeftAxis,
                                yRightAxis,
                                point0 = point0,
                                point1 = point1,
                                point2 = point2,
                                point3 = point3,
                            )
                            /**画xy轴*/
                            drawXYAxis(
                                this,
                                xAxis,
                                yLeftInsideAxis,
                                yLeftAxis,
                                yRightAxis,
                                point0 = point0,
                                point1 = point1,
                                point2 = point2,
                                point3 = point3,
                            )
                            /**刻度 label*/
                            drawLable(
                                this,
                                xAxis,
                                yLeftInsideAxis,
                                yLeftAxis,
                                yRightAxis,
                                point0 = point0,
                                point1 = point1,
                                point2 = point2,
                                point3 = point3,
                                scale = scale
                            )
                            /**划限制线*/
                            drawLimitLine(
                                this,
                                xAxis,
                                yLeftInsideAxis,
                                yLeftAxis,
                                yRightAxis,
                                point0 = point0,
                                point1 = point1,
                                point2 = point2,
                                point3 = point3,
                                scale = scale
                            )
                            /**坐标轴名称*/
                            drawAxisName(
                                this,
                                xAxis,
                                yLeftInsideAxis,
                                yLeftAxis,
                                yRightAxis,
                                point0 = point0,
                                point1 = point1,
                                point2 = point2,
                                point3 = point3,
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
//    val maxListData = list?.maxOrNull() ?: axis.max
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


fun getYAxisPaddingLeft(
    boxScope: BoxScope, yLeftAxis: Axis?, xAxis: Axis, textMeasurer: TextMeasurer
): Float {
    var padding = 0f
    var paddingYLeft = 0f
    var paddingXLeft = 0f
    boxScope.run {

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
            var labelWidth = 0
            var maxLabelWidth = 0
            var minLabelWidth = 0
            if (it.position == null || it.position!! == xAxis.min) {
                labelWidth = if (it.isDrawLabel) maxTextLayoutResult.size.width else 0
                maxLabelWidth = if (it.isDrawLabel) maxLabelValueLayoutResult.size.width else 0
                minLabelWidth = if (it.isDrawLabel) minLabelValueLayoutResult.size.width else 0
            }
            mutableListOf(
                maxLabelWidth, minLabelWidth, labelWidth, nameTextLayoutResult.size.width
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

        mutableListOf(
            paddingYLeft, paddingXLeft
        ).maxOrNull()?.let {
            padding += it
        }
    }

    return if (padding == 0f) padding else padding + 8f
}


fun getAxisPaddingRight(
    boxScope: BoxScope, yRightAxis: Axis?, xAxis: Axis, textMeasurer: TextMeasurer
): Float {
    var padding = 0f
    var paddingYRight = 0f
    var paddingXRight = 0f
    boxScope.run {
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
            var labelWidth = 0
            var maxLabelWidth = 0
            var minLabelWidth = 0
            if (it.position == null || it.position!! == yRightAxis.min) {
                maxLabelWidth = if (it.isDrawLabel) maxLabelValueLayoutResult.size.width else 0
                minLabelWidth = if (it.isDrawLabel) minLabelValueLayoutResult.size.width else 0
                labelWidth = if (it.isDrawLabel) maxTextLayoutResult.size.width else 0
            }
            mutableListOf(
                maxLabelWidth, minLabelWidth, labelWidth, nameTextLayoutResult.size.width
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

    }

    return if (padding == 0f) padding else padding + 8f
}

fun getXAxisPaddingTop(
    drawScope: BoxScope, yLeftAxis: Axis?, yRightAxis: Axis?, textMeasurer: TextMeasurer
): Float {
    var padding = 0f
    drawScope.run {


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

    }

    return if (padding == 0f) padding else padding + 8f
}


fun getXAxisPaddingBottom(drawScope: BoxScope, axis: Axis?, textMeasurer: TextMeasurer): Float {
    var padding = 0f
    drawScope.run {

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

            mutableListOf(

                nameHeight, labelHeight
            ).maxOrNull()?.let {
                padding += it
            }

        }
    }

    return if (padding == 0f) padding else padding + 8f
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

                when {
                    line.isPoints -> {
                        it.points?.let { points ->
                            drawPoints(
                                points = points,
                                pointMode = PointMode.Points,
                                color = color,
                                strokeWidth = line.width.toPx()
                            )
                        }


                    }

                    line.isDrawArea -> {
                        //绘制面

                        it.path?.let { path ->
                            drawPath(
                                path = path, color = color, style = Fill
                            )
                        }
                    }

                    else -> {
                        //绘制path
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
            }
        }


    }

}

fun createCurvePathOrPoints(
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

    ): MutableList<PathAndPoints> {


    val oneDataXPx = (point1.x - point0.x) / (xAxisMax - xAxisMin) // X轴上 1f单位数据点对应的px数
    val oneDataYPx = (point0.y - point3.y) / (yAxisMax - yAxisMin) // X轴上 1f单位数据点对应的px数
    val offsetXPx = xAxisMin * oneDataXPx
    val offsetYPx = yAxisMin * oneDataYPx
    val pathAndPointsList = mutableListOf<PathAndPoints>()
    lineList.forEach {
        val pathAndPoints = PathAndPoints(line = it)
        val pointList = if (it.isDrawArea) {
            buildList {
                it.pointList.firstOrNull()?.let { add(Point(it.x, xAxisPosition)) }
                addAll(it.pointList)
                it.pointList.lastOrNull()?.let { add(Point(it.x, xAxisPosition)) }
            }
        } else {
            it.pointList
        }.asSequence()/*数据量大的时候asSequence比list的性能更高*/
            .filter { it.x in xAxisMin..xAxisMax } /*过滤掉不在范围内的点*/.toList()

        if (it.isPoints) { //散点
            pathAndPoints.points =
                getPoints(
                    it.pointList.asSequence()/*数据量大的时候asSequence比list的性能更高*/
                        .filter { it.x in xAxisMin..xAxisMax } /*过滤掉不在范围内的点*/.toList(),
                    point0.x,
                    point0.y,
                    oneDataXPx,
                    oneDataYPx,
                    offsetXPx,
                    offsetYPx,
                    scale)

        } else {//曲线
            pathAndPoints.path = if (it.isDrawCubic) {
                //平滑
                getCubicPath(
                    pointList,
                    point0.x,
                    point0.y,
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
                    point0.x,
                    point0.y,
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


/**
 * @author Brian
 * @Description: 获取曲线的路径，TODO 颜色值
 */
fun getPath(
    pointList: List<Point>? = null,
    originX: Float = 1f,
    originY: Float,
    oneDataXPx: Float,
    oneDataYPx: Float,
    offsetXPx: Float,
    offsetYPx: Float,
    scale: Float = 1f,
): Path {

    val path = Path()
    val points = pointList ?: return path // 如果 pointList 为空，直接返回空 Path

    // 提前计算公共部分
    val originXOffset = originX - offsetXPx
    val originYOffset = originY + offsetYPx

    // 处理第一个点
    val firstPoint = points.firstOrNull() ?: return path
    val startX = (originXOffset + firstPoint.x * oneDataXPx) * scale
    val startY = (originYOffset - firstPoint.y * oneDataYPx) * scale
    path.moveTo(startX, startY)

    // 处理剩余的点
    points.drop(1).forEach { point ->
        val x = (originXOffset + point.x * oneDataXPx) * scale
        val y = (originYOffset - point.y * oneDataYPx) * scale
        path.lineTo(x, y)
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
    scale: Float,
): Path {
    val path = Path()
    val points = pointList ?: return path // 如果 pointList 为空，直接返回空 Path

    // 提前计算公共部分
    val originXOffset = (originX - offsetXPx) * scale
    val originYOffset = (originY + offsetYPx)
    val oneDataXPxScaled = oneDataXPx * scale
    val oneDataYPxScaled = oneDataYPx

    // 处理第一个点
    val firstPoint = points.firstOrNull() ?: return path
    val startX = originXOffset + firstPoint.x * oneDataXPxScaled
    val startY = originYOffset - firstPoint.y * oneDataYPxScaled
    path.moveTo(startX, startY)

    // 处理剩余的点
    var lastX = startX
    var lastY = startY
    for (index in 1 until points.size) {
        val item = points[index]
        val X = originXOffset + item.x * oneDataXPxScaled
        val Y = originYOffset - item.y * oneDataYPxScaled

        val firstControlPointX = lastX + (X - lastX) / 2
        val firstControlPointY = lastY
        val secondControlPointX = lastX + (X - lastX) / 2
        val secondControlPointY = Y

        path.cubicTo(
            x1 = firstControlPointX,
            y1 = firstControlPointY,
            x2 = secondControlPointX,
            y2 = secondControlPointY,
            x3 = X,
            y3 = Y,
        )

        lastX = X
        lastY = Y
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
                    lineList = list, xAxis = Axis(
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
                modifier = Modifier.padding(2.dp), data = LineChartData(
                    xAxis = Axis(
                        max = 0.833f,
                        min = -0f,
                        position = 0f,
                        scaleInterval = 0.1f,
                        labelInterval = 0.1f,
                        name = "",
                    ),

                    yLeftAxis = Axis(
                        max = 0.02f,
                        scaleInterval = 0.003f,
                        labelInterval = 0.003f,
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
                modifier = Modifier.padding(2.dp), data = LineChartData(
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
fun LineChartPreview9() {
    MaterialTheme {
        Surface {

            val list = getTestLineList()

            LineChart(
                modifier = Modifier.padding(2.dp), data = LineChartData(
                    lineList = list,
                    isSelfAdaptation = true,
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
fun LineChartPreview10() {
    MaterialTheme {
        Surface {

            val list = getTestLineList()
            list.forEach { it.isDrawArea = true }

            LineChart(
                modifier = Modifier.padding(2.dp), data = LineChartData(
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

/**
 *@author Brian
 *@Description:实时绘图，性能测试
 */
@Composable
@Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun LineChartWithTimer() {
    // 使用不可变数据结构
    var lineData by remember {
        mutableStateOf(
            LineChartData(
                isSelfAdaptation = false,
                lineList = listOf(
                    Line(
                        pointList = mutableListOf(), color = Color(0xff50E3C2)
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

    Box(modifier = Modifier.background(Color.Black)) {
        Button(
            modifier = Modifier.align(Alignment.TopEnd), onClick = {
                isRunning = !isRunning
                if (isRunning) {
                    // 清理数据
                    lineData = lineData.copy(lineList = lineData.lineList?.map { line ->
                        line.copy(pointList = mutableListOf())
                    } as MutableList?)

                    // 启动数据生成
                    job = scope.launch {
                        val amplitude = 200.0
                        val frequency = 0.2

                        timerFlow.take(1000) // 限制生成1000次
                            .collect { i ->
                                val newPoints = (0 until 10).map { j ->
                                    val x = (i * 10 + j).toDouble()
                                    val y = amplitude * sin(2 * Math.PI * frequency * x / 100.0)
                                    Point(x.toFloat(), y.toFloat())
                                }

                                lineData = lineData.copy(lineList = lineData.lineList?.map { line ->
                                    line.copy(pointList = (line.pointList + newPoints) as MutableList<Point>)
                                } as MutableList<Line>?)
                            }
                    }
                } else {
                    job?.cancel() // 停止生成
                }
            }) {
            Text(if (isRunning) "Stop" else "Start")
        }

        // 显示当前点数
        Text(
            "Points: ${lineData.lineList?.first()?.pointList?.size}",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .height(40.dp)
                .wrapContentHeight(Alignment.CenterVertically)
                .padding(end = 100.dp),
            color = Color.Blue
        )

        LineChart(data = lineData)
    }
}