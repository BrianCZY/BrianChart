package com.czy.brianchart.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.czy.brianchart.ui.components.TopBar
import com.czy.brianchart.ui.navigation.ChartNavigationActions
import com.hxj.chart.compose.view.chart.Axis
import com.hxj.chart.compose.view.chart.BarChart
import com.hxj.chart.compose.view.chart.BarChartData
import com.hxj.chart.compose.view.chart.BarData
import com.hxj.chart.compose.view.chart.getTestBarData
import com.hxj.chart.compose.view.chart.getTestBarData2
import com.hxj.chart.compose.view.chart.getTestBarData3
import com.hxj.chart.compose.view.chart.getTestBarData4
import com.hxj.chart.compose.view.chart.getTestChunkList
import com.hxj.chart.compose.view.chart.getTestLimitLineList
import com.hxj.chart.compose.view.chart.getTestXLimitLineList
import com.hxj.chart.compose.view.chart.settingLabelValue
import com.hxj.chart.compose.view.chart.settingLabelValue2
import androidx.compose.runtime.getValue

@Composable
fun BarChartPage(navigationActions: ChartNavigationActions? = null) {
    val barChartViewModel: BarChartViewModel = viewModel()
    val barChartUIState by barChartViewModel.barChartUIState.collectAsStateWithLifecycle()
    BarChartView(barChartUIState = barChartUIState, modifier = Modifier.fillMaxSize(), backClick = {
        navigationActions?.navigateBack()
    })

}

@Composable
fun BarChartView(modifier: Modifier, barChartUIState: BarChartUIState, backClick: () -> Unit?) {
    Surface(modifier = modifier) {
        Column(Modifier.fillMaxSize()) {
            TopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp)
                    .height(48.dp),
                title = "BarChart"
            ) { backClick?.invoke() }
            HorizontalDivider(thickness = 1.dp)
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                BarChart1(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                HorizontalDivider(thickness = 8.dp)
                BarChart2(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                HorizontalDivider(thickness = 8.dp)
                BarChart3(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                HorizontalDivider(thickness = 8.dp)
                BarChart4(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 40.dp)
                        .height(200.dp)
                )

            }
        }
    }
}

@Composable
fun BarChart1(modifier: Modifier) {

    var barData = BarData()
    barData = getTestBarData()
    val listChunk = getTestChunkList()
    val limitLineList = getTestLimitLineList()
    val xLimitLineList = getTestXLimitLineList()
    BarChart(
        modifier = modifier,
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

@Composable
fun BarChart2(modifier: Modifier) {

    var barData = BarData()
    barData = getTestBarData2()
    val listChunk = getTestChunkList()
    val limitLineList = getTestLimitLineList()
    val xLimitLineList = getTestXLimitLineList()
    BarChart(
        modifier = modifier,
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

@Composable
fun BarChart3(modifier: Modifier) {

    var barData = BarData()
    barData = getTestBarData3()
    val listChunk = getTestChunkList()
    val limitLineList = getTestLimitLineList()
    val xLimitLineList = getTestXLimitLineList()
    BarChart(
        modifier = modifier,
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

@Composable
fun BarChart4(modifier: Modifier) {

    var barData = BarData()
    barData = getTestBarData4()
    val listChunk = getTestChunkList()
    val limitLineList = getTestLimitLineList()
    val xLimitLineList = getTestXLimitLineList()
    BarChart(
        modifier = modifier,
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


@Composable
@Preview
fun BarChartPagePreview() {
    BarChartPage()
}

@Composable
@Preview(heightDp = 1200)
fun BarChartPageLongPreview() {
    BarChartPage()
}