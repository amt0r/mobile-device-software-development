package com.example.emergencypower.ui.pw18

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.emergencypower.ui.components.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun Practice18Screen(
    viewModel: Practice18ViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader(
                title = "ПЗ №18: Push та Локальні Сповіщення",
                subtitle = "Канали NotificationManager, високий пріоритет та інтерактивні дії"
            )
        }

        item {
            IndustrialCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "ГЕНЕРАЦІЯ АВАРІЙНИХ СПОВІЩЕНЬ У ШТОРКУ ПРИСТРОЮ",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Натисніть кнопку для відправки локального сповіщення з кнопками швидкої дії:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    IndustrialButton(
                        onClick = { viewModel.sendBlackoutAlert() },
                        isEmergency = true,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("1. СПОВІЩЕННЯ ПРО ЗНЕСТРУМЛЕННЯ (BLACKOUT)")
                    }
                    IndustrialButton(
                        onClick = { viewModel.sendBatteryLowAlert() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("2. СПОВІЩЕННЯ ПРО КРИТИЧНИЙ РОЗРЯД АКБ < 20%")
                    }
                    IndustrialOutlinedButton(
                        onClick = { viewModel.sendGeneratorOverheatAlert() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("3. ПОПЕРЕДЖЕННЯ ПРО ПЕРЕГРІВ ГЕНЕРАТОРА")
                    }
                }
            }
        }

        item {
            Text(
                text = "СИНХРОНІЗОВАНИЙ ЖУРНАЛ СПОВІЩЕНЬ (${uiState.notificationList.size})",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (uiState.notificationList.isEmpty()) {
            item {
                IndustrialCard(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Активних сповіщень ще не надходило",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(uiState.notificationList, key = { it.id }) { notif ->
                val dateStr = remember(notif.timestamp) {
                    SimpleDateFormat("HH:mm:ss dd.MM.yyyy", Locale.getDefault()).format(Date(notif.timestamp))
                }
                IndustrialCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = notif.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (notif.acknowledged) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
                            )
                            StatusBadge(
                                statusText = if (notif.acknowledged) "КВИТОВАНО" else "АКТИВНА",
                                isCritical = !notif.acknowledged,
                                isNormal = notif.acknowledged
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = notif.text,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Час: $dateStr",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (!notif.acknowledged) {
                                IndustrialOutlinedButton(
                                    onClick = { viewModel.acknowledgeAlert(notif.id) }
                                ) {
                                    Text("КВИТУВАТИ")
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
