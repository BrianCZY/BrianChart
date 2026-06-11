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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
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
import com.brian.chart.compose.widgets.chart.GroupedBarChart
import com.brian.chart.compose.widgets.model.*
import com.czy.brianchart.ui.components.TopBar
import com.czy.brianchart.ui.navigation.ChartNavigationActions
import com.czy.brianchart.ui.theme.BrianChartTheme
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
            TopBar(modifier = Modifier.fillMaxWidth().padding(top = 28.dp).height(48.dp), title = "BarChart") { backClick?.invoke() }
            HorizontalDivider(thickness = 1.dp)
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                BarChart1(modifier = Modifier.fillMaxWidth().height(200.dp))
                HorizontalDivider(thickness = 8.dp)
                BarChart2(modifier = Modifier.fillMaxWidth().height(200.dp))
                HorizontalDivider(thickness = 8.dp)
                BarChart3(modifier = Modifier.fillMaxWidth().height(200.dp))
                HorizontalDivider(thickness = 8.dp)
                BarChart4(modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp).height(200.dp))
                HorizontalDivider(thickness = 8.dp)
                BarChart5(modifier = Modifier.fillMaxWidth().height(200.dp))
                HorizontalDivider(thickness = 8.dp)
                BarChart6(modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp).height(200.dp))
                HorizontalDivider(thickness = 8.dp)
                BarChart7(modifier = Modifier.fillMaxWidth().padding(bottom = 40.dp).height(200.dp))
                HorizontalDivider(thickness = 8.dp)
                BarChartWithTouch(modifier = Modifier.padding(bottom = 20.dp).height(300.dp))
            }
        }
    }
}

@Composable
fun BarChart1(modifier: Modifier) {
    val xLimitLineList = getBarTestXLimitLineList()
    GroupedBarChart(modifier = modifier, config = BarChartConfig(dataSets = mutableListOf(BarSet(entries = mutableListOf(BarEntry(1f, 60f), BarEntry(2f, 200f)), color = Color.Gray, backgroundPainter = background1))),
        xAxis = ChartAxis(upperBound = 5f, tickStep = 10f, labelStep = 10f, thresholdLines = xLimitLineList, label = "x轴", labelFormatter = ::settingLabelValue),
        yAxis = ChartAxis(upperBound = 300f, tickStep = 10f, labelStep = 50f, label = "y轴", thresholdLines = xLimitLineList))
}

@Composable
fun BarChart2(modifier: Modifier) {
    GroupedBarChart(modifier = modifier, config = BarChartConfig(dataSets = mutableListOf(
        BarSet(entries = mutableListOf(BarEntry(1f, 60f), BarEntry(2f, 200f)), color = Color.Gray, valueColor = Color.Red, backgroundPainter = background1, label = "CHO", valueFormatter = ::settingValueText2),
        BarSet(entries = mutableListOf(BarEntry(1f, 80f), BarEntry(2f, 250f)), color = Color.Gray, backgroundPainter = background2, label = "FAT", valueFormatter = ::settingValueText2))),
        xAxis = ChartAxis(upperBound = 5f, label = ""),
        yAxis = ChartAxis(upperBound = 300f, tickStep = 10f, labelStep = 50f, label = ""))
}

@Composable
fun BarChart3(modifier: Modifier) {
    GroupedBarChart(modifier = modifier, config = BarChartConfig(dataSets = mutableListOf(
        BarSet(entries = mutableListOf(BarEntry(1f, 60f), BarEntry(2f, 200f)), color = Color.Gray, backgroundPainter = background1, label = "CHO", valueFormatter = ::settingValueText),
        BarSet(entries = mutableListOf(BarEntry(1f, 80f), BarEntry(2f, 250f)), color = Color.Gray, backgroundPainter = background2, label = "FAT", valueFormatter = ::settingValueText),
        BarSet(entries = mutableListOf(BarEntry(1f, 50f), BarEntry(2f, 150f)), color = Color.Gray, backgroundPainter = background3, label = "PRO", valueFormatter = ::settingValueText))),
        xAxis = ChartAxis(upperBound = 2.9f, tickStep = 1f, labelStep = 1f, label = "", labelFormatter = ::settingLabelValue2),
        yAxis = ChartAxis(upperBound = 300f, tickStep = 10f, labelStep = 50f, label = "", labelFormatter = ::settingLabelValue))
}

