package com.example.emergencypower.ui.pw9

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.emergencypower.model.PowerSource
import com.example.emergencypower.model.SystemStatus
import com.example.emergencypower.ui.components.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun Practice9Screen(
    viewModel: Practice9ViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val telemetryList by viewModel.telemetryList.collectAsState()

    val filterOptions = listOf("Всі", PowerSource.GRID.title, PowerSource.BATTERY.title, PowerSource.DIESEL_GENERATOR.title)
    val sortOptions = listOf("Час (нові)", "Час (старі)", "Потужність (max)", "Напруга (max)")

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.toggleAddDialog(true) },
                shape = RectShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Додати запис")
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
                    title = "ПЗ №9: Repository & Data Source",
                    subtitle = "Відокремлення бізнес-логіки, кешування в Room та агреговані розрахунки"
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricTile(
                        label = "К-сть подій",
                        value = "${uiState.aggregates.totalCount}",
                        modifier = Modifier.weight(1f)
                    )
                    MetricTile(
                        label = "Сер. навантаж.",
                        value = String.format(Locale.US, "%.1f", uiState.aggregates.averagePowerKw),
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
                        label = "Пікове навантаж.",
                        value = String.format(Locale.US, "%.1f", uiState.aggregates.peakPowerKw),
                        unit = "кВт",
                        modifier = Modifier.weight(1f)
                    )
                    MetricTile(
                        label = "Сер. напруга",
                        value = String.format(Locale.US, "%.1f", uiState.aggregates.averageVoltage),
                        unit = "В",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Text(
                    text = "ФІЛЬТРАЦІЯ ЗА ДЖЕРЕЛОМ ЖИВЛЕННЯ",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filterOptions) { opt ->
                        val isSelected = uiState.selectedSourceFilter == opt
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setSourceFilter(opt) },
                            label = { Text(opt) },
                            shape = RectShape,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }

            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "СОРТУВАННЯ",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(sortOptions) { opt ->
                        val isSelected = uiState.sortOption == opt
                        SuggestionChip(
                            onClick = { viewModel.setSortOption(opt) },
                            label = { Text(opt) },
                            shape = RectShape,
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
                            )
                        )
                    }
                }
            }

            item {
                Text(
                    text = "ЖУРНАЛ ТЕЛЕМЕТРІЇ РЕПОЗИТОРІЮ (${telemetryList.size})",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (telemetryList.isEmpty()) {
                item {
                    IndustrialCard(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Записів за обраними критеріями не знайдено",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(telemetryList, key = { it.id }) { item ->
                    val dateStr = remember(item.timestamp) {
                        SimpleDateFormat("HH:mm:ss dd.MM.yyyy", Locale.getDefault()).format(Date(item.timestamp))
                    }
                    IndustrialCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.stationName,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                StatusBadge(
                                    statusText = item.statusName,
                                    isCritical = item.statusName == SystemStatus.CRITICAL.label,
                                    isNormal = item.statusName == SystemStatus.NORMAL.label
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Час фіксації: $dateStr",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Напруга: ${String.format(Locale.US, "%.1f", item.voltage)} В")
                                Text("Струм: ${String.format(Locale.US, "%.1f", item.current)} А")
                                Text("Потужність: ${String.format(Locale.US, "%.1f", item.powerKw)} кВт")
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Джерело: ${item.sourceName}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                IconButton(onClick = { viewModel.deleteRecord(item) }) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Видалити",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }

    if (uiState.showAddDialog) {
        AddTelemetryDialog(
            onDismiss = { viewModel.toggleAddDialog(false) },
            onConfirm = { name, v, p, src, st ->
                viewModel.addRecord(name, v, p, src, st)
            }
        )
    }
}

@Composable
fun AddTelemetryDialog(
    onDismiss: () -> Unit,
    onConfirm: (station: String, voltage: Double, powerKw: Double, source: String, status: String) -> Unit
) {
    var stationName by remember { mutableStateOf("ТП-24/18 (Резервна секція)") }
    var voltageText by remember { mutableStateOf("225.0") }
    var powerText by remember { mutableStateOf("42.5") }
    var selectedSource by remember { mutableStateOf(PowerSource.GRID.title) }
    var selectedStatus by remember { mutableStateOf(SystemStatus.NORMAL.label) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RectShape,
        title = {
            Text(
                text = "НОВИЙ ЗАПИС ТЕЛЕМЕТРІЇ (REPOSITORY)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                IndustrialTextField(
                    value = stationName,
                    onValueChange = { stationName = it },
                    label = "Назва енергооб'єкта"
                )
                IndustrialTextField(
                    value = voltageText,
                    onValueChange = { voltageText = it },
                    label = "Напруга (В)"
                )
                IndustrialTextField(
                    value = powerText,
                    onValueChange = { powerText = it },
                    label = "Потужність (кВт)"
                )
            }
        },
        confirmButton = {
            IndustrialButton(onClick = {
                val v = voltageText.toDoubleOrNull() ?: 220.0
                val p = powerText.toDoubleOrNull() ?: 30.0
                onConfirm(stationName, v, p, selectedSource, selectedStatus)
            }) {
                Text("ЗБЕРЕГТИ")
            }
        },
        dismissButton = {
            IndustrialOutlinedButton(onClick = onDismiss) {
                Text("СКАСУВАТИ")
            }
        }
    )
}
