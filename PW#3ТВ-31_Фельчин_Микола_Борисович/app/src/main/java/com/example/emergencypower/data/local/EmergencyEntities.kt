package com.example.emergencypower.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "telemetry_records")
data class TelemetryRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val stationName: String,
    val timestamp: Long,
    val voltage: Double,
    val current: Double,
    val powerKw: Double,
    val sourceName: String,
    val statusName: String
)

@Entity(tableName = "network_incidents")
data class NetworkIncidentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val endpoint: String,
    val httpCode: Int,
    val errorMessage: String,
    val retryCount: Int,
    val resolved: Boolean
)
