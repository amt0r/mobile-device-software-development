package com.example.emergencypower.ui.pw23

import androidx.lifecycle.ViewModel
import com.example.emergencypower.data.model.SubstationPhoto
import com.example.emergencypower.data.model.TechDocument
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class Pw23UiState(
    val photos: List<SubstationPhoto> = emptyList(),
    val documents: List<TechDocument> = emptyList(),
    val selectedFilter: String = "ВСІ",
    val selectedPhoto: SubstationPhoto? = null,
    val selectedDoc: TechDocument? = null,
    val statusMessage: String = "Архів підстанції завантажено"
)

class Practice23ViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(Pw23UiState())
    val uiState: StateFlow<Pw23UiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val samplePhotos = listOf(
            SubstationPhoto("P-01", "Шафа автоматики АВР-0.4кВ", "ОБЛАДНАННЯ", "17.11 08:30", "НОРМА", "2048x1536", 0xFF1E3A5F),
            SubstationPhoto("P-02", "Акумуляторний блок LiFePO4", "БАТАРЕЇ", "17.11 09:15", "НОРМА", "2048x1536", 0xFF2E4A3E),
            SubstationPhoto("P-03", "Дизель-генераторна установка 500 кВА", "ДГУ", "17.11 10:00", "УВАГА", "1920x1080", 0xFF5A2E2E),
            SubstationPhoto("P-04", "Трансформатор ТМН-2500/110 кВ", "ТРАНСФОРМАТОР", "17.11 10:45", "НОРМА", "2048x1536", 0xFF3B3B58)
        )

        val sampleDocs = listOf(
            TechDocument("D-01", "Акт перевірки автоматики АВР-18", "PDF", 1420, "15.11.2026", "Служба РЗА"),
            TechDocument("D-02", "Принципова електрична схема ТП-110", "PDF", 4850, "10.11.2026", "ДП ЕнергоПроект"),
            TechDocument("D-03", "Регламент техобслуговування ДГУ", "DOCX", 620, "01.11.2026", "Інженерна служба"),
            TechDocument("D-04", "Протокол випробувань акумуляторів", "PDF", 890, "12.11.2026", "Випробувальна лабораторія")
        )

        _uiState.value = _uiState.value.copy(
            photos = samplePhotos,
            documents = sampleDocs
        )
    }

    fun selectFilter(filter: String) {
        _uiState.value = _uiState.value.copy(selectedFilter = filter)
    }

    fun selectPhoto(photo: SubstationPhoto?) {
        _uiState.value = _uiState.value.copy(selectedPhoto = photo)
    }

    fun selectDoc(doc: TechDocument?) {
        _uiState.value = _uiState.value.copy(selectedDoc = doc)
    }
}
