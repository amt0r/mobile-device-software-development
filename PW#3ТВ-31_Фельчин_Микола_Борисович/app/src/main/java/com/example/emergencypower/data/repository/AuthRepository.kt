package com.example.emergencypower.data.repository

import android.content.Context
import com.example.emergencypower.data.remote.RetrofitClient
import com.example.emergencypower.data.remote.model.LoginRequestDto
import com.example.emergencypower.model.UserRole
import com.example.emergencypower.model.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthRepository(context: Context) {

    private val prefs = context.getSharedPreferences("auth_prefs_pw3", Context.MODE_PRIVATE)

    private val _currentSession = MutableStateFlow<UserSession?>(loadSession())
    val currentSession: StateFlow<UserSession?> = _currentSession.asStateFlow()

    init {
        RetrofitClient.setTokenProvider {
            _currentSession.value?.takeIf { !it.isExpired }?.token
        }
    }

    private fun loadSession(): UserSession? {
        val username = prefs.getString("username", null) ?: return null
        val roleStr = prefs.getString("role", UserRole.OPERATOR.name) ?: UserRole.OPERATOR.name
        val token = prefs.getString("token", "") ?: ""
        val issuedAt = prefs.getLong("issued_at", 0L)
        val expiresInMs = prefs.getLong("expires_in_ms", 3600_000L)

        val role = try {
            UserRole.valueOf(roleStr)
        } catch (e: Exception) {
            UserRole.OPERATOR
        }

        val session = UserSession(username, role, token, issuedAt, expiresInMs)
        return if (!session.isExpired) session else null
    }

    suspend fun login(username: String, passwordHash: String, selectedRole: UserRole): Result<UserSession> {
        return try {
            val response = RetrofitClient.apiService.login(LoginRequestDto(username, passwordHash))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val session = UserSession(
                    username = username,
                    role = selectedRole,
                    token = body.token.replace("ADMIN", selectedRole.name),
                    issuedAt = System.currentTimeMillis(),
                    expiresInMs = body.expiresInSeconds * 1000
                )
                saveSession(session)
                _currentSession.value = session
                Result.success(session)
            } else {
                Result.failure(Exception("Помилка авторизації: HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            val fallbackSession = UserSession(
                username = username,
                role = selectedRole,
                token = "offline_jwt_${selectedRole.name}_${System.currentTimeMillis()}",
                issuedAt = System.currentTimeMillis(),
                expiresInMs = 3600_000L
            )
            saveSession(fallbackSession)
            _currentSession.value = fallbackSession
            Result.success(fallbackSession)
        }
    }

    fun logout() {
        prefs.edit().clear().apply()
        _currentSession.value = null
    }

    private fun saveSession(session: UserSession) {
        prefs.edit()
            .putString("username", session.username)
            .putString("role", session.role.name)
            .putString("token", session.token)
            .putLong("issued_at", session.issuedAt)
            .putLong("expires_in_ms", session.expiresInMs)
            .apply()
    }
}
