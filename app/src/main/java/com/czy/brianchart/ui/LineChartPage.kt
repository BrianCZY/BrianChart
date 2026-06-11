package com.czy.brianchart.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import com.brian.chart.compose.widgets.chart.TrendChart
import com.brian.chart.compose.widgets.chart.TrendChartConfig
import com.brian.chart.compose.widgets.model.*
import com.czy.brianchart.ui.components.TopBar
import com.czy.brianchart.ui.navigation.ChartNavigationActions
import com.czy.brianchart.ui.theme.BrianChartTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlin.math.abs
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
                modifier = Modifier.fillMaxWidth().padding(top = 28.dp).height(48.dp),
                title = "LineChart"
            ) { backClick?.invoke() }
            HorizontalDivider(thickness = 1.dp)
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text("动态示例", modifier = Modifier.height(40.dp).fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.primaryContainer)
                    .wrapContentSize(Alignment.Center),
                    color = MaterialTheme.colorScheme.onPrimaryContainer)
                LineChartWithTimer(modifier = Modifier.height(220.dp))
                HorizontalDivider(thickness = 8.dp)
                Text("静态示例", modifier = Modifier.height(40.dp).fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.primaryContainer)
                    .wrapContentSize(Alignment.Center),
                    color = MaterialTheme.colorScheme.onPrimaryContainer)
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
                ChartSelfDefine(modifier = Modifier.padding(bottom = 40.dp).height(220.dp))
                HorizontalDivider(thickness = 8.dp)
                Text("触摸交互示例", modifier = Modifier.height(40.dp).fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.primaryContainer)
                    .wrapContentSize(Alignment.Center),
                    color = MaterialTheme.colorScheme.onPrimaryContainer)
                ChartWithTouch(modifier = Modifier.padding(bottom = 20.dp).height(300.dp))
                HorizontalDivider(thickness = 8.dp)
            }
        }
    }
}

@Composable
fun LineChartWithTimer(modifier: Modifier) {
    var lineData by remember {
        mutableStateOf(TrendChartConfig(
            series = listOf(DataSeries(dataPoints = mutableListOf(), seriesColor = Color(0xff50E3C2))),
            xAxis = ChartAxis(upperBound = 10000f, labelStep = 1000f, tickStep = 1000f),
            yPrimaryAxis = ChartAxis(upperBound = 500f, lowerBound = -500f, labelStep = 100f, tickStep = 100f),
        ))
    }
    val scope = rememberCoroutineScope()
    val timerFlow = remember { flow { var i = 0; while (true) { emit(i++); delay(10) } } }
    var isRunning by remember { mutableStateOf(false) }
    var job by remember { mutableStateOf<Job?>(null) }

    Box(modifier = modifier) {
        Button(modifier = Modifier.align(Alignment.TopEnd), onClick = {
            isRunning = !isRunning
            if (isRunning) {
                lineData = lineData.copy(series = lineData.series?.map { it.copy(dataPoints = mutableListOf()) })
                job = scope.launch {
                    val amplitude = 200.0; val frequency = 0.2
                    timerFlow.take(1000).collect { i ->
                        val newPoints = (0 until 10).map { j ->
                            val x = (i * 10 + j).toDouble()
                            Coordinate(x.toFloat(), (amplitude * sin(2 * Math.PI * frequency * x / 100.0)).toFloat())
                        }
                        lineData = lineData.copy(series = lineData.series?.map { it.copy(dataPoints = it.dataPoints + newPoints) })
                    }
                }
            } else job?.cancel()
        }) { Text(if (isRunning) "Stop" else "Start") }
        Text("Points: ${lineData.series?.first()?.dataPoints?.size}",
            modifier = Modifier.align(Alignment.TopEnd).height(40.dp).wrapContentHeight(Alignment.CenterVertically).padding(end = 100.dp),
            color = Color.Blue)
        TrendChart(config = lineData)
    }
}

