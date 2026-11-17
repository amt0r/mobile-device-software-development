package com.example.emergencypower.ui.pw14

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emergencypower.data.PowerStationRepository
import com.example.emergencypower.model.PowerStation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class Practice14UiState(
    val stations: List<PowerStation> = emptyList(),
    val selectedStationId: String? = "ST-18-01",
    val isMapFullscreen: Boolean = false
)

class Practice14ViewModel : ViewModel() {

    private val repository = PowerStationRepository()

    private val _uiState = MutableStateFlow(Practice14UiState())
    val uiState: StateFlow<Practice14UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllStations().collect { list ->
                _uiState.update { it.copy(stations = list) }
            }
        }
    }

    fun selectStation(id: String) {
        _uiState.update { it.copy(selectedStationId = id) }
    }

    fun getSelectedStation(): PowerStation? {
        val id = _uiState.value.selectedStationId ?: return null
        return _uiState.value.stations.find { it.id == id }
    }
}
