package com.example.emergencypower.model

enum class StationType(val label: String) {
    DIESEL("Дизель-генераторна установка"),
    BATTERY_UPS("Акумуляторний комплекс ДБЖ"),
    SOLAR_HYBRID("Сонячно-дизельний гібрид")
}

enum class StationStatus(val label: String) {
    ONLINE("В роботі"),
    STANDBY("Резерв"),
    MAINTENANCE("Тех. обслуговування"),
    FAULT("Аварія")
}

data class GeoLocation(
    val latitude: Double,
    val longitude: Double
)

data class PowerStation(
    val id: String,
    val name: String,
    val code: String,
    val type: StationType,
    val status: StationStatus,
    val nominalPowerKw: Double,
    val currentLoadKw: Double,
    val reserveLevelPercent: Int,
    val location: GeoLocation,
    val address: String,
    val lastMaintenanceDate: String
)

data class SensorStreamData(
    val timestamp: Long,
    val gridFrequencyHz: Double,
    val voltagePhaseA: Double,
    val voltagePhaseB: Double,
    val voltagePhaseC: Double,
    val generatorFuelPercent: Double,
    val batteryTemperatureC: Double,
    val overallReliabilityIndex: Double,
    val hasFault: Boolean
)