@Composable
fun ChartPading(modifier: Modifier) {
    Row(modifier = modifier.padding(8.dp)) {
        TrendChart(config = TrendChartConfig(series = null,
            xAxis = ChartAxis(upperBound = 6f, lowerBound = 0f, strokeColor = MaterialTheme.colorScheme.onSurfaceVariant, tickStep = 1f, labelFontSize = 14.sp, showLabels = false),
            yPrimaryAxis = ChartAxis(upperBound = 4.0f, lowerBound = -4f, strokeColor = MaterialTheme.colorScheme.onSurfaceVariant, tickStep = 2f, labelStep = 2f, labelFontSize = 14.sp, origin = 3f),
        ), modifier = Modifier.weight(1f))
        TrendChart(config = TrendChartConfig(series = null,
            xAxis = ChartAxis(upperBound = 6f, lowerBound = 0f, strokeColor = MaterialTheme.colorScheme.onSurfaceVariant, tickStep = 1f, labelFontSize = 14.sp, showLabels = false),
            yPrimaryAxis = ChartAxis(upperBound = 4.0f, lowerBound = -4f, strokeColor = MaterialTheme.colorScheme.onSurfaceVariant, tickStep = 2f, labelStep = 2f, labelFontSize = 14.sp, origin = 3f),
        ), modifier = Modifier.weight(1f))
    }
}

@Composable
fun ChartSelfDefine(modifier: Modifier) {
    val context = LocalContext.current
    TrendChart(modifier = modifier, config = TrendChartConfig(series = getTestLineListSelfDefined(context),
        xAxis = ChartAxis(upperBound = 500f, tickStep = 20f, labelStep = 100f, label = "", gridStyle = GridStyle(10f, lineWidth = 0.5.dp)),
        yPrimaryAxis = ChartAxis(upperBound = 300f, tickStep = 10f, labelStep = 50f, label = "", gridStyle = GridStyle(10f, lineWidth = 0.5.dp)),
        scrollEnabled = true))
}

@Composable
fun Chart10(modifier: Modifier) {
    val list = getTestLineList().map { it.copy(areaFillEnabled = true) }.toMutableList()
    TrendChart(modifier = modifier.padding(2.dp), config = TrendChartConfig(series = list,
        xAxis = ChartAxis(upperBound = 500f, gridStyle = GridStyle(spacing = 10f, lineWidth = 0.5.dp), showLabels = false, showAxis = false),
        yPrimaryAxis = ChartAxis(upperBound = 300f, gridStyle = GridStyle(spacing = 10f, lineWidth = 0.5.dp), showLabels = false, showAxis = false)))
}

@Composable
fun Chart9(modifier: Modifier) {
    TrendChart(modifier = modifier.padding(2.dp), config = TrendChartConfig(series = getTestLineList(), autoFit = true,
        xAxis = ChartAxis(upperBound = 500f, gridStyle = GridStyle(spacing = 10f, lineWidth = 0.5.dp), showLabels = false, showAxis = false),
        yPrimaryAxis = ChartAxis(upperBound = 300f, gridStyle = GridStyle(spacing = 10f, lineWidth = 0.5.dp), showLabels = false, showAxis = false)))
}

@Composable
fun Chart8(modifier: Modifier) {
    TrendChart(modifier = modifier.padding(2.dp), config = TrendChartConfig(
        xAxis = ChartAxis(upperBound = 8f, lowerBound = -0f, origin = 0f, tickStep = 1f, labelStep = 1f, label = ""),
        yPrimaryAxis = ChartAxis(upperBound = 0f, tickStep = 10f, labelStep = 10f, origin = 0f, label = "", lowerBound = -80f)))
}

@Composable
fun Chart7(modifier: Modifier) {
    TrendChart(modifier = modifier.padding(2.dp), config = TrendChartConfig(
        xAxis = ChartAxis(upperBound = 0.833f, lowerBound = -0f, origin = 0f, tickStep = 0.1f, labelStep = 0.1f, label = ""),
        yPrimaryAxis = ChartAxis(upperBound = 0.02f, tickStep = 0.003f, labelStep = 0.003f, origin = 0f, label = "", lowerBound = 0f)))
}

@Composable
fun Chart6(modifier: Modifier) {
    TrendChart(modifier = modifier, config = TrendChartConfig(series = getTestPointLineList(),
        xAxis = ChartAxis(upperBound = 800f, lowerBound = -400f, origin = 0f, tickStep = 100f, labelStep = 100f, label = ""),
        yPrimaryAxis = ChartAxis(upperBound = 200f, tickStep = 50f, labelStep = 50f, origin = 0f, label = "", lowerBound = -300f)))
}

@Composable
fun Chart5(modifier: Modifier) {
    val limitLineList = getTestPlusOrMinusLimitLineList()
    TrendChart(modifier = modifier, config = TrendChartConfig(series = getTestPlusOrMinusLineList(),
        xAxis = ChartAxis(upperBound = 800f, lowerBound = -400f, origin = 0f, tickStep = 100f, labelStep = 100f, label = "", thresholdLines = limitLineList),
        yPrimaryAxis = ChartAxis(upperBound = 200f, tickStep = 50f, labelStep = 50f, origin = 0f, label = "", lowerBound = -300f, thresholdLines = limitLineList)))
}

