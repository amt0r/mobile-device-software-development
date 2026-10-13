package com.example.emergencypower.logic

import com.example.emergencypower.model.FormValidationResult

class EmergencyFormValidator {

    private val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

    fun validateForm(
        email: String,
        timeoutSecStr: String,
        batteryCutoffStr: String,
        pin: String,
        maxLoadStr: String
    ): FormValidationResult {
        val errors = mutableMapOf<String, String>()

        if (email.isBlank()) {
            errors["email"] = "Email диспетчера є обов'язковим для заповнення"
        } else if (!email.matches(emailRegex)) {
            errors["email"] = "Некоректний формат email (приклад: engineer@epss.ua)"
        } else if (email.equals("blocked@epss.ua", ignoreCase = true)) {
            errors["email"] = "Даний email заблоковано в системі диспетчеризації"
        }

        val timeout = timeoutSecStr.toIntOrNull()
        if (timeoutSecStr.isBlank()) {
            errors["timeout"] = "Вкажіть час затримки перемикання АВР"
        } else if (timeout == null) {
            errors["timeout"] = "Значення має бути цілим числом"
        } else if (timeout !in 3..60) {
            errors["timeout"] = "Затримка повинна бути в діапазоні від 3 до 60 секунд"
        }

        val cutoff = batteryCutoffStr.toIntOrNull()
        if (batteryCutoffStr.isBlank()) {
            errors["batteryCutoff"] = "Вкажіть критичний поріг відключення АКБ"
        } else if (cutoff == null) {
            errors["batteryCutoff"] = "Введіть ціле число відсотків"
        } else if (cutoff !in 10..50) {
            errors["batteryCutoff"] = "Критичний поріг має бути в межах 10% - 50%"
        }

        if (pin.isBlank()) {
            errors["pin"] = "Введіть аварійний код авторизації (PIN)"
        } else if (pin.length < 6) {
            errors["pin"] = "Код повинен містити не менше 6 символів"
        } else if (!pin.any { it.isDigit() } || !pin.any { it.isLetter() }) {
            errors["pin"] = "Код повинен містити як літери, так і цифри"
        }

        val maxLoad = maxLoadStr.toDoubleOrNull()
        if (maxLoadStr.isBlank()) {
            errors["maxLoad"] = "Вкажіть максимальну потужність аварійної лінії"
        } else if (maxLoad == null) {
            errors["maxLoad"] = "Введіть коректне числове значення потужності"
        } else if (maxLoad !in 500.0..15000.0) {
            errors["maxLoad"] = "Потужність має бути від 500 до 15 000 Вт"
        }

        val isValid = errors.isEmpty()
        val summary = if (isValid) {
            "Конфігурацію аварійного живлення успішно перевірено та схвалено!"
        } else {
            "Виявлено помилки у ${errors.size} полях введення. Виправте їх для збереження."
        }

        return FormValidationResult(
            isValid = isValid,
            errors = errors,
            summaryMessage = summary
        )
    }
}
