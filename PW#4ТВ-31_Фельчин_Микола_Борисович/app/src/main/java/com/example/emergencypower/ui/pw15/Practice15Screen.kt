package com.example.emergencypower.ui.pw15

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.emergencypower.model.StationStatus
import com.example.emergencypower.model.StationType
import com.example.emergencypower.ui.components.*

@Composable
fun Practice15Screen(
    viewModel: Practice15ViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val stations by viewModel.filteredStations.collectAsState()

    val typeFilters = listOf("Всі", StationType.DIESEL.name, StationType.BATTERY_UPS.name, StationType.SOLAR_HYBRID.name)
    val statusFilters = listOf("Всі", StationStatus.ONLINE.name, StationStatus.STANDBY.name, StationStatus.MAINTENANCE.name, StationStatus.FAULT.name)
    val sortOptions = listOf("Потужність (max)", "Потужність (min)", "Запас % (max)", "Поточне навантаження")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            SectionHeader(
                title = "ПЗ №15: Пошук та Фільтрація",
                subtitle = "Багаторівнева фільтрація, повнотекстовий пошук та сортування у LazyColumn"
            )
        }

        item {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                label = { Text("Пошук станції за назвою, кодом чи адресою") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    }
                },
                shape = RectShape,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Text(
                text = "ФІЛЬТР ЗА ТИПОМ ОБЛАДНАННЯ",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(typeFilters) { t ->
                    FilterChip(
                        selected = uiState.selectedType == t,
                        onClick = { viewModel.onTypeSelected(t) },
                        label = { Text(t) },
                        shape = RectShape
                    )
                }
            }
        }

        item {
            Text(
                text = "ФІЛЬТР ЗА СТАТУСОМ ЕКСПЛУАТАЦІЇ",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(statusFilters) { s ->
                    FilterChip(
                        selected = uiState.selectedStatus == s,
                        onClick = { viewModel.onStatusSelected(s) },
                        label = { Text(s) },
                        shape = RectShape
                    )
                }
            }
        }

        item {
            Text(
                text = "СОРТУВАННЯ ДАНИХ",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(sortOptions) { s ->
                    SuggestionChip(
                        onClick = { viewModel.onSortSelected(s) },
                        label = { Text(s) },
                        shape = RectShape,
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = if (uiState.sortOption == s) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        )
                    )
                }
            }
        }

        item {
            Text(
                text = "ЗНАЙДЕНО ОБ'ЄКТІВ: ${stations.size}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (stations.isEmpty()) {
            item {
                IndustrialCard(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Жоден об'єкт не відповідає встановленим критеріям пошуку",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(stations, key = { it.id }) { st ->
                IndustrialCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = st.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            StatusBadge(
                                statusText = st.status.label,
                                isCritical = st.status == StationStatus.FAULT,
                                isNormal = st.status == StationStatus.ONLINE
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Тип: ${st.type.label} • Код: ${st.code}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Номінал: ${st.nominalPowerKw} кВт")
                            Text("Поточне: ${st.currentLoadKw} кВт")
                            Text("Запас: ${st.reserveLevelPercent}%")
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Адреса: ${st.address}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary
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
