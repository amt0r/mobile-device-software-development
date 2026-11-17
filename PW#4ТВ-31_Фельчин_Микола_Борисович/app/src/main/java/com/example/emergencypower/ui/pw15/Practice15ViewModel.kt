package com.example.emergencypower.ui.pw15

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emergencypower.data.PowerStationRepository
import com.example.emergencypower.model.PowerStation
import com.example.emergencypower.model.StationStatus
import com.example.emergencypower.model.StationType
import kotlinx.coroutines.flow.*

data class Practice15UiState(
    val searchQuery: String = "",
    val selectedType: String = "Всі",
    val selectedStatus: String = "Всі",
    val sortOption: String = "Потужність (max)"
)

class Practice15ViewModel : ViewModel() {

    private val repository = PowerStationRepository()

    private val _uiState = MutableStateFlow(Practice15UiState())
    val uiState: StateFlow<Practice15UiState> = _uiState.asStateFlow()

    private val allStationsFlow = repository.getAllStations()

    val filteredStations: StateFlow<List<PowerStation>> = combine(
        allStationsFlow,
        _uiState
    ) { list, state ->
        var res = list

        if (state.searchQuery.isNotBlank()) {
            val q = state.searchQuery.trim().lowercase()
            res = res.filter {
                it.name.lowercase().contains(q) ||
                it.code.lowercase().contains(q) ||
                it.address.lowercase().contains(q)
            }
        }

        if (state.selectedType != "Всі") {
            res = res.filter { it.type.name == state.selectedType }
        }

        if (state.selectedStatus != "Всі") {
            res = res.filter { it.status.name == state.selectedStatus }
        }

        when (state.sortOption) {
            "Потужність (max)" -> res.sortedByDescending { it.nominalPowerKw }
            "Потужність (min)" -> res.sortedBy { it.nominalPowerKw }
            "Запас % (max)" -> res.sortedByDescending { it.reserveLevelPercent }
            "Поточне навантаження" -> res.sortedByDescending { it.currentLoadKw }
            else -> res
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChanged(q: String) {
        _uiState.update { it.copy(searchQuery = q) }
    }

    fun onTypeSelected(t: String) {
        _uiState.update { it.copy(selectedType = t) }
    }

    fun onStatusSelected(s: String) {
        _uiState.update { it.copy(selectedStatus = s) }
    }

    fun onSortSelected(s: String) {
        _uiState.update { it.copy(sortOption = s) }
    }
}
