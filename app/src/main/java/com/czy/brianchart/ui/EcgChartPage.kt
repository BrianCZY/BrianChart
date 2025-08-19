package com.czy.brianchart.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.czy.brianchart.data.testdata.BERRY_MED_WAVE_DATA_LIST
import com.czy.brianchart.data.testdata.RD2000_WAVE_AVFDATA_LIST
import com.czy.brianchart.data.testdata.RD2000_WAVE_AVLDATA_LIST
import com.czy.brianchart.data.testdata.RD2000_WAVE_AVRDATA_LIST
import com.czy.brianchart.data.testdata.RD2000_WAVE_IDATA_LIST
import com.czy.brianchart.data.testdata.RD2000_WAVE_IIDATA_LIST
import com.czy.brianchart.data.testdata.RD2000_WAVE_IIIDATA_LIST
import com.czy.brianchart.data.testdata.RD2000_WAVE_V1DATA_LIST
import com.czy.brianchart.ui.components.TopBar
import com.czy.brianchart.ui.navigation.ChartNavigationActions
import com.hxj.chart.compose.view.chart.EcgChart
import com.hxj.chart.compose.view.chart.EcgChartData
import com.hxj.chart.compose.view.chart.GrideDataSet
import com.hxj.chart.compose.view.chart.LineDataSet
import com.hxj.chart.compose.view.chart.PointDataSet
import com.hxj.chart.compose.view.chart.colorFFFFC4C3
import androidx.compose.runtime.getValue

@Composable
fun EcgChartPage(navigationActions: ChartNavigationActions? = null) {
    var ecgChartViewModel:EcgChartViewModel = viewModel()
    val ecgChartUIState by ecgChartViewModel.ecgChartUIState.collectAsStateWithLifecycle()
    EcgChartView(modifier = Modifier.fillMaxSize(),ecgChartUIState = ecgChartUIState, backClick = {
        navigationActions?.navigateBack()
    })

}

@Composable
fun EcgChartView(modifier: Modifier, ecgChartUIState: EcgChartUIState, backClick: () -> Unit?) {
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
                EcgChart1(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                HorizontalDivider(thickness = 8.dp)
                EcgChart2(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                HorizontalDivider(thickness = 8.dp)
                EcgChartSevenLead(
                    modifier = Modifier
                        .fillMaxWidth()
                )
                HorizontalDivider(thickness = 8.dp)
                EcgChartOne(modifier = Modifier.height(200.dp))
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
fun EcgChartOne(modifier: Modifier) {
    Box(modifier = modifier) {
        EcgChart(
            modifier = Modifier
                .padding(8.dp)
                .height(180.dp)
                .aspectRatio(20 / 8f) // 其中：（20 /8）  即是 x：20个格子 / y：8个格子   time:50*0.2s
            , data = EcgChartData(
                ecgWaveLists = getTestWaveList().map { it.iterator() },
                lineDataSet = LineDataSet(

                    onSecondDataNum = 250
                ),//y 8个格子，
                grideDataSet = GrideDataSet(color = colorFFFFC4C3, width = 1.dp),
                pointDataSet = PointDataSet(radius = 0.5.dp, color = colorFFFFC4C3)
            )

        )
    }


}

@Composable

fun EcgChartSevenLead(modifier: Modifier) {

    Box(modifier = modifier) {

        EcgChart(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .aspectRatio(10 / 8f) // 其中：（50 /8）  即是 x：30个格子 / y：8个格子   time:50*0.2s
            , data = EcgChartData(
                ecgWaveLists = listOf(
                    RD2000_WAVE_IDATA_LIST.map { it.toFloat() }.iterator(),
                    RD2000_WAVE_IIDATA_LIST.map { it.toFloat() }.iterator(),
                    RD2000_WAVE_IIIDATA_LIST.map { it.toFloat() }.iterator(),
                    RD2000_WAVE_AVRDATA_LIST.map { it.toFloat() }.iterator(),
                    RD2000_WAVE_AVLDATA_LIST.map { it.toFloat() }.iterator(),
                    RD2000_WAVE_AVFDATA_LIST.map { it.toFloat() }.iterator(),
                    RD2000_WAVE_V1DATA_LIST.map { it.toFloat() }.iterator(),
                ),
                lineDataSet = LineDataSet(

                    onSecondDataNum = 200,
                    leadCellNum = 4,
                    yCellNum = 4 * 7
                ),//y 8个格子，
                grideDataSet = GrideDataSet(color = colorFFFFC4C3, width = 1.dp),
                pointDataSet = PointDataSet(radius = 0.5.dp, color = colorFFFFC4C3)
            )

        )
    }


}

fun getTestWaveList(): List<MutableList<Float>> {
//    1倍增益下，128是基线，70（128+70）对应1mv
    val list = BERRY_MED_WAVE_DATA_LIST.map { (it - 128f) / 70f }.toMutableList()
    return listOf(list)
}



@Composable
@Preview
fun EcgChartPagePreview() {
    EcgChartPage()
}