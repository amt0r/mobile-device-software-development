package com.example.emergencypower.model

enum class PowerSourceType(val displayName: String) {
    GRID("Основна мережа 230V"),
    GENERATOR("Дизель-генератор"),
    BATTERY_UPS("Акумуляторний ДБЖ"),
    SOLAR_INVERTER("Гібридний інвертор / СЕС")
}

enum class AlarmSeverity(val displayName: String) {
    NORMAL("Норма"),
    INFO("Інформація"),
    WARNING("Попередження"),
    CRITICAL("Аварія / Критично")
}

enum class GeneratorState(val displayName: String) {
    STOPPED("Зупинено"),
    STARTING("Прогрів та синхронізація..."),
    RUNNING("В роботі (під навантаженням)"),
    FAULT("Аварійне блокування")
}

data class EmergencySystemTelemetry(
    val gridVoltage: Double = 228.4,
    val gridFrequency: Double = 50.02,
    val loadWatts: Double = 2450.0,
    val batterySocPercent: Int = 92,
    val generatorState: GeneratorState = GeneratorState.STOPPED,
    val activeSource: PowerSourceType = PowerSourceType.GRID,
    val isAtsEngaged: Boolean = true,
    val estimatedBackupMinutes: Int = 420
)

data class EmergencyConfig(
    val operatorEmail: String,
    val switchoverTimeoutSec: Int,
    val batteryCutoffPercent: Int,
    val emergencyPin: String,
    val maxLoadWatts: Int
)

data class FormValidationResult(
    val isValid: Boolean,
    val errors: Map<String, String> = emptyMap(),
    val summaryMessage: String = ""
)
