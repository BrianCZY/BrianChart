package com.czy.brianchart

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hxj.chart.compose.view.chart.Axis
import com.hxj.chart.compose.view.chart.GridLine
import com.hxj.chart.compose.view.chart.LineChart
import com.hxj.chart.compose.view.chart.LineChartData
import com.hxj.chart.compose.view.chart.getTestLineList

@Composable
fun ChartView() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
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

@Composable
@Preview
fun ChartViewPreview() {
    ChartView()
}