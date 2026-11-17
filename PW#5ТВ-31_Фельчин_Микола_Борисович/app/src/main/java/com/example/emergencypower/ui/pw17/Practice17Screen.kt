package com.example.emergencypower.ui.pw17

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.emergencypower.ui.components.*
import java.util.*

@Composable
fun Practice17Screen(
    viewModel: Practice17ViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val st = uiState.sensorState

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader(
                title = "ПЗ №17: Сенсори пристрою",
                subtitle = "Зчитування фізичних сенсорів смартфона та пороговий моніторинг"
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "СТАТУС МОНІТОРИНГУ СЕНСОРІВ",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IndustrialOutlinedButton(onClick = { viewModel.toggleMonitoring() }) {
                    Text(if (uiState.isMonitoringActive) "ЗУПИНИТИ" else "ВІДНОВИТИ")
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricTile(
                    label = "Рівень вібрації",
                    value = String.format(Locale.US, "%.2f", st.vibrationMagnitude),
                    unit = "м/с²",
                    highlightColor = if (st.isVibrationAlarm) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    label = "Освітленість",
                    value = String.format(Locale.US, "%.0f", st.lightLux),
                    unit = "lux",
                    highlightColor = if (st.isBlackoutAlarm) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricTile(
                    label = "Температура АКБ",
                    value = String.format(Locale.US, "%.1f", st.batteryTempC),
                    unit = "°C",
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    label = "Вектор X / Y / Z",
                    value = String.format(Locale.US, "%.1f/%.1f", st.accelerometerX, st.accelerometerY),
                    unit = "g",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            IndustrialCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "ПОРОГОВІ ЗНАЧЕННЯ ДЛЯ СПРАЦЬОВУВАННЯ АВАРІЇ",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Поріг вібрації генератора: ${String.format(Locale.US, "%.1f", uiState.vibrationThreshold)} м/с²",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Slider(
                        value = uiState.vibrationThreshold,
                        onValueChange = { viewModel.setVibrationThreshold(it) },
                        valueRange = 1f..10f,
                        steps = 9
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Поріг знеструмлення щитової (Blackout Lux): ${String.format(Locale.US, "%.0f", uiState.lightThreshold)} lux",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Slider(
                        value = uiState.lightThreshold,
                        onValueChange = { viewModel.setLightThreshold(it) },
                        valueRange = 10f..200f,
                        steps = 19
                    )
                }
            }
        }

        item {
            IndustrialCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ДИСПЕТЧЕРСЬКИЙ СТАТУС БЕЗПЕКИ",
                            style = MaterialTheme.typography.titleMedium
                        )
                        StatusBadge(
                            statusText = if (st.isVibrationAlarm || st.isBlackoutAlarm) "АВАРІЯ" else "НОРМА",
                            isCritical = st.isVibrationAlarm || st.isBlackoutAlarm,
                            isNormal = !st.isVibrationAlarm && !st.isBlackoutAlarm
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    if (st.isVibrationAlarm) {
                        Text(
                            text = "УВАГА: Перевищено критичний поріг вібрації опор дизель-генератора ДГ-50!",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    if (st.isBlackoutAlarm) {
                        Text(
                            text = "УВАГА: Рівень освітленості впав нижче критичного (можливе аварійне знеструмлення щитової)!",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    if (!st.isVibrationAlarm && !st.isBlackoutAlarm) {
                        Text(
                            text = "Усі фізичні параметри об'єкта перебувають у межах допустимих інженерних норм.",
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
