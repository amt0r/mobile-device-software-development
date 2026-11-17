package com.example.emergencypower.ui.pw11

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.emergencypower.data.local.EmergencyDatabase
import com.example.emergencypower.data.repository.AuthRepository
import com.example.emergencypower.data.repository.EmergencyRepository
import com.example.emergencypower.model.UserRole
import com.example.emergencypower.model.UserSession
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class Practice11UiState(
    val session: UserSession? = null,
    val loginInput: String = "felchyn_m",
    val passwordInput: String = "admin18_kpi",
    val selectedRole: UserRole = UserRole.ADMIN,
    val isLoggingIn: Boolean = false,
    val actionMessage: String? = null,
    val isActionSuccess: Boolean = true,
    val errorMessage: String? = null
)

class Practice11ViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository = AuthRepository(application)
    private val emergencyRepository = EmergencyRepository(
        EmergencyDatabase.getInstance(application).emergencyDao()
    )

    private val _uiState = MutableStateFlow(Practice11UiState())
    val uiState: StateFlow<Practice11UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.currentSession.collect { sess ->
                _uiState.update { it.copy(session = sess) }
            }
        }
    }

    fun onLoginChanged(login: String) {
        _uiState.update { it.copy(loginInput = login, errorMessage = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(passwordInput = password, errorMessage = null) }
    }

    fun onRoleSelected(role: UserRole) {
        _uiState.update { it.copy(selectedRole = role) }
    }

    fun login() {
        val login = _uiState.value.loginInput.trim()
        val pass = _uiState.value.passwordInput.trim()

        if (login.isEmpty() || pass.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Логін та пароль не можуть бути порожніми") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoggingIn = true, errorMessage = null) }
            val result = authRepository.login(login, pass, _uiState.value.selectedRole)
            _uiState.update {
                it.copy(
                    isLoggingIn = false,
                    actionMessage = if (result.isSuccess) "Автентифікація успішна. Токен збережено в системі." else null,
                    isActionSuccess = result.isSuccess,
                    errorMessage = result.exceptionOrNull()?.message
                )
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _uiState.update {
            it.copy(
                actionMessage = "Сесію диспетчера завершено. Токен очищено.",
                isActionSuccess = true
            )
        }
    }

    fun executeSecureCommand(command: String) {
        viewModelScope.launch {
            val result = emergencyRepository.executeGeneratorControl(command, "DG-50-KYIV")
            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        actionMessage = result.getOrNull(),
                        isActionSuccess = true,
                        errorMessage = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        actionMessage = null,
                        errorMessage = "Помилка виконання команди: ${result.exceptionOrNull()?.message}",
                        isActionSuccess = false
                    )
                }
            }
        }
    }
}