@Composable
fun BarChart4(modifier: Modifier) {
    GroupedBarChart(modifier = modifier, config = BarChartConfig(dataSets = mutableListOf(
        BarSet(entries = mutableListOf(BarEntry(1f, 60f)), color = Color.Gray, backgroundPainter = background1, label = "CHO", valueFormatter = ::settingValueText3),
        BarSet(entries = mutableListOf(BarEntry(1f, 80f)), color = Color.Gray, backgroundPainter = background2, label = "FAT", valueFormatter = ::settingValueText3),
        BarSet(entries = mutableListOf(BarEntry(1f, 50f)), color = Color.Gray, backgroundPainter = background3, label = "PRO", valueFormatter = ::settingValueText3)),
        barWidth = 40.dp, setPadding = 40.dp, widthRatio = 1f),
        xAxis = ChartAxis(upperBound = 3f, tickStep = 1f, labelStep = 1f, label = "", labelFormatter = ::settingLabelValue2),
        yAxis = ChartAxis(upperBound = 300f, tickStep = 10f, labelStep = 50f, label = "", labelFormatter = ::settingLabelValue))
}

@Composable
fun BarChart5(modifier: Modifier) {
    val xLimitLineList = getBarTestXLimitLineList()
    GroupedBarChart(modifier = modifier, config = BarChartConfig(dataSets = mutableListOf(BarSet(entries = mutableListOf(BarEntry(1f, 60f), BarEntry(2f, 200f)), color = Color.Gray, backgroundPainter = background1, showValues = false))),
        xAxis = ChartAxis(upperBound = 5f, tickStep = 10f, labelStep = 10f, thresholdLines = xLimitLineList, label = "x轴", labelFormatter = ::settingLabelValue),
        yAxis = ChartAxis(upperBound = 300f, tickStep = 10f, labelStep = 50f, label = "y轴", thresholdLines = xLimitLineList))
}

@Composable
fun BarChart6(modifier: Modifier) {
    GroupedBarChart(modifier = modifier, config = BarChartConfig(dataSets = mutableListOf(BarSet(entries = mutableListOf(BarEntry(1f, 60f), BarEntry(2f, 200f)), color = Color.Gray, backgroundPainter = background1, showValues = false))),
        xAxis = ChartAxis(upperBound = 5f, showAxis = false),
        yAxis = ChartAxis(upperBound = 200f, showAxis = false))
}

