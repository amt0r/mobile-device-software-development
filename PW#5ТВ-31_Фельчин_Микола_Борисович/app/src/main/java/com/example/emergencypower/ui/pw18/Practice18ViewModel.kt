package com.example.emergencypower.ui.pw18

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.emergencypower.data.SensorNotificationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class NotificationHistoryItem(
    val id: Int,
    val title: String,
    val text: String,
    val timestamp: Long,
    val acknowledged: Boolean
)

data class Practice18UiState(
    val notificationList: List<NotificationHistoryItem> = emptyList(),
    val totalSentCount: Int = 0
)

class Practice18ViewModel(application: Application) : AndroidViewModel(application) {

    private val notificationManager = SensorNotificationManager(application)

    private val _uiState = MutableStateFlow(Practice18UiState())
    val uiState: StateFlow<Practice18UiState> = _uiState.asStateFlow()

    fun sendBlackoutAlert() {
        val id = 101 + _uiState.value.totalSentCount
        val title = "АВАРІЯ: Знеструмлення мережі 230В"
        val text = "Зафіксовано зникнення напруги на вводі №1. ДБЖ перехопив живлення."
        notificationManager.sendEmergencyNotification(title, text, id)
        addHistory(id, title, text)
    }

    fun sendBatteryLowAlert() {
        val id = 101 + _uiState.value.totalSentCount
        val title = "УВАГА: Критичний розряд АКБ < 20%"
        val text = "Залишковий час автономної роботи: 14 хв. Потрібен запуск ДГ-50."
        notificationManager.sendEmergencyNotification(title, text, id)
        addHistory(id, title, text)
    }

    fun sendGeneratorOverheatAlert() {
        val id = 101 + _uiState.value.totalSentCount
        val title = "ПОПЕРЕДЖЕННЯ: Перегрів обмоток ДГ-50"
        val text = "Температура охолоджувальної рідини 98°C. Знизьте навантаження."
        notificationManager.sendEmergencyNotification(title, text, id)
        addHistory(id, title, text)
    }

    fun acknowledgeAlert(id: Int) {
        _uiState.update { state ->
            state.copy(
                notificationList = state.notificationList.map {
                    if (it.id == id) it.copy(acknowledged = true) else it
                }
            )
        }
    }

    private fun addHistory(id: Int, title: String, text: String) {
        val item = NotificationHistoryItem(id, title, text, System.currentTimeMillis(), false)
        _uiState.update {
            it.copy(
                notificationList = listOf(item) + it.notificationList,
                totalSentCount = it.totalSentCount + 1
            )
        }
    }
}
