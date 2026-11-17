package com.example.emergencypower.data.remote.model

import com.google.gson.annotations.SerializedName

data class GridStatusDto(
    @SerializedName("station_id") val stationId: String,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("grid_voltage") val gridVoltage: Double,
    @SerializedName("frequency_hz") val frequencyHz: Double,
    @SerializedName("current_load_kw") val currentLoadKw: Double,
    @SerializedName("active_source") val activeSource: String,
    @SerializedName("backup_ready") val backupReady: Boolean,
    @SerializedName("battery_soc_percent") val batterySocPercent: Int
)

data class LoadForecastItemDto(
    @SerializedName("hour_offset") val hourOffset: Int,
    @SerializedName("predicted_kw") val predictedKw: Double,
    @SerializedName("risk_level") val riskLevel: String
)

data class LoadForecastResponseDto(
    @SerializedName("substation_name") val substationName: String,
    @SerializedName("generated_at") val generatedAt: Long,
    @SerializedName("forecast_items") val forecastItems: List<LoadForecastItemDto>
)

data class LoginRequestDto(
    @SerializedName("username") val username: String,
    @SerializedName("password_hash") val passwordHash: String
)

data class LoginResponseDto(
    @SerializedName("token") val token: String,
    @SerializedName("role") val role: String,
    @SerializedName("expires_in_seconds") val expiresInSeconds: Long
)

data class GeneratorControlDto(
    @SerializedName("command") val command: String,
    @SerializedName("generator_id") val generatorId: String
)

data class ApiResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)