@Composable
fun BarChart7(modifier: Modifier) {
    GroupedBarChart(modifier = modifier, config = BarChartConfig(dataSets = mutableListOf(
        BarSet(entries = mutableListOf(
            BarEntry(1f, 100f, stackValues = listOf(50f, 30f, 20f)),
            BarEntry(2f, 170f, stackValues = listOf(80f, 50f, 40f)),
            BarEntry(3f, 160f, stackValues = listOf(60f, 70f, 30f)),
            BarEntry(4f, 190f, stackValues = listOf(90f, 40f, 60f))),
            color = Color.Blue, backgroundPainter = background2, showValues = true, valueColor = Color.White, label = "销售额",
            stackColors = listOf(Color.Blue.copy(alpha = 0.8f), Color.Red.copy(alpha = 0.8f), Color.Yellow.copy(alpha = 0.8f)),
            stackValueColors = listOf(Color.White, Color.White, Color.Black),
            valueFormatter = { _, value -> "${value.toInt()}" }),
        BarSet(entries = mutableListOf(
            BarEntry(1f, 10f, stackValues = listOf(40f, 20f, 10f)),
            BarEntry(2f, 15f, stackValues = listOf(60f, 30f, 15f)),
            BarEntry(3f, -10f, stackValues = listOf(50f, 40f, 20f)),
            BarEntry(4f, 30f, stackValues = listOf(-20f, -25f, -15f))),
            color = Color.Green, backgroundPainter = background2, showValues = true, valueColor = Color.White, label = "利润",
            stackColors = listOf(Color(0xFF4CAF50).copy(alpha = 0.8f), Color(0xFFF44336).copy(alpha = 0.8f), Color(0xFFFF9800).copy(alpha = 0.8f)),
            stackValueColors = listOf(Color.White, Color.White, Color.White),
            valueFormatter = { _, value -> "${value.toInt()}" })),
        barWidth = 80.dp),
        xAxis = ChartAxis(upperBound = 5f, tickStep = 1f, labelStep = 1f, origin = 0f, label = "月份"),
        yAxis = ChartAxis(upperBound = 200f, lowerBound = -50f, tickStep = 50f, labelStep = 50f, label = "金额"))
}

val background1: ((DrawScope, Color, Offset, Size) -> Unit) = { ds, color, offset, size ->
    ds.run { drawRoundRect(color = color, topLeft = offset, size = size, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round), cornerRadius = CornerRadius(2f, 2f)) }
}

val background2: ((DrawScope, Color, Offset, Size) -> Unit) = { ds, color, offset, size ->
    ds.run {
        drawRoundRect(color = color, topLeft = offset, size = size, cornerRadius = CornerRadius(2f, 2f))
        drawRoundRect(color = color, topLeft = offset, size = size, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round), cornerRadius = CornerRadius(2f, 2f))
    }
}

val background3: ((DrawScope, Color, Offset, Size) -> Unit) = { ds, color, offset, size ->
    ds.run {
        val width = 2.dp.toPx()
        drawRoundRect(color = color, topLeft = offset, size = size, style = Stroke(width = width, cap = StrokeCap.Round), cornerRadius = CornerRadius(2f, 2f))
        var heighTemp = size.height
        val coefficient = if (heighTemp > 0) 1 else -1
        for (i in 0..abs(heighTemp).toInt() step 20) {
            drawLine(start = Offset(x = offset.x, y = offset.y + i * coefficient), end = Offset(x = offset.x + size.width, y = offset.y + i * coefficient), color = color, strokeWidth = width)
        }
    }
}

fun settingValueText(name: String, value: Float) = if (name.isNullOrEmpty()) "$${value}" else "${name}:${value}"
fun settingValueText2(name: String, value: Float) = if (name.isNullOrEmpty()) "$${value}" else "${name}=${value}"
fun settingValueText3(name: String, value: Float) = if (name.isNullOrEmpty()) "${value}" else "${name}\n${value}"

fun settingLabelValue(value: Float): String {
    return when { value.toInt().toFloat() == value -> "${value.toInt()}"; else -> "${value}" }
}

fun settingLabelValue2(value: Float): String {
    val time = System.currentTimeMillis()
    var dateFormatYMD = "yyyy-MM-dd"
    return if (value > 0) getStringByFormat((time + value * 24 * 60 * 60 * 1000).toLong(), dateFormatYMD).toString() else ""
}

fun getStringByFormat(milliseconds: Long, format: String?): String? {
    return try { SimpleDateFormat(format).format(milliseconds) } catch (e: Exception) { e.printStackTrace(); null }
}

