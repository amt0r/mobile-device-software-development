package com.example.emergencypower.data

import com.example.emergencypower.model.GeoLocation
import com.example.emergencypower.model.PowerStation
import com.example.emergencypower.model.SensorStreamData
import com.example.emergencypower.model.StationStatus
import com.example.emergencypower.model.StationType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlin.random.Random

class PowerStationRepository {

    private val stations = listOf(
        PowerStation(
            id = "ST-18-01",
            name = "ПС-110/10 «Київська Енергетична №18»",
            code = "KYIV-SUB-18",
            type = StationType.DIESEL,
            status = StationStatus.ONLINE,
            nominalPowerKw = 50.0,
            currentLoadKw = 42.4,
            reserveLevelPercent = 88,
            location = GeoLocation(50.4501, 30.5234),
            address = "вул. Хрещатик, 22, м. Київ",
            lastMaintenanceDate = "15.10.2026"
        ),
        PowerStation(
            id = "ST-18-02",
            name = "Модуль ДБЖ «Політехнічний-18»",
            code = "KPI-UPS-18",
            type = StationType.BATTERY_UPS,
            status = StationStatus.ONLINE,
            nominalPowerKw = 120.0,
            currentLoadKw = 68.5,
            reserveLevelPercent = 95,
            location = GeoLocation(50.4485, 30.4570),
            address = "просп. Берестейський, 37, м. Київ",
            lastMaintenanceDate = "02.11.2026"
        ),
        PowerStation(
            id = "ST-18-03",
            name = "Дизель-генераторна станція ДГ-50 «КПІ Корпус №8»",
            code = "KPI-GEN-08",
            type = StationType.DIESEL,
            status = StationStatus.STANDBY,
            nominalPowerKw = 65.0,
            currentLoadKw = 0.0,
            reserveLevelPercent = 78,
            location = GeoLocation(50.4510, 30.4610),
            address = "вул. Політехнічна, 6, м. Київ",
            lastMaintenanceDate = "28.09.2026"
        ),
        PowerStation(
            id = "ST-18-04",
            name = "Гібридна сонячно-дизельна підстанція СДП-18",
            code = "SOLAR-HYB-18",
            type = StationType.SOLAR_HYBRID,
            status = StationStatus.ONLINE,
            nominalPowerKw = 80.0,
            currentLoadKw = 55.2,
            reserveLevelPercent = 91,
            location = GeoLocation(50.4370, 30.4900),
            address = "вул. Жилянська, 97, м. Київ",
            lastMaintenanceDate = "08.11.2026"
        ),
        PowerStation(
            id = "ST-18-05",
            name = "Резервний щит АВР «Дніпровський вузол №18»",
            code = "DNIPRO-ATS-18",
            type = StationType.BATTERY_UPS,
            status = StationStatus.MAINTENANCE,
            nominalPowerKw = 90.0,
            currentLoadKw = 12.0,
            reserveLevelPercent = 45,
            location = GeoLocation(50.4560, 30.5920),
            address = "вул. Раїси Окіпної, 4, м. Київ",
            lastMaintenanceDate = "11.11.2026"
        ),
        PowerStation(
            id = "ST-18-06",
            name = "Мобільний дизель-комплекс МДК-18",
            code = "MOB-DIESEL-18",
            type = StationType.DIESEL,
            status = StationStatus.FAULT,
            nominalPowerKw = 40.0,
            currentLoadKw = 0.0,
            reserveLevelPercent = 25,
            location = GeoLocation(50.4100, 30.5300),
            address = "вул. Саперно-Слобідська, 10, м. Київ",
            lastMaintenanceDate = "20.08.2026"
        )
    )

    fun getAllStations(): Flow<List<PowerStation>> = flowOf(stations)

    fun getStationById(id: String): PowerStation? = stations.find { it.id == id }

    val rawFrequencyFlow: Flow<Double> = flow {
        while (true) {
            val freq = 50.0 + (Random.nextDouble(-0.15, 0.15))
            emit(freq)
            delay(800)
        }
    }

    val rawVoltagePhaseAFlow: Flow<Double> = flow {
        while (true) {
            val v = 225.0 + (Random.nextDouble(-6.0, 5.0))
            emit(v)
            delay(800)
        }
    }

    val rawFuelAndSocFlow: Flow<Double> = flow {
        var level = 85.0
        while (true) {
            level = (level - 0.1).coerceAtLeast(20.0)
            emit(level)
            delay(1200)
        }
    }

    val combinedSensorStream: Flow<SensorStreamData> = combine(
        rawFrequencyFlow,
        rawVoltagePhaseAFlow,
        rawFuelAndSocFlow
    ) { freq, vA, fuel ->
        val vB = vA + Random.nextDouble(-1.5, 1.5)
        val vC = vA + Random.nextDouble(-1.5, 1.5)
        val temp = 34.0 + (Random.nextDouble(-1.0, 2.0))
        val reliability = if (freq in 49.8..50.2 && vA in 215.0..235.0) 0.98 else 0.82
        val hasFault = vA < 210.0 || freq < 49.5

        SensorStreamData(
            timestamp = System.currentTimeMillis(),
            gridFrequencyHz = freq,
            voltagePhaseA = vA,
            voltagePhaseB = vB,
            voltagePhaseC = vC,
            generatorFuelPercent = fuel,
            batteryTemperatureC = temp,
            overallReliabilityIndex = reliability,
            hasFault = hasFault
        )
    }.catch {
        emit(
            SensorStreamData(
                timestamp = System.currentTimeMillis(),
                gridFrequencyHz = 50.0,
                voltagePhaseA = 220.0,
                voltagePhaseB = 220.0,
                voltagePhaseC = 220.0,
                generatorFuelPercent = 75.0,
                batteryTemperatureC = 30.0,
                overallReliabilityIndex = 0.95,
                hasFault = false
            )
        )
    }
}
