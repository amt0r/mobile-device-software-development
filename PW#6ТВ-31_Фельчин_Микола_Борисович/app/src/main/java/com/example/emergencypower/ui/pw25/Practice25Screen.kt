package com.example.emergencypower.ui.pw25

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
fun Practice25Screen(viewModel: Practice25ViewModel) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IndustrialCard(title = "ПЗ 25: АВТОМАТИЗОВАНЕ ТЕСТУВАННЯ ТА ВАЛІДАЦІЯ") {
            Text(
                text = "Виконання набору модульних (Unit) тестів для алгоритмів АВР, енергетичного балансу та серіалізації даних.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { viewModel.runAllTests() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isRunning,
                shape = RoundedCornerShape(0.dp)
            ) {
                if (state.isRunning) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(18.dp).width(18.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ВИКОНАННЯ ТЕСТІВ...")
                } else {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ЗАПУСТИТИ ВСІ ТЕСТИ")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFF1B5E20))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("ПРОЙДЕНО: ${state.passedCount}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(if (state.failedCount > 0) Color(0xFFB71C1C) else Color(0xFF263238))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("ПРОВАЛЕНО: ${state.failedCount}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.White)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFF1E293B))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("ЧАС: ${state.totalDurationMs} мс", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color(0xFFFFB300))
                }
            }
        }

        IndustrialCard(title = "РЕЗУЛЬТАТИ ВИКОНАННЯ ТЕСТ-КЕЙСІВ") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                state.testResults.forEach { test ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F172A))
                            .border(1.dp, if (test.isPassed) Color(0xFF2E7D32) else Color(0xFFB71C1C), RectangleShape)
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    imageVector = if (test.isPassed) Icons.Default.CheckCircle else Icons.Default.Error,
                                    contentDescription = null,
                                    tint = if (test.isPassed) Color(0xFF81C784) else Color(0xFFCF6679),
                                    modifier = Modifier.height(16.dp).width(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(test.testName, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(
                                text = "${test.durationMs} мс",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(test.category, fontSize = 10.sp, color = Color(0xFF64B5F6))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(test.assertionDetails, fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = Color.LightGray)
                    }
                }
            }
        }
    }
}