@Composable
fun Chart4(modifier: Modifier) {
    TrendChart(modifier = modifier.padding(2.dp).background(Color(0xffaabbcc)), config = TrendChartConfig(series = getTestLineList(),
        xAxis = ChartAxis(upperBound = 500f, gridStyle = GridStyle(spacing = 10f, lineWidth = 0.5.dp), showLabels = false, showAxis = false),
        yPrimaryAxis = ChartAxis(upperBound = 300f, gridStyle = GridStyle(spacing = 10f, lineWidth = 0.5.dp), showLabels = false, showAxis = false)))
}

@Composable
fun Chart3(modifier: Modifier) {
    val list = getTestLineList(); val listChunk = getTestChunkList(); val xLimitLineList = getTestXLimitLineList()
    val limitLineList = getTestLimitLineList()
    TrendChart(modifier = modifier, config = TrendChartConfig(series = list,
        xAxis = ChartAxis(upperBound = 500f, tickStep = 20f, labelStep = 100f, thresholdLines = xLimitLineList, coloredRegions = listChunk, label = "",
            labelFormatter = ::settingLineChartLabelValue, gridStyle = GridStyle(10f)),
        yPrimaryAxis = ChartAxis(upperBound = 300f, tickStep = 10f, labelStep = 50f, label = "",
            coloredRegions = listChunk, thresholdLines = limitLineList, labelFormatter = ::settingLineChartLabelValue, gridStyle = GridStyle(10f))))
}

@Composable
fun Chart2(modifier: Modifier) {
    val list = getTestLineList(); val listChunk = getTestChunkList(); val xLimitLineList = getTestXLimitLineList()
    val limitLineList = getTestLimitLineList()
    TrendChart(modifier = modifier, config = TrendChartConfig(series = list,
        xAxis = ChartAxis(upperBound = 500f, tickStep = 20f, labelStep = 100f, thresholdLines = xLimitLineList, coloredRegions = listChunk, label = ""),
        yPrimaryAxis = ChartAxis(upperBound = 300f, tickStep = 10f, labelStep = 50f, label = "", coloredRegions = listChunk, thresholdLines = limitLineList)))
}

@Composable
fun Chart1(modifier: Modifier) {
    val list = getTestLineList2(); val listChunkX = getTestXChunkList()
    val listChunk1 = getTestChunkList1(); val listChunk2 = getTestChunkList2(); val listChunk3 = getTestChunkList3()
    val xLimitLineList1 = getTestXLimitLineList1(); val yLimitLineList1 = getTestYLimitLineList1()
    val yLimitLineList2 = getTestYLimitLineList2(); val yLimitLineList3 = getTestYLimitLineList3()
    TrendChart(config = TrendChartConfig(series = list,
        xAxis = ChartAxis(lowerBound = 10f, upperBound = 40f, tickStep = 5f, labelStep = 10f, thresholdLines = xLimitLineList1, coloredRegions = listChunkX, label = "x轴"),
        ySecondaryAxis = ChartAxis(upperBound = 200f, tickStep = 25f, labelStep = 25f, label = "Load\nW", strokeColor = Color(0XFF18D276), coloredRegions = listChunk1, thresholdLines = yLimitLineList1),
        yPrimaryAxis = ChartAxis(upperBound = 2000f, tickStep = 100f, labelStep = 500f, label = "  VO2\nml/min", strokeColor = Color(0XFFFF4E87), coloredRegions = listChunk2, thresholdLines = yLimitLineList2),
        yRightAxis = ChartAxis(upperBound = 2000f, tickStep = 100f, labelStep = 500f, label = "  VCO2\nml/min", strokeColor = Color(0XFF058BF6), coloredRegions = listChunk3, thresholdLines = yLimitLineList3)
    ), modifier = modifier)
}

@Composable @Preview fun ChartViewPreview() { BrianChartTheme { LineChartPage() } }
@Composable @Preview(heightDp = 2000) fun ChartViewLongPreview() { BrianChartTheme { LineChartPage() } }

fun getTestChunkList() = mutableListOf(ColoredRegion(40f, 60f), ColoredRegion(10f, 20f))
fun getTestXChunkList() = mutableListOf(ColoredRegion(20f, 25f))
fun getTestChunkList1() = mutableListOf(ColoredRegion(25f, 50f, fillColor = Color(0X2218D276)))
fun getTestChunkList2() = mutableListOf(ColoredRegion(800f, 1000f, fillColor = Color(0X222FF4E87)))
fun getTestChunkList3() = mutableListOf(ColoredRegion(1500f, 1800f, fillColor = Color(0X22058BF6)))

