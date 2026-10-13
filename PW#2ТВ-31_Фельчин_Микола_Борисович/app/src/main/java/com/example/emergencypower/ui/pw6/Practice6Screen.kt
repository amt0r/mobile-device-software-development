package com.example.emergencypower.ui.pw6

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.emergencypower.logic.EmergencyFormValidator
import com.example.emergencypower.model.FormValidationResult
import com.example.emergencypower.ui.components.EmergencyHeader
import com.example.emergencypower.ui.components.PrimaryActionButton
import com.example.emergencypower.ui.components.ValidatedInputField
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Practice6Screen() {
    val validator = remember { EmergencyFormValidator() }
    val coroutineScope = rememberCoroutineScope()

    var email by remember { mutableStateOf("dispatcher@epss.kpi.ua") }
    var switchoverTimeout by remember { mutableStateOf("5") }
    var batteryCutoff by remember { mutableStateOf("20") }
    var emergencyPin by remember { mutableStateOf("sec99A") }
    var maxLoad by remember { mutableStateOf("4500") }

    var validationResult by remember { mutableStateOf<FormValidationResult?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    var savedSuccessConfig by remember { mutableStateOf<Map<String, String>?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        EmergencyHeader(
            title = "ПЗ №6: Форми та валідація даних",
            subtitle = "Конфігурація автоматики введення резерву (АВР) та лімітів АЕП"
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "Параметри системи аварійного живлення:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                ValidatedInputField(
                    value = email,
                    onValueChange = {
                        email = it
                        validationResult = null
                    },
                    label = "Email чергового інженера",
                    helperText = "Адреса для надсилання аварійних сповіщень та журналів",
                    isError = validationResult?.errors?.containsKey("email") == true,
                    errorMessage = validationResult?.errors?.get("email"),
                    keyboardType = KeyboardType.Email
                )

                ValidatedInputField(
                    value = switchoverTimeout,
                    onValueChange = {
                        switchoverTimeout = it
                        validationResult = null
                    },
                    label = "Затримка перемикання АВР (секунди)",
                    helperText = "Час фільтрації короткочасних провалів напруги (від 3 до 60 сек)",
                    isError = validationResult?.errors?.containsKey("timeout") == true,
                    errorMessage = validationResult?.errors?.get("timeout"),
                    keyboardType = KeyboardType.Number
                )

                ValidatedInputField(
                    value = batteryCutoff,
                    onValueChange = {
                        batteryCutoff = it
                        validationResult = null
                    },
                    label = "Критичний поріг розряду АКБ (%)",
                    helperText = "Мінімальний залишок заряду перед захисним вимкненням (10-50%)",
                    isError = validationResult?.errors?.containsKey("batteryCutoff") == true,
                    errorMessage = validationResult?.errors?.get("batteryCutoff"),
                    keyboardType = KeyboardType.Number
                )

                ValidatedInputField(
                    value = emergencyPin,
                    onValueChange = {
                        emergencyPin = it
                        validationResult = null
                    },
                    label = "Аварійний PIN-код доступу",
                    helperText = "Мінімум 6 символів, обов'язково цифри та латинські літери",
                    isError = validationResult?.errors?.containsKey("pin") == true,
                    errorMessage = validationResult?.errors?.get("pin"),
                    isPassword = true
                )

                ValidatedInputField(
                    value = maxLoad,
                    onValueChange = {
                        maxLoad = it
                        validationResult = null
                    },
                    label = "Максимальне навантаження резерву (Вт)",
                    helperText = "Ліміт споживання аварійної шини (500 .. 15 000 Вт)",
                    isError = validationResult?.errors?.containsKey("maxLoad") == true,
                    errorMessage = validationResult?.errors?.get("maxLoad"),
                    keyboardType = KeyboardType.Number
                )
            }
        }

        PrimaryActionButton(
            text = if (isSaving) "Перевірка та синхронізація з контролером..." else "Застосувати налаштування АЕП",
            isLoading = isSaving,
            onClick = {
                val res = validator.validateForm(
                    email = email,
                    timeoutSecStr = switchoverTimeout,
                    batteryCutoffStr = batteryCutoff,
                    pin = emergencyPin,
                    maxLoadStr = maxLoad
                )
                validationResult = res

                if (res.isValid) {
                    isSaving = true
                    coroutineScope.launch {
                        delay(1200)
                        isSaving = false
                        savedSuccessConfig = mapOf(
                            "Диспетчер" to email,
                            "Затримка АВР" to "$switchoverTimeout сек",
                            "Поріг АКБ" to "$batteryCutoff %",
                            "Ліміт навантаження" to "$maxLoad Вт",
                            "Статус конфігурації" to "АКТИВНО В КОНТРОЛЕРІ"
                        )
                    }
                } else {
                    savedSuccessConfig = null
                }
            }
        )

        OutlinedButton(
            onClick = {
                email = "dispatcher@epss.kpi.ua"
                switchoverTimeout = "5"
                batteryCutoff = "20"
                emergencyPin = "sec99A"
                maxLoad = "4500"
                validationResult = null
                savedSuccessConfig = null
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.SettingsBackupRestore, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Скинути до заводських установок")
        }

        savedSuccessConfig?.let { cfg ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Конфігурацію успішно збережено!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    cfg.forEach { (key, value) ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "$key:", color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text(text = value, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                }
            }
        }
    }
}
