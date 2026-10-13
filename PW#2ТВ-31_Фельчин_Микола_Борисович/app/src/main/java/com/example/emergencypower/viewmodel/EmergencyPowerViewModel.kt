package com.example.emergencypower.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emergencypower.logic.EmergencyPowerCalculator
import com.example.emergencypower.model.EmergencySystemTelemetry
import com.example.emergencypower.model.GeneratorState
import com.example.emergencypower.model.PowerSourceType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

data class EmergencyPowerUiState(
    val isLoading: Boolean = false,
    val telemetry: EmergencySystemTelemetry = EmergencySystemTelemetry(),
    val gridStabilityIndex: Int = 98,
    val fuelConsumptionLph: Double = 0.0,
    val statusBanner: String = "Система аварійного живлення в черговому режимі. Мережа активна.",
    val errorMessage: String? = null,
    val lastActionSuccess: String? = null
)

class EmergencyPowerViewModel(
    private val calculator: EmergencyPowerCalculator = EmergencyPowerCalculator()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmergencyPowerUiState())
    val uiState: StateFlow<EmergencyPowerUiState> = _uiState.asStateFlow()

    init {
        updateDerivedMetrics()
    }

    private fun updateDerivedMetrics() {
        _uiState.update { current ->
            val tel = current.telemetry
            val stability = calculator.calculateGridStabilityIndex(tel.gridVoltage, tel.gridFrequency)
            val backupMin = calculator.calculateRemainingBackupMinutes(
                socPercent = tel.batterySocPercent,
                currentLoadWatts = tel.loadWatts
            )
            val fuel = if (tel.generatorState == GeneratorState.RUNNING) {
                calculator.estimateFuelConsumptionLph(tel.loadWatts)
            } else 0.0

            current.copy(
                telemetry = tel.copy(estimatedBackupMinutes = backupMin),
                gridStabilityIndex = stability,
                fuelConsumptionLph = fuel
            )
        }
    }

    fun refreshTelemetry() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, lastActionSuccess = null) }
            delay(500)

            _uiState.update { current ->
                val currentSrc = current.telemetry.activeSource
                val volt = if (currentSrc == PowerSourceType.GRID) {
                    225.0 + Random.nextDouble(-8.0, 8.0)
                } else if (currentSrc == PowerSourceType.GENERATOR) {
                    230.0 + Random.nextDouble(-4.0, 4.0)
                } else {
                    220.0 + Random.nextDouble(-2.0, 2.0)
                }

                val freq = 50.0 + Random.nextDouble(-0.15, 0.15)
                val load = (current.telemetry.loadWatts + Random.nextDouble(-200.0, 200.0)).coerceIn(800.0, 8000.0)

                current.copy(
                    isLoading = false,
                    telemetry = current.telemetry.copy(
                        gridVoltage = Math.round(volt * 10.0) / 10.0,
                        gridFrequency = Math.round(freq * 100.0) / 100.0,
                        loadWatts = Math.round(load * 10.0) / 10.0
                    ),
                    lastActionSuccess = "Телеметрію сенсорів оновлено."
                )
            }
            updateDerivedMetrics()
        }
    }

    fun simulateGridFailure() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    statusBanner = "УВАГА! Зникла напруга мережі. Спрацював АВР: живлення від ДБЖ.",
                    telemetry = it.telemetry.copy(
                        gridVoltage = 0.0,
                        gridFrequency = 0.0,
                        activeSource = PowerSourceType.BATTERY_UPS
                    ),
                    errorMessage = "Мережа 230V знеструмлена!"
                )
            }
            updateDerivedMetrics()

            delay(1200)
            _uiState.update {
                it.copy(
                    statusBanner = "Команда АВР: Прогрів та синхронізація дизель-генератора...",
                    telemetry = it.telemetry.copy(generatorState = GeneratorState.STARTING)
                )
            }

            delay(1800)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    statusBanner = "Дизель-генератор вийшов на номінальні оберти. Об'єкт заживлено від генератора.",
                    telemetry = it.telemetry.copy(
                        gridVoltage = 231.2,
                        gridFrequency = 50.1,
                        activeSource = PowerSourceType.GENERATOR,
                        generatorState = GeneratorState.RUNNING
                    ),
                    lastActionSuccess = "Аварійне електропостачання відновлено через генератор."
                )
            }
            updateDerivedMetrics()
        }
    }

    fun simulateGridRestore() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    statusBanner = "Мережу відновлено. Синхронізація фаз та повернення на міську мережу...",
                    errorMessage = null
                )
            }
            delay(1500)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    statusBanner = "Основна мережа в нормі. Дизель-генератор охолоджено та зупинено.",
                    telemetry = it.telemetry.copy(
                        gridVoltage = 228.6,
                        gridFrequency = 50.01,
                        activeSource = PowerSourceType.GRID,
                        generatorState = GeneratorState.STOPPED
                    ),
                    lastActionSuccess = "Роботу в штатному режимі відновлено."
                )
            }
            updateDerivedMetrics()
        }
    }

    fun toggleAts() {
        _uiState.update {
            val newState = !it.telemetry.isAtsEngaged
            it.copy(
                telemetry = it.telemetry.copy(isAtsEngaged = newState),
                lastActionSuccess = if (newState) "АВР переведено в АВТОМАТИЧНИЙ режим" else "АВР переведено в РУЧНИЙ режим"
            )
        }
    }
}
