package com.brian.chart.compose.view.chart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path

data class PathAndPoints(
    var line: Line? = null,
    var path: Path? = null,//曲线路径
    var areaPath: Path? = null,//填充路径
    var offsetList: List<Offset>? = null
)