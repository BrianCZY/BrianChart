package com.czy.brianchart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.czy.brianchart.ui.theme.BrianChartTheme
import com.hxj.chart.compose.view.chart.Axis
import com.hxj.chart.compose.view.chart.GridLine
import com.hxj.chart.compose.view.chart.LineChart
import com.hxj.chart.compose.view.chart.LineChartData
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

@Composable
fun ChartView() {

    Surface {
        Column {
            Text(
                "BrianChart",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .height(48.dp)
                    .wrapContentSize(Alignment.Center)
            )
            HorizontalDivider(thickness = 1.dp)
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
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
@Preview(heightDp = 3800)
fun ChartViewLongPreview() {
    BrianChartTheme {
        ChartView()
    }

}