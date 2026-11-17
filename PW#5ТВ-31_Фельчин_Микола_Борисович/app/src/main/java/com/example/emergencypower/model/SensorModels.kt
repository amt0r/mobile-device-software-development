package com.example.emergencypower.model

data class PhysicalSensorState(
    val accelerometerX: Float = 0f,
    val accelerometerY: Float = 0f,
    val accelerometerZ: Float = 9.8f,
    val vibrationMagnitude: Float = 0f,
    val lightLux: Float = 350f,
    val batteryTempC: Float = 31.5f,
    val isVibrationAlarm: Boolean = false,
    val isBlackoutAlarm: Boolean = false
)

data class EnergyConsumptionPoint(
    val timeLabel: String,
    val totalKw: Double,
    val phaseL1Kw: Double,
    val phaseL2Kw: Double,
    val phaseL3Kw: Double
)

enum class TimeRange(val label: String) {
    ONE_HOUR("1 год"),
    TWENTY_FOUR_HOURS("24 год"),
    SEVEN_DAYS("7 днів")
}
