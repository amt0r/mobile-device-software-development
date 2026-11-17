package com.example.emergencypower.ui.pw19

import androidx.lifecycle.ViewModel
import com.example.emergencypower.model.EnergyConsumptionPoint
import com.example.emergencypower.model.TimeRange
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class Practice19UiState(
    val timeRange: TimeRange = TimeRange.TWENTY_FOUR_HOURS,
    val points: List<EnergyConsumptionPoint> = emptyList(),
    val averageKw: Double = 0.0,
    val peakKw: Double = 0.0,
    val minKw: Double = 0.0,
    val selectedPoint: EnergyConsumptionPoint? = null
)

class Practice19ViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(Practice19UiState())
    val uiState: StateFlow<Practice19UiState> = _uiState.asStateFlow()

    init {
        loadDataForRange(TimeRange.TWENTY_FOUR_HOURS)
    }

    fun selectTimeRange(range: TimeRange) {
        loadDataForRange(range)
    }

    fun selectPoint(point: EnergyConsumptionPoint?) {
        _uiState.update { it.copy(selectedPoint = point) }
    }

    private fun loadDataForRange(range: TimeRange) {
        val data = when (range) {
            TimeRange.ONE_HOUR -> listOf(
                EnergyConsumptionPoint("11:00", 42.0, 14.0, 13.5, 14.5),
                EnergyConsumptionPoint("11:15", 45.5, 15.0, 15.2, 15.3),
                EnergyConsumptionPoint("11:30", 52.0, 17.5, 17.0, 17.5),
                EnergyConsumptionPoint("11:45", 48.0, 16.0, 16.0, 16.0),
                EnergyConsumptionPoint("12:00", 54.4, 18.2, 18.0, 18.2)
            )
            TimeRange.TWENTY_FOUR_HOURS -> listOf(
                EnergyConsumptionPoint("00:00", 28.0, 9.5, 9.2, 9.3),
                EnergyConsumptionPoint("04:00", 25.4, 8.5, 8.4, 8.5),
                EnergyConsumptionPoint("08:00", 48.2, 16.0, 16.1, 16.1),
                EnergyConsumptionPoint("12:00", 54.4, 18.2, 18.0, 18.2),
                EnergyConsumptionPoint("16:00", 51.0, 17.0, 17.0, 17.0),
                EnergyConsumptionPoint("20:00", 58.6, 19.5, 19.5, 19.6)
            )
            TimeRange.SEVEN_DAYS -> listOf(
                EnergyConsumptionPoint("Пн", 46.2, 15.4, 15.4, 15.4),
                EnergyConsumptionPoint("Вт", 48.8, 16.2, 16.3, 16.3),
                EnergyConsumptionPoint("Ср", 52.4, 17.4, 17.5, 17.5),
                EnergyConsumptionPoint("Чт", 54.4, 18.1, 18.1, 18.2),
                EnergyConsumptionPoint("Пт", 50.0, 16.6, 16.7, 16.7),
                EnergyConsumptionPoint("Сб", 38.5, 12.8, 12.8, 12.9),
                EnergyConsumptionPoint("Нд", 34.0, 11.3, 11.3, 11.4)
            )
        }

        val avg = data.map { it.totalKw }.average()
        val peak = data.maxOf { it.totalKw }
        val min = data.minOf { it.totalKw }

        _uiState.update {
            it.copy(
                timeRange = range,
                points = data,
                averageKw = avg,
                peakKw = peak,
                minKw = min,
                selectedPoint = data.lastOrNull()
            )
        }
    }
}
