package com.example.emergencypower.model

enum class PowerSource(val title: String) {
    GRID("Основна мережа 230В"),
    BATTERY("Акумуляторний ДБЖ"),
    DIESEL_GENERATOR("Дизель-генератор ДГ-50")
}

enum class SystemStatus(val label: String) {
    NORMAL("Норма"),
    WARNING("Увага"),
    CRITICAL("Аварія"),
    STANDBY("Очікування")
}

enum class UserRole(val roleName: String) {
    ADMIN("Головний енергетик (Admin)"),
    OPERATOR("Черговий оператор (Operator)")
}

data class UserSession(
    val username: String,
    val role: UserRole,
    val token: String,
    val issuedAt: Long,
    val expiresInMs: Long
) {
    val isExpired: Boolean
        get() = (System.currentTimeMillis() - issuedAt) > expiresInMs
}

data class TelemetryMetric(
    val id: String,
    val timestamp: Long,
    val voltage: Double,
    val current: Double,
    val powerKw: Double,
    val frequencyHz: Double,
    val source: PowerSource,
    val status: SystemStatus
)
