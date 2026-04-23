package com.czy.brianchart.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.czy.brianchart.ui.components.TopBar
import com.czy.brianchart.ui.navigation.ChartNavigationActions
import com.brian.chart.compose.view.chart.Axis
import com.brian.chart.compose.view.chart.BarChart
import com.brian.chart.compose.view.chart.BarChartData
import com.brian.chart.compose.view.chart.BarData
import com.brian.chart.compose.view.chart.BarDataSet
import com.brian.chart.compose.view.chart.BarEntry
import java.text.SimpleDateFormat
import kotlin.math.abs

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
                HorizontalDivider(thickness = 8.dp)
                BarChart5(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
                HorizontalDivider(thickness = 8.dp)
                BarChart6(
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
fun BarChart5(modifier: Modifier) {

    var barData = BarData()
    barData = getTestBarDataNoValue()
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
fun BarChart6(modifier: Modifier) {

    var barData = BarData()
    barData = getTestBarDataNoValueNoAxis()
    val listChunk = getTestChunkList()
    val limitLineList = getTestLimitLineList()
    val xLimitLineList = getTestXLimitLineList()
    BarChart(
        modifier = modifier,
        data = BarChartData(
            barData = barData,

            xAxis = Axis(
                max = 5f,
                isDrawAxis = false,
            ),

            yLeftAxis = Axis(
                max = 200f,
                isDrawAxis = false,
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


fun getTestBarData(): BarData {
    val barData = BarData(width = 80.dp)
    var barDataSetListTemp: MutableList<BarDataSet> = mutableListOf()
    var barEntryList: MutableList<BarEntry> = mutableListOf()
    barEntryList.add(BarEntry(1f, 60f))
    barEntryList.add(BarEntry(2f, 200f))

    barDataSetListTemp.add(
        BarDataSet(
            barEntryList = barEntryList, color = Color.Gray, background = background1
        )
    )

    barData.barDataSetList = barDataSetListTemp
    return barData
}

fun getTestBarData2(): BarData {
    val barData = BarData()
    var barDataSetListTemp: MutableList<BarDataSet> = mutableListOf()
    var barEntryList: MutableList<BarEntry> = mutableListOf()
    barEntryList.add(BarEntry(1f, 60f))
    barEntryList.add(BarEntry(2f, 200f))

    barDataSetListTemp.add(
        BarDataSet(
            name = "CHO",
            barEntryList = barEntryList,
            color = Color.Gray,
            background = background1,
            settingValueText = ::settingValueText2//定制顶部的值显示
        )
    )
    var barEntryList2: MutableList<BarEntry> = mutableListOf()

    barEntryList2.add(BarEntry(1f, 80f))
    barEntryList2.add(BarEntry(2f, 250f))

    barDataSetListTemp.add(
        BarDataSet(
            name = "FAT",
            barEntryList = barEntryList2,
            color = Color.Gray,
            background = background2,
            settingValueText = ::settingValueText2//定制顶部的值显示
        )
    )

    barData.barDataSetList = barDataSetListTemp
    return barData
}

fun getTestBarData3(): BarData {
    val barData = BarData()
    var barDataSetListTemp: MutableList<BarDataSet> = mutableListOf()
    var barEntryList: MutableList<BarEntry> = mutableListOf()
    barEntryList.add(BarEntry(1f, 60f))
    barEntryList.add(BarEntry(2f, 200f))

    barDataSetListTemp.add(
        BarDataSet(
            name = "CHO",
            barEntryList = barEntryList,
            color = Color.Gray,
            background = background1,
            settingValueText = ::settingValueText//定制顶部的值显示
        )
    )
    var barEntryList2: MutableList<BarEntry> = mutableListOf()

    barEntryList2.add(BarEntry(1f, 80f))
    barEntryList2.add(BarEntry(2f, 250f))

    barDataSetListTemp.add(
        BarDataSet(
            name = "FAT",
            barEntryList = barEntryList2,
            color = Color.Gray,
            background = background2,
            settingValueText = ::settingValueText//定制顶部的值显示
        )
    )
    var barEntryList3: MutableList<BarEntry> = mutableListOf()
    barEntryList3.add(BarEntry(1f, 50f))
    barEntryList3.add(BarEntry(2f, 150f))

    barDataSetListTemp.add(
        BarDataSet(
            name = "PRO",
            barEntryList = barEntryList3,
            color = Color.Gray,
            background = background3,
            settingValueText = ::settingValueText//定制顶部的值显示
        )
    )
    barData.barDataSetList = barDataSetListTemp
    return barData
}

fun getTestBarData4(): BarData {
    val barData = BarData(weight = 1f, dataSetPadding = 40.dp, width = 40.dp)
    var barDataSetListTemp: MutableList<BarDataSet> = mutableListOf()
    var barEntryList: MutableList<BarEntry> = mutableListOf()
    barEntryList.add(BarEntry(1f, 60f))
//    barEntryList.add(BarEntry(2f, 200f))

    barDataSetListTemp.add(
        BarDataSet(
            name = "CHO",
            barEntryList = barEntryList,
            color = Color.Gray,
            background = background1,
            settingValueText = ::settingValueText3//定制顶部的值显示
        )
    )
    var barEntryList2: MutableList<BarEntry> = mutableListOf()

    barEntryList2.add(BarEntry(1f, 80f))
//    barEntryList2.add(BarEntry(2f, 250f))

    barDataSetListTemp.add(
        BarDataSet(
            name = "FAT",
            barEntryList = barEntryList2,
            color = Color.Gray,
            background = background2,
            settingValueText = ::settingValueText3//定制顶部的值显示
        )
    )
    var barEntryList3: MutableList<BarEntry> = mutableListOf()
    barEntryList3.add(BarEntry(1f, 50f))
//    barEntryList3.add(BarEntry(2f, 150f))

    barDataSetListTemp.add(
        BarDataSet(
            name = "PRO",
            barEntryList = barEntryList3,
            color = Color.Gray,
            background = background3,
            settingValueText = ::settingValueText3//定制顶部的值显示
        )
    )
    barData.barDataSetList = barDataSetListTemp
    return barData
}

fun getTestBarDataNoValue(): BarData {
    val barData = BarData(width = 80.dp)
    var barDataSetListTemp: MutableList<BarDataSet> = mutableListOf()
    var barEntryList: MutableList<BarEntry> = mutableListOf()
    barEntryList.add(BarEntry(1f, 60f))
    barEntryList.add(BarEntry(2f, 200f))

    barDataSetListTemp.add(
        BarDataSet(
            barEntryList = barEntryList,
            color = Color.Gray,
            background = background1,
            showValue = false // 不显示数值
        )
    )

    barData.barDataSetList = barDataSetListTemp
    return barData
}

fun getTestBarDataNoValueNoAxis(): BarData {
    val barData = BarData(width = 80.dp)
    val barDataSetListTemp: MutableList<BarDataSet> = mutableListOf()
    val barEntryList: MutableList<BarEntry> = mutableListOf()
    barEntryList.add(BarEntry(1f, 60f))
    barEntryList.add(BarEntry(2f, 200f))

    barDataSetListTemp.add(
        BarDataSet(
            barEntryList = barEntryList,
            color = Color.Gray,
            background = background1,
            showValue = false // 不显示数值
        )
    )
    barData.barDataSetList = barDataSetListTemp
    return barData
}

val background1: ((drawScope: DrawScope, color: Color, offset: Offset, size: Size) -> Unit) =
    { drawScope, color, offset, size ->
        drawScope.run {
            drawRoundRect(
                color = color,
                topLeft = offset,
                size = size,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
                cornerRadius = CornerRadius(2f, 2f)
            )
        }
    }
val background2: ((drawScope: DrawScope, color: Color, offset: Offset, size: Size) -> Unit) =
    { drawScope, color, offset, size ->
        drawScope.run {
            drawRoundRect(
                color = color, topLeft = offset, size = size, cornerRadius = CornerRadius(2f, 2f)
            )
            drawRoundRect(
                color = color,
                topLeft = offset,
                size = size,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
                cornerRadius = CornerRadius(2f, 2f)
            )

        }
    }
val background3: ((drawScope: DrawScope, color: Color, offset: Offset, size: Size) -> Unit) =
    { drawScope, color, offset, size ->
        drawScope.run {
            val width = 2.dp.toPx()
            drawRoundRect(
                color = color,
                topLeft = offset,
                size = size,
                style = Stroke(width = width, cap = StrokeCap.Round),
                cornerRadius = CornerRadius(2f, 2f)
            )
            var heighTemp = size.height
//            val heightAbs = abs(heighTemp).toInt()
            val coefficient = if (heighTemp > 0) 1 else -1
            for (i in 0..abs(heighTemp).toInt() step 20) {
                drawLine(
                    start = Offset(x = offset.x, y = offset.y + i * coefficient),
                    end = Offset(x = offset.x + size.width, y = offset.y + i * coefficient),
                    color = color,
                    strokeWidth = width
                )
            }


        }
    }


fun settingValueText(name: String, value: Float) =
    if (name.isNullOrEmpty()) "$${value}" else "${name}:${value}"

fun settingValueText2(name: String, value: Float) =
    if (name.isNullOrEmpty()) "$${value}" else "${name}=${value}"

fun settingValueText3(name: String, value: Float) =
    if (name.isNullOrEmpty()) "${value}" else "${name}\n${value}"


fun settingLabelValue(value: Float): String {
    val label = when {
        value.toInt().toFloat() == value -> {//为整数浮点数
            "${value.toInt()}"
        }

        else -> {//为小数浮点数
            "${value}"
        }
    }
    return label
}

fun settingLabelValue2(value: Float): String {
    val time = System.currentTimeMillis()
    var valueStr = ""
    var dateFormatYMD = "yyyy-MM-dd"
    if (value > 0) {
        valueStr = getStringByFormat(
            (time + value * 24 * 60 * 60 * 1000).toLong(), dateFormatYMD
        ).toString()
    }
    return valueStr
}
/**
 * 描述：获取milliseconds表示的日期时间的字符串.
 *
 * @param format 格式化字符串，如："yyyy-MM-dd HH:mm:ss"
 * @return String 日期时间字符串
 */
fun getStringByFormat(milliseconds: Long, format: String?): String? {
    var thisDateTime: String? = null
    try {
        val mSimpleDateFormat = SimpleDateFormat(format)
        thisDateTime = mSimpleDateFormat.format(milliseconds)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return thisDateTime
}

@Composable
@Preview(showSystemUi = false, showBackground = true, widthDp = 1096, heightDp = 250)
fun BarChartPreview4() {
    MaterialTheme {
        Surface {
            var barData = BarData()
            barData = getTestBarData4()
            val listChunk = getTestChunkList()
            val limitLineList = getTestLimitLineList()
            val xLimitLineList = getTestXLimitLineList()
            BarChart(
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
    }
}

@Composable
@Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun BarChartPreview3() {
    MaterialTheme {
        Surface {
            var barData = BarData()
            barData = getTestBarData3()
            val listChunk = getTestChunkList()
            val limitLineList = getTestLimitLineList()
            val xLimitLineList = getTestXLimitLineList()
            BarChart(
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
    }
}

@Composable
@Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun BarChartPreview2() {
    MaterialTheme {
        Surface {
            var barData = BarData()
            barData = getTestBarData2()
            val listChunk = getTestChunkList()
            val limitLineList = getTestLimitLineList()
            val xLimitLineList = getTestXLimitLineList()
            BarChart(
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
    }
}

@Composable
@Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun BarChartPreview1() {
    MaterialTheme {
        Surface {
            var barData = BarData()
            barData = getTestBarData()
            val listChunk = getTestChunkList()
            val limitLineList = getTestLimitLineList()
            val xLimitLineList = getTestXLimitLineList()
            BarChart(
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
    }
}

@Composable
@Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun BarChartPreviewNoValue() {
    MaterialTheme {
        Surface {
            val barData = BarData(width = 80.dp)
            val barDataSetListTemp: MutableList<BarDataSet> = mutableListOf()
            val barEntryList: MutableList<BarEntry> = mutableListOf()
            barEntryList.add(BarEntry(1f, 60f))
            barEntryList.add(BarEntry(2f, 200f))

            barDataSetListTemp.add(
                BarDataSet(
                    barEntryList = barEntryList,
                    color = Color.Gray,
                    background = background1,
                    showValue = false // 不显示数值
                )
            )
            barData.barDataSetList = barDataSetListTemp

            BarChart(
                data = BarChartData(
                    barData = barData,
                    xAxis = Axis(
                        max = 5f,
                        scaleInterval = 10f,
                        labelInterval = 10f,
                        name = "x轴"
                    ),
                    yLeftAxis = Axis(
                        max = 300f,
                        scaleInterval = 10f,
                        labelInterval = 50f,
                        name = "y轴"
                    ),
                )
            )
        }
    }
}

@Composable
@Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun BarChartPreviewNoValueNoAxis() {
    MaterialTheme {
        Surface {
            val barData = BarData(width = 80.dp)
            val barDataSetListTemp: MutableList<BarDataSet> = mutableListOf()
            val barEntryList: MutableList<BarEntry> = mutableListOf()
            barEntryList.add(BarEntry(1f, 60f))
            barEntryList.add(BarEntry(2f, 200f))

            barDataSetListTemp.add(
                BarDataSet(
                    barEntryList = barEntryList,
                    color = Color.Gray,
                    background = background1,
                    showValue = false // 不显示数值
                )
            )
            barData.barDataSetList = barDataSetListTemp

            BarChart(
                data = BarChartData(
                    barData = barData,
                    xAxis = Axis(
                        max = 5f,
                        isDrawAxis = false,
                    ),
                    yLeftAxis = Axis(
                        max = 200f,
                        isDrawAxis = false,
                    ),
                )
            )
        }
    }
}
