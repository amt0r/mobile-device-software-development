package com.example.emergencypower.ui.pw8

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.emergencypower.data.local.EmergencyDatabase
import com.example.emergencypower.data.local.EmergencyEventEntity
import com.example.emergencypower.data.repository.EmergencyLogRepository
import com.example.emergencypower.model.AlarmSeverity
import com.example.emergencypower.model.PowerSourceType
import com.example.emergencypower.ui.components.EmergencyHeader
import com.example.emergencypower.ui.components.PrimaryActionButton
import com.example.emergencypower.ui.components.StatusBadge
import com.example.emergencypower.ui.components.ValidatedInputField
import com.example.emergencypower.viewmodel.EmergencyLogViewModel
import com.example.emergencypower.viewmodel.LogSortOption
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Practice8Screen() {
    val context = LocalContext.current
    val database = remember { EmergencyDatabase.getDatabase(context) }
    val repository = remember { EmergencyLogRepository(database.emergencyLogDao()) }
    val viewModel: EmergencyLogViewModel = viewModel(factory = EmergencyLogViewModel.Factory(repository))

    val uiState by viewModel.uiState.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var eventToEdit by remember { mutableStateOf<EmergencyEventEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        EmergencyHeader(
            title = "ПЗ №8: Room Database та CRUD",
            subtitle = "Журнал аварійних подій та параметрів електропостачання"
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                AnalyticsItem(label = "Записів", value = "${uiState.analytics.totalEvents}")
                AnalyticsItem(
                    label = "Аварій",
                    value = "${uiState.analytics.criticalAlarms}",
                    color = if (uiState.analytics.criticalAlarms > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
                AnalyticsItem(
                    label = "Сер. навантаж.",
                    value = "${uiState.analytics.averageLoadWatts.toInt()} Вт"
                )
                AnalyticsItem(
                    label = "Пік",
                    value = "${uiState.analytics.maxLoadWatts.toInt()} Вт"
                )
            }
        }

        Text(text = "Фільтр джерела енергії:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val sources = listOf(
                "ALL" to "Всі",
                PowerSourceType.GRID.name to "Мережа",
                PowerSourceType.GENERATOR.name to "ДГ",
                PowerSourceType.BATTERY_UPS.name to "ДБЖ",
                PowerSourceType.SOLAR_INVERTER.name to "СЕС"
            )
            sources.forEach { (code, label) ->
                FilterChip(
                    selected = uiState.selectedSourceFilter == code,
                    onClick = { viewModel.setSourceFilter(code) },
                    label = { Text(label, fontSize = 11.sp) }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Додати подію")
            }

            OutlinedButton(
                onClick = { viewModel.clearAllLogs() },
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.DeleteSweep, contentDescription = "Очистити")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Очистити")
            }
        }

        uiState.feedbackMessage?.let { msg ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = msg, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    IconButton(onClick = { viewModel.dismissFeedback() }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Закрити", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        if (uiState.events.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Записів за обраними критеріями не знайдено.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.events, key = { it.id }) { event ->
                    EventCardItem(
                        event = event,
                        onEdit = { eventToEdit = event },
                        onDelete = { viewModel.deleteEvent(event) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddEventDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { src, volt, load, soc, sev, desc ->
                viewModel.addEvent(src, volt, load, soc, sev, desc)
                showAddDialog = false
            }
        )
    }

    eventToEdit?.let { event ->
        EditEventDialog(
            event = event,
            onDismiss = { eventToEdit = null },
            onUpdate = { updated ->
                viewModel.updateEvent(updated)
                eventToEdit = null
            }
        )
    }
}

@Composable
fun AnalyticsItem(label: String, value: String, color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
fun EventCardItem(
    event: EmergencyEventEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("HH:mm:ss dd.MM", Locale.getDefault()) }
    val formattedTime = remember(event.timestamp) { dateFormat.format(Date(event.timestamp)) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val severityEnum = try {
                        AlarmSeverity.valueOf(event.severity)
                    } catch (e: Exception) {
                        AlarmSeverity.INFO
                    }
                    StatusBadge(severity = severityEnum)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Джерело: ${event.sourceType}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${event.voltage} В | ${event.loadWatts.toInt()} Вт | АКБ: ${event.batterySoc}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Редагувати", modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Видалити", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun AddEventDialog(
    onDismiss: () -> Unit,
    onAdd: (source: String, voltage: Double, load: Double, soc: Int, severity: String, description: String) -> Unit
) {
    var sourceType by remember { mutableStateOf(PowerSourceType.GENERATOR.name) }
    var voltageText by remember { mutableStateOf("230.0") }
    var loadText by remember { mutableStateOf("3200") }
    var socText by remember { mutableStateOf("80") }
    var severity by remember { mutableStateOf(AlarmSeverity.WARNING.name) }
    var description by remember { mutableStateOf("Плановий тестовий запуск резервної лінії") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новий запис в Room Database") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ValidatedInputField(
                    value = voltageText,
                    onValueChange = { voltageText = it },
                    label = "Напруга (В)"
                )
                ValidatedInputField(
                    value = loadText,
                    onValueChange = { loadText = it },
                    label = "Навантаження (Вт)"
                )
                ValidatedInputField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Опис події"
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val volt = voltageText.toDoubleOrNull() ?: 230.0
                val load = loadText.toDoubleOrNull() ?: 2500.0
                val soc = socText.toIntOrNull() ?: 85
                onAdd(sourceType, volt, load, soc, severity, description)
            }) {
                Text("Зберегти")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Скасувати")
            }
        }
    )
}

@Composable
fun EditEventDialog(
    event: EmergencyEventEntity,
    onDismiss: () -> Unit,
    onUpdate: (EmergencyEventEntity) -> Unit
) {
    var desc by remember { mutableStateOf(event.description) }
    var loadText by remember { mutableStateOf(event.loadWatts.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Редагування запису #${event.id}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ValidatedInputField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = "Опис події"
                )
                ValidatedInputField(
                    value = loadText,
                    onValueChange = { loadText = it },
                    label = "Навантаження (Вт)"
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val updated = event.copy(
                    description = desc,
                    loadWatts = loadText.toDoubleOrNull() ?: event.loadWatts
                )
                onUpdate(updated)
            }) {
                Text("Оновити")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Скасувати")
            }
        }
    )
}
