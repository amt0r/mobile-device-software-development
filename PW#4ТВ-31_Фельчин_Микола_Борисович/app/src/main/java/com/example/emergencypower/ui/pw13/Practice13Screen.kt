package com.example.emergencypower.ui.pw13

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun Practice13Screen(
    viewModel: Practice13ViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val history by viewModel.history.collectAsState()
    val data = uiState.latestData

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader(
                title = "ПЗ №13: Flow та Coroutines",
                subtitle = "Асинхронний збір даних датчиків, оператори combine, filter та StateFlow"
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "КЕРУВАННЯ ПОТОКОМ (FLOW OPERATORS)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = uiState.isFilterNormalOnly,
                        onClick = { viewModel.toggleFilterNormalOnly() },
                        label = { Text("Фільтр: Тільки норма") },
                        shape = RectShape
                    )
                    IndustrialOutlinedButton(
                        onClick = { viewModel.toggleStreamPause() }
                    ) {
                        Text(if (uiState.isStreamPaused) "ВІДНОВИТИ" else "ПАУЗА")
                    }
                }
            }
        }

        if (data != null) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricTile(
                        label = "Частота мережі",
                        value = String.format(Locale.US, "%.2f", data.gridFrequencyHz),
                        unit = "Гц",
                        modifier = Modifier.weight(1f)
                    )
                    MetricTile(
                        label = "Напруга Фази A",
                        value = String.format(Locale.US, "%.1f", data.voltagePhaseA),
                        unit = "В",
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
                        label = "Запас палива ДГ",
                        value = String.format(Locale.US, "%.1f", data.generatorFuelPercent),
                        unit = "%",
                        modifier = Modifier.weight(1f)
                    )
                    MetricTile(
                        label = "Температура АКБ",
                        value = String.format(Locale.US, "%.1f", data.batteryTemperatureC),
                        unit = "°C",
                        modifier = Modifier.weight(1f)
                    )
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
                                text = "ТРИФАЗНИЙ МОНІТОРИНГ НАПРУГИ",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            StatusBadge(
                                statusText = if (data.hasFault) "ВІДХИЛЕННЯ" else "НОРМА",
                                isCritical = data.hasFault,
                                isNormal = !data.hasFault
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Фаза A: ${String.format(Locale.US, "%.1f", data.voltagePhaseA)} В")
                            Text("Фаза B: ${String.format(Locale.US, "%.1f", data.voltagePhaseB)} В")
                            Text("Фаза C: ${String.format(Locale.US, "%.1f", data.voltagePhaseC)} В")
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Індекс надійності енергосистеми: ${String.format(Locale.US, "%.0f", data.overallReliabilityIndex * 100)}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "РЕАКТИВНИЙ ЖУРНАЛ ОСТАННІХ ВИБІРОК FLOW (${history.size})",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        items(history) { h ->
            val timeStr = remember(h.timestamp) {
                SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault()).format(Date(h.timestamp))
            }
            IndustrialCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Вибірка $timeStr",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "f: ${String.format(Locale.US, "%.2f", h.gridFrequencyHz)} Гц • U_A: ${String.format(Locale.US, "%.1f", h.voltagePhaseA)} В • Паливо: ${String.format(Locale.US, "%.1f", h.generatorFuelPercent)}%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    StatusBadge(
                        statusText = if (h.hasFault) "ALERT" else "OK",
                        isCritical = h.hasFault,
                        isNormal = !h.hasFault
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
