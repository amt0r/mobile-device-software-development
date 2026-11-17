package com.example.emergencypower.ui.pw20

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.emergencypower.ui.components.*
import com.example.emergencypower.ui.theme.EmergencyRed
import com.example.emergencypower.ui.theme.GridGreen
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Practice20Screen(
    viewModel: Practice20ViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val animatedVoltage by animateFloatAsState(
        targetValue = uiState.voltage,
        animationSpec = tween(durationMillis = 600),
        label = "VoltageNeedle"
    )

    val animatedBattery by animateFloatAsState(
        targetValue = uiState.batteryPercent,
        animationSpec = tween(durationMillis = 600),
        label = "BatteryRing"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader(
                title = "ПЗ №20: Кастомні Компоненти Compose",
                subtitle = "Спеціалізовані прилади на Canvas: аналоговий вольтметр, заряд АКБ та вектор"
            )
        }

        item {
            IndustrialCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "АНАЛОГОВИЙ ВОЛЬТМЕТР 0–300 В (CANVAS)",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .size(240.dp, 140.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val center = Offset(w / 2f, h - 10f)
                            val radius = w * 0.42f

                            val arcRect = androidx.compose.ui.geometry.Rect(
                                center.x - radius, center.y - radius,
                                center.x + radius, center.y + radius
                            )

                            drawArc(
                                color = Color(0xFFE53935),
                                startAngle = 180f,
                                sweepAngle = 36f,
                                useCenter = false,
                                topLeft = Offset(center.x - radius, center.y - radius),
                                size = Size(radius * 2, radius * 2),
                                style = Stroke(width = 16f)
                            )
                            drawArc(
                                color = Color(0xFFFFB300),
                                startAngle = 216f,
                                sweepAngle = 24f,
                                useCenter = false,
                                topLeft = Offset(center.x - radius, center.y - radius),
                                size = Size(radius * 2, radius * 2),
                                style = Stroke(width = 16f)
                            )
                            drawArc(
                                color = Color(0xFF43A047),
                                startAngle = 240f,
                                sweepAngle = 36f,
                                useCenter = false,
                                topLeft = Offset(center.x - radius, center.y - radius),
                                size = Size(radius * 2, radius * 2),
                                style = Stroke(width = 16f)
                            )
                            drawArc(
                                color = Color(0xFFFFB300),
                                startAngle = 276f,
                                sweepAngle = 24f,
                                useCenter = false,
                                topLeft = Offset(center.x - radius, center.y - radius),
                                size = Size(radius * 2, radius * 2),
                                style = Stroke(width = 16f)
                            )
                            drawArc(
                                color = Color(0xFFE53935),
                                startAngle = 300f,
                                sweepAngle = 60f,
                                useCenter = false,
                                topLeft = Offset(center.x - radius, center.y - radius),
                                size = Size(radius * 2, radius * 2),
                                style = Stroke(width = 16f)
                            )

                            val angleDeg = 180f + (animatedVoltage / 300f * 180f).coerceIn(0f, 180f)
                            val angleRad = Math.toRadians(angleDeg.toDouble())
                            val needleLen = radius * 0.9f
                            val needleEnd = Offset(
                                (center.x + needleLen * cos(angleRad)).toFloat(),
                                (center.y + needleLen * sin(angleRad)).toFloat()
                            )

                            drawLine(
                                color = Color(0xFF00E5FF),
                                start = center,
                                end = needleEnd,
                                strokeWidth = 5f,
                                cap = StrokeCap.Round
                            )
                            drawCircle(color = Color(0xFF00E5FF), radius = 10f, center = center)
                        }
                    }

                    Text(
                        text = "${String.format(Locale.US, "%.1f", uiState.voltage)} В",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Зелена зона: 220–230 В • Жовта: 200–250 В • Червона: <200 або >250 В",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Slider(
                        value = uiState.voltage,
                        onValueChange = { viewModel.setVoltage(it) },
                        valueRange = 150f..280f
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IndustrialCard(modifier = Modifier.weight(1f)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ЗАРЯД АКБ (SOC %)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawArc(
                                    color = Color(0xFF24282D),
                                    startAngle = 0f,
                                    sweepAngle = 360f,
                                    useCenter = false,
                                    style = Stroke(width = 10.dp.toPx())
                                )
                                drawArc(
                                    color = if (uiState.batteryPercent > 20f) Color(0xFF43A047) else Color(0xFFE53935),
                                    startAngle = -90f,
                                    sweepAngle = (animatedBattery / 100f * 360f),
                                    useCenter = false,
                                    style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }
                            Text(
                                text = "${animatedBattery.toInt()}%",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        IndustrialOutlinedButton(onClick = { viewModel.toggleCharging() }) {
                            Text(if (uiState.isCharging) "ЗАРЯДЖАЄТЬСЯ" else "РОЗРЯДЖАЄТЬСЯ")
                        }
                    }
                }

                IndustrialCard(modifier = Modifier.weight(1f)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ВЕКТОР ВІБРАЦІЙ",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(modifier = Modifier.size(100.dp), contentAlignment = Alignment.Center) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val c = Offset(size.width / 2f, size.height / 2f)
                                drawCircle(color = Color(0xFF24282D), radius = size.width / 2f, style = Stroke(2f))
                                drawCircle(color = Color(0xFF37474F), radius = size.width / 3f, style = Stroke(1f))
                                drawLine(color = Color(0xFF37474F), start = Offset(c.x, 0f), end = Offset(c.x, size.height), strokeWidth = 1f)
                                drawLine(color = Color(0xFF37474F), start = Offset(0f, c.y), end = Offset(size.width, c.y), strokeWidth = 1f)

                                val vx = c.x + (uiState.vibrationX * 25f)
                                val vy = c.y + (uiState.vibrationY * 25f)
                                drawCircle(color = Color(0xFFFFB300), radius = 6.dp.toPx(), center = Offset(vx, vy))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Двоосьовий датчик ДГ-50",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
