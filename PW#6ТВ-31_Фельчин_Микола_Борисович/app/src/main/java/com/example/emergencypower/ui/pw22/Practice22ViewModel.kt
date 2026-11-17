package com.example.emergencypower.ui.pw22

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.emergencypower.data.model.SystemConfig
import com.example.emergencypower.data.model.TelemetryLog
import com.example.emergencypower.data.storage.CsvJsonManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class Pw22UiState(
    val logs: List<TelemetryLog> = emptyList(),
    val config: SystemConfig = SystemConfig(),
    val statusMessage: String = "Готово до операцій експорту/імпорту",
    val csvFileSize: Long = 0L,
    val jsonContentPreview: String = ""
)

class Practice22ViewModel(application: Application) : AndroidViewModel(application) {

    private val storageManager = CsvJsonManager(application)

    private val _uiState = MutableStateFlow(Pw22UiState())
    val uiState: StateFlow<Pw22UiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        val sampleLogs = storageManager.getSampleLogs()
        _uiState.value = _uiState.value.copy(
            logs = sampleLogs,
            config = SystemConfig()
        )
    }

    fun exportToCsv() {
        viewModelScope.launch {
            val file = storageManager.exportLogsToCsv(_uiState.value.logs)
            _uiState.value = _uiState.value.copy(
                statusMessage = "Успішно експортовано CSV (${file.length()} байт) у сховище",
                csvFileSize = file.length()
            )
        }
    }

    fun readFromCsv() {
        viewModelScope.launch {
            val readLogs = storageManager.readCsvLogs()
            if (readLogs.isNotEmpty()) {
                _uiState.value = _uiState.value.copy(
                    logs = readLogs,
                    statusMessage = "Зчитано ${readLogs.size} рядків телеметрії з CSV файлу"
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    statusMessage = "Файл CSV ще не створено. Спочатку натисніть Експорт"
                )
            }
        }
    }

    fun exportToJson() {
        viewModelScope.launch {
            val file = storageManager.exportConfigToJson(_uiState.value.config)
            _uiState.value = _uiState.value.copy(
                statusMessage = "Конфігурацію збережено в JSON (${file.name})",
                jsonContentPreview = file.readText()
            )
        }
    }

    fun importFromJson() {
        viewModelScope.launch {
            val imported = storageManager.importConfigFromJson()
            _uiState.value = _uiState.value.copy(
                config = imported,
                statusMessage = "Успішно імпортовано налаштування підстанції з JSON"
            )
        }
    }

    fun updateConfig(nominalV: Float, maxKw: Float, delayMs: Long) {
        val updated = _uiState.value.config.copy(
            nominalVoltage = nominalV,
            maxLoadKw = maxKw,
            emergencyDelayMs = delayMs
        )
        _uiState.value = _uiState.value.copy(config = updated)
    }
}
