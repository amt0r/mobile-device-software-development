package com.example.emergencypower.ui.pw26

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emergencypower.ui.components.IndustrialCard

@Composable
fun Practice26Screen(viewModel: Practice26ViewModel) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val res = state.resourceInfo

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IndustrialCard(title = "ПЗ 26: МОНІТОРИНГ АПАРАТНИХ РЕСУРСІВ ТА ОПТИМІЗАЦІЯ") {
            Text(
                text = "Контроль споживання пам'яті (RAM), навантаження процесора, температури акумулятора та мережевих адаптерів термінала.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { viewModel.refreshMetrics() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(0.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("ОНОВИТИ АПАРАТНІ МЕТРИКИ")
            }
        }

        IndustrialCard(title = "ПОТОЧНІ СИСТЕМНІ ПОКАЗНИКИ ПРИСТРОЮ") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF334155), RectangleShape)
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Memory, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Оперативна пам'ять (RAM)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Text("${res.usedRamMb} / ${res.totalRamMb} МБ (${res.ramUsagePercent}%)", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color(0xFFFFB300))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { res.ramUsagePercent / 100f },
                        modifier = Modifier.fillMaxWidth().height(6.dp),
                        color = if (res.ramUsagePercent > 80) Color(0xFFCF6679) else MaterialTheme.colorScheme.primary,
                        trackColor = Color(0xFF1E293B)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF334155), RectangleShape)
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.BatteryChargingFull, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Батарея", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${res.batteryLevelPercent}%", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        Text("${res.batteryTempCelsius} °C • ${if (res.isCharging) "Заряджається" else "Розряд"}", fontSize = 10.sp, color = Color.Gray)
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF334155), RectangleShape)
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = Color(0xFF64B5F6), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("CPU / Ядра", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${res.cpuCores} ядер", fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        Text("Архітектура ARM64", fontSize = 10.sp, color = Color.Gray)
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF334155), RectangleShape)
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Wifi, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Активний канал зв'язку", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        Text(res.networkType, fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = Color(0xFF81C784))
                    }
                }
            }
        }

        IndustrialCard(title = "НАЛАШТУВАННЯ ПРОДУКТИВНОСТІ") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Висококонтрастний індустріальний режим", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text("Збільшена чіткість у сонячну погоду", fontSize = 10.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = state.isHighContrast,
                        onCheckedChange = { viewModel.toggleHighContrast() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFFB300))
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Фонова синхронізація телеметрії", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text("Автоматичне оновлення кожні ${state.pollingIntervalMs} мс", fontSize = 10.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = state.isBackgroundSync,
                        onCheckedChange = { viewModel.toggleBackgroundSync() },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFFFB300))
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text("Частота опитування датчиків АВР:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(500L, 1000L, 2000L).forEach { interval ->
                        OutlinedButton(
                            onClick = { viewModel.setPollingInterval(interval) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(0.dp)
                        ) {
                            Text("${interval}мс", fontSize = 10.sp, color = if (state.pollingIntervalMs == interval) Color(0xFFFFB300) else Color.White)
                        }
                    }
                }
            }
        }
    }
}
