package com.example.emergencypower.ui.pw19

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.emergencypower.model.TimeRange
import com.example.emergencypower.ui.components.*
import java.util.*

@Composable
fun Practice19Screen(
    viewModel: Practice19ViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val points = uiState.points
    val selected = uiState.selectedPoint

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader(
                title = "ПЗ №19: Графіки та Діаграми",
                subtitle = "Візуалізація динаміки навантаження, фазні діаграми та інтерактивні підказки"
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ЧАСОВИЙ ДІАПАЗОН МОНІТОРИНГУ",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    TimeRange.entries.forEach { tr ->
                        FilterChip(
                            selected = uiState.timeRange == tr,
                            onClick = { viewModel.selectTimeRange(tr) },
                            label = { Text(tr.label) },
                            shape = RectShape
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
                    label = "Сер. навантаж.",
                    value = String.format(Locale.US, "%.1f", uiState.averageKw),
                    unit = "кВт",
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    label = "Пікове навантаж.",
                    value = String.format(Locale.US, "%.1f", uiState.peakKw),
                    unit = "кВт",
                    highlightColor = MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    label = "Мін. споживання",
                    value = String.format(Locale.US, "%.1f", uiState.minKw),
                    unit = "кВт",
                    highlightColor = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            IndustrialCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "ЛІНІЙНИЙ ГРАФІК НАВАНТАЖЕННЯ (кВт / ЧАС)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(modifier = Modifier.fillMaxSize()) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(points) {
                                    detectTapGestures { tapOffset ->
                                        if (points.isEmpty()) return@detectTapGestures
                                        val stepX = size.width / (points.size - 1).coerceAtLeast(1)
                                        val idx = (tapOffset.x / stepX).toInt().coerceIn(0, points.size - 1)
                                        viewModel.selectPoint(points[idx])
                                    }
                                }
                        ) {
                            val w = size.width
                            val h = size.height - 30f
                            val maxVal = 70.0
                            val minVal = 0.0

                            for (i in 1..3) {
                                val gy = h * (i / 4f)
                                drawLine(
                                    color = Color(0xFF263238),
                                    start = Offset(0f, gy),
                                    end = Offset(w, gy),
                                    strokeWidth = 1f
                                )
                            }

                            if (points.size >= 2) {
                                val path = Path()
                                val stepX = w / (points.size - 1)

                                points.forEachIndexed { idx, p ->
                                    val x = idx * stepX
                                    val y = (h - ((p.totalKw - minVal) / (maxVal - minVal) * h)).toFloat()
                                    if (idx == 0) path.moveTo(x, y) else path.lineTo(x, y)
                                }

                                drawPath(
                                    path = path,
                                    color = Color(0xFFFFB300),
                                    style = Stroke(width = 3.dp.toPx())
                                )

                                points.forEachIndexed { idx, p ->
                                    val x = idx * stepX
                                    val y = (h - ((p.totalKw - minVal) / (maxVal - minVal) * h)).toFloat()
                                    val isSel = (p == selected)

                                    if (isSel) {
                                        drawCircle(
                                            color = Color(0xFF00E5FF),
                                            radius = 8.dp.toPx(),
                                            center = Offset(x, y)
                                        )
                                    }
                                    drawCircle(
                                        color = if (isSel) Color.Black else Color(0xFFFFB300),
                                        radius = 4.dp.toPx(),
                                        center = Offset(x, y)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (selected != null) {
            item {
                IndustrialCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ДЕТАЛІЗАЦІЯ ТОЧКИ (${selected.timeLabel}): ${selected.totalKw} кВт",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            StatusBadge(
                                statusText = "АКТИВНА ТОЧКА",
                                isNormal = true
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Фаза L1: ${String.format(Locale.US, "%.1f", selected.phaseL1Kw)} кВт")
                            Text("Фаза L2: ${String.format(Locale.US, "%.1f", selected.phaseL2Kw)} кВт")
                            Text("Фаза L3: ${String.format(Locale.US, "%.1f", selected.phaseL3Kw)} кВт")
                        }
                    }
                }
            }
        }

        item {
            IndustrialCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "СТОВПЧИКОВА ДІАГРАМА РОЗПОДІЛУ ПО ФАЗАХ L1 / L2 / L3",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height - 20f
                        val currentPoint = selected ?: points.lastOrNull()
                        if (currentPoint != null) {
                            val phases = listOf(
                                "Фаза L1" to currentPoint.phaseL1Kw,
                                "Фаза L2" to currentPoint.phaseL2Kw,
                                "Фаза L3" to currentPoint.phaseL3Kw
                            )
                            val barW = w / 5f
                            val maxP = 25.0

                            phases.forEachIndexed { i, pair ->
                                val bx = (i + 1) * (w / 4f) - barW / 2
                                val barH = ((pair.second / maxP) * h).toFloat()
                                val by = h - barH
                                drawRect(
                                    color = if (i == 0) Color(0xFFFFB300) else if (i == 1) Color(0xFF00E5FF) else Color(0xFF43A047),
                                    topLeft = Offset(bx, by),
                                    size = Size(barW, barH)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
