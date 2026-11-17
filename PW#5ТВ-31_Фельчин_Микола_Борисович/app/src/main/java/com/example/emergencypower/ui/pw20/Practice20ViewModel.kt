package com.example.emergencypower.ui.pw20

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class Practice20UiState(
    val voltage: Float = 224.5f,
    val batteryPercent: Float = 88f,
    val isCharging: Boolean = true,
    val vibrationX: Float = 0.2f,
    val vibrationY: Float = -0.3f
)

class Practice20ViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(Practice20UiState())
    val uiState: StateFlow<Practice20UiState> = _uiState.asStateFlow()

    fun setVoltage(v: Float) {
        _uiState.update { it.copy(voltage = v) }
    }

    fun setBatteryPercent(p: Float) {
        _uiState.update { it.copy(batteryPercent = p) }
    }

    fun toggleCharging() {
        _uiState.update { it.copy(isCharging = !it.isCharging) }
    }

    fun setVibration(x: Float, y: Float) {
        _uiState.update { it.copy(vibrationX = x, vibrationY = y) }
    }
}