@Composable @Preview(showSystemUi = false, showBackground = true, widthDp = 1096, heightDp = 250)
fun BarChartPreview4() {
    MaterialTheme { Surface {
        GroupedBarChart(config = BarChartConfig(dataSets = mutableListOf(
            BarSet(entries = mutableListOf(BarEntry(1f, 60f)), color = Color.Gray, backgroundPainter = background1, label = "CHO", valueFormatter = ::settingValueText3),
            BarSet(entries = mutableListOf(BarEntry(1f, 80f)), color = Color.Gray, backgroundPainter = background2, label = "FAT", valueFormatter = ::settingValueText3),
            BarSet(entries = mutableListOf(BarEntry(1f, 50f)), color = Color.Gray, backgroundPainter = background3, label = "PRO", valueFormatter = ::settingValueText3)),
            barWidth = 40.dp, setPadding = 40.dp, widthRatio = 1f),
            xAxis = ChartAxis(upperBound = 3f, tickStep = 1f, labelStep = 1f, label = "", labelFormatter = ::settingLabelValue2),
            yAxis = ChartAxis(upperBound = 300f, tickStep = 10f, labelStep = 50f, label = "", labelFormatter = ::settingLabelValue))
    } }
}

@Composable @Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun BarChartPreview3() {
    MaterialTheme { Surface {
        GroupedBarChart(config = BarChartConfig(dataSets = mutableListOf(
            BarSet(entries = mutableListOf(BarEntry(1f, 60f), BarEntry(2f, 200f)), color = Color.Gray, backgroundPainter = background1, label = "CHO", valueFormatter = ::settingValueText),
            BarSet(entries = mutableListOf(BarEntry(1f, 80f), BarEntry(2f, 250f)), color = Color.Gray, backgroundPainter = background2, label = "FAT", valueFormatter = ::settingValueText),
            BarSet(entries = mutableListOf(BarEntry(1f, 50f), BarEntry(2f, 150f)), color = Color.Gray, backgroundPainter = background3, label = "PRO", valueFormatter = ::settingValueText))),
            xAxis = ChartAxis(upperBound = 2.9f, tickStep = 1f, labelStep = 1f, label = "", labelFormatter = ::settingLabelValue2),
            yAxis = ChartAxis(upperBound = 300f, tickStep = 10f, labelStep = 50f, label = "", labelFormatter = ::settingLabelValue))
    } }
}

@Composable @Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun BarChartPreview2() {
    MaterialTheme { Surface {
        GroupedBarChart(config = BarChartConfig(dataSets = mutableListOf(
            BarSet(entries = mutableListOf(BarEntry(1f, 60f), BarEntry(2f, 200f)), color = Color.Gray, valueColor = Color.Red, backgroundPainter = background1, label = "CHO", valueFormatter = ::settingValueText2),
            BarSet(entries = mutableListOf(BarEntry(1f, 80f), BarEntry(2f, 250f)), color = Color.Gray, backgroundPainter = background2, label = "FAT", valueFormatter = ::settingValueText2))),
            xAxis = ChartAxis(upperBound = 5f, label = ""),
            yAxis = ChartAxis(upperBound = 300f, tickStep = 10f, labelStep = 50f, label = ""))
    } }
}

@Composable @Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun BarChartPreview1() {
    MaterialTheme { Surface {
        val xLimitLineList = getBarTestXLimitLineList()
        GroupedBarChart(config = BarChartConfig(dataSets = mutableListOf(BarSet(entries = mutableListOf(BarEntry(1f, 60f), BarEntry(2f, 200f)), color = Color.Gray, backgroundPainter = background1))),
            xAxis = ChartAxis(upperBound = 5f, tickStep = 10f, labelStep = 10f, thresholdLines = xLimitLineList, label = "x轴", labelFormatter = ::settingLabelValue),
            yAxis = ChartAxis(upperBound = 300f, tickStep = 10f, labelStep = 50f, label = "y轴", thresholdLines = xLimitLineList))
    } }
}

