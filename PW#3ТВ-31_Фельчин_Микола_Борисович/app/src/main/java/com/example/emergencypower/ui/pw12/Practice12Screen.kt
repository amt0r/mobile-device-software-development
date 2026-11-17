package com.example.emergencypower.ui.pw12

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
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
fun Practice12Screen(
    viewModel: Practice12ViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val incidents by viewModel.incidentsList.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            val result = snackbarHostState.showSnackbar(
                message = msg,
                actionLabel = "ПОВТОРИТИ",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.triggerNetworkRequestWithRetry()
            }
            viewModel.clearSnackbar()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    shape = RectShape,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    actionColor = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SectionHeader(
                    title = "ПЗ №12: Обробка мережевих помилок",
                    subtitle = "Моніторинг з'єднання, автоповтор (Retry), обробка HTTP-кодів та логування"
                )
            }

            item {
                IndustrialCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (uiState.isOnline) Icons.Default.Wifi else Icons.Default.WifiOff,
                                contentDescription = null,
                                tint = if (uiState.isOnline) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (uiState.isOnline) "МЕРЕЖА ПІДКЛЮЧЕНА (ONLINE)" else "ВІДСУТНІЙ ІНТЕРНЕТ (OFFLINE)",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (uiState.isOnline) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = "Перевірка через ConnectivityManager API",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            item {
                IndustrialCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "СИМУЛЯТОР МЕРЕЖЕВИХ ЗБОЇВ ТА RETRY-МЕХАНІЗМУ",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Оберіть HTTP-код відповіді сервера для тестування:",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        val codes = listOf(200, 503, 500, 403, 404)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            codes.forEach { code ->
                                FilterChip(
                                    selected = uiState.simulatedCode == code,
                                    onClick = { viewModel.setSimulatedCode(code) },
                                    label = { Text(if (code == 200) "200 OK" else "$code Error") },
                                    shape = RectShape
                                )
                            }
                        }

                        if (uiState.isExecutingRequest) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Спроба ${uiState.currentRetryAttempt} з 3 (експоненційне запізнення)...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }

                        IndustrialButton(
                            onClick = { viewModel.triggerNetworkRequestWithRetry() },
                            enabled = !uiState.isExecutingRequest,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("ВИКОНАТИ ЗАПИТ З АВТОПОВТОРОМ")
                        }

                        Text(
                            text = "Статус останнього запиту: ${uiState.lastResponseStatus}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ЖУРНАЛ МЕРЕЖЕВИХ ІНЦИДЕНТІВ (${incidents.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    IconButton(onClick = { viewModel.clearIncidents() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Очистити", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            if (incidents.isEmpty()) {
                item {
                    IndustrialCard(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Журнал інцидентів порожній",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(incidents) { inc ->
                    val dateStr = remember(inc.timestamp) {
                        SimpleDateFormat("HH:mm:ss dd.MM.yyyy", Locale.getDefault()).format(Date(inc.timestamp))
                    }
                    IndustrialCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${inc.endpoint} (HTTP ${inc.httpCode})",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = inc.errorMessage,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (inc.resolved) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = "Час: $dateStr • Спроб: ${inc.retryCount}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            StatusBadge(
                                statusText = if (inc.resolved) "ВИРІШЕНО" else "ПОМИЛКА",
                                isCritical = !inc.resolved,
                                isNormal = inc.resolved
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
}
