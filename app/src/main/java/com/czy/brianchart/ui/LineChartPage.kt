package com.czy.brianchart.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.czy.brianchart.ui.components.TopBar
import com.czy.brianchart.ui.navigation.ChartNavigationActions
import com.czy.brianchart.ui.theme.BrianChartTheme
import com.brian.chart.compose.view.chart.Axis
import com.brian.chart.compose.view.chart.AxisType
import com.brian.chart.compose.view.chart.Chunk
import com.brian.chart.compose.view.chart.GridLine
import com.brian.chart.compose.view.chart.LimitLine
import com.brian.chart.compose.view.chart.Line
import com.brian.chart.compose.view.chart.LineChart
import com.brian.chart.compose.view.chart.LineChartData
import com.brian.chart.compose.view.chart.Point
import com.brian.chart.compose.view.chart.TouchEventData
import com.brian.view.chart.AxisPadding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun LineChartPage(navigationActions: ChartNavigationActions? = null) {
    val lineChartViewModel: LineChartViewModel = viewModel()
    val lineChartUIState by lineChartViewModel.lineChartUIState.collectAsStateWithLifecycle()
    LineChartView(
        modifier = Modifier.fillMaxSize(), lineChartUIState = lineChartUIState, backClick = {
            navigationActions?.navigateBack()
        })


}

@Composable
fun LineChartView(modifier: Modifier, lineChartUIState: LineChartUIState, backClick: () -> Unit?) {
    Surface(modifier = modifier) {
        Column {
            TopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp)
                    .height(48.dp),
                title = "LineChart"
            ) { backClick?.invoke() }
            HorizontalDivider(thickness = 1.dp)
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

                Text(
                    "动态示例",
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                        .wrapContentSize(Alignment.Center),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )


                LineChartWithTimer(modifier = Modifier.height(220.dp))
                HorizontalDivider(thickness = 8.dp)
                Text(
                    "静态示例",
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                        .wrapContentSize(Alignment.Center),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Chart1(modifier = Modifier.height(220.dp))
                HorizontalDivider(thickness = 8.dp)
                Chart2(modifier = Modifier.height(220.dp))
                HorizontalDivider(thickness = 8.dp)
                Chart3(modifier = Modifier.height(220.dp))
                HorizontalDivider(thickness = 8.dp)
                Chart4(modifier = Modifier.height(220.dp))
                HorizontalDivider(thickness = 8.dp)
                Chart5(modifier = Modifier.height(220.dp))
                HorizontalDivider(thickness = 8.dp)
                Chart6(modifier = Modifier.height(220.dp))
                HorizontalDivider(thickness = 8.dp)
                Chart7(modifier = Modifier.height(220.dp))
                HorizontalDivider(thickness = 8.dp)
                Chart8(modifier = Modifier.height(220.dp))
                HorizontalDivider(thickness = 8.dp)
                Chart9(modifier = Modifier.height(220.dp))
                HorizontalDivider(thickness = 8.dp)
                Chart10(modifier = Modifier.height(220.dp))
                HorizontalDivider(thickness = 8.dp)
                ChartPading(modifier = Modifier.height(220.dp))
                HorizontalDivider(thickness = 8.dp)
                ChartSelfDefine(
                    modifier = Modifier
                        .padding(bottom = 40.dp)
                        .height(220.dp)
                )
                HorizontalDivider(thickness = 8.dp)
                Text(
                    "触摸交互示例",
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                        .wrapContentSize(Alignment.Center),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                ChartWithTouch(
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .height(300.dp)
                )
                HorizontalDivider(thickness = 8.dp)


            }


        }
    }
}

/**
 *@author Brian
 *@Description:实时绘图，性能测试
 */
@Composable
fun LineChartWithTimer(modifier: Modifier) {
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

    Box(modifier = modifier) {
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

@Composable
fun ChartPading(modifier: Modifier) {
    Row(modifier = modifier.padding(8.dp)) {
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


                ), modifier = Modifier.weight(1f)
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


                ), modifier = Modifier.weight(1f)
        )

    }
}

@Composable
fun ChartSelfDefine(modifier: Modifier) {
    val context = LocalContext.current
    val list = getTestLineListSelfDefined(context)
    LineChart(
        modifier = modifier, data = LineChartData(
            lineList = list, xAxis = Axis(
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
            ), isScroll = true
        )
    )
}