fun getTestLimitLineList() = mutableListOf(ThresholdLine(50f, dashed = true, lineWidth = 2.dp, lineColor = Color.Gray, caption = "测试"), ThresholdLine(15f))
fun getTestPlusOrMinusLimitLineList() = mutableListOf(ThresholdLine(50f, dashed = true, lineWidth = 2.dp, lineColor = Color.Gray, caption = "测试"), ThresholdLine(-25f))
fun getTestXLimitLineList() = mutableListOf(ThresholdLine(100f, dashed = true, lineWidth = 2.dp, lineColor = Color.Gray, caption = "测试"), ThresholdLine(20f))
fun getTestXLimitLineList1() = mutableListOf(ThresholdLine(10f, dashed = true, lineWidth = 2.dp, lineColor = Color.Gray, caption = "测试"), ThresholdLine(20f))
fun getTestYLimitLineList1() = mutableListOf(ThresholdLine(10f, dashed = true, lineWidth = 2.dp, lineColor = Color(0XFF18D276), caption = "限制线"))
fun getTestYLimitLineList2() = mutableListOf(ThresholdLine(600f, dashed = true, lineWidth = 2.dp, lineColor = Color(0XFFFF4E87), caption = "限制线"))
fun getTestYLimitLineList3() = mutableListOf(ThresholdLine(1200f, dashed = true, lineWidth = 2.dp, lineColor = Color(0XFF058BF6), caption = "限制线"))

fun getTestLineList(): MutableList<DataSeries> {
    val p1 = listOf(Coordinate(10f, 210f), Coordinate(50f, 150f), Coordinate(100f, 130f), Coordinate(150f, 200f), Coordinate(200f, 80f), Coordinate(250f, 240f), Coordinate(300f, 20f), Coordinate(350f, 150f), Coordinate(400f, 50f), Coordinate(450f, 240f), Coordinate(500f, 140f))
    return mutableListOf(
        DataSeries(p1, seriesColor = Color(0xff4A90E2), smoothCurve = true, areaFillEnabled = true,
            areaGradient = Brush.linearGradient(colors = listOf(Color(0xff4A90E2), Color(0x204A90E2)), start = Offset(0f, 0f), end = Offset(0f, Float.POSITIVE_INFINITY))),
        DataSeries(p1, seriesColor = Color(0xffFF90E2)))
}

fun getTestLineListSelfDefined(context: Context): MutableList<DataSeries> {
    val pts = mutableListOf(
        Coordinate(100f, 50f, customRenderer = { ds, o -> drawSelfDefinedTextAndShape(ds, o, 100f, 50f, Color.Green) }),
        Coordinate(200f, 120f, customRenderer = { ds, o -> drawSelfDefinedTextAndShape(ds, o, 200f, 120f, Color.Red) }),
        Coordinate(300f, 220f, customRenderer = { ds, o -> drawSelfDefinedText(ds, o, 300f, 220f, Color.Black) }),
        Coordinate(400f, 80f, customRenderer = { ds, o -> drawSelfDefinedBitmap(ds, BitmapFactory.decodeResource(context.resources, android.R.drawable.ic_menu_edit).asImageBitmap(), o) }),
        Coordinate(500f, 200f, customRenderer = { ds, o -> drawSelfDefinedBitmap(ds, BitmapFactory.decodeResource(context.resources, android.R.drawable.ic_menu_edit).asImageBitmap(), o) }))
    return mutableListOf(DataSeries(pts, seriesColor = Color(0xff50E3C2), dashedLine = true,
        dashPattern = PathEffect.dashPathEffect(floatArrayOf(18f, 12f), 2f),
        customPainter = { ds, series, offsetList -> series?.dataPoints?.forEachIndexed { i, pt -> offsetList?.getOrNull(i)?.let { pt.customRenderer?.invoke(ds, it) } } }))
}

fun drawSelfDefinedBitmap(ds: DrawScope, bitmap: ImageBitmap, offset: Offset) {
    ds.run { drawImage(image = bitmap, topLeft = Offset(offset.x - bitmap.width / 2, offset.y - bitmap.height / 2)) }
}

