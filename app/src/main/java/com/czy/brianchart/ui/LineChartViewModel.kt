package com.czy.brianchart.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class LineChartUIState(
    val check :Boolean  = false
)

class LineChartViewModel: ViewModel() {
    val _lineChartUIState = MutableStateFlow((LineChartUIState()))
    val lineChartUIState :StateFlow<LineChartUIState> = _lineChartUIState



}