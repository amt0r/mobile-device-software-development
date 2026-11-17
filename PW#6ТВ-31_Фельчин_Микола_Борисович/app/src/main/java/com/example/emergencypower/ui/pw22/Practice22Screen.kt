package com.example.emergencypower.ui.pw22

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun Practice22Screen(viewModel: Practice22ViewModel) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    var vInput by remember { mutableStateOf(state.config.nominalVoltage.toString()) }
    var kwInput by remember { mutableStateOf(state.config.maxLoadKw.toString()) }
    var delayInput by remember { mutableStateOf(state.config.emergencyDelayMs.toString()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IndustrialCard(title = "ПЗ 22: ЕКСПОРТ ТА ІМПОРТ CSV ТА JSON ФАЙЛІВ") {
            Text(
                text = "Збереження журналів подій телеметрії в CSV та налаштувань підстанції у форматі JSON з перевіркою цілісності даних.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E293B))
                    .padding(8.dp)
            ) {
                Text(
                    text = "Статус: ${state.statusMessage}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = Color(0xFFFFB300)
                )
            }
        }

        IndustrialCard(title = "ЖУРНАЛ ТЕЛЕМЕТРІЇ (ЕКСПОРТ/ІМПОРТ CSV)") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.exportToCsv() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text("ЕКСПОРТ У CSV", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = { viewModel.readFromCsv() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text("ЗЧИТАТИ З CSV", fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F172A))
                    .border(1.dp, Color(0xFF334155), RectangleShape)
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("ЧАС", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.Gray, modifier = Modifier.weight(1f))
                    Text("НАПРУГА", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.Gray, modifier = Modifier.weight(1.2f))
                    Text("НАВАНТ.", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.Gray, modifier = Modifier.weight(1.2f))
                    Text("ДЖЕРЕЛО", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.Gray, modifier = Modifier.weight(2f))
                }

                state.logs.take(5).forEach { log ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(log.timestamp, fontSize = 10.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(1f))
                        Text("${log.voltage} В", fontSize = 10.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(1.2f))
                        Text("${log.loadKw} кВт", fontSize = 10.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(1.2f))
                        Text(log.source, fontSize = 10.sp, modifier = Modifier.weight(2f), color = if (log.status == "АВАРІЯ МЕРЕЖІ") Color(0xFFCF6679) else Color(0xFF81C784))
                    }
                }
            }
        }

        IndustrialCard(title = "НАЛАШТУВАННЯ ПІДСТАНЦІЇ (ФОРМАТ JSON)") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = vInput,
                    onValueChange = {
                        vInput = it
                        vInput.toFloatOrNull()?.let { v -> viewModel.updateConfig(v, kwInput.toFloatOrNull() ?: 450f, delayInput.toLongOrNull() ?: 1500L) }
                    },
                    label = { Text("Ном. U (В)", fontSize = 10.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(0.dp)
                )

                OutlinedTextField(
                    value = kwInput,
                    onValueChange = {
                        kwInput = it
                        kwInput.toFloatOrNull()?.let { kw -> viewModel.updateConfig(vInput.toFloatOrNull() ?: 220f, kw, delayInput.toLongOrNull() ?: 1500L) }
                    },
                    label = { Text("Макс. P (кВт)", fontSize = 10.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(0.dp)
                )

                OutlinedTextField(
                    value = delayInput,
                    onValueChange = {
                        delayInput = it
                        delayInput.toLongOrNull()?.let { d -> viewModel.updateConfig(vInput.toFloatOrNull() ?: 220f, kwInput.toFloatOrNull() ?: 450f, d) }
                    },
                    label = { Text("Затримка (мс)", fontSize = 10.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(0.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.exportToJson() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text("ЕКСПОРТ JSON", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = { viewModel.importFromJson() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text("ІМПОРТ JSON", fontSize = 11.sp)
                }
            }

            if (state.jsonContentPreview.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A))
                        .padding(8.dp)
                ) {
                    Text(
                        text = state.jsonContentPreview,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = Color(0xFF81C784)
                    )
                }
            }
        }
    }
}