fun drawSelfDefinedTextAndShape(ds: DrawScope, offset: Offset, x: Float, y: Float, color: Color) {
    ds.run {
        val ts = 12.sp
        drawRoundRect(color = color, topLeft = Offset(offset.x - 20f, offset.y - 20f), size = Size(40f, 40f), style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round), cornerRadius = CornerRadius(2f, 2f))
        drawContext.canvas.nativeCanvas.apply { val p = Paint().apply { textSize = ts.toPx(); this.color = color.toArgb(); isAntiAlias = true }; drawText("(${x},${y})", offset.x - 80f, offset.y - ts.toPx(), p) }
    }
}

fun drawSelfDefinedText(ds: DrawScope, offset: Offset, x: Float, y: Float, color: Color) {
    ds.run {
        val ts = 12.sp
        drawContext.canvas.nativeCanvas.apply { val p = Paint().apply { textSize = ts.toPx(); this.color = color.toArgb(); isAntiAlias = true }; drawText("${y.toInt()}次", offset.x - 40f, offset.y - ts.toPx() / 2, p) }
    }
}

fun getTestLineList2(): MutableList<DataSeries> {
    return mutableListOf(
        DataSeries(listOf(Coordinate(0f, 10f), Coordinate(5f, 100f), Coordinate(10f, 30f), Coordinate(15f, 200f), Coordinate(20f, 120f), Coordinate(25f, 10f), Coordinate(30f, 180f), Coordinate(35f, 100f), Coordinate(40f, 10f)), seriesColor = Color(0XFF18D276), axisTarget = AxisOrientation.SECONDARY_LEFT),
        DataSeries(listOf(Coordinate(0f, 1000f), Coordinate(5f, 1000f), Coordinate(10f, 2000f), Coordinate(15f, 120f), Coordinate(20f, 1120f), Coordinate(25f, 1000f), Coordinate(30f, 180f), Coordinate(35f, 100f), Coordinate(40f, 1000f)), seriesColor = Color(0XFFFF4E87), smoothCurve = true, dashedLine = true),
        DataSeries(listOf(Coordinate(0f, 1200f), Coordinate(5f, 100f), Coordinate(10f, 2200f), Coordinate(15f, 600f), Coordinate(20f, 120f), Coordinate(25f, 1500f), Coordinate(30f, 680f), Coordinate(35f, 200f), Coordinate(40f, 1500f)), seriesColor = Color(0XFF058BF6), axisTarget = AxisOrientation.RIGHT, smoothCurve = true))
}

fun drawableToBitmap(drawable: Drawable? = null): ImageBitmap? {
    if (drawable == null) return null
    val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap); drawable.setBounds(0, 0, canvas.width, canvas.height); drawable.draw(canvas)
    return bitmap.asImageBitmap()
}

fun getTestPlusOrMinusLineList(): MutableList<DataSeries> {
    val p1 = mutableListOf<Coordinate>().apply { for (i in 0..20) { add(Coordinate(Random.nextInt(-500, 500).toFloat(), Random.nextInt(-200, 200).toFloat())) } }
    val p2 = mutableListOf<Coordinate>().apply { for (i in -500..500 step 50) { add(Coordinate(i.toFloat(), Random.nextInt(-200, 200).toFloat())) } }
    return mutableListOf(DataSeries(p1, seriesColor = Color(0xff50E3C2), smoothCurve = true), DataSeries(p2, seriesColor = Color(0xff4A90E2), smoothCurve = true))
}

fun getTestPointLineList() = mutableListOf(DataSeries(
    listOf(Coordinate(10f, 210f), Coordinate(50f, 150f), Coordinate(100f, 130f), Coordinate(150f, 200f), Coordinate(200f, 80f), Coordinate(250f, 240f), Coordinate(300f, 20f), Coordinate(350f, 150f), Coordinate(400f, 50f), Coordinate(450f, 240f), Coordinate(500f, 140f)),
    strokeWidth = 2.dp, seriesColor = Color(0xff4A90E2), smoothCurve = true, showPoints = true, showPath = false))

fun settingLineChartLabelValue(value: Float): String = "${if (value.toInt().toFloat() == value) value.toInt() else value}T"

