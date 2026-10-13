package com.example.emergencypower

import com.example.emergencypower.logic.EmergencyPowerCalculator
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class EmergencyPowerCalculatorTest {

    private lateinit var calculator: EmergencyPowerCalculator

    @Before
    fun setUp() {
        calculator = EmergencyPowerCalculator()
    }

    @Test
    fun calculateRemainingBackupMinutes_normalValues_returnsExpectedTime() {

        val minutes = calculator.calculateRemainingBackupMinutes(
            batteryCapacityAh = 200.0,
            systemVoltage = 48.0,
            socPercent = 100,
            currentLoadWatts = 1000.0,
            inverterEfficiency = 0.90
        )
        assertEquals(518, minutes)
    }

    @Test
    fun calculateGridStabilityIndex_perfectGrid_returnsHighStability() {
        val stability = calculator.calculateGridStabilityIndex(230.0, 50.0)
        assertEquals(100, stability)
    }

    @Test
    fun estimateFuelConsumptionLph_runningLoad_returnsPositiveValue() {
        val fuel = calculator.estimateFuelConsumptionLph(3000.0)
        assertTrue(fuel > 1.0)
    }
}
