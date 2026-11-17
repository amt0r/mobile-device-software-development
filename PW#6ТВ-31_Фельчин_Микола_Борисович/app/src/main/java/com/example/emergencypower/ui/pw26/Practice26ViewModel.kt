package com.example.emergencypower.ui.pw26

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import androidx.lifecycle.AndroidViewModel
import com.example.emergencypower.data.model.DeviceResourceInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Pw26UiState(
    val resourceInfo: DeviceResourceInfo = DeviceResourceInfo(
        totalRamMb = 6144,
        usedRamMb = 3250,
        ramUsagePercent = 53,
        batteryLevelPercent = 88,
        batteryTempCelsius = 29.5f,
        isCharging = false,
        networkType = "Wi-Fi (5 GHz)",
        cpuCores = 8
    ),
    val pollingIntervalMs: Long = 1000L,
    val isHighContrast: Boolean = true,
    val isBackgroundSync: Boolean = true
)

class Practice26ViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(Pw26UiState())
    val uiState: StateFlow<Pw26UiState> = _uiState.asStateFlow()

    init {
        refreshMetrics()
    }

    fun refreshMetrics() {
        val context = getApplication<Application>()

        var totalMb = 6144L
        var availMb = 2894L
        try {
            val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val memInfo = ActivityManager.MemoryInfo()
            if (actManager != null) {
                actManager.getMemoryInfo(memInfo)
                totalMb = memInfo.totalMem / (1024 * 1024)
                availMb = memInfo.availMem / (1024 * 1024)
            }
        } catch (_: Exception) {}
        val usedMb = (totalMb - availMb).coerceAtLeast(0)
        val ramPercent = if (totalMb > 0) ((usedMb.toFloat() / totalMb) * 100).toInt() else 50

        var batLevel = 88
        var tempC = 29.5f
        var isCharging = false
        try {
            val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val rawLevel = batteryIntent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: 88
            val rawScale = batteryIntent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: 100
            if (rawLevel >= 0 && rawScale > 0) {
                batLevel = ((rawLevel.toFloat() / rawScale) * 100).toInt()
            }
            val rawTemp = batteryIntent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 295) ?: 295
            tempC = rawTemp / 10.0f
            val status = batteryIntent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
            isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
        } catch (_: Exception) {}

        var netType = "Wi-Fi 802.11ax"
        try {
            val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            if (connManager != null) {
                val net = connManager.activeNetwork
                val caps = connManager.getNetworkCapabilities(net)
                netType = when {
                    caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> "Wi-Fi 802.11ax"
                    caps?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> "Мобільна мережа 4G/LTE"
                    caps?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> "Ethernet LAN"
                    else -> "Локальна підмережа"
                }
            }
        } catch (_: Exception) {
            netType = "Мережа зв'язку SCADA"
        }

        var cores = 8
        try {
            cores = Runtime.getRuntime().availableProcessors()
        } catch (_: Exception) {}

        _uiState.value = _uiState.value.copy(
            resourceInfo = DeviceResourceInfo(
                totalRamMb = totalMb,
                usedRamMb = usedMb,
                ramUsagePercent = ramPercent,
                batteryLevelPercent = batLevel,
                batteryTempCelsius = tempC,
                isCharging = isCharging,
                networkType = netType,
                cpuCores = cores
            )
        )
    }

    fun setPollingInterval(interval: Long) {
        _uiState.value = _uiState.value.copy(pollingIntervalMs = interval)
    }

    fun toggleHighContrast() {
        _uiState.value = _uiState.value.copy(isHighContrast = !_uiState.value.isHighContrast)
    }

    fun toggleBackgroundSync() {
        _uiState.value = _uiState.value.copy(isBackgroundSync = !_uiState.value.isBackgroundSync)
    }
}
