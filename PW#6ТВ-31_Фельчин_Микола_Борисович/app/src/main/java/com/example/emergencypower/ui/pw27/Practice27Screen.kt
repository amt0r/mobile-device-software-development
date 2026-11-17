package com.example.emergencypower.ui.pw27

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Verified
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
fun Practice27Screen(viewModel: Practice27ViewModel) {
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
        IndustrialCard(title = "ПЗ 27: ПІДГОТОВКА ДО РЕЛІЗУ ТА ПРЕЗЕНТАЦІЯ") {
            Text(
                text = "Фінальна верифікація збірки додатку, аудит конфігурацій оптимізації ProGuard/R8 та зведений звіт за курсом мобільної розробки.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFF1E293B))
                        .padding(8.dp)
                ) {
                    Column {
                        Text("ВЕРСІЯ ДОДАТКУ", fontSize = 9.sp, color = Color.Gray)
                        Text(state.appVersion, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, color = Color(0xFFFFB300))
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFF1E293B))
                        .padding(8.dp)
                ) {
                    Column {
                        Text("TARGET SDK", fontSize = 9.sp, color = Color.Gray)
                        Text("Android ${state.targetSdk} (API ${state.targetSdk})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFF1E293B))
                        .padding(8.dp)
                ) {
                    Column {
                        Text("ВАРІАНТ РОБОТИ", fontSize = 9.sp, color = Color.Gray)
                        Text("Варіант № 18", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF81C784))
                    }
                }
            }
        }

        IndustrialCard(title = "ЧЕКЛИСТ ГОТОВНОСТІ ДО РЕЛІЗУ (PRODUCTION AUDIT)") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                state.checklist.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F172A))
                            .border(1.dp, Color(0xFF334155), RectangleShape)
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(0xFF1B5E20)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(item.title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(item.description, fontSize = 10.sp, color = Color.LightGray)
                        }
                    }
                }
            }
        }

        IndustrialCard(title = "АРХІТЕКТУРНИЙ ПІДСУМОК ПРАКТИЧНОГО КУРСУ") {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("• Загальна кількість реалізованих занять: 27 з 27 (100% програми)", fontSize = 11.sp, color = Color.LightGray)
                Text("• Практична 3 (ПЗ 9-12): Архітектура MVVM, Room DB, Retrofit REST API, Token Auth, Error Retry", fontSize = 11.sp, color = Color.LightGray)
                Text("• Практична 4 (ПЗ 13-16): Kotlin Flow реактивні потоки, інтерактивні карти, мультикритеріальні фільтри, GPS", fontSize = 11.sp, color = Color.LightGray)
                Text("• Практична 5 (ПЗ 17-20): Апаратні сенсори, Push-сповіщення, графіки споживання, стрілочні прилади на Canvas", fontSize = 11.sp, color = Color.LightGray)
                Text("• Практична 6 (ПЗ 21-27): Анімації переходів, CSV/JSON експорт, архів документів, PDF звіти, Unit-тести, оптимізація", fontSize = 11.sp, color = Color.LightGray)
            }
        }
    }
}
