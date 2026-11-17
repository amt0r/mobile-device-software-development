package com.example.emergencypower.ui.pw21

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class PowerSourceType {
    GRID, BATTERY, GENERATOR, OFF
}

data class Pw21UiState(
    val activeSource: PowerSourceType = PowerSourceType.GRID,
    val gridVoltage: Float = 221.5f,
    val batterySoC: Float = 98f,
    val generatorRpm: Float = 0f,
    val currentLoadKw: Float = 312.4f,
    val isAutoMode: Boolean = true,
    val isGridFault: Boolean = false,
    val isExpandedDiagnostics: Boolean = false,
    val isExpandedPhaseDetails: Boolean = false
)

class Practice21ViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(Pw21UiState())
    val uiState: StateFlow<Pw21UiState> = _uiState.asStateFlow()

    fun triggerGridFailure() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isGridFault = true,
                gridVoltage = 0f,
                activeSource = PowerSourceType.BATTERY
            )
            delay(1200)
            _uiState.value = _uiState.value.copy(
                generatorRpm = 1500f,
                activeSource = PowerSourceType.GENERATOR
            )
        }
    }

    fun restoreGrid() {
        _uiState.value = _uiState.value.copy(
            isGridFault = false,
            gridVoltage = 220.8f,
            activeSource = PowerSourceType.GRID,
            generatorRpm = 0f
        )
    }

    fun selectSource(source: PowerSourceType) {
        _uiState.value = _uiState.value.copy(
            activeSource = source,
            isGridFault = source != PowerSourceType.GRID
        )
    }

    fun toggleAutoMode() {
        _uiState.value = _uiState.value.copy(isAutoMode = !_uiState.value.isAutoMode)
    }

    fun toggleDiagnostics() {
        _uiState.value = _uiState.value.copy(isExpandedDiagnostics = !_uiState.value.isExpandedDiagnostics)
    }

    fun togglePhaseDetails() {
        _uiState.value = _uiState.value.copy(isExpandedPhaseDetails = !_uiState.value.isExpandedPhaseDetails)
    }
}
