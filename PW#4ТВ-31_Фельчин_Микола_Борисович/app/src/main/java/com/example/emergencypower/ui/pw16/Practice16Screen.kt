package com.example.emergencypower.ui.pw16

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.emergencypower.model.StationStatus
import com.example.emergencypower.ui.components.*
import java.util.*

@Composable
fun Practice16Screen(
    viewModel: Practice16ViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val distanceList by viewModel.stationDistances.collectAsState()
    val selectedItem = distanceList.find { it.station.id == uiState.selectedStationId } ?: distanceList.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader(
                title = "ПЗ №16: GPS та Геолокація",
                subtitle = "Визначення геопозиції диспетчера, розрахунок дистанції до аварійних станцій та InfoWindow"
            )
        }

        item {
            IndustrialCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MyLocation, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ПОТОЧНА ПОЗИЦІЯ ДИСПЕТЧЕРА",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Широта: ${String.format(Locale.US, "%.4f", uiState.userLocation.latitude)}° N • Довгота: ${String.format(Locale.US, "%.4f", uiState.userLocation.longitude)}° E",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    StatusBadge(
                        statusText = "GPS OK",
                        isNormal = true
                    )
                }
            }
        }

        if (selectedItem != null) {
            val st = selectedItem.station
            item {
                IndustrialCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "INFOWINDOW: ${st.name}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            StatusBadge(
                                statusText = st.status.label,
                                isCritical = st.status == StationStatus.FAULT,
                                isNormal = st.status == StationStatus.ONLINE
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Дистанція: ${String.format(Locale.US, "%.2f", selectedItem.distanceKm)} км",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DirectionsCar, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Доїзд: ~${selectedItem.estimatedArrivalMinutes} хв",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Координати: ${st.location.latitude}° N, ${st.location.longitude}° E",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Адреса: ${st.address}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Потужність: ${st.nominalPowerKw} кВт • Поточне навантаження: ${st.currentLoadKw} кВт",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "СТАНЦІЇ ЗА ВІДСТАННЮ ВІД ДИСПЕТЧЕРА (${distanceList.size})",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        items(distanceList, key = { it.station.id }) { item ->
            val isSelected = item.station.id == uiState.selectedStationId
            val borderCol = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline
            IndustrialCard(
                borderStroke = androidx.compose.foundation.BorderStroke(if (isSelected) 2.dp else 1.dp, borderCol),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.selectStation(item.station.id) }
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
                            text = item.station.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Дистанція: ${String.format(Locale.US, "%.2f", item.distanceKm)} км • Час прибуття: ~${item.estimatedArrivalMinutes} хв",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = item.station.address,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    StatusBadge(
                        statusText = item.station.status.label,
                        isCritical = item.station.status == StationStatus.FAULT,
                        isNormal = item.station.status == StationStatus.ONLINE
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
