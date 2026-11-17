package com.example.emergencypower.ui.pw24

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.emergencypower.ui.components.IndustrialCard

@Composable
fun Practice24Screen(viewModel: Practice24ViewModel) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        IndustrialCard(title = "ПЗ 24: ГЕНЕРАЦІЯ АНАЛІТИЧНИХ PDF-ЗВІТІВ") {
            Text(
                text = "Формування офіційного багатосторінкового звіту підстанції з таблицями телеметрії, графіками навантаження та висновком експерта РЗА через PdfDocument API.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { viewModel.generatePdf() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isGenerating,
                shape = RoundedCornerShape(0.dp)
            ) {
                if (state.isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(18.dp).width(18.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ГЕНЕРАЦІЯ ДОКУМЕНТА...")
                } else {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ЗГЕНЕРУВАТИ PDF-ЗВІТ ПІДСТАНЦІЇ")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1E293B))
                    .padding(8.dp)
            ) {
                Text(
                    text = state.statusMessage,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = Color(0xFFFFB300)
                )
            }
        }

        IndustrialCard(title = "СТРУКТУРА СФОРМОВАНОГО ДОКУМЕНТА") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF334155), RectangleShape)
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.height(16.dp).width(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Сторінка 1: Паспорт та протокол комутацій АВР", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Text("• Технічний паспорт ТП-110/10 кВ «Південна-18» (Варіант 18)", fontSize = 11.sp, color = Color.LightGray)
                        Text("• Таблиця телеметрії: 8 зафіксованих подій перемикань контакторів", fontSize = 11.sp, color = Color.LightGray)
                        Text("• Зведені показники: пікове навантаження 360 кВт, час спрацьовування 45 мс", fontSize = 11.sp, color = Color.LightGray)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF334155), RectangleShape)
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF81C784), modifier = Modifier.height(16.dp).width(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Сторінка 2: Графічна діаграма та блок погодження", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Text("• Векторна гістограма розподілу потужності за джерелами енергії", fontSize = 11.sp, color = Color.LightGray)
                        Text("• Експертний висновок щодо придатності обладнання до експлуатації", fontSize = 11.sp, color = Color.LightGray)
                        Text("• Блок підписів: інженер РЗА Фельчин М.Б., викладач Недашківський О.Л.", fontSize = 11.sp, color = Color.LightGray)
                    }
                }
            }
        }

        if (state.generatedPdfFile != null) {
            IndustrialCard(title = "ФАЙЛ ГОТОВИЙ ДО ВІДПРАВКИ / ДРУКУ") {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("• Файл: ${state.generatedPdfFile?.name}", fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                    Text("• Кількість сторінок: ${state.pageCount}", fontSize = 11.sp)
                    Text("• Розмір: ${state.fileSizeKb} КБ", fontSize = 11.sp)
                    Text("• Локальний шлях: ${state.generatedPdfFile?.absolutePath}", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = Color.Gray)
                }
            }
        }
    }
}
