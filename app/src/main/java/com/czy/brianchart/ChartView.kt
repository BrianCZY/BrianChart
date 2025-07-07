package com.czy.brianchart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.czy.brianchart.ui.theme.BrianChartTheme
import com.hxj.chart.compose.view.chart.Axis
import com.hxj.chart.compose.view.chart.GridLine
import com.hxj.chart.compose.view.chart.Line
import com.hxj.chart.compose.view.chart.LineChart
import com.hxj.chart.compose.view.chart.LineChartData
import com.hxj.chart.compose.view.chart.Point
import com.hxj.chart.compose.view.chart.getTestChunkList
import com.hxj.chart.compose.view.chart.getTestChunkList1
import com.hxj.chart.compose.view.chart.getTestChunkList2
import com.hxj.chart.compose.view.chart.getTestChunkList3
import com.hxj.chart.compose.view.chart.getTestLimitLineList
import com.hxj.chart.compose.view.chart.getTestLineList
import com.hxj.chart.compose.view.chart.getTestLineList2
import com.hxj.chart.compose.view.chart.getTestPlusOrMinusLimitLineList
import com.hxj.chart.compose.view.chart.getTestPlusOrMinusLineList
import com.hxj.chart.compose.view.chart.getTestPointLineList
import com.hxj.chart.compose.view.chart.getTestXChunkList
import com.hxj.chart.compose.view.chart.getTestXLimitLineList
import com.hxj.chart.compose.view.chart.getTestXLimitLineList1
import com.hxj.chart.compose.view.chart.getTestYLimitLineList1
import com.hxj.chart.compose.view.chart.getTestYLimitLineList2
import com.hxj.chart.compose.view.chart.getTestYLimitLineList3
import com.hxj.chart.compose.view.chart.settingLineChartLabelValue
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlin.math.sin

@Composable
fun ChartView() {

    Surface {
        Column {
            Text(
                "BrianChart",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 20.dp)
                    .height(48.dp)
                    .wrapContentSize(Alignment.Center)
            )
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
                    "静态示例", modifier = Modifier
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
        modifier = modifier,
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

@Composable
fun Chart5(modifier: Modifier) {
    val list = getTestPlusOrMinusLineList()
    val limitLineList = getTestPlusOrMinusLimitLineList()
    LineChart(
        modifier = modifier,
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

@Composable
fun Chart4(modifier: Modifier) {
    val list = getTestLineList()
    LineChart(
        modifier = modifier
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

@Composable
fun Chart3(modifier: Modifier) {
    val list = getTestLineList()
    val listChunk = getTestChunkList()
    val listChunkX = getTestXChunkList()
    val limitLineList = getTestLimitLineList()
    val xLimitLineList = getTestXLimitLineList()
    LineChart(
        modifier = modifier,
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

@Composable
fun Chart2(modifier: Modifier) {
    val list = getTestLineList()
    val listChunk = getTestChunkList()
    val listChunkX = getTestXChunkList()
    val limitLineList = getTestLimitLineList()
    val xLimitLineList = getTestXLimitLineList()
    LineChart(
        modifier = modifier,
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
        ),
        modifier = modifier
    )
}

@Composable
@Preview
fun ChartViewPreview() {
    BrianChartTheme {
        ChartView()
    }

}

@Composable
@Preview(heightDp = 2000)
fun ChartViewLongPreview() {
    BrianChartTheme {
        ChartView()
    }

}