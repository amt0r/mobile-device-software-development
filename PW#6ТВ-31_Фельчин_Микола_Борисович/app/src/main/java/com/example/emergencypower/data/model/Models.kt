package com.example.emergencypower.data.model

data class TelemetryLog(
    val timestamp: String,
    val voltage: Float,
    val frequency: Float,
    val loadKw: Float,
    val source: String,
    val status: String
)

data class SystemConfig(
    val stationName: String = "ТП-110/10 кВ «Південна-18»",
    val nominalVoltage: Float = 220f,
    val minVoltageThreshold: Float = 195f,
    val maxVoltageThreshold: Float = 245f,
    val maxLoadKw: Float = 450f,
    val emergencyDelayMs: Long = 1500L,
    val autoTransferEnabled: Boolean = true
)

data class SubstationPhoto(
    val id: String,
    val title: String,
    val category: String,
    val timestamp: String,
    val status: String,
    val resolution: String,
    val simulatedColorHex: Long
)

data class TechDocument(
    val id: String,
    val title: String,
    val docType: String,
    val fileSizeKb: Int,
    val updatedDate: String,
    val author: String
)

data class TestCaseResult(
    val id: String,
    val testName: String,
    val category: String,
    val isPassed: Boolean,
    val durationMs: Long,
    val assertionDetails: String
)

data class DeviceResourceInfo(
    val totalRamMb: Long,
    val usedRamMb: Long,
    val ramUsagePercent: Int,
    val batteryLevelPercent: Int,
    val batteryTempCelsius: Float,
    val isCharging: Boolean,
    val networkType: String,
    val cpuCores: Int
)
