package com.example.emergencypower.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyDao {

    @Query("SELECT * FROM telemetry_records ORDER BY timestamp DESC")
    fun getAllTelemetry(): Flow<List<TelemetryRecordEntity>>

    @Query("SELECT * FROM telemetry_records WHERE sourceName = :source ORDER BY timestamp DESC")
    fun getTelemetryBySource(source: String): Flow<List<TelemetryRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTelemetry(entity: TelemetryRecordEntity): Long

    @Update
    suspend fun updateTelemetry(entity: TelemetryRecordEntity)

    @Delete
    suspend fun deleteTelemetry(entity: TelemetryRecordEntity)

    @Query("DELETE FROM telemetry_records WHERE id = :id")
    suspend fun deleteTelemetryById(id: Long)

    @Query("SELECT COUNT(*) FROM telemetry_records")
    suspend fun getCount(): Int

    @Query("SELECT * FROM network_incidents ORDER BY timestamp DESC")
    fun getAllIncidents(): Flow<List<NetworkIncidentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncident(incident: NetworkIncidentEntity): Long

    @Query("DELETE FROM network_incidents")
    suspend fun clearIncidents()
}
