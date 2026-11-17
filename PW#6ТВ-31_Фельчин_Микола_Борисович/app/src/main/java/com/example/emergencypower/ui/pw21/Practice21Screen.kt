package com.example.emergencypower.ui.pw21

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emergencypower.ui.components.IndustrialCard

@Composable
fun Practice21Screen(viewModel: Practice21ViewModel) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    val infiniteTransition = rememberInfiniteTransition(label = "powerFlow")
    val flowPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 60f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flowDots"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IndustrialCard(title = "ПЗ 21: ДИНАМІЧНА АНІМАЦІЯ СХЕМИ АВР ТА СТАНИ") {
            Text(
                text = "Візуалізація потоків енергії в реальному часі через Canvas та переходи станів автомата введення резерву.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IndustrialCard(title = "ІНТЕРАКТИВНА СХЕМА РОЗПОДІЛУ ЖИВЛЕННЯ") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Color(0xFF0F141C))
                    .border(1.dp, Color(0xFF263238), RectangleShape)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height

                    val gridX = w * 0.18f
                    val batX = w * 0.50f
                    val genX = w * 0.82f

                    val sourceY = 35f
                    val busY = h * 0.52f
                    val loadY = h * 0.88f

                    val activeColor = Color(0xFFFFB300)
                    val idleColor = Color(0xFF37474F)
                    val faultColor = Color(0xFFCF6679)

                    val gridColor = if (state.isGridFault) faultColor else if (state.activeSource == PowerSourceType.GRID) activeColor else idleColor
                    val batColor = if (state.activeSource == PowerSourceType.BATTERY) activeColor else idleColor
                    val genColor = if (state.activeSource == PowerSourceType.GENERATOR) activeColor else idleColor

                    fun drawFlow(startX: Float, startY: Float, endX: Float, endY: Float, color: Color, isActive: Boolean) {
                        drawLine(
                            color = color,
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = if (isActive) 5f else 2f,
                            pathEffect = if (isActive) PathEffect.dashPathEffect(floatArrayOf(15f, 15f), flowPhase) else null
                        )
                    }

                    drawFlow(gridX, sourceY + 15f, gridX, busY, gridColor, state.activeSource == PowerSourceType.GRID)
                    drawFlow(batX, sourceY + 15f, batX, busY, batColor, state.activeSource == PowerSourceType.BATTERY)
                    drawFlow(genX, sourceY + 15f, genX, busY, genColor, state.activeSource == PowerSourceType.GENERATOR)

                    drawLine(
                        color = Color(0xFF546E7A),
                        start = Offset(gridX, busY),
                        end = Offset(genX, busY),
                        strokeWidth = 6f
                    )

                    val loadActive = state.activeSource != PowerSourceType.OFF
                    drawFlow(batX, busY, batX, loadY - 15f, if (loadActive) activeColor else idleColor, loadActive)

                    drawCircle(color = gridColor, radius = 18f, center = Offset(gridX, sourceY))
                    drawCircle(color = batColor, radius = 18f, center = Offset(batX, sourceY))
                    drawCircle(color = genColor, radius = 18f, center = Offset(genX, sourceY))

                    drawRect(
                        color = Color(0xFF1E293B),
                        topLeft = Offset(batX - 55f, loadY - 16f),
                        size = androidx.compose.ui.geometry.Size(110f, 32f)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Text("МЕРЕЖА", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (state.isGridFault) Color(0xFFCF6679) else Color(0xFFE2E8F0))
                    Text("ДБЖ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE2E8F0))
                    Text("ГЕНЕРАТОР", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE2E8F0))
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "СПОЖИВАЧІ I КАТЕГОРІЇ: ${state.currentLoadKw} кВт",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFB300)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        if (state.isGridFault) viewModel.restoreGrid() else viewModel.triggerGridFailure()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.isGridFault) Color(0xFF2E7D32) else Color(0xFFB71C1C)
                    )
                ) {
                    Text(if (state.isGridFault) "ВІДНОВИТИ МЕРЕЖУ" else "АВАРІЯ МЕРЕЖІ", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = { viewModel.selectSource(PowerSourceType.BATTERY) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text("ТЕСТ ДБЖ", fontSize = 11.sp)
                }
            }
        }

        IndustrialCard(title = "ДІАГНОСТИЧНІ ПАНЕЛІ (РОЗГОРТАННЯ З АНІМАЦІЄЮ)") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(animationSpec = tween(300, easing = FastOutSlowInEasing))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E293B))
                        .clickable { viewModel.toggleDiagnostics() }
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Power, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Діагностика контактів секційного вимикача АВР", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Icon(
                        imageVector = if (state.isExpandedDiagnostics) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }

                AnimatedVisibility(
                    visible = state.isExpandedDiagnostics,
                    enter = expandVertically(tween(250)),
                    exit = shrinkVertically(tween(200))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F172A))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("• Стан контактора К1 (Мережа): ${if (!state.isGridFault) "ЗАМКНЕНО (Норма)" else "РОЗІМКНЕНО"}", fontSize = 12.sp, color = Color.LightGray)
                        Text("• Стан контактора К2 (ДБЖ): ${if (state.activeSource == PowerSourceType.BATTERY) "АКТИВНИЙ" else "СТЕНДБАЙ"}", fontSize = 12.sp, color = Color.LightGray)
                        Text("• Стан контактора К3 (Генератор): ${if (state.activeSource == PowerSourceType.GENERATOR) "ЗАМКНЕНО (Під навантаженням)" else "ВІДКЛЮЧЕНО"}", fontSize = 12.sp, color = Color.LightGray)
                        Text("• Час механічного спрацьовування приводу: 45 мс", fontSize = 12.sp, color = Color.LightGray)
                        Text("• Опір ізоляції шин: 124 МОм (Відмінно)", fontSize = 12.sp, color = Color.LightGray)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E293B))
                        .clickable { viewModel.togglePhaseDetails() }
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Синхронізація кута зсуву фаз (Δφ)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Icon(
                        imageVector = if (state.isExpandedPhaseDetails) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }

                AnimatedVisibility(
                    visible = state.isExpandedPhaseDetails,
                    enter = expandVertically(tween(250)),
                    exit = shrinkVertically(tween(200))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F172A))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("• Кут між напругою мережі та генератора: 1.2° (Синхронізовано)", fontSize = 12.sp, color = Color.LightGray)
                        Text("• Частота мережі: 50.02 Гц | Частота генератора: 50.00 Гц", fontSize = 12.sp, color = Color.LightGray)
                        Text("• Різниця діючих напруг (ΔU): 1.4 В (Допустимо)", fontSize = 12.sp, color = Color.LightGray)
                        Text("• Дозвіл на безрозривне перемикання (Make-Before-Break): НАДАНО", fontSize = 12.sp, color = Color(0xFF81C784))
                    }
                }
            }
        }
    }
}
