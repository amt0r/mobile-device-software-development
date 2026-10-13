package com.example.emergencypower.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.emergencypower.data.local.EmergencyEventEntity
import com.example.emergencypower.data.repository.EmergencyLogRepository
import com.example.emergencypower.data.repository.LogAnalytics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class LogSortOption(val title: String) {
    NEWEST_FIRST("Спочатку нові"),
    OLDEST_FIRST("Спочатку старі"),
    HIGHEST_LOAD("Найбільше навантаження (Вт)")
}

data class LogUiState(
    val events: List<EmergencyEventEntity> = emptyList(),
    val analytics: LogAnalytics = LogAnalytics(0, 0, 0.0, 0.0),
    val selectedSourceFilter: String = "ALL",
    val selectedSeverityFilter: String = "ALL",
    val sortOption: LogSortOption = LogSortOption.NEWEST_FIRST,
    val isLoading: Boolean = false,
    val feedbackMessage: String? = null
)

class EmergencyLogViewModel(
    private val repository: EmergencyLogRepository
) : ViewModel() {

    private val _selectedSource = MutableStateFlow("ALL")
    private val _selectedSeverity = MutableStateFlow("ALL")
    private val _sortOption = MutableStateFlow(LogSortOption.NEWEST_FIRST)
    private val _isLoading = MutableStateFlow(false)
    private val _feedbackMessage = MutableStateFlow<String?>(null)

    private val _filteredLogs = combine(
        repository.getAllEvents(),
        _selectedSource,
        _selectedSeverity,
        _sortOption
    ) { allEvents: List<EmergencyEventEntity>, sourceFilter: String, severityFilter: String, sort: LogSortOption ->
        if (allEvents.isEmpty()) {
            repository.seedInitialDataIfEmpty(0)
        }

        val filtered = allEvents.filter { event ->
            val matchSource = (sourceFilter == "ALL" || event.sourceType == sourceFilter)
            val matchSeverity = (severityFilter == "ALL" || event.severity == severityFilter)
            matchSource && matchSeverity
        }

        val sorted = when (sort) {
            LogSortOption.NEWEST_FIRST -> filtered.sortedByDescending { it.timestamp }
            LogSortOption.OLDEST_FIRST -> filtered.sortedBy { it.timestamp }
            LogSortOption.HIGHEST_LOAD -> filtered.sortedByDescending { it.loadWatts }
        }

        val analytics = repository.computeAnalytics(allEvents)

        LogUiState(
            events = sorted,
            analytics = analytics,
            selectedSourceFilter = sourceFilter,
            selectedSeverityFilter = severityFilter,
            sortOption = sort,
            isLoading = false
        )
    }

    val uiState: StateFlow<LogUiState> = combine(
        _filteredLogs,
        _feedbackMessage,
        _isLoading
    ) { state, feedback, loading ->
        state.copy(
            feedbackMessage = feedback,
            isLoading = loading
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LogUiState(isLoading = true)
    )

    fun setSourceFilter(source: String) {
        _selectedSource.value = source
    }

    fun setSeverityFilter(severity: String) {
        _selectedSeverity.value = severity
    }

    fun setSortOption(option: LogSortOption) {
        _sortOption.value = option
    }

    fun addEvent(
        sourceType: String,
        voltage: Double,
        loadWatts: Double,
        batterySoc: Int,
        severity: String,
        description: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val newEvent = EmergencyEventEntity(
                sourceType = sourceType,
                voltage = voltage,
                loadWatts = loadWatts,
                batterySoc = batterySoc,
                severity = severity,
                description = description
            )
            repository.insertEvent(newEvent)
            _isLoading.value = false
            _feedbackMessage.value = "Запис успішно додано до Room Database"
        }
    }

    fun updateEvent(event: EmergencyEventEntity) {
        viewModelScope.launch {
            repository.updateEvent(event)
            _feedbackMessage.value = "Запис #${event.id} оновлено в базі даних"
        }
    }

    fun deleteEvent(event: EmergencyEventEntity) {
        viewModelScope.launch {
            repository.deleteEvent(event)
            _feedbackMessage.value = "Запис #${event.id} видалено з бази даних"
        }
    }

    fun clearAllLogs() {
        viewModelScope.launch {
            repository.clearAll()
            _feedbackMessage.value = "Усі записи очищено з бази даних"
        }
    }

    fun dismissFeedback() {
        _feedbackMessage.value = null
    }

    class Factory(private val repository: EmergencyLogRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return EmergencyLogViewModel(repository) as T
        }
    }
}
