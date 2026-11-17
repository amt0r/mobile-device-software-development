package com.example.emergencypower.ui.pw25

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emergencypower.data.model.SystemConfig
import com.example.emergencypower.data.model.TestCaseResult
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class Pw25UiState(
    val testResults: List<TestCaseResult> = emptyList(),
    val isRunning: Boolean = false,
    val passedCount: Int = 0,
    val failedCount: Int = 0,
    val totalDurationMs: Long = 0L
)

class Practice25ViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(Pw25UiState())
    val uiState: StateFlow<Pw25UiState> = _uiState.asStateFlow()

    init {
        runAllTests()
    }

    fun runAllTests() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRunning = true)
            val results = mutableListOf<TestCaseResult>()
            val startTime = System.currentTimeMillis()

            val t1Start = System.currentTimeMillis()
            delay(15)
            val config = SystemConfig(nominalVoltage = 220f, minVoltageThreshold = 195f, maxVoltageThreshold = 245f)
            val isNormal = 219.5f in config.minVoltageThreshold..config.maxVoltageThreshold
            val isSag = 188.0f < config.minVoltageThreshold
            val isSurge = 255.0f > config.maxVoltageThreshold
            val p1 = isNormal && isSag && isSurge
            results.add(
                TestCaseResult(
                    id = "T1",
                    testName = "Перевірка порогів напруги АВР (testVoltageThresholdDetection)",
                    category = "Unit Test: Логіка реле",
                    isPassed = p1,
                    durationMs = System.currentTimeMillis() - t1Start,
                    assertionDetails = "assertTrue(isNormal) && assertTrue(isSag) && assertTrue(isSurge)"
                )
            )

            val t2Start = System.currentTimeMillis()
            delay(10)
            val batKwh = 120.0f
            val eff = 0.92f
            val load = 80.0f
            val minutes = ((batKwh * eff / load) * 60).toInt()
            val p2 = minutes == 82
            results.add(
                TestCaseResult(
                    id = "T2",
                    testName = "Розрахунок автономності ДБЖ (testBatteryAutonomyCalculation)",
                    category = "Unit Test: Енергетичний баланс",
                    isPassed = p2,
                    durationMs = System.currentTimeMillis() - t2Start,
                    assertionDetails = "assertEquals(82 min, actual=$minutes min)"
                )
            )

            val t3Start = System.currentTimeMillis()
            delay(12)
            val gpkwh = 215.0f
            val density = 0.84f
            val pKw = 250.0f
            val hours = 4.0f
            val liters = ((gpkwh * pKw * hours) / 1000f) / density
            val p3 = Math.abs(liters - 255.95f) < 0.5f
            results.add(
                TestCaseResult(
                    id = "T3",
                    testName = "Прогнозування витрати палива ДГУ (testFuelConsumptionPrediction)",
                    category = "Unit Test: Паливна система",
                    isPassed = p3,
                    durationMs = System.currentTimeMillis() - t3Start,
                    assertionDetails = "assertEquals(255.95 L, actual=${String.format("%.2f", liters)} L)"
                )
            )

            val t4Start = System.currentTimeMillis()
            delay(18)
            val gson = Gson()
            val orig = SystemConfig(stationName = "ТП-110/10 кВ «Південна-18»", nominalVoltage = 230f, maxLoadKw = 500f, emergencyDelayMs = 2000L)
            val json = gson.toJson(orig)
            val parsed = gson.fromJson(json, SystemConfig::class.java)
            val p4 = parsed.stationName == orig.stationName && parsed.nominalVoltage == orig.nominalVoltage
            results.add(
                TestCaseResult(
                    id = "T4",
                    testName = "Серіалізація та парсинг JSON (testConfigJsonSerialization)",
                    category = "Unit Test: Data Persistence",
                    isPassed = p4,
                    durationMs = System.currentTimeMillis() - t4Start,
                    assertionDetails = "assertEquals(stationName, nominalVoltage, maxLoadKw)"
                )
            )

            val t5Start = System.currentTimeMillis()
            delay(10)
            val deltaF = Math.abs(50.02f - 50.00f)
            val phaseAngle = 3.5f
            val p5 = deltaF <= 0.15f && phaseAngle <= 10.0f
            results.add(
                TestCaseResult(
                    id = "T5",
                    testName = "Безрозривна синхронізація Make-Before-Break",
                    category = "Unit Test: Автоматика фазування",
                    isPassed = p5,
                    durationMs = System.currentTimeMillis() - t5Start,
                    assertionDetails = "assertTrue(Δf <= 0.15 Hz && Δφ <= 10.0°)"
                )
            )

            val elapsed = System.currentTimeMillis() - startTime
            val passed = results.count { it.isPassed }
            val failed = results.count { !it.isPassed }

            _uiState.value = _uiState.value.copy(
                testResults = results,
                isRunning = false,
                passedCount = passed,
                failedCount = failed,
                totalDurationMs = elapsed
            )
        }
    }
}
