package com.example.emergencypower.ui.pw17

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.emergencypower.data.SensorNotificationManager
import com.example.emergencypower.model.PhysicalSensorState
import kotlinx.coroutines.flow.*

data class Practice17UiState(
    val sensorState: PhysicalSensorState = PhysicalSensorState(),
    val vibrationThreshold: Float = 3.5f,
    val lightThreshold: Float = 50f,
    val isMonitoringActive: Boolean = true
)

class Practice17ViewModel(application: Application) : AndroidViewModel(application) {

    private val manager = SensorNotificationManager(application)

    private val _uiState = MutableStateFlow(Practice17UiState())
    val uiState: StateFlow<Practice17UiState> = _uiState.asStateFlow()

    init {
        manager.getSensorFlow()
            .onEach { state ->
                if (_uiState.value.isMonitoringActive) {
                    _uiState.update { it.copy(sensorState = state) }
                }
            }
            .launchIn(viewModelScope)
    }

    fun setVibrationThreshold(v: Float) {
        _uiState.update { it.copy(vibrationThreshold = v) }
    }

    fun setLightThreshold(l: Float) {
        _uiState.update { it.copy(lightThreshold = l) }
    }

    fun toggleMonitoring() {
        _uiState.update { it.copy(isMonitoringActive = !it.isMonitoringActive) }
    }
}
