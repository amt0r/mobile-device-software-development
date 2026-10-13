package com.example.emergencypower.data.repository

import com.example.emergencypower.data.local.EmergencyEventEntity
import com.example.emergencypower.data.local.EmergencyLogDao
import com.example.emergencypower.model.AlarmSeverity
import com.example.emergencypower.model.PowerSourceType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

data class LogAnalytics(
    val totalEvents: Int,
    val criticalAlarms: Int,
    val averageLoadWatts: Double,
    val maxLoadWatts: Double
)

class EmergencyLogRepository(private val dao: EmergencyLogDao) {

    fun getAllEvents(): Flow<List<EmergencyEventEntity>> = dao.getAllEventsFlow()

    fun getEventsBySource(source: String): Flow<List<EmergencyEventEntity>> =
        dao.getEventsBySourceFlow(source)

    fun getEventsBySeverity(severity: String): Flow<List<EmergencyEventEntity>> =
        dao.getEventsBySeverityFlow(severity)

    suspend fun insertEvent(event: EmergencyEventEntity): Long = withContext(Dispatchers.IO) {
        dao.insertEvent(event)
    }

    suspend fun updateEvent(event: EmergencyEventEntity) = withContext(Dispatchers.IO) {
        dao.updateEvent(event)
    }

    suspend fun deleteEvent(event: EmergencyEventEntity) = withContext(Dispatchers.IO) {
        dao.deleteEvent(event)
    }

    suspend fun deleteById(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteById(id)
    }

    suspend fun clearAll() = withContext(Dispatchers.IO) {
        dao.clearAll()
    }

    fun computeAnalytics(events: List<EmergencyEventEntity>): LogAnalytics {
        if (events.isEmpty()) {
            return LogAnalytics(
                totalEvents = 0,
                criticalAlarms = 0,
                averageLoadWatts = 0.0,
                maxLoadWatts = 0.0
            )
        }

        val total = events.size
        val critical = events.count { it.severity == AlarmSeverity.CRITICAL.name }
        val avgLoad = events.map { it.loadWatts }.average()
        val maxLoad = events.maxOfOrNull { it.loadWatts } ?: 0.0

        return LogAnalytics(
            totalEvents = total,
            criticalAlarms = critical,
            averageLoadWatts = avgLoad,
            maxLoadWatts = maxLoad
        )
    }

    suspend fun seedInitialDataIfEmpty(currentEventsCount: Int) = withContext(Dispatchers.IO) {
        if (currentEventsCount == 0) {
            val now = System.currentTimeMillis()
            val seedLogs = listOf(
                EmergencyEventEntity(
                    timestamp = now - 3600_000 * 3,
                    sourceType = PowerSourceType.GRID.name,
                    voltage = 229.5,
                    loadWatts = 2100.0,
                    batterySoc = 98,
                    severity = AlarmSeverity.INFO.name,
                    description = "Основна мережа в межах норми. АВР у режимі очікування."
                ),
                EmergencyEventEntity(
                    timestamp = now - 3600_000 * 2,
                    sourceType = PowerSourceType.BATTERY_UPS.name,
                    voltage = 218.0,
                    loadWatts = 3400.0,
                    batterySoc = 85,
                    severity = AlarmSeverity.WARNING.name,
                    description = "Стрибок напруги мережі. Миттєве перемикання на інвертор ДБЖ."
                ),
                EmergencyEventEntity(
                    timestamp = now - 3600_000,
                    sourceType = PowerSourceType.GENERATOR.name,
                    voltage = 231.0,
                    loadWatts = 4250.0,
                    batterySoc = 78,
                    severity = AlarmSeverity.CRITICAL.name,
                    description = "Повне знеструмлення підстанції. Автозапуск дизель-генератора ДГ-1."
                ),
                EmergencyEventEntity(
                    timestamp = now - 600_000,
                    sourceType = PowerSourceType.SOLAR_INVERTER.name,
                    voltage = 227.0,
                    loadWatts = 1800.0,
                    batterySoc = 84,
                    severity = AlarmSeverity.INFO.name,
                    description = "СЕС компенсує 65% власного споживання об'єкта."
                )
            )
            seedLogs.forEach { dao.insertEvent(it) }
        }
    }
}
