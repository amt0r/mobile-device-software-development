package com.example.emergencypower.ui.pw27

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ReleaseCheckItem(
    val title: String,
    val description: String,
    val isReady: Boolean
)

data class Pw27UiState(
    val appVersion: String = "1.0.0-RELEASE",
    val buildNumber: Int = 18,
    val targetSdk: Int = 37,
    val minSdk: Int = 31,
    val checklist: List<ReleaseCheckItem> = listOf(
        ReleaseCheckItem("ProGuard / R8 обфускація", "Правила оптимізації збереження моделей конфігуровано в proguard-rules.pro", true),
        ReleaseCheckItem("Підпис Release Keystore", "Налаштовано автоматичну верифікацію цілісності APK", true),
        ReleaseCheckItem("Аудит AndroidManifest", "Використовуються виключно задекларовані права доступу до сховища", true),
        ReleaseCheckItem("Сумісність з Android 12 - 15", "Підтримка динамічних палітр Dynamic Color та Edge-to-Edge", true),
        ReleaseCheckItem("Автоматичні Unit-тести", "100% проходження тестів логіки АВР та розрахунків автономності", true),
        ReleaseCheckItem("Чистота вихідного коду", "Повна відповідність Clean Code без надлишкових коментарів", true)
    ),
    val totalPracticalsCovered: Int = 27
)

class Practice27ViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(Pw27UiState())
    val uiState: StateFlow<Pw27UiState> = _uiState.asStateFlow()
}
