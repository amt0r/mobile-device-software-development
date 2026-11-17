package com.example.emergencypower.ui.pw10

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.emergencypower.ui.components.*
import java.util.*

@Composable
fun Practice10Screen(
    viewModel: Practice10ViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val filteredForecast = viewModel.getFilteredForecast()
    val riskFilters = listOf("Всі", "LOW", "MODERATE", "HIGH")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader(
                title = "ПЗ №10: REST API, Retrofit та Gson",
                subtitle = "Отримання структурованих JSON-даних SCADA, агрегація та офлайн-кеш"
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ОНЛАЙН СТАТУС SCADA",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                IndustrialOutlinedButton(
                    onClick = { viewModel.loadData() },
                    enabled = !uiState.isLoading
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("ОНОВИТИ")
                }
            }
        }

        if (uiState.gridStatus != null) {
            val status = uiState.gridStatus!!
            item {
                IndustrialCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = status.stationId,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            StatusBadge(
                                statusText = if (status.backupReady) "АВР ГОТОВИЙ" else "АВР ПОМИЛКА",
                                isCritical = !status.backupReady,
                                isNormal = status.backupReady
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Напруга мережі: ${String.format(Locale.US, "%.1f", status.gridVoltage)} В")
                            Text("Частота: ${String.format(Locale.US, "%.2f", status.frequencyHz)} Гц")
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Навантаження: ${String.format(Locale.US, "%.1f", status.currentLoadKw)} кВт")
                            Text("Заряд АКБ: ${status.batterySocPercent} %")
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Активне живлення: ${status.activeSource}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricTile(
                    label = "Сер. прогноз",
                    value = String.format(Locale.US, "%.1f", uiState.avgForecastKw),
                    unit = "кВт",
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    label = "Піковий прогноз",
                    value = String.format(Locale.US, "%.1f", uiState.peakForecastKw),
                    unit = "кВт",
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
                    label = "Крит. години (High)",
                    value = "${uiState.highRiskCount}",
                    highlightColor = if (uiState.highRiskCount > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    label = "Джерело даних",
                    value = if (uiState.isOfflineFallback) "КЕШ" else "SCADA API",
                    highlightColor = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Text(
                text = "ФІЛЬТР ПРОГНОЗУ ЗА РІВНЕМ РИЗИКУ ПЕРЕВАНТАЖЕННЯ",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                riskFilters.forEach { r ->
                    FilterChip(
                        selected = uiState.selectedRiskFilter == r,
                        onClick = { viewModel.setRiskFilter(r) },
                        label = { Text(r) },
                        shape = RectShape
                    )
                }
            }
        }

        item {
            Text(
                text = "ПРОГНОЗ НАВАНТАЖЕННЯ ЕНЕРГОСИСТЕМИ (${filteredForecast.size})",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        items(filteredForecast) { item ->
            val isHigh = item.riskLevel == "HIGH"
            IndustrialCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "+${item.hourOffset} год від поточного часу",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Прогнозоване споживання: ${String.format(Locale.US, "%.1f", item.predictedKw)} кВт",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    StatusBadge(
                        statusText = "РИЗИК: ${item.riskLevel}",
                        isCritical = isHigh,
                        isNormal = item.riskLevel == "LOW"
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