@Composable @Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun BarChartPreviewNoValue() {
    MaterialTheme { Surface {
        GroupedBarChart(config = BarChartConfig(dataSets = mutableListOf(BarSet(entries = mutableListOf(BarEntry(1f, 60f), BarEntry(2f, -100f)), color = Color.Gray, backgroundPainter = background1, showValues = false))),
            xAxis = ChartAxis(upperBound = 5f, tickStep = 10f, labelStep = 20f, origin = 0f, label = "x轴"),
            yAxis = ChartAxis(upperBound = 200f, lowerBound = -200f, tickStep = 10f, labelStep = 100f, label = "y轴"))
    } }
}

@Composable @Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun BarChartPreviewNoValueNoAxis() {
    MaterialTheme { Surface {
        GroupedBarChart(config = BarChartConfig(dataSets = mutableListOf(BarSet(entries = mutableListOf(BarEntry(1f, 60f), BarEntry(2f, 200f)), color = Color.Gray, backgroundPainter = background1, showValues = false))),
            xAxis = ChartAxis(upperBound = 5f, showAxis = false),
            yAxis = ChartAxis(upperBound = 200f, showAxis = false))
    } }
}

@Composable @Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 300)
fun BarChartPreviewRenderer() {
    val density = androidx.compose.ui.platform.LocalDensity.current
    MaterialTheme { Surface {
        GroupedBarChart(config = BarChartConfig(dataSets = mutableListOf<BarSet>(BarSet(entries = mutableListOf(
            BarEntry(1f, 150f, customPainter = { ds, color, offset, size, value, name, valueRelativeToXAxis ->
                with(density) {
                    ds.drawRoundRect(color = Color.Blue.copy(alpha = 0.7f), topLeft = offset, size = size, cornerRadius = CornerRadius(8f, 8f))
                    ds.drawCircle(color = Color.Blue, radius = 8f, center = Offset(offset.x + size.width / 2, offset.y))
                    val label = "${value.toInt()}"; val ts = 10.sp.toPx()
                    val p = android.graphics.Paint().apply { textSize = ts; setColor(Color.White.toArgb()); isAntiAlias = true }
                    ds.drawContext.canvas.nativeCanvas.drawText(label, offset.x + size.width / 2 - (label.length * ts) / 4, offset.y + size.height / 2 + ts / 3, p)
                }
            }),
            BarEntry(2f, 100f),
            BarEntry(3f, -80f, customPainter = { ds, color, offset, size, value, name, valueRelativeToXAxis ->
                with(density) {
                    ds.drawRoundRect(color = Color.Red.copy(alpha = 0.6f), topLeft = offset, size = size, cornerRadius = CornerRadius(4f, 4f))
                    val label = "${value.toInt()}"; val ts = 10.sp.toPx()
                    val p = android.graphics.Paint().apply { textSize = ts; setColor(Color.Red.toArgb()); isAntiAlias = true }
                    ds.drawContext.canvas.nativeCanvas.drawText(label, offset.x + size.width / 2 - (label.length * ts) / 4, offset.y + size.height + ts + 4f, p)
                }
            })), color = Color.Gray, showValues = true)), barWidth = 60.dp),
            xAxis = ChartAxis(upperBound = 5f, tickStep = 1f, labelStep = 1f, origin = 0f, label = "X轴"),
            yAxis = ChartAxis(upperBound = 200f, lowerBound = -200f, tickStep = 10f, labelStep = 100f, label = "Y轴"))
    } }
}

