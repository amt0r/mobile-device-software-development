package com.example.emergencypower.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emergency_events")
data class EmergencyEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val sourceType: String,
    val voltage: Double,
    val loadWatts: Double,
    val batterySoc: Int,
    val severity: String,
    val description: String
)
