package com.example.emergencypower.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EmergencyEventEntity): Long

    @Update
    suspend fun updateEvent(event: EmergencyEventEntity)

    @Delete
    suspend fun deleteEvent(event: EmergencyEventEntity)

    @Query("DELETE FROM emergency_events WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM emergency_events")
    suspend fun clearAll()

    @Query("SELECT * FROM emergency_events ORDER BY timestamp DESC")
    fun getAllEventsFlow(): Flow<List<EmergencyEventEntity>>

    @Query("SELECT * FROM emergency_events WHERE sourceType = :sourceType ORDER BY timestamp DESC")
    fun getEventsBySourceFlow(sourceType: String): Flow<List<EmergencyEventEntity>>

    @Query("SELECT * FROM emergency_events WHERE severity = :severity ORDER BY timestamp DESC")
    fun getEventsBySeverityFlow(severity: String): Flow<List<EmergencyEventEntity>>
}
