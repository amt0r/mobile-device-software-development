package com.example.emergencypower.data.storage

import android.content.Context
import com.example.emergencypower.data.model.SystemConfig
import com.example.emergencypower.data.model.TelemetryLog
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileWriter

class CsvJsonManager(private val context: Context) {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    fun exportLogsToCsv(logs: List<TelemetryLog>): File {
        val file = File(context.filesDir, "telemetry_export.csv")
        FileWriter(file).use { writer ->
            writer.append("Timestamp,Voltage_V,Frequency_Hz,Load_kW,PowerSource,Status\n")
            for (log in logs) {
                writer.append("${log.timestamp},${log.voltage},${log.frequency},${log.loadKw},${log.source},${log.status}\n")
            }
        }
        return file
    }

    fun readCsvLogs(): List<TelemetryLog> {
        val file = File(context.filesDir, "telemetry_export.csv")
        if (!file.exists()) return emptyList()
        val list = mutableListOf<TelemetryLog>()
        file.bufferedReader().useLines { lines ->
            lines.drop(1).forEach { line ->
                val tokens = line.split(",")
                if (tokens.size >= 6) {
                    list.add(
                        TelemetryLog(
                            timestamp = tokens[0],
                            voltage = tokens[1].toFloatOrNull() ?: 0f,
                            frequency = tokens[2].toFloatOrNull() ?: 0f,
                            loadKw = tokens[3].toFloatOrNull() ?: 0f,
                            source = tokens[4],
                            status = tokens[5]
                        )
                    )
                }
            }
        }
        return list
    }

    fun exportConfigToJson(config: SystemConfig): File {
        val file = File(context.filesDir, "system_config.json")
        val json = gson.toJson(config)
        file.writeText(json)
        return file
    }

    fun importConfigFromJson(): SystemConfig {
        val file = File(context.filesDir, "system_config.json")
        if (!file.exists()) return SystemConfig()
        val json = file.readText()
        return try {
            gson.fromJson(json, SystemConfig::class.java) ?: SystemConfig()
        } catch (e: Exception) {
            SystemConfig()
        }
    }

    fun getSampleLogs(): List<TelemetryLog> {
        return listOf(
            TelemetryLog("12:00:15", 221.4f, 50.02f, 320.5f, "Мережа 110 кВ", "НОРМА"),
            TelemetryLog("12:05:30", 218.7f, 49.98f, 345.1f, "Мережа 110 кВ", "НОРМА"),
            TelemetryLog("12:10:02", 188.2f, 48.75f, 360.0f, "Мережа 110 кВ", "ПРОСІДАННЯ"),
            TelemetryLog("12:10:05", 0.0f, 0.0f, 0.0f, "АВР СПРАЦЮВАВ", "АВАРІЯ МЕРЕЖІ"),
            TelemetryLog("12:10:08", 220.1f, 50.00f, 210.0f, "ДБЖ (Акумулятори)", "РЕЗЕРВ АКТИВНИЙ"),
            TelemetryLog("12:12:40", 220.8f, 50.05f, 315.4f, "Дизель-генератор", "АВТОНОМНИЙ РЕЖИМ"),
            TelemetryLog("12:25:10", 221.0f, 50.01f, 330.2f, "Дизель-генератор", "СТАБІЛЬНО"),
            TelemetryLog("12:40:00", 222.5f, 50.00f, 325.0f, "Мережа 110 кВ", "ВІДНОВЛЕНО")
        )
    }
}