@Composable @Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun LineChartPreview() {
    BrianChartTheme { Surface {
        val list = getTestLineList2(); val listChunkX = getTestXChunkList()
        val listChunk1 = getTestChunkList1(); val listChunk2 = getTestChunkList2(); val listChunk3 = getTestChunkList3()
        val xLimitLineList1 = getTestXLimitLineList1(); val yLimitLineList1 = getTestYLimitLineList1()
        val yLimitLineList2 = getTestYLimitLineList2(); val yLimitLineList3 = getTestYLimitLineList3()
        TrendChart(config = TrendChartConfig(series = list,
            xAxis = ChartAxis(lowerBound = 10f, upperBound = 40f, tickStep = 5f, labelStep = 10f, thresholdLines = xLimitLineList1, coloredRegions = listChunkX, label = "x轴"),
            ySecondaryAxis = ChartAxis(upperBound = 200f, tickStep = 25f, labelStep = 25f, label = "Load\nW", strokeColor = Color(0XFF18D276), coloredRegions = listChunk1, thresholdLines = yLimitLineList1),
            yPrimaryAxis = ChartAxis(upperBound = 2000f, tickStep = 100f, labelStep = 500f, label = "  VO2\nml/min", strokeColor = Color(0XFFFF4E87), coloredRegions = listChunk2, thresholdLines = yLimitLineList2),
            yRightAxis = ChartAxis(upperBound = 2000f, tickStep = 100f, labelStep = 500f, label = "  VCO2\nml/min", strokeColor = Color(0XFF058BF6), coloredRegions = listChunk3, thresholdLines = yLimitLineList3)))
    } }
}

@Composable @Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun LineChartPreview2() {
    BrianChartTheme { Surface {
        val list = getTestLineList(); val listChunk = getTestChunkList(); val xLimitLineList = getTestXLimitLineList(); val limitLineList = getTestLimitLineList()
        TrendChart(config = TrendChartConfig(series = list,
            xAxis = ChartAxis(upperBound = 500f, tickStep = 20f, labelStep = 100f, thresholdLines = xLimitLineList, coloredRegions = listChunk, label = ""),
            yPrimaryAxis = ChartAxis(upperBound = 300f, tickStep = 10f, labelStep = 50f, label = "", coloredRegions = listChunk, thresholdLines = limitLineList)))
    } }
}

@Composable @Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun LineChartPreview3() {
    BrianChartTheme { Surface {
        val list = getTestLineList(); val listChunk = getTestChunkList(); val xLimitLineList = getTestXLimitLineList(); val limitLineList = getTestLimitLineList()
        TrendChart(config = TrendChartConfig(series = list,
            xAxis = ChartAxis(upperBound = 500f, tickStep = 20f, labelStep = 100f, thresholdLines = xLimitLineList, coloredRegions = listChunk, label = "", labelFormatter = ::settingLineChartLabelValue, gridStyle = GridStyle(10f)),
            yPrimaryAxis = ChartAxis(upperBound = 300f, tickStep = 10f, labelStep = 50f, label = "", coloredRegions = listChunk, thresholdLines = limitLineList, labelFormatter = ::settingLineChartLabelValue, gridStyle = GridStyle(10f))))
    } }
}

@Composable @Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun LineChartPreview4() {
    BrianChartTheme { Surface {
        TrendChart(modifier = Modifier.padding(2.dp).background(Color(0xffaabbcc)), config = TrendChartConfig(series = getTestLineList(),
            xAxis = ChartAxis(upperBound = 500f, gridStyle = GridStyle(spacing = 10f, lineWidth = 0.5.dp), showLabels = false, showAxis = false),
            yPrimaryAxis = ChartAxis(upperBound = 300f, gridStyle = GridStyle(spacing = 10f, lineWidth = 0.5.dp), showLabels = false, showAxis = false)))
    } }
}

@Composable @Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun LineChartPreview5() {
    BrianChartTheme { Surface {
        val limitLineList = getTestPlusOrMinusLimitLineList()
        TrendChart(config = TrendChartConfig(series = getTestPlusOrMinusLineList(),
            xAxis = ChartAxis(upperBound = 800f, lowerBound = -400f, origin = 0f, tickStep = 100f, labelStep = 100f, label = "", thresholdLines = limitLineList),
            yPrimaryAxis = ChartAxis(upperBound = 200f, tickStep = 50f, labelStep = 50f, origin = 0f, label = "", lowerBound = -300f, thresholdLines = limitLineList)))
    } }
}

@Composable @Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun LineChartPreview6() {
    BrianChartTheme { Surface {
        TrendChart(config = TrendChartConfig(series = getTestPointLineList(),
            xAxis = ChartAxis(upperBound = 800f, lowerBound = -400f, origin = 0f, tickStep = 100f, labelStep = 100f, label = ""),
            yPrimaryAxis = ChartAxis(upperBound = 200f, tickStep = 50f, labelStep = 50f, origin = 0f, label = "", lowerBound = -300f)))
    } }
}

