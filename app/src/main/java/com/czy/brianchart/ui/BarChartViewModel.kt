package com.czy.brianchart.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class BarChartUIState(
    val check :Boolean  = false
)

class BarChartViewModel: ViewModel() {
    val _barChartUIState = MutableStateFlow((BarChartUIState()))
    val barChartUIState :StateFlow<BarChartUIState> = _barChartUIState



}