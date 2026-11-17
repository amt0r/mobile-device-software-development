package com.example.emergencypower.data.repository

import com.example.emergencypower.data.local.EmergencyDao
import com.example.emergencypower.data.local.NetworkIncidentEntity
import com.example.emergencypower.data.local.TelemetryRecordEntity
import com.example.emergencypower.data.remote.RetrofitClient
import com.example.emergencypower.data.remote.model.GeneratorControlDto
import com.example.emergencypower.data.remote.model.GridStatusDto
import com.example.emergencypower.data.remote.model.LoadForecastResponseDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

data class TelemetryAggregates(
    val totalCount: Int,
    val averagePowerKw: Double,
    val peakPowerKw: Double,
    val averageVoltage: Double
)

class EmergencyRepository(
    private val dao: EmergencyDao
) {

    val localTelemetryFlow: Flow<List<TelemetryRecordEntity>> = dao.getAllTelemetry()
    val localIncidentsFlow: Flow<List<NetworkIncidentEntity>> = dao.getAllIncidents()

    fun getTelemetryFiltered(source: String): Flow<List<TelemetryRecordEntity>> {
        return if (source == "Всі") {
            dao.getAllTelemetry()
        } else {
            dao.getTelemetryBySource(source)
        }
    }

    suspend fun addTelemetryRecord(record: TelemetryRecordEntity): Long {
        return dao.insertTelemetry(record)
    }

    suspend fun updateTelemetryRecord(record: TelemetryRecordEntity) {
        dao.updateTelemetry(record)
    }

    suspend fun deleteTelemetryRecord(record: TelemetryRecordEntity) {
        dao.deleteTelemetry(record)
    }

    suspend fun deleteTelemetryById(id: Long) {
        dao.deleteTelemetryById(id)
    }

    suspend fun calculateAggregates(): TelemetryAggregates {
        val records = dao.getAllTelemetry().first()
        if (records.isEmpty()) {
            return TelemetryAggregates(0, 0.0, 0.0, 0.0)
        }
        val count = records.size
        val avgPower = records.map { it.powerKw }.average()
        val peakPower = records.maxOfOrNull { it.powerKw } ?: 0.0
        val avgVoltage = records.map { it.voltage }.average()
        return TelemetryAggregates(count, avgPower, peakPower, avgVoltage)
    }

    suspend fun fetchRemoteGridStatus(forceError: Int? = null): Result<GridStatusDto> {
        return try {
            val response = RetrofitClient.apiService.getGridStatus(forceError)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                dao.insertTelemetry(
                    TelemetryRecordEntity(
                        stationName = body.stationId,
                        timestamp = body.timestamp,
                        voltage = body.gridVoltage,
                        current = body.currentLoadKw * 1000.0 / (body.gridVoltage * 1.73),
                        powerKw = body.currentLoadKw,
                        sourceName = body.activeSource,
                        statusName = if (body.backupReady) "Норма" else "Увага"
                    )
                )
                Result.success(body)
            } else {
                Result.failure(Exception("HTTP Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchLoadForecast(hours: Int = 6, forceError: Int? = null): Result<LoadForecastResponseDto> {
        return try {
            val response = RetrofitClient.apiService.getLoadForecast(hours, forceError)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun executeGeneratorControl(command: String, generatorId: String): Result<String> {
        return try {
            val response = RetrofitClient.apiService.executeGeneratorControl(
                GeneratorControlDto(command, generatorId)
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.message)
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logIncident(
        endpoint: String,
        httpCode: Int,
        message: String,
        retries: Int,
        resolved: Boolean
    ): Long {
        return dao.insertIncident(
            NetworkIncidentEntity(
                timestamp = System.currentTimeMillis(),
                endpoint = endpoint,
                httpCode = httpCode,
                errorMessage = message,
                retryCount = retries,
                resolved = resolved
            )
        )
    }

    suspend fun clearIncidents() {
        dao.clearIncidents()
    }
}