@Composable @Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun LineChartPreview7() {
    BrianChartTheme { Surface {
        TrendChart(modifier = Modifier.padding(2.dp), config = TrendChartConfig(
            xAxis = ChartAxis(upperBound = 0.833f, lowerBound = -0f, origin = 0f, tickStep = 0.1f, labelStep = 0.1f, label = ""),
            yPrimaryAxis = ChartAxis(upperBound = 0.02f, tickStep = 0.003f, labelStep = 0.003f, origin = 0f, label = "", lowerBound = 0f)))
    } }
}

@Composable @Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun LineChartPreview8() {
    BrianChartTheme { Surface {
        TrendChart(modifier = Modifier.padding(2.dp), config = TrendChartConfig(
            xAxis = ChartAxis(upperBound = 8f, lowerBound = -0f, origin = 0f, tickStep = 1f, labelStep = 1f, label = ""),
            yPrimaryAxis = ChartAxis(upperBound = 0f, tickStep = 10f, labelStep = 10f, origin = 0f, label = "", lowerBound = -80f)))
    } }
}

@Composable @Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun LineChartSelfAdaptationPreview() {
    BrianChartTheme { Surface {
        val list = getTestPlusOrMinusLineList()
        TrendChart(config = TrendChartConfig(series = list, xAxis = ChartAxis(origin = 0f, tickStep = 100f, labelStep = 100f, label = ""),
            yPrimaryAxis = ChartAxis(tickStep = 50f, labelStep = 50f, origin = 0f, label = ""), autoFit = true))
    } }
}

@Composable @Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun LineChartPadingPreview() {
    BrianChartTheme { Surface {
        Row(modifier = Modifier.padding(8.dp)) {
            TrendChart(config = TrendChartConfig(series = null,
                xAxis = ChartAxis(upperBound = 6f, lowerBound = 0f, strokeColor = MaterialTheme.colorScheme.onSurfaceVariant, tickStep = 1f, labelFontSize = 14.sp, showLabels = false),
                yPrimaryAxis = ChartAxis(upperBound = 4.0f, lowerBound = -4f, strokeColor = MaterialTheme.colorScheme.onSurfaceVariant, tickStep = 2f, labelStep = 2f, labelFontSize = 14.sp, origin = 3f),
            ), modifier = Modifier.weight(1f))
            TrendChart(config = TrendChartConfig(series = null,
                xAxis = ChartAxis(upperBound = 6f, lowerBound = 0f, strokeColor = MaterialTheme.colorScheme.onSurfaceVariant, tickStep = 1f, labelFontSize = 14.sp, showLabels = false),
                yPrimaryAxis = ChartAxis(upperBound = 4.0f, lowerBound = -4f, strokeColor = MaterialTheme.colorScheme.onSurfaceVariant, tickStep = 2f, labelStep = 2f, labelFontSize = 14.sp, origin = 3f),
            ), modifier = Modifier.weight(1f))
        }
    } }
}

@Composable @Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun LineChartPadingSelfDefinePreview() {
    BrianChartTheme { Surface {
        Row(modifier = Modifier.padding(8.dp)) {
            TrendChart(config = TrendChartConfig(series = null,
                xAxis = ChartAxis(upperBound = 6f, lowerBound = 0f, strokeColor = MaterialTheme.colorScheme.onSurfaceVariant, tickStep = 1f, labelStep = 1f, labelFontSize = 14.sp),
                yPrimaryAxis = ChartAxis(upperBound = 4.0f, lowerBound = -4f, strokeColor = MaterialTheme.colorScheme.onSurfaceVariant, tickStep = 2f, labelStep = 2f, labelFontSize = 14.sp),
            ), modifier = Modifier.weight(1f).background(Color(0x10000000)))
        }
    } }
}

@Composable @Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun LineChartPreviewSelfDefined() {
    BrianChartTheme { Surface {
        val context = LocalContext.current
        TrendChart(config = TrendChartConfig(series = getTestLineListSelfDefined(context),
            xAxis = ChartAxis(upperBound = 500f, tickStep = 20f, labelStep = 100f, label = "", gridStyle = GridStyle(10f, lineWidth = 0.5.dp)),
            yPrimaryAxis = ChartAxis(upperBound = 300f, tickStep = 10f, labelStep = 50f, label = "", gridStyle = GridStyle(10f, lineWidth = 0.5.dp)),
            scrollEnabled = true))
    } }
}

