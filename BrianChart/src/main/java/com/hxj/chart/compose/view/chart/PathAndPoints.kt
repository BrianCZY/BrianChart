package com.hxj.chart.compose.view.chart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path

data class PathAndPoints(
    var line: Line? = null,
    var path: Path? = null,
    var points: List<Offset>? = null
)