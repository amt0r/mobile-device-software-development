package com.example.emergencypower.ui.pw24

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.emergencypower.data.storage.CsvJsonManager
import com.example.emergencypower.data.storage.PdfReportGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

data class Pw24UiState(
    val isGenerating: Boolean = false,
    val generatedPdfFile: File? = null,
    val fileSizeKb: Long = 0L,
    val pageCount: Int = 2,
    val statusMessage: String = "Готово до формування аналітичного PDF-звіту"
)

class Practice24ViewModel(application: Application) : AndroidViewModel(application) {

    private val pdfGenerator = PdfReportGenerator(application)
    private val storageManager = CsvJsonManager(application)

    private val _uiState = MutableStateFlow(Pw24UiState())
    val uiState: StateFlow<Pw24UiState> = _uiState.asStateFlow()

    fun generatePdf() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isGenerating = true,
                statusMessage = "Генерація векторних сторінок та графіків PDF..."
            )

            val logs = storageManager.getSampleLogs()
            val file = withContext(Dispatchers.IO) {
                pdfGenerator.generateAnalyticalReport(logs)
            }

            _uiState.value = _uiState.value.copy(
                isGenerating = false,
                generatedPdfFile = file,
                fileSizeKb = file.length() / 1024,
                pageCount = 2,
                statusMessage = "Звіт успішно створено: ${file.name} (${file.length() / 1024} КБ)"
            )
        }
    }
}
