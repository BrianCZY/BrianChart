package com.czy.brianchart.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class EcgChartUIState(
    val check :Boolean  = false
)

class EcgChartViewModel: ViewModel() {
    val _ecgChartUIState = MutableStateFlow((EcgChartUIState()))
    val ecgChartUIState :StateFlow<EcgChartUIState> = _ecgChartUIState



}