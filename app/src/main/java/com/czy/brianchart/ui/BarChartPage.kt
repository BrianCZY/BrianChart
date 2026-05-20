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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                HorizontalDivider(thickness = 8.dp)
                BarChart7(
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
    getTestLimitLineList()
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
    getTestChunkList()
    getTestLimitLineList()
    getTestXLimitLineList()
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
    getTestChunkList()
    getTestLimitLineList()
    getTestXLimitLineList()
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
    getTestChunkList()
    getTestLimitLineList()
    getTestXLimitLineList()
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
    getTestLimitLineList()
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
    getTestChunkList()
    getTestLimitLineList()
    getTestXLimitLineList()
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
fun BarChart7(modifier: Modifier) {
    var barData = BarData()
    barData = getTestStackedBarData()

    BarChart(
        modifier = modifier,
        data = BarChartData(
            barData = barData,
            xAxis = Axis(
                max = 5f,
                scaleInterval = 1f,
                labelInterval = 1f,
                position = 0f,
                name = "月份",
            ),
            yLeftAxis = Axis(
                max = 200f,
                min = -50f, // 支持负值
                scaleInterval = 50f,
                labelInterval = 50f,
                name = "金额",
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
            valueColor = Color.Red,
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

fun getTestStackedBarData(): BarData {
    val barData = BarData(width = 80.dp)
    val barDataSetListTemp: MutableList<BarDataSet> = mutableListOf()

    // ========== 第一个数据集：销售额（全部正值）==========
    val barEntryList1: MutableList<BarEntry> = mutableListOf()

    // x=1: 产品A(50) + 产品B(30) + 产品C(20) = 100
    barEntryList1.add(
        BarEntry(
            x = 1f,
            y = 100f,
            stackValues = listOf(50f, 30f, 20f)
        )
    )

    // x=2: 80 + 50 + 40 = 170
    barEntryList1.add(
        BarEntry(
            x = 2f,
            y = 170f,
            stackValues = listOf(80f, 50f, 40f)
        )
    )

    // x=3: 60 + 70 + 30 = 160
    barEntryList1.add(
        BarEntry(
            x = 3f,
            y = 160f,
            stackValues = listOf(60f, 70f, 30f)
        )
    )

    // x=4: 90 + 40 + 60 = 190
    barEntryList1.add(
        BarEntry(
            x = 4f,
            y = 190f,
            stackValues = listOf(90f, 40f, 60f)
        )
    )

    barDataSetListTemp.add(
        BarDataSet(
            name = "销售额",
            barEntryList = barEntryList1,
            color = Color.Blue,
            background = background2,
            showValue = true,
            valueColor = Color.White, // 数值文字颜色
            stackColors = listOf(
                Color.Blue.copy(alpha = 0.8f),
                Color.Red.copy(alpha = 0.8f),
                Color.Yellow.copy(alpha = 0.8f)
            ),
            stackValueColors = listOf(
                Color.White,
                Color.White,
                Color.Black // 黄色背景用黑色文字更清晰
            ),
            settingValueText = { name, value -> "${value.toInt()}" }
        )
    )

    // ========== 第二个数据集：利润（有正有负）==========
    val barEntryList2: MutableList<BarEntry> = mutableListOf()

    // 定义每层的颜色
    val profitColors = listOf(
        Color(0xFF4CAF50).copy(alpha = 0.8f), // 收入 - 绿色
        Color(0xFFF44336).copy(alpha = 0.8f), // 成本 - 红色
        Color(0xFFFF9800).copy(alpha = 0.8f)  // 税费 - 橙色
    )

    // 定义每层的数值文字颜色
    val profitValueColors = listOf(
        Color.White,  // 绿色背景用白色
        Color.White,  // 红色背景用白色
        Color.White   // 橙色背景用白色
    )

    barEntryList2.add(
        BarEntry(
            x = 1f,
            y = 10f,
            stackValues = listOf(40f, 20f, 10f)
        )
    )

    barEntryList2.add(
        BarEntry(
            x = 2f,
            y = 15f,
            stackValues = listOf(60f, 30f, 15f)
        )
    )

    barEntryList2.add(
        BarEntry(
            x = 3f,
            y = -10f,
            stackValues = listOf(50f, 40f, 20f)
        )
    )

    barEntryList2.add(
        BarEntry(
            x = 4f,
            y = 30f,
            stackValues = listOf(-20f, -25f, -15f)
        )
    )

    barDataSetListTemp.add(
        BarDataSet(
            name = "利润",
            barEntryList = barEntryList2,
            color = Color.Green,
            showValue = true,
            background = background2,
            valueColor = Color.White, // 数值文字颜色
            stackColors = profitColors,           // ⭐ 在 DataSet 级别配置每层颜色
            stackValueColors = profitValueColors, // ⭐ 在 DataSet 级别配置每层数值颜色
            settingValueText = { name, value -> "${value.toInt()}" }
        )
    )

    barData.barDataSetList = barDataSetListTemp
    return barData
}

/**
 * 测试数据：使用 stackRenderer 自定义堆积图样式
 */
fun getTestStackedBarDataWithCustomRenderer(): BarData {
    val barData = BarData(width = 80.dp)
    val barDataSetListTemp: MutableList<BarDataSet> = mutableListOf()

    val barEntryList: MutableList<BarEntry> = mutableListOf()

    // x=1: 使用 stackRenderer 自定义每层样式
    barEntryList.add(
        BarEntry(
            x = 1f,
            y = 100f,
            stackValues = listOf(50f, 30f, 20f),
            stackRenderer = { drawScope, color, offset, size, value, name, valueRelativeToXAxis, stackIndex ->
                with(drawScope) {
                    // 根据 stackIndex 为不同层设置不同的圆角和样式
                    when (stackIndex) {
                        0 -> {
                            // 第1层：大圆角，蓝色
                            drawRoundRect(
                                color = Color(0xFF2196F3),
                                topLeft = offset,
                                size = size,
                                cornerRadius = CornerRadius(12f, 12f)
                            )
                        }
                        1 -> {
                            // 第2层：无圆角，绿色
                            drawRoundRect(
                                color = Color(0xFF4CAF50),
                                topLeft = offset,
                                size = size,
                                cornerRadius = CornerRadius(0f, 0f)
                            )
                        }
                        2 -> {
                            // 第3层：小圆角，橙色
                            drawRoundRect(
                                color = Color(0xFFFF9800),
                                topLeft = offset,
                                size = size,
                                cornerRadius = CornerRadius(6f, 6f)
                            )
                        }
                    }

                    // 绘制数值（白色文字）
                    val label = "${value.toInt()}"
                    val valueTextSizePx = 10.sp.toPx()
                    val nativePaint = android.graphics.Paint().let {
                        it.apply {
                            textSize = valueTextSizePx
                            setColor(Color.White.toArgb())
                            isAntiAlias = true
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    }
                    val textX = offset.x + size.width / 2
                    val textY = offset.y + size.height / 2 + valueTextSizePx / 3
                    drawContext.canvas.nativeCanvas.drawText(label, textX, textY, nativePaint)
                }
            }
        )
    )

    // x=2: 使用默认样式（不设置 stackRenderer）
    barEntryList.add(
        BarEntry(
            x = 2f,
            y = 150f,
            stackValues = listOf(60f, 50f, 40f)
        )
    )

    // x=3: 另一个自定义样式的柱子
    barEntryList.add(
        BarEntry(
            x = 3f,
            y = 120f,
            stackValues = listOf(40f, 40f, 40f),
            stackRenderer = { drawScope, color, offset, size, value, name, valueRelativeToXAxis, stackIndex ->
                with(drawScope) {
                    // 所有层使用相同的渐变效果
                    drawRoundRect(
                        color = when (stackIndex) {
                            0 -> Color(0xFF9C27B0) // 紫色
                            1 -> Color(0xFFE91E63) // 粉色
                            else -> Color(0xFFF44336) // 红色
                        },
                        topLeft = offset,
                        size = size,
                        cornerRadius = CornerRadius(4f, 4f)
                    )

                    // 添加边框
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.5f),
                        topLeft = offset,
                        size = size,
                        style = Stroke(width = 2f),
                        cornerRadius = CornerRadius(4f, 4f)
                    )

                    // 绘制数值
                    val label = "${value.toInt()}"
                    val valueTextSizePx = 10.sp.toPx()
                    val nativePaint = android.graphics.Paint().let {
                        it.apply {
                            textSize = valueTextSizePx
                            setColor(Color.White.toArgb())
                            isAntiAlias = true
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    }
                    val textX = offset.x + size.width / 2
                    val textY = offset.y + size.height / 2 + valueTextSizePx / 3
                    drawContext.canvas.nativeCanvas.drawText(label, textX, textY, nativePaint)
                }
            }
        )
    )

    barDataSetListTemp.add(
        BarDataSet(
            name = "自定义堆积",
            barEntryList = barEntryList,
            color = Color.Blue,
            showValue = false, // 关闭默认数值显示，因为 stackRenderer 中已经绘制
            settingValueText = { name, value -> "${value.toInt()}" }
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
            getTestChunkList()
            getTestLimitLineList()
            getTestXLimitLineList()
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
            getTestChunkList()
            getTestLimitLineList()
            getTestXLimitLineList()
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
            getTestChunkList()
            getTestLimitLineList()
            getTestXLimitLineList()
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
            getTestLimitLineList()
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
            barEntryList.add(BarEntry(2f, -100f))

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
                        labelInterval = 20f,
                        position = 0f,
                        name = "x轴"
                    ),
                    yLeftAxis = Axis(
                        max = 200f,
                        min = -200f,
                        scaleInterval = 10f,
                        labelInterval = 100f,
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

@Composable
@Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 300)
fun BarChartPreviewRenderer() {
    val density = androidx.compose.ui.platform.LocalDensity.current

    MaterialTheme {
        Surface {
            val barData = BarData(width = 60.dp)
            val barDataSetListTemp: MutableList<BarDataSet> = mutableListOf()
            val barEntryList: MutableList<BarEntry> = mutableListOf()

            // 第一个柱子：使用自定义渲染器（同时绘制柱子和数值）
            barEntryList.add(
                BarEntry(
                    x = 1f,
                    y = 150f,
                    renderer = { drawScope, color, offset, size, value, name, valueRelativeToXAxis ->
                        with(density) {
                            // 绘制渐变效果的柱状图
                            drawScope.drawRoundRect(
                                color = Color.Blue.copy(alpha = 0.7f),
                                topLeft = offset,
                                size = size,
                                cornerRadius = CornerRadius(8f, 8f)
                            )
                            // 在柱子顶部添加装饰
                            drawScope.drawCircle(
                                color = Color.Blue,
                                radius = 8f,
                                center = Offset(offset.x + size.width / 2, offset.y)
                            )

                            // 绘制数值（柱子内部居中）
                            val label = "${value.toInt()}"
                            val valueTextSizePx = 10.sp.toPx()
                            val nativePaint = android.graphics.Paint().let {
                                it.apply {
                                    textSize = valueTextSizePx
                                    setColor(Color.White.toArgb())
                                    isAntiAlias = true
                                }
                            }
                            val textX =
                                offset.x + size.width / 2 - (label.length * valueTextSizePx) / 4
                            val textY = offset.y + size.height / 2 + valueTextSizePx / 3
                            drawScope.drawContext.canvas.nativeCanvas.drawText(
                                label, textX, textY, nativePaint
                            )
                        }
                    }
                )
            )

            // 第二个柱子：使用默认绘制，不设置 renderer
            barEntryList.add(
                BarEntry(
                    x = 2f,
                    y = 100f
                    // 不设置 renderer，将使用 BarDataSet 的默认配置
                )
            )

            // 第三个柱子：负值，自定义样式
            barEntryList.add(
                BarEntry(
                    x = 3f,
                    y = -80f,
                    renderer = { drawScope, color, offset, size, value, name, valueRelativeToXAxis ->
                        with(density) {
                            // 负值用不同颜色
                            drawScope.drawRoundRect(
                                color = Color.Red.copy(alpha = 0.6f),
                                topLeft = offset,
                                size = size,
                                cornerRadius = CornerRadius(4f, 4f)
                            )

                            // 绘制数值（负值显示在更下方）
                            val label = "${value.toInt()}"
                            val valueTextSizePx = 10.sp.toPx()
                            val nativePaint = android.graphics.Paint().let {
                                it.apply {
                                    textSize = valueTextSizePx
                                    setColor(Color.Red.toArgb())
                                    isAntiAlias = true
                                }
                            }
                            val textX =
                                offset.x + size.width / 2 - (label.length * valueTextSizePx) / 4
                            val textY = offset.y + size.height + valueTextSizePx + 4f
                            drawScope.drawContext.canvas.nativeCanvas.drawText(
                                label, textX, textY, nativePaint
                            )
                        }
                    }
                )
            )

            barDataSetListTemp.add(
                BarDataSet(
                    barEntryList = barEntryList,
                    color = Color.Gray,
                    showValue = true
                )
            )
            barData.barDataSetList = barDataSetListTemp

            BarChart(
                data = BarChartData(
                    barData = barData,
                    xAxis = Axis(
                        max = 5f,
                        scaleInterval = 1f,
                        labelInterval = 1f,
                        position = 0f,
                        name = "X轴"
                    ),
                    yLeftAxis = Axis(
                        max = 200f,
                        min = -200f,
                        scaleInterval = 10f,
                        labelInterval = 100f,
                        name = "Y轴"
                    ),
                )
            )
        }
    }
}

@Composable
@Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 300)
fun BarChartPreviewStacked() {
    MaterialTheme {
        Surface {
            val barData = getTestStackedBarData()

            BarChart(
                data = BarChartData(
                    barData = barData,
                    xAxis = Axis(
                        max = 5f,
                        scaleInterval = 1f,
                        labelInterval = 1f,
                        position = 0f,
                        name = "月份"
                    ),
                    yLeftAxis = Axis(
                        max = 200f,
                        min = -100f, // 支持负值
                        scaleInterval = 50f,
                        labelInterval = 50f,
                        name = "金额"
                    ),
                )
            )
        }
    }
}

@Composable
@Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 300)
fun BarChartPreviewStackedWithCustomRenderer() {
    MaterialTheme {
        Surface {
            val barData = getTestStackedBarDataWithCustomRenderer()

            BarChart(
                data = BarChartData(
                    barData = barData,
                    xAxis = Axis(
                        max = 5f,
                        scaleInterval = 1f,
                        labelInterval = 1f,
                        name = "X轴"
                    ),
                    yLeftAxis = Axis(
                        max = 200f,
                        scaleInterval = 50f,
                        labelInterval = 50f,
                        name = "Y轴"
                    ),
                )
            )
        }
    }
}