@Composable @Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 300)
fun BarChartPreviewStacked() {
    MaterialTheme { Surface {
        GroupedBarChart(config = BarChartConfig(dataSets = mutableListOf(BarSet(entries = mutableListOf(
            BarEntry(1f, 100f, stackValues = listOf(50f, 30f, 20f)),
            BarEntry(2f, 170f, stackValues = listOf(80f, 50f, 40f)),
            BarEntry(3f, 160f, stackValues = listOf(60f, 70f, 30f)),
            BarEntry(4f, 190f, stackValues = listOf(90f, 40f, 60f))),
            color = Color.Blue, backgroundPainter = background2, showValues = true, valueColor = Color.White, label = "销售额",
            stackColors = listOf(Color.Blue.copy(alpha = 0.8f), Color.Red.copy(alpha = 0.8f), Color.Yellow.copy(alpha = 0.8f)),
            stackValueColors = listOf(Color.White, Color.White, Color.Black),
            valueFormatter = { _, value -> "${value.toInt()}" }),
            BarSet(entries = mutableListOf(
                BarEntry(1f, 10f, stackValues = listOf(40f, 20f, 10f)),
                BarEntry(2f, 15f, stackValues = listOf(60f, 30f, 15f)),
                BarEntry(3f, -10f, stackValues = listOf(50f, 40f, 20f)),
                BarEntry(4f, 30f, stackValues = listOf(-20f, -25f, -15f))),
                color = Color.Green, backgroundPainter = background2, showValues = true, valueColor = Color.White, label = "利润",
                stackColors = listOf(Color(0xFF4CAF50).copy(alpha = 0.8f), Color(0xFFF44336).copy(alpha = 0.8f), Color(0xFFFF9800).copy(alpha = 0.8f)),
                stackValueColors = listOf(Color.White, Color.White, Color.White),
                valueFormatter = { _, value -> "${value.toInt()}" })),
            barWidth = 80.dp),
            xAxis = ChartAxis(upperBound = 5f, tickStep = 1f, labelStep = 1f, origin = 0f, label = "月份"),
            yAxis = ChartAxis(upperBound = 200f, lowerBound = -100f, tickStep = 50f, labelStep = 50f, label = "金额"))
    } }
}

@Composable @Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 300)
fun BarChartPreviewStackedWithCustomRenderer() {
    MaterialTheme { Surface {
        GroupedBarChart(config = BarChartConfig(dataSets = mutableListOf(BarSet(entries = mutableListOf(
            BarEntry(1f, 100f, stackValues = listOf(50f, 30f, 20f), stackPainter = { ds, color, offset, size, value, name, valueRelativeToXAxis, stackIndex ->
                with(ds) {
                    val c = when (stackIndex) { 0 -> Color(0xFF2196F3); 1 -> Color(0xFF4CAF50); else -> Color(0xFFFF9800) }
                    val r = when (stackIndex) { 0 -> 12f; 1 -> 0f; else -> 6f }
                    drawRoundRect(color = c, topLeft = offset, size = size, cornerRadius = CornerRadius(r, r))
                    val label = "${value.toInt()}"; val ts = 10.sp.toPx()
                    val p = android.graphics.Paint().apply { textSize = ts; setColor(Color.White.toArgb()); isAntiAlias = true; textAlign = android.graphics.Paint.Align.CENTER }
                    drawContext.canvas.nativeCanvas.drawText(label, offset.x + size.width / 2, offset.y + size.height / 2 + ts / 3, p)
                }
            }),
            BarEntry(2f, 150f, stackValues = listOf(60f, 50f, 40f)),
            BarEntry(3f, 120f, stackValues = listOf(40f, 40f, 40f), stackPainter = { ds, color, offset, size, value, name, valueRelativeToXAxis, stackIndex ->
                with(ds) {
                    val c = when (stackIndex) { 0 -> Color(0xFF9C27B0); 1 -> Color(0xFFE91E63); else -> Color(0xFFF44336) }
                    drawRoundRect(color = c, topLeft = offset, size = size, cornerRadius = CornerRadius(4f, 4f))
                    drawRoundRect(color = Color.White.copy(alpha = 0.5f), topLeft = offset, size = size, style = Stroke(width = 2f), cornerRadius = CornerRadius(4f, 4f))
                    val label = "${value.toInt()}"; val ts = 10.sp.toPx()
                    val p = android.graphics.Paint().apply { textSize = ts; setColor(Color.White.toArgb()); isAntiAlias = true; textAlign = android.graphics.Paint.Align.CENTER }
                    drawContext.canvas.nativeCanvas.drawText(label, offset.x + size.width / 2, offset.y + size.height / 2 + ts / 3, p)
                }
            })), color = Color.Blue, showValues = false, label = "自定义堆积", valueFormatter = { _, value -> "${value.toInt()}" })), barWidth = 80.dp),
            xAxis = ChartAxis(upperBound = 5f, tickStep = 1f, labelStep = 1f, label = "X轴"),
            yAxis = ChartAxis(upperBound = 200f, tickStep = 50f, labelStep = 50f, label = "Y轴"))
    } }
}

