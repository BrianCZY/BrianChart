package com.czy.brianchart

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.czy.brianchart.ui.components.TopBar
import com.czy.brianchart.ui.navigation.ChartNavigationActions
import com.hxj.chart.compose.view.chart.EcgChart
import com.hxj.chart.compose.view.chart.EcgChartData
import com.hxj.chart.compose.view.chart.GrideDataSet
import com.hxj.chart.compose.view.chart.LineDataSet

@Composable
fun EcgChartPage(navigationActions: ChartNavigationActions? = null) {
    EcgChartView(modifier = Modifier.fillMaxSize(), backClick = {
        navigationActions?.navigateBack()
    })

}

@Composable
fun EcgChartView(modifier: Modifier, backClick: () -> Unit?) {
    Surface(modifier = modifier) {
        Column {
            TopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp)
                    .height(48.dp),
                title = "EcgChart"
            ) { backClick?.invoke() }
            HorizontalDivider(thickness = 1.dp)
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                EcgChart1(modifier = Modifier.fillMaxWidth().height(200.dp))
                HorizontalDivider(thickness = 8.dp)
                EcgChart2(modifier = Modifier.fillMaxWidth().height(200.dp))
            }
        }
    }
}


@Composable

fun EcgChart1(modifier: Modifier) {

    EcgChart(
        modifier = modifier,
        data = EcgChartData(
            lineDataSet = LineDataSet(),
            grideDataSet = GrideDataSet(color = Color.Red, width = 2.dp)
        )
    )


}

@Composable

fun EcgChart2(modifier: Modifier) {

    EcgChart(
        modifier = modifier,
        data = EcgChartData(
            lineDataSet = LineDataSet(),
            grideDataSet = GrideDataSet(color = Color.Gray, width = 2.dp)
        )
    )


}

@Composable
@Preview
fun EcgChartPreview() {
    EcgChartPage()
}