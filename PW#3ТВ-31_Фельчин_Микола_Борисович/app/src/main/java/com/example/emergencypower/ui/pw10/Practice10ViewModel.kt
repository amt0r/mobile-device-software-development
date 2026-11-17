package com.example.emergencypower.ui.pw10

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.emergencypower.data.local.EmergencyDatabase
import com.example.emergencypower.data.remote.model.GridStatusDto
import com.example.emergencypower.data.remote.model.LoadForecastItemDto
import com.example.emergencypower.data.remote.model.LoadForecastResponseDto
import com.example.emergencypower.data.repository.EmergencyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class Practice10UiState(
    val isLoading: Boolean = false,
    val gridStatus: GridStatusDto? = null,
    val forecastResponse: LoadForecastResponseDto? = null,
    val errorMessage: String? = null,
    val selectedRiskFilter: String = "Всі",
    val isOfflineFallback: Boolean = false,
    val avgForecastKw: Double = 0.0,
    val peakForecastKw: Double = 0.0,
    val highRiskCount: Int = 0
)

class Practice10ViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = EmergencyRepository(
        EmergencyDatabase.getInstance(application).emergencyDao()
    )

    private val _uiState = MutableStateFlow(Practice10UiState())
    val uiState: StateFlow<Practice10UiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val statusResult = repository.fetchRemoteGridStatus()
            val forecastResult = repository.fetchLoadForecast(6)

            val gridStatus = statusResult.getOrNull()
            val forecast = forecastResult.getOrNull()

            if (forecast != null) {
                val items = forecast.forecastItems
                val avg = if (items.isNotEmpty()) items.map { it.predictedKw }.average() else 0.0
                val peak = items.maxOfOrNull { it.predictedKw } ?: 0.0
                val highCount = items.count { it.riskLevel == "HIGH" }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        gridStatus = gridStatus,
                        forecastResponse = forecast,
                        isOfflineFallback = false,
                        avgForecastKw = avg,
                        peakForecastKw = peak,
                        highRiskCount = highCount
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Не вдалося отримати онлайн-дані з сервера SCADA. Використовуються кешовані дані.",
                        isOfflineFallback = true
                    )
                }
            }
        }
    }

    fun setRiskFilter(filter: String) {
        _uiState.update { it.copy(selectedRiskFilter = filter) }
    }

    fun getFilteredForecast(): List<LoadForecastItemDto> {
        val all = _uiState.value.forecastResponse?.forecastItems ?: emptyList()
        val filter = _uiState.value.selectedRiskFilter
        return if (filter == "Всі") {
            all
        } else {
            all.filter { it.riskLevel == filter }
        }
    }
}
