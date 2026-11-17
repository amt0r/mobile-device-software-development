package com.example.emergencypower.ui.pw16

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.emergencypower.data.PowerStationRepository
import com.example.emergencypower.model.GeoLocation
import com.example.emergencypower.model.PowerStation
import kotlinx.coroutines.flow.*
import kotlin.math.*

data class StationDistanceInfo(
    val station: PowerStation,
    val distanceKm: Double,
    val estimatedArrivalMinutes: Int
)

data class Practice16UiState(
    val userLocation: GeoLocation = GeoLocation(50.4501, 30.5234),
    val selectedStationId: String = "ST-18-02",
    val hasLocationPermission: Boolean = true
)

class Practice16ViewModel : ViewModel() {

    private val repository = PowerStationRepository()

    private val _uiState = MutableStateFlow(Practice16UiState())
    val uiState: StateFlow<Practice16UiState> = _uiState.asStateFlow()

    val stationDistances: StateFlow<List<StationDistanceInfo>> = combine(
        repository.getAllStations(),
        _uiState
    ) { stations, state ->
        stations.map { st ->
            val dist = calculateHaversineDistance(
                state.userLocation.latitude, state.userLocation.longitude,
                st.location.latitude, st.location.longitude
            )
            val minutes = (dist / 35.0 * 60).roundToInt().coerceAtLeast(4)
            StationDistanceInfo(st, dist, minutes)
        }.sortedBy { it.distanceKm }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectStation(id: String) {
        _uiState.update { it.copy(selectedStationId = id) }
    }

    fun updateUserLocation(lat: Double, lon: Double) {
        _uiState.update { it.copy(userLocation = GeoLocation(lat, lon)) }
    }

    private fun calculateHaversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
