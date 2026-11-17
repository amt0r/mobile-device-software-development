package com.example.emergencypower.ui.pw12

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.emergencypower.data.local.EmergencyDatabase
import com.example.emergencypower.data.local.NetworkIncidentEntity
import com.example.emergencypower.data.repository.EmergencyRepository
import com.example.emergencypower.data.repository.NetworkMonitor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class Practice12UiState(
    val isOnline: Boolean = true,
    val isExecutingRequest: Boolean = false,
    val currentRetryAttempt: Int = 0,
    val lastResponseStatus: String = "Очікування запиту",
    val snackbarMessage: String? = null,
    val simulatedCode: Int = 503
)

class Practice12ViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = EmergencyRepository(
        EmergencyDatabase.getInstance(application).emergencyDao()
    )
    private val networkMonitor = NetworkMonitor(application)

    private val _uiState = MutableStateFlow(Practice12UiState())
    val uiState: StateFlow<Practice12UiState> = _uiState.asStateFlow()

    val incidentsList: StateFlow<List<NetworkIncidentEntity>> =
        repository.localIncidentsFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            networkMonitor.isOnlineFlow.collect { online ->
                _uiState.update { it.copy(isOnline = online) }
            }
        }
    }

    fun setSimulatedCode(code: Int) {
        _uiState.update { it.copy(simulatedCode = code) }
    }

    fun triggerNetworkRequestWithRetry(forceCode: Int? = null) {
        val codeToTest = forceCode ?: _uiState.value.simulatedCode

        viewModelScope.launch {
            if (!_uiState.value.isOnline) {
                val errorMsg = "Помилка мережі: відсутнє підключення до інтернету"
                _uiState.update {
                    it.copy(
                        lastResponseStatus = errorMsg,
                        snackbarMessage = errorMsg
                    )
                }
                repository.logIncident("/api/v1/grid/status", 0, errorMsg, 0, false)
                return@launch
            }

            _uiState.update {
                it.copy(
                    isExecutingRequest = true,
                    currentRetryAttempt = 0,
                    lastResponseStatus = "Виконується запит до SCADA..."
                )
            }

            var success = false
            var attempt = 0
            val maxAttempts = 3
            var lastErrorMessage = ""

            while (attempt < maxAttempts && !success) {
                attempt++
                _uiState.update { it.copy(currentRetryAttempt = attempt) }

                val currentCode = if (attempt == maxAttempts && codeToTest == 503) null else codeToTest
                val result = repository.fetchRemoteGridStatus(currentCode)

                if (result.isSuccess) {
                    success = true
                    val msg = "Запит успішно виконано (Спроба $attempt з $maxAttempts)"
                    _uiState.update {
                        it.copy(
                            isExecutingRequest = false,
                            lastResponseStatus = msg,
                            snackbarMessage = msg
                        )
                    }
                    repository.logIncident(
                        "/api/v1/grid/status",
                        200,
                        "Успішна відповідь після повторних спроб",
                        attempt,
                        true
                    )
                } else {
                    lastErrorMessage = result.exceptionOrNull()?.message ?: "Помилка HTTP"
                    if (attempt < maxAttempts) {
                        _uiState.update {
                            it.copy(lastResponseStatus = "Збій (HTTP $codeToTest). Автоматичний повтор через ${attempt * 500}мс...")
                        }
                        delay(attempt * 500L)
                    }
                }
            }

            if (!success) {
                val failureMsg = "Вичерпано ліміт повторних спроб ($maxAttempts). Помилка: $lastErrorMessage"
                _uiState.update {
                    it.copy(
                        isExecutingRequest = false,
                        lastResponseStatus = failureMsg,
                        snackbarMessage = failureMsg
                    )
                }
                repository.logIncident(
                    "/api/v1/grid/status",
                    codeToTest,
                    lastErrorMessage,
                    maxAttempts,
                    false
                )
            }
        }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun clearIncidents() {
        viewModelScope.launch {
            repository.clearIncidents()
        }
    }
}
