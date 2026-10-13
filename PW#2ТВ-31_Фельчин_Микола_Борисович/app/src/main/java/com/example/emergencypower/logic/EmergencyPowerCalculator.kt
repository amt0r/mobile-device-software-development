package com.example.emergencypower.logic

import kotlin.math.abs

class EmergencyPowerCalculator {

    fun calculateRemainingBackupMinutes(
        batteryCapacityAh: Double = 200.0,
        systemVoltage: Double = 48.0,
        socPercent: Int,
        currentLoadWatts: Double,
        inverterEfficiency: Double = 0.90
    ): Int {
        if (currentLoadWatts <= 0 || socPercent <= 5) return 0
        val remainingEnergyWh = batteryCapacityAh * systemVoltage * (socPercent / 100.0) * inverterEfficiency
        val hours = remainingEnergyWh / currentLoadWatts
        return (hours * 60).toInt().coerceAtLeast(0)
    }

    fun calculateGridStabilityIndex(voltage: Double, frequency: Double): Int {
        val voltageDeviation = abs(voltage - 230.0) / 230.0
        val frequencyDeviation = abs(frequency - 50.0) / 50.0
        val totalDeviation = (voltageDeviation * 0.7) + (frequencyDeviation * 0.3)
        val stability = (1.0 - totalDeviation * 5).coerceIn(0.0, 1.0)
        return (stability * 100).toInt()
    }

    fun estimateFuelConsumptionLph(loadWatts: Double): Double {
        if (loadWatts <= 0) return 0.0

        val loadKw = loadWatts / 1000.0
        return 0.6 + (loadKw * 0.28)
    }
}