@Composable
fun Chart10(modifier: Modifier) {
    val list = getTestLineList()
    list.forEach { it.isDrawArea = true }

    LineChart(
        modifier = modifier.padding(2.dp), data = LineChartData(
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


@Composable
fun Chart9(modifier: Modifier) {
    val list = getTestLineList()

    LineChart(

        modifier = modifier.padding(2.dp), data = LineChartData(
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

@Composable
fun Chart8(modifier: Modifier) {
    LineChart(
        modifier = modifier.padding(2.dp), data = LineChartData(
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

@Composable
fun Chart7(modifier: Modifier) {
    LineChart(
        modifier = modifier.padding(2.dp), data = LineChartData(
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

@Composable
fun Chart6(modifier: Modifier) {
    val list = getTestPointLineList()
    LineChart(
        modifier = modifier, data = LineChartData(
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

@Composable
fun Chart5(modifier: Modifier) {
    val list = getTestPlusOrMinusLineList()
    val limitLineList = getTestPlusOrMinusLimitLineList()
    LineChart(
        modifier = modifier, data = LineChartData(
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

@Composable
fun Chart4(modifier: Modifier) {
    val list = getTestLineList()
    LineChart(
        modifier = modifier
            .padding(2.dp)
            .background(Color(0xffaabbcc)), data = LineChartData(
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

@Composable
fun Chart3(modifier: Modifier) {
    val list = getTestLineList()
    val listChunk = getTestChunkList()
    val listChunkX = getTestXChunkList()
    val limitLineList = getTestLimitLineList()
    val xLimitLineList = getTestXLimitLineList()
    LineChart(
        modifier = modifier, data = LineChartData(
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

@Composable
fun Chart2(modifier: Modifier) {
    val list = getTestLineList()
    val listChunk = getTestChunkList()
    val listChunkX = getTestXChunkList()
    val limitLineList = getTestLimitLineList()
    val xLimitLineList = getTestXLimitLineList()
    LineChart(
        modifier = modifier, data = LineChartData(
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

@Composable
fun Chart1(modifier: Modifier) {
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
        ), modifier = modifier
    )
}

@Composable
@Preview
fun ChartViewPreview() {
    BrianChartTheme {
        LineChartPage()
    }

}

@Composable
@Preview(heightDp = 2000)
fun ChartViewLongPreview() {
    BrianChartTheme {
        LineChartPage()
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
            10f, true, width = 2.dp, color = Color(0XFF18D276), text = "限制线"
        )
    )
    return limitLineList
}

fun getTestYLimitLineList2(): MutableList<LimitLine> {
    val limitLineList: MutableList<LimitLine> = mutableListOf()
    limitLineList.add(
        LimitLine(
            600f, true, width = 2.dp, color = Color(0XFFFF4E87), text = "限制线"
        )
    )
    return limitLineList
}

fun getTestYLimitLineList3(): MutableList<LimitLine> {
    val limitLineList: MutableList<LimitLine> = mutableListOf()
    limitLineList.add(
        LimitLine(
            1200f, true, width = 2.dp, color = Color(0XFF058BF6), text = "限制线"
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
//    linList.add(
//        Line(
//            point,
//            color = Color(0xff50E3C2),
//            isDashes = true,
//            pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 12f), 2f)
//        )
//    )
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
        Point(100f, 50f, selfDefinedValue = { drawScope, offset ->
            drawSelfDefinedTextAndShape(
                drawScope = drawScope, offset = offset, x = 100f, y = 50f, color = Color.Green
            )

        }), Point(200f, 120f, selfDefinedValue = { drawScope, offset ->
            drawSelfDefinedTextAndShape(
                drawScope = drawScope, offset = offset, x = 200f, y = 120f, color = Color.Red
            )

        }), Point(300f, 220f, selfDefinedValue = { drawScope, offset ->
            drawSelfDefinedText(
                drawScope = drawScope, offset = offset, x = 300f, y = 220f, color = Color.Black
            )

        }), Point(400f, 80f, selfDefinedValue = { drawScope, offset ->
            val bitmap = BitmapFactory.decodeResource(
                context.resources,  // 需要 Context
                android.R.drawable.ic_menu_edit
            ).asImageBitmap()
            drawSelfDefinedBitmap(
                drawScope = drawScope,
                bitmap = bitmap,
                offset = offset,
            )

        }), Point(500f, 200f, selfDefinedValue = { drawScope, offset ->
            val bitmap = BitmapFactory.decodeResource(
                context.resources,  // 需要 Context
                android.R.drawable.ic_menu_edit
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
            })
    )/*linList.add(
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
            image = bitmap, topLeft = Offset(
                offset.x - bitmap.width / 2, offset.y - bitmap.height / 2
            ) // Example position adjustment
        )

    }

}

/**
 *@author Brian
 *@Description:自定义样式，示例
 */
fun drawSelfDefinedTextAndShape(
    drawScope: DrawScope, offset: Offset, x: Float, y: Float, color: Color
) {
    drawScope.run {
        val textSize = 12.sp
        drawRoundRect(
            color = color,
            topLeft = Offset(offset.x - 20f, offset.y - 20f),
            size = Size(40f, 40f),
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
                "(${x},${y})", offset.x - 80f, offset.y - textSize.toPx(), nativePaint
            )
        }
    }
}

/**
 *@author Brian
 *@Description:自定义样式，示例
 */
fun drawSelfDefinedText(
    drawScope: DrawScope, offset: Offset, x: Float, y: Float, color: Color
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
                "${y.toInt()}次", offset.x - 40f, offset.y - textSize.toPx() / 2, nativePaint
            )
        }
    }
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
            point2, color = Color(0XFF058BF6), axisType = AxisType.RIGHT, isDrawCubic = true
        )
    )
    return linList
}


fun drawableToBitmap(drawable: Drawable? = null): ImageBitmap? {
    if (drawable == null) {
        return null
    }
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
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
            point, color = Color(0xff50E3C2), isDrawCubic = true
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
    linList.add(Line(point, width = 2.dp, color = Color(0xff4A90E2), isDrawCubic = true))
    return linList
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
    BrianChartTheme {
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
    BrianChartTheme {
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
    BrianChartTheme {
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
    BrianChartTheme {
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
    BrianChartTheme {
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
    BrianChartTheme {
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
    BrianChartTheme {
        Surface {
            LineChart(
                modifier = Modifier.padding(2.dp), data = LineChartData(
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
    BrianChartTheme {
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
fun LineChartSelfAdaptationPreview() {
    BrianChartTheme {
        Surface {

            val list = getTestPlusOrMinusLineList()

            LineChart(
                data = LineChartData(
                    lineList = list, xAxis = Axis(
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
                    ), isSelfAdaptation = true
                ),

                )
        }
    }
}


@Composable
@Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun LineChartPadingPreview() {
    val context = LocalContext.current
    BrianChartTheme {
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


                        ), modifier = Modifier.weight(1f)
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


                        ), modifier = Modifier.weight(1f)
                )

            }
        }
    }
}

@Composable
@Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun LineChartPadingSelfDefinePreview() {
    val context = LocalContext.current
    BrianChartTheme {
        Surface {
            Row(modifier = Modifier.padding(8.dp)) {

                LineChart(
                    data = LineChartData(
                        lineList = null, xAxis = Axis(
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
                        ), axisPadding = AxisPadding().padding(40.dp)

                    ), modifier = Modifier
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
                        pointList = mutableListOf(), color = Color(0xff50E3C2)
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
                modifier = Modifier.align(Alignment.CenterVertically),
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
                        lineData = lineData.copy(lineList = lineData.lineList?.map { line ->
                            line.copy(pointList = mutableListOf())
                        } as MutableList?)
                        val startTime = System.currentTimeMillis()
                        CoroutineScope(Dispatchers.Default).launch {
                            // 启动数据生成，正弦波数据
                            job = scope.launch {
                                val amplitude = 200.0
                                val frequency = 0.2

                                timerFlow.take(max.toInt()) // 限制生成max次
                                    .collect { i ->
                                        val newPoints = (0 until 10).map { j ->
                                            val x = (i * 1 + j).toDouble()
                                            val y =
                                                amplitude * sin(2 * Math.PI * frequency * x / 100.0)
                                            Point(x.toFloat(), y.toFloat())
                                        }

                                        lineData =
                                            lineData.copy(lineList = lineData.lineList?.map { line ->
                                                line.copy(pointList = (line.pointList + newPoints) as MutableList<Point>)
                                            } as MutableList<Line>?)
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
                }) {
                Text(if (isRunning) "Stop" else "Start")
            }


        }


    }
}


@Composable
@Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun LineChartPreviewSelfDefined() {
    BrianChartTheme {
        Surface {
            val context = LocalContext.current
            val list = getTestLineListSelfDefined(context)
            LineChart(
                data = LineChartData(
                    lineList = list, xAxis = Axis(
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
                    ), isScroll = true
                )
            )
        }
    }
}

@Composable
@Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun LineChartPreviewChunk() {
    BrianChartTheme {
        Surface {
            val context = LocalContext.current
            LineChart(
                data = LineChartData(
                    lineList = null,
                    xAxis = Axis(
                        max = 500f,
                        min = 200f,
                        scaleInterval = 20f,
                        labelInterval = 100f,
                        chunkList = mutableListOf(
                            Chunk(200f, 300f, Color.Red.copy(alpha = 0.5f)),
                            Chunk(400f, 500f, Color.Blue.copy(alpha = 0.5f))
                        ),
                        name = "",
                    ),

                    yLeftAxis = Axis(
                        max = 800f,
                        min = 300f,
                        scaleInterval = 10f,
                        labelInterval = 50f,
                        chunkList = mutableListOf(
                            Chunk(400f, 420f, Color.Red.copy(alpha = 0.5f)),
                            Chunk(600f, 700f, Color.Blue.copy(alpha = 0.5f))
                        ),
                        name = "",
                    ),
                )
            )
        }
    }
}

@Composable
@Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun ChartWithTouchPreview() {
    BrianChartTheme {
        Surface {
            ChartWithTouch(
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .height(300.dp)
            )
        }
    }
}

/**
 * 触摸交互示例 - 拖动显示限制线
 */
@Composable
fun ChartWithTouch(modifier: Modifier) {

    // 将静态图表数据与动态限制线分离，避免在拖拽时重建整个 LineChartData 导致卡顿
    // 使用 mutableStateOf 包装 lineData，确保对 lineData 重新赋值时会触发重组
    var lineData by remember {
        mutableStateOf(
            LineChartData(
                lineList = listOf(
                    Line(
                        pointList = mutableListOf(
                            Point(0f, 10f),
                            Point(25f, 80f),
                            Point(50f, 40f),
                            Point(75f, 120f),
                            Point(100f, 90f),
                            Point(125f, 160f),
                            Point(150f, 130f),
                            Point(175f, 200f),
                            Point(200f, 170f)
                        ), color = Color(0xff4A90E2), isDrawCubic = true, isDrawPath = true
                    )
                ), xAxis = Axis(
                    max = 200f,
                    min = 0f,
                    scaleInterval = 20f,
                    labelInterval = 50f,
                    name = "时间 (s)",
                    // 使用原有的 limitLineList 字段，初始化为空列表以便后续就地更新
                    limitLineList = mutableListOf()
                ), yLeftAxis = Axis(
                    max = 250f, min = 0f, scaleInterval = 25f, labelInterval = 50f, name = "数值"
                )
            )
        )
    }


    var selectedX by remember { mutableStateOf<Float?>(200f) }

    fun limitLineValue(drawScope: DrawScope, start: Offset, end: Offset, limitLine: LimitLine) {
        drawScope.apply {
            drawLine(
                start = start, end = end, brush = Brush.linearGradient(       // 使用 brush 而不是 color
                    colors = listOf(Color.Red, Color.Green),
                    start = start,
                    end = end
                ), strokeWidth = limitLine.width.toPx()
            )
            drawCircle(
                color = Color.Cyan.copy(0.6f), radius = limitLine.width.toPx() * 2, center = end
            )
            drawCircle(color = Color.White, radius = 2.dp.toPx(), center = end)
        }

    }

    fun updateLimitLine(x: Float?) {
        val min = lineData.xAxis.min
        val max = lineData.xAxis.max
        val clamped = x?.coerceIn(min, max)
        val list = if (clamped != null) mutableListOf(
            LimitLine(
                clamped,
                color = Color.Red,
                width = 2.dp,
                text = "X=%.1f".format(clamped),
                selfDefinedValue = ::limitLineValue
            )
        ) else mutableListOf()
//        lineData.xAxis.limitLineList = list
        lineData = lineData.copy(xAxis = lineData.xAxis.copy(limitLineList = list))
    }

    // 初始同步（若 selectedX 有初始值）
    LaunchedEffect(Unit) {
        updateLimitLine(selectedX)
    }

    Column(modifier = modifier.padding(8.dp)) {
        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f), data = lineData,
            // 不再使用 dynamicLimitLines，改为直接更新 data.xAxis.limitLineList
            onTouch = { touchEvent: TouchEventData ->
                updateLimitLine(touchEvent.dataX)

            })

    }
}
