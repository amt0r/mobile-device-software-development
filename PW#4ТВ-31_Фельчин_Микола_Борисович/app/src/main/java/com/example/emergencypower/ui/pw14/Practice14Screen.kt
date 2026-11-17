package com.example.emergencypower.ui.pw14

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.emergencypower.model.PowerStation
import com.example.emergencypower.model.StationStatus
import com.example.emergencypower.ui.components.*
import com.example.emergencypower.ui.theme.EmergencyRed
import com.example.emergencypower.ui.theme.GridGreen

@Composable
fun Practice14Screen(
    viewModel: Practice14ViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val stations = uiState.stations
    val selectedStation = viewModel.getSelectedStation()
    val listState = rememberLazyListState()

    LaunchedEffect(uiState.selectedStationId) {
        val idx = stations.indexOfFirst { it.id == uiState.selectedStationId }
        if (idx >= 0) {
            listState.animateScrollToItem(idx)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        SectionHeader(
            title = "ПЗ №14: LazyColumn та Мапа",
            subtitle = "Двостороння інтерактивність між списком об'єктів та картосхемою"
        )

        IndustrialCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(stations) {
                            detectTapGestures { tapOffset ->
                                val minLat = 50.4000
                                val maxLat = 50.4700
                                val minLon = 30.4400
                                val maxLon = 30.6100

                                var closestStation: PowerStation? = null
                                var minDistance = Float.MAX_VALUE

                                stations.forEach { st ->
                                    val x = ((st.location.longitude - minLon) / (maxLon - minLon) * size.width).toFloat()
                                    val y = ((maxLat - st.location.latitude) / (maxLat - minLat) * size.height).toFloat()
                                    val dist = (Offset(x, y) - tapOffset).getDistance()
                                    if (dist < 40f && dist < minDistance) {
                                        minDistance = dist
                                        closestStation = st
                                    }
                                }
                                closestStation?.let { viewModel.selectStation(it.id) }
                            }
                        }
                ) {
                    val w = size.width
                    val h = size.height

                    val stepX = w / 6f
                    for (i in 1..5) {
                        drawLine(
                            color = Color(0xFF263238),
                            start = Offset(i * stepX, 0f),
                            end = Offset(i * stepX, h),
                            strokeWidth = 1f
                        )
                    }
                    val stepY = h / 4f
                    for (i in 1..3) {
                        drawLine(
                            color = Color(0xFF263238),
                            start = Offset(0f, i * stepY),
                            end = Offset(w, i * stepY),
                            strokeWidth = 1f
                        )
                    }

                    val minLat = 50.4000
                    val maxLat = 50.4700
                    val minLon = 30.4400
                    val maxLon = 30.6100

                    stations.forEach { st ->
                        val x = ((st.location.longitude - minLon) / (maxLon - minLon) * w).toFloat()
                        val y = ((maxLat - st.location.latitude) / (maxLat - minLat) * h).toFloat()
                        val isSelected = st.id == uiState.selectedStationId

                        val pinColor = when (st.status) {
                            StationStatus.ONLINE -> GridGreen
                            StationStatus.STANDBY -> Color(0xFFFFB300)
                            StationStatus.FAULT -> EmergencyRed
                            StationStatus.MAINTENANCE -> Color(0xFF78909C)
                        }

                        if (isSelected) {
                            drawCircle(
                                color = Color(0xFF00E5FF),
                                radius = 16f,
                                center = Offset(x, y),
                                style = Stroke(width = 3f)
                            )
                        }
                        drawCircle(
                            color = pinColor,
                            radius = if (isSelected) 10f else 7f,
                            center = Offset(x, y)
                        )
                    }
                }

                Text(
                    text = "ІНТЕРАКТИВНА КАРТОСХЕМА ЕНЕРГООБ'ЄКТІВ (м. КИЇВ)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (selectedStation != null) {
            IndustrialCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedStation.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        StatusBadge(
                            statusText = selectedStation.status.label,
                            isCritical = selectedStation.status == StationStatus.FAULT,
                            isNormal = selectedStation.status == StationStatus.ONLINE
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Тип: ${selectedStation.type.label} • Код: ${selectedStation.code}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Потужність: ${selectedStation.nominalPowerKw} кВт • Навантаження: ${selectedStation.currentLoadKw} кВт • Запас: ${selectedStation.reserveLevelPercent}%",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Адреса: ${selectedStation.address}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "ДИНАМІЧНИЙ СПИСОК СТАНЦІЙ (LAZYCOLUMN)",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(4.dp))

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(stations, key = { it.id }) { st ->
                val isSelected = st.id == uiState.selectedStationId
                val borderCol = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                IndustrialCard(
                    borderStroke = androidx.compose.foundation.BorderStroke(if (isSelected) 2.dp else 1.dp, borderCol),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectStation(st.id) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = st.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${st.type.label} • ${st.nominalPowerKw} кВт",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Запас енергії: ${st.reserveLevelPercent}% • ТО: ${st.lastMaintenanceDate}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        StatusBadge(
                            statusText = st.status.label,
                            isCritical = st.status == StationStatus.FAULT,
                            isNormal = st.status == StationStatus.ONLINE
                        )
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