@Composable @Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun BarChartWithTouchPreview() {
    BrianChartTheme { Surface {
        BarChartWithTouch(modifier = Modifier.padding(bottom = 20.dp).height(300.dp))
    } }
}

@Composable
fun BarChartWithTouch(modifier: Modifier) {
    val barEntryList = mutableListOf(
        BarEntry(1f, 20f), BarEntry(2f, 10f), BarEntry(3f, 30f), BarEntry(4f, 20f),
        BarEntry(5f, 20f), BarEntry(6f, 50f), BarEntry(7f, 20f), BarEntry(8f, 20f), BarEntry(9f, 20f))

    var barChartConfig by remember {
        mutableStateOf(BarChartConfig(dataSets = mutableListOf(BarSet(entries = barEntryList))))
    }
    var xAxis by remember {
        mutableStateOf(ChartAxis(upperBound = 10f, lowerBound = 0f, tickStep = 20f, labelStep = 50f, label = "时间 (s)", thresholdLines = mutableListOf()))
    }
    var yAxis by remember {
        mutableStateOf(ChartAxis(upperBound = 50f, lowerBound = 0f, tickStep = 25f, labelStep = 50f, label = "数值"))
    }
    var selectedX by remember { mutableStateOf<Float?>(9f) }
    var selectedBarEntry by remember { mutableStateOf<BarEntry?>(null) }

    fun thresholdLinePainter(drawScope: DrawScope, start: Offset, end: Offset, line: ThresholdLine) {
        drawScope.apply {
            drawLine(brush = Brush.linearGradient(colors = listOf(Color.Red, Color.Green), start = start, end = end),
                start = start, end = end, strokeWidth = line.lineWidth.toPx())
            drawCircle(color = Color.Cyan.copy(0.6f), radius = line.lineWidth.toPx() * 2, center = end)
            drawCircle(color = Color.White, radius = 2.dp.toPx(), center = end)
        }
    }

    fun updateThresholdLine(x: Float?) {
        val min = xAxis.lowerBound; val max = xAxis.upperBound
        val clamped = x?.coerceIn(min, max)
        val list = if (clamped != null) mutableListOf(ThresholdLine(clamped, lineColor = Color.Red, lineWidth = 2.dp, caption = "X=%.1f".format(clamped), customPainter = ::thresholdLinePainter)) else mutableListOf()
        xAxis = xAxis.copy(thresholdLines = list)
    }

    LaunchedEffect(Unit) { updateThresholdLine(selectedX) }

    Column(modifier = modifier.padding(8.dp)) {
        Text(text = "X：${selectedX} selectedBarEntry：${selectedBarEntry}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.align(Alignment.CenterHorizontally))
        GroupedBarChart(modifier = Modifier.fillMaxWidth().weight(1f), config = barChartConfig, xAxis = xAxis, yAxis = yAxis)
    }
}

fun getClosestBarEntry(barDataSetList: MutableList<BarSet>?, dataX: Float): BarEntry? {
    return barDataSetList?.flatMap { it.entries ?: emptyList() }?.minByOrNull { abs(it.x - dataX) }
}

fun getBarTestXLimitLineList(): MutableList<ThresholdLine> {
    return mutableListOf(ThresholdLine(100f, dashed = true, lineWidth = 2.dp, lineColor = Color.Gray, caption = "测试"), ThresholdLine(20f))
}
