package com.example.emergencypower.ui.pw7

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.emergencypower.model.AlarmSeverity
import com.example.emergencypower.model.GeneratorState
import com.example.emergencypower.model.PowerSourceType
import com.example.emergencypower.ui.components.EmergencyHeader
import com.example.emergencypower.ui.components.MetricCard
import com.example.emergencypower.ui.components.PrimaryActionButton
import com.example.emergencypower.ui.components.StatusBadge
import com.example.emergencypower.viewmodel.EmergencyPowerViewModel

@Composable
fun Practice7Screen(
    viewModel: EmergencyPowerViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        EmergencyHeader(
            title = "ПЗ №7: Архітектура MVVM та StateFlow",
            subtitle = "Реактивна панель диспетчера системи аварійного електропостачання"
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Активне живлення об'єкта:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    val (severity, label) = when (uiState.telemetry.activeSource) {
                        PowerSourceType.GRID -> AlarmSeverity.NORMAL to "ОСНОВНА МЕРЕЖА"
                        PowerSourceType.GENERATOR -> AlarmSeverity.CRITICAL to "ДИЗЕЛЬ-ГЕНЕРАТОР"
                        PowerSourceType.BATTERY_UPS -> AlarmSeverity.WARNING to "АКБ / ДБЖ"
                        PowerSourceType.SOLAR_INVERTER -> AlarmSeverity.INFO to "ГІБРИДНА СЕС"
                    }
                    StatusBadge(severity = severity, text = label)
                }

                Text(
                    text = uiState.statusBanner,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (uiState.isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                label = "Напруга лінії",
                value = "${uiState.telemetry.gridVoltage}",
                unit = "В",
                icon = Icons.Default.Bolt,
                accentColor = if (uiState.telemetry.gridVoltage < 180.0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                label = "Частота мережі",
                value = "${uiState.telemetry.gridFrequency}",
                unit = "Гц",
                icon = Icons.Default.Speed,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                label = "Поточне навантаження",
                value = "${uiState.telemetry.loadWatts.toInt()}",
                unit = "Вт",
                icon = Icons.Default.Power,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                label = "Заряд АКБ",
                value = "${uiState.telemetry.batterySocPercent}",
                unit = "%",
                icon = Icons.Default.BatteryChargingFull,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                label = "Резервний час АКБ",
                value = "${uiState.telemetry.estimatedBackupMinutes}",
                unit = "хв",
                icon = Icons.Default.Timer,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                label = "Якість мережі",
                value = "${uiState.gridStabilityIndex}",
                unit = "%",
                icon = Icons.Default.CheckCircle,
                modifier = Modifier.weight(1f)
            )
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Агрегат ДГ-50:", fontWeight = FontWeight.Bold)
                    Text(
                        text = uiState.telemetry.generatorState.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (uiState.telemetry.generatorState == GeneratorState.RUNNING) {
                    Text(
                        text = "Витрата: ${String.format("%.2f", uiState.fuelConsumptionLph)} л/год",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Text(
            text = "Аварійні та диспетчерські команди (MVVM Events):",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { viewModel.simulateGridFailure() },
                enabled = !uiState.isLoading && uiState.telemetry.activeSource == PowerSourceType.GRID,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Імітація Blackout", fontSize = 13.sp)
            }

            Button(
                onClick = { viewModel.simulateGridRestore() },
                enabled = !uiState.isLoading && uiState.telemetry.activeSource != PowerSourceType.GRID,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Відновити мережу", fontSize = 13.sp)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.refreshTelemetry() },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Опитування датчиків", fontSize = 13.sp)
            }

            OutlinedButton(
                onClick = { viewModel.toggleAts() },
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (uiState.telemetry.isAtsEngaged) "АВР: АВТО" else "АВР: РУЧНИЙ",
                    fontSize = 13.sp
                )
            }
        }

        uiState.lastActionSuccess?.let { successMsg ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = successMsg,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(12.dp),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
