package com.example.emergencypower.ui.pw9

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.emergencypower.data.local.EmergencyDatabase
import com.example.emergencypower.data.local.TelemetryRecordEntity
import com.example.emergencypower.data.repository.EmergencyRepository
import com.example.emergencypower.data.repository.TelemetryAggregates
import com.example.emergencypower.model.PowerSource
import com.example.emergencypower.model.SystemStatus
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class Practice9UiState(
    val selectedSourceFilter: String = "Всі",
    val sortOption: String = "Час (нові)",
    val aggregates: TelemetryAggregates = TelemetryAggregates(0, 0.0, 0.0, 0.0),
    val isLoading: Boolean = false,
    val showAddDialog: Boolean = false
)

class Practice9ViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = EmergencyRepository(
        EmergencyDatabase.getInstance(application).emergencyDao()
    )

    private val _uiState = MutableStateFlow(Practice9UiState())
    val uiState: StateFlow<Practice9UiState> = _uiState.asStateFlow()

    private val _sourceFilter = MutableStateFlow("Всі")
    private val _sortOption = MutableStateFlow("Час (нові)")

    val telemetryList: StateFlow<List<TelemetryRecordEntity>> = combine(
        _sourceFilter.flatMapLatest { repository.getTelemetryFiltered(it) },
        _sortOption
    ) { list, sort ->
        when (sort) {
            "Час (нові)" -> list.sortedByDescending { it.timestamp }
            "Час (старі)" -> list.sortedBy { it.timestamp }
            "Потужність (max)" -> list.sortedByDescending { it.powerKw }
            "Напруга (max)" -> list.sortedByDescending { it.voltage }
            else -> list
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        seedInitialDataIfEmpty()
        refreshAggregates()
    }

    private fun seedInitialDataIfEmpty() {
        viewModelScope.launch {
            val db = EmergencyDatabase.getInstance(getApplication())
            if (db.emergencyDao().getCount() == 0) {
                val baseTime = System.currentTimeMillis()
                val initialData = listOf(
                    TelemetryRecordEntity(
                        stationName = "ПС-110/10 «Київська Енергетична»",
                        timestamp = baseTime - 3600000,
                        voltage = 227.4,
                        current = 212.0,
                        powerKw = 48.2,
                        sourceName = PowerSource.GRID.title,
                        statusName = SystemStatus.NORMAL.label
                    ),
                    TelemetryRecordEntity(
                        stationName = "Модуль АКБ LiFePO4 120кВт·год",
                        timestamp = baseTime - 1800000,
                        voltage = 230.1,
                        current = 135.5,
                        powerKw = 31.2,
                        sourceName = PowerSource.BATTERY.title,
                        statusName = SystemStatus.NORMAL.label
                    ),
                    TelemetryRecordEntity(
                        stationName = "Дизель-генераторна установка ДГ-50",
                        timestamp = baseTime - 600000,
                        voltage = 221.8,
                        current = 245.0,
                        powerKw = 54.4,
                        sourceName = PowerSource.DIESEL_GENERATOR.title,
                        statusName = SystemStatus.WARNING.label
                    )
                )
                initialData.forEach { repository.addTelemetryRecord(it) }
                refreshAggregates()
            }
        }
    }

    fun setSourceFilter(source: String) {
        _sourceFilter.value = source
        _uiState.update { it.copy(selectedSourceFilter = source) }
    }

    fun setSortOption(sort: String) {
        _sortOption.value = sort
        _uiState.update { it.copy(sortOption = sort) }
    }

    fun toggleAddDialog(show: Boolean) {
        _uiState.update { it.copy(showAddDialog = show) }
    }

    fun addRecord(stationName: String, voltage: Double, powerKw: Double, source: String, status: String) {
        viewModelScope.launch {
            val current = if (voltage > 0) (powerKw * 1000.0) / (voltage * 1.73) else 0.0
            val record = TelemetryRecordEntity(
                stationName = stationName,
                timestamp = System.currentTimeMillis(),
                voltage = voltage,
                current = current,
                powerKw = powerKw,
                sourceName = source,
                statusName = status
            )
            repository.addTelemetryRecord(record)
            refreshAggregates()
            toggleAddDialog(false)
        }
    }

    fun deleteRecord(record: TelemetryRecordEntity) {
        viewModelScope.launch {
            repository.deleteTelemetryRecord(record)
            refreshAggregates()
        }
    }

    fun refreshAggregates() {
        viewModelScope.launch {
            val aggregates = repository.calculateAggregates()
            _uiState.update { it.copy(aggregates = aggregates) }
        }
    }
}
