package com.example.emergencypower

import com.example.emergencypower.data.model.SystemConfig
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EmergencyLogicTest {

    @Test
    fun testVoltageThresholdDetection() {
        val config = SystemConfig(nominalVoltage = 220f, minVoltageThreshold = 195f, maxVoltageThreshold = 245f)

        val normalVoltage = 219.5f
        val isNormal = normalVoltage in config.minVoltageThreshold..config.maxVoltageThreshold
        assertTrue(isNormal)

        val sagVoltage = 188.0f
        val isSagFault = sagVoltage < config.minVoltageThreshold
        assertTrue(isSagFault)

        val surgeVoltage = 255.0f
        val isSurgeFault = surgeVoltage > config.maxVoltageThreshold
        assertTrue(isSurgeFault)
    }

    @Test
    fun testBatteryAutonomyCalculation() {
        val batteryCapacityKwh = 120.0f
        val dischargeEfficiency = 0.92f
        val currentLoadKw = 80.0f

        val effectiveEnergy = batteryCapacityKwh * dischargeEfficiency
        val hoursRemaining = effectiveEnergy / currentLoadKw
        val minutesRemaining = (hoursRemaining * 60).toInt()

        assertTrue(minutesRemaining > 0)
        assertEquals(82, minutesRemaining)
    }

    @Test
    fun testFuelConsumptionPrediction() {
        val specificFuelConsumptionGramsPerKwh = 215.0f
        val dieselDensityKgPerLiter = 0.84f
        val loadKw = 250.0f
        val runtimeHours = 4.0f

        val totalGrams = specificFuelConsumptionGramsPerKwh * loadKw * runtimeHours
        val totalKg = totalGrams / 1000f
        val litersRequired = totalKg / dieselDensityKgPerLiter

        assertEquals(255.95f, litersRequired, 0.5f)
    }

    @Test
    fun testConfigJsonSerialization() {
        val gson = Gson()
        val original = SystemConfig(
            stationName = "ТП-110/10 кВ «Південна-18»",
            nominalVoltage = 230f,
            maxLoadKw = 500f,
            emergencyDelayMs = 2000L
        )

        val json = gson.toJson(original)
        assertTrue(json.contains("ТП-110/10 кВ «Південна-18»"))

        val restored = gson.fromJson(json, SystemConfig::class.java)
        assertEquals(original.stationName, restored.stationName)
        assertEquals(original.nominalVoltage, restored.nominalVoltage, 0.001f)
        assertEquals(original.maxLoadKw, restored.maxLoadKw, 0.001f)
        assertEquals(original.emergencyDelayMs, restored.emergencyDelayMs)
    }

    @Test
    fun testMakeBeforeBreakSynchronization() {
        val gridFrequency = 50.02f
        val generatorFrequency = 50.00f
        val deltaFreq = Math.abs(gridFrequency - generatorFrequency)

        val maxAllowedDeltaFreq = 0.15f
        val isFrequencySynced = deltaFreq <= maxAllowedDeltaFreq
        assertTrue(isFrequencySynced)

        val phaseAngleDifferenceDegrees = 3.5f
        val maxAllowedPhaseAngle = 10.0f
        val isPhaseSynced = phaseAngleDifferenceDegrees <= maxAllowedPhaseAngle
        assertTrue(isPhaseSynced)
    }
}
