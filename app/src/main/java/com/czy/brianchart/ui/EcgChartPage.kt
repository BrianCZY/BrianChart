package com.czy.brianchart.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.brian.chart.compose.widgets.chart.EcgTrace
import com.brian.chart.compose.widgets.model.*
import com.czy.brianchart.data.testdata.RD2000_WAVE_IDATA_LIST
import com.czy.brianchart.ui.components.TopBar
import com.czy.brianchart.ui.navigation.ChartNavigationActions
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlin.math.sin

@Composable
fun EcgChartPage(navigationActions: ChartNavigationActions? = null) {
    val vm: EcgChartViewModel = viewModel()
    val s by vm.ecgChartUIState.collectAsStateWithLifecycle()
    EcgChartView(s, Modifier.fillMaxSize()) { navigationActions?.navigateBack() }
}

@Composable
fun EcgChartView(ecgChartUIState: EcgChartUIState, modifier: Modifier, backClick: () -> Unit?) {
    Surface(modifier = modifier) {
        Column {
            TopBar(Modifier.fillMaxWidth().padding(top = 28.dp).height(48.dp), "EcgChart") { backClick?.invoke() }
            HorizontalDivider(thickness = 1.dp)
            Column(Modifier.verticalScroll(rememberScrollState())) {
                Text("动态示例", Modifier.fillMaxWidth().height(40.dp).padding(start = 8.dp))
                EcgChartWithTimer(Modifier.fillMaxWidth().height(200.dp))
                HorizontalDivider(thickness = 8.dp)
                Text("静态示例", Modifier.fillMaxWidth().height(40.dp).padding(start = 8.dp))
                (1..100).forEach { i ->
                    EcgChartSample(i, Modifier.fillMaxWidth().height(200.dp))
                    HorizontalDivider(thickness = 8.dp)
                }
            }
        }
    }
}

@Composable
fun EcgChartWithTimer(modifier: Modifier) {
    var cfg by remember { mutableStateOf(EcgConfig(
        waveforms = listOf(mutableListOf<Float>()),
        lineStyle = EcgLineStyle(color = Color(0xff50E3C2)),
    )) }
    val scope = rememberCoroutineScope()
    val tf = remember { flow { var i = 0; while (true) { emit(i++); delay(10) } } }
    var running by remember { mutableStateOf(false) }
    var job by remember { mutableStateOf<Job?>(null) }
    Box(modifier) {
        Button(modifier = Modifier.align(Alignment.TopEnd), onClick = {
            running = !running
            if (running) {
                cfg = cfg.copy(waveforms = listOf(mutableListOf()))
                job = scope.launch {
                    val a = 200.0; val f = 0.2
                    tf.take(1000).collect { i ->
                        val pts = (0 until 10).map { j ->
                            (a * sin(2 * Math.PI * f * (i * 10 + j) / 100.0)).toFloat()
                        }
                        cfg = cfg.copy(waveforms = listOf(cfg.waveforms?.get(0)?.plus(pts)?.toMutableList() ?: mutableListOf()))
                    }
                }
            } else job?.cancel()
        }) { Text(if (running) "Stop" else "Start") }
        EcgTrace(config = cfg)
    }
}

@Composable
fun EcgChartSample(index: Int, modifier: Modifier) {
    val cfg = ecgConfigs.getOrNull(index - 1) ?: EcgConfig()
    EcgTrace(modifier = modifier, config = cfg)
}

private val ecgConfigs: List<EcgConfig> = (1..100).map { i ->
    EcgConfig(
        waveforms = listOf(RD2000_WAVE_IDATA_LIST.map { it.toFloat() }),
        lineStyle = EcgLineStyle(
            color = if (i % 3 == 0) Color.Red else if (i % 3 == 1) Color.Blue else Color.Black,
            thickness = when { i >= 20 -> 2.dp; i >= 10 -> 1.5.dp; else -> 1.dp },
            samplesPerSecond = 100 + i * 10,
        ),
        gridStyle = EcgGridStyle(
            visible = i % 5 != 0,
            color = when { i >= 30 -> Color(0xFFCCCCCC); i >= 15 -> Color.LightGray; else -> Color.Gray },
            lineWidth = when { i >= 25 -> 1.5.dp; i >= 12 -> 1.dp; else -> 0.5.dp },
        ),
        dotStyle = EcgDotStyle(
            visible = i % 7 != 0,
            color = when { i >= 40 -> Color(0xFFAAAAAA); i >= 20 -> Color.Gray; else -> Color.DarkGray },
            radius = when { i >= 35 -> 1.5.dp; i >= 18 -> 1.dp; else -> 0.5.dp },
        ),
    )
}