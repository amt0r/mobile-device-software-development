package com.example.emergencypower.ui.pw23

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
fun Practice23Screen(viewModel: Practice23ViewModel) {
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
        IndustrialCard(title = "ПЗ 23: РОБОТА З ФОТОГРАФІЯМИ ТА ТЕХДОКУМЕНТАЦІЄЮ") {
            Text(
                text = "Перегляд фотофіксації вузлів підстанції та архіву супровідної виконавчої документації (схеми, протоколи, акти).",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IndustrialCard(title = "ФОТОФІКСАЦІЯ ТЕХНІЧНОГО ОГЛЯДУ ОБ'ЄКТА") {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.photos) { photo ->
                    Column(
                        modifier = Modifier
                            .width(160.dp)
                            .background(Color(0xFF1E293B))
                            .border(1.dp, Color(0xFF334155), RectangleShape)
                            .clickable { viewModel.selectPhoto(photo) }
                            .padding(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .background(Color(photo.simulatedColorHex)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = photo.title,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(photo.category, fontSize = 9.sp, color = Color.Gray)
                            Text(
                                text = photo.status,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (photo.status == "НОРМА") Color(0xFF81C784) else Color(0xFFE57373)
                            )
                        }
                    }
                }
            }
        }

        IndustrialCard(title = "АРХІВ ТЕХНІЧНИХ ДОКУМЕНТІВ ТА АКТІВ") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                state.documents.forEach { doc ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E293B))
                            .border(1.dp, Color(0xFF334155), RectangleShape)
                            .clickable { viewModel.selectDoc(doc) }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(if (doc.docType == "PDF") Color(0xFFC62828) else Color(0xFF1565C0)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = doc.docType,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Text(doc.title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text("${doc.author} • ${doc.updatedDate}", fontSize = 10.sp, color = Color.Gray)
                            }
                        }

                        Text("${doc.fileSizeKb} КБ", fontSize = 10.sp, fontFamily = FontFamily.Monospace, color = Color(0xFFFFB300))
                    }
                }
            }
        }
    }

    state.selectedPhoto?.let { photo ->
        AlertDialog(
            onDismissRequest = { viewModel.selectPhoto(null) },
            confirmButton = {
                TextButton(onClick = { viewModel.selectPhoto(null) }) {
                    Text("ЗАКРИТИ")
                }
            },
            title = { Text(photo.title, fontSize = 14.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Категорія: ${photo.category}")
                    Text("Роздільна здатність: ${photo.resolution}")
                    Text("Час зйомки: ${photo.timestamp}")
                    Text("Технічний стан: ${photo.status}")
                }
            },
            shape = RoundedCornerShape(0.dp)
        )
    }

    state.selectedDoc?.let { doc ->
        AlertDialog(
            onDismissRequest = { viewModel.selectDoc(null) },
            confirmButton = {
                TextButton(onClick = { viewModel.selectDoc(null) }) {
                    Text("ВІДКРИТИ")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.selectDoc(null) }) {
                    Text("СКАСУВАТИ")
                }
            },
            title = { Text(doc.title, fontSize = 14.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Формат: ${doc.docType}")
                    Text("Розмір файлу: ${doc.fileSizeKb} КБ")
                    Text("Дата реєстрації: ${doc.updatedDate}")
                    Text("Організація-розробник: ${doc.author}")
                }
            },
            shape = RoundedCornerShape(0.dp)
        )
    }
}