@Composable @Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun LineChartPreviewChunk() {
    BrianChartTheme { Surface {
        TrendChart(config = TrendChartConfig(series = null,
            xAxis = ChartAxis(upperBound = 500f, lowerBound = 200f, tickStep = 20f, labelStep = 100f,
                coloredRegions = mutableListOf(ColoredRegion(200f, 300f, Color.Red.copy(alpha = 0.5f)), ColoredRegion(400f, 500f, Color.Blue.copy(alpha = 0.5f))), label = ""),
            yPrimaryAxis = ChartAxis(upperBound = 800f, lowerBound = 300f, tickStep = 10f, labelStep = 50f,
                coloredRegions = mutableListOf(ColoredRegion(400f, 420f, Color.Red.copy(alpha = 0.5f)), ColoredRegion(600f, 700f, Color.Blue.copy(alpha = 0.5f))), label = "")))
    } }
}

@Composable @Preview(showSystemUi = false, showBackground = true, widthDp = 500, heightDp = 250)
fun ChartWithTouchPreview() {
    BrianChartTheme { Surface {
        ChartWithTouch(modifier = Modifier.padding(bottom = 20.dp).height(300.dp))
    } }
}

@Composable
fun ChartWithTouch(modifier: Modifier) {
    var config by remember {
        mutableStateOf(TrendChartConfig(
            series = listOf(DataSeries(dataPoints = mutableListOf(
                Coordinate(0f, 10f), Coordinate(25f, 80f), Coordinate(50f, 40f), Coordinate(75f, 120f),
                Coordinate(100f, 90f), Coordinate(125f, 160f), Coordinate(150f, 130f), Coordinate(175f, 200f), Coordinate(200f, 170f)),
                seriesColor = Color(0xff4A90E2), smoothCurve = true, showPath = true)),
            xAxis = ChartAxis(upperBound = 200f, lowerBound = 0f, tickStep = 20f, labelStep = 50f, label = "时间 (s)", thresholdLines = mutableListOf()),
            yPrimaryAxis = ChartAxis(upperBound = 250f, lowerBound = 0f, tickStep = 25f, labelStep = 50f, label = "数值")))
    }
    var selectedX by remember { mutableStateOf<Float?>(200f) }

    fun thresholdLinePainter(drawScope: DrawScope, start: Offset, end: Offset, line: ThresholdLine) {
        drawScope.apply {
            drawLine(brush = Brush.linearGradient(colors = listOf(Color.Red, Color.Green), start = start, end = end),
                start = start, end = end, strokeWidth = line.lineWidth.toPx())
            drawCircle(color = Color.Cyan.copy(0.6f), radius = line.lineWidth.toPx() * 2, center = end)
            drawCircle(color = Color.White, radius = 2.dp.toPx(), center = end)
        }
    }

    fun updateThresholdLine(x: Float?) {
        val min = config.xAxis.lowerBound; val max = config.xAxis.upperBound
        val clamped = x?.coerceIn(min, max)
        val list = if (clamped != null) mutableListOf(ThresholdLine(clamped, lineColor = Color.Red, lineWidth = 2.dp, caption = "X=%.1f".format(clamped), customPainter = ::thresholdLinePainter)) else mutableListOf()
        config = config.copy(xAxis = config.xAxis.copy(thresholdLines = list))
    }

    LaunchedEffect(Unit) { updateThresholdLine(selectedX) }

    var selectedPoint by remember { mutableStateOf<Coordinate?>(null) }
    Column(modifier = modifier.padding(8.dp)) {
        Text(text = "X：${selectedX} selectedPoint：${selectedPoint}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.align(Alignment.CenterHorizontally))
        TrendChart(modifier = Modifier.fillMaxWidth().weight(1f), config = config.copy(onTouch = { payload: TouchPayload ->
            updateThresholdLine(payload.dataX); selectedPoint = getClosestLinePoint(config.series, payload.dataX); selectedX = payload.dataX
            when (payload.gesture) {
                GestureEvent.TAP -> updateThresholdLine(selectedPoint?.x ?: payload.dataX)
                GestureEvent.DRAG -> updateThresholdLine(payload.dataX)
                GestureEvent.RELEASE -> updateThresholdLine(selectedPoint?.x ?: payload.dataX)
                GestureEvent.PRESS -> {}
            }
        }))
    }
}

fun getClosestLinePoint(series: List<DataSeries>? = null, dataX: Float): Coordinate? {
    return series?.flatMap { it.dataPoints }?.minByOrNull { abs(it.x - dataX) }
}
