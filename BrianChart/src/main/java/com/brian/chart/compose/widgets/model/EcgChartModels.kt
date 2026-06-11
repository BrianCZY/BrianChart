package com.brian.chart.compose.widgets.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * ECG chart data configuration
 */
data class EcgConfig(
    val waveforms: List<List<Float>?>? = null,
    val lineStyle: EcgLineStyle = EcgLineStyle(),
    val gridStyle: EcgGridStyle = EcgGridStyle(),
    val dotStyle: EcgDotStyle = EcgDotStyle()
)

/**
 * ECG line appearance settings
 */
data class EcgLineStyle(
    var color: Color = Color.Black,
    var thickness: Dp = 1.dp,
    var samplesPerSecond: Int = 100,
    @Deprecated("Use leadCount * leads.size instead")
    var gridCellsY: Int = 4,
    @Deprecated("Use leadCount * leads.size")
    var cellsPerLead: Int = 4
)

/**
 * ECG grid appearance settings
 */
data class EcgGridStyle(
    var visible: Boolean = true,
    var color: Color = Color.Gray,
    var lineWidth: Dp = 1.dp
)

/**
 * ECG background dot appearance settings
 */
data class EcgDotStyle(
    var visible: Boolean = true,
    var color: Color = Color.Gray,
    var radius: Dp = 1.dp
)

// ECG constants
const val ECG_UNIT_HEIGHT = 0.1f // 0.1mV per small square
const val ECG_UNIT_WIDTH = 0.04f // 0.04s per small square
const val ECG_CELL_SIZE = 5 // 5 small squares per large cell
