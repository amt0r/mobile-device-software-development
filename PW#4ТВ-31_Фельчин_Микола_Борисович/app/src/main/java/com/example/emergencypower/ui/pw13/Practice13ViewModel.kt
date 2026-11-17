package com.example.emergencypower.ui.pw13

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emergencypower.data.PowerStationRepository
import com.example.emergencypower.model.SensorStreamData
import kotlinx.coroutines.flow.*

data class Practice13UiState(
    val latestData: SensorStreamData? = null,
    val isFilterNormalOnly: Boolean = false,
    val isStreamPaused: Boolean = false,
    val historyCount: Int = 0
)

class Practice13ViewModel : ViewModel() {

    private val repository = PowerStationRepository()

    private val _uiState = MutableStateFlow(Practice13UiState())
    val uiState: StateFlow<Practice13UiState> = _uiState.asStateFlow()

    private val _history = MutableStateFlow<List<SensorStreamData>>(emptyList())
    val history: StateFlow<List<SensorStreamData>> = _history.asStateFlow()

    init {
        repository.combinedSensorStream
            .filter { data ->
                if (_uiState.value.isFilterNormalOnly) !data.hasFault else true
            }
            .onEach { data ->
                if (!_uiState.value.isStreamPaused) {
                    _uiState.update { it.copy(latestData = data, historyCount = it.historyCount + 1) }
                    _history.update { (listOf(data) + it).take(10) }
                }
            }
            .launchIn(viewModelScope)
    }

    fun toggleFilterNormalOnly() {
        _uiState.update { it.copy(isFilterNormalOnly = !it.isFilterNormalOnly) }
    }

    fun toggleStreamPause() {
        _uiState.update { it.copy(isStreamPaused = !it.isStreamPaused) }
    }
}
