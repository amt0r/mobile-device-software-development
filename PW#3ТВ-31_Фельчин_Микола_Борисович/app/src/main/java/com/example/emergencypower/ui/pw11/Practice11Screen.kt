package com.example.emergencypower.ui.pw11

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.emergencypower.model.UserRole
import com.example.emergencypower.ui.components.*

@Composable
fun Practice11Screen(
    viewModel: Practice11ViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader(
                title = "ПЗ №11: Автентифікація та RBAC",
                subtitle = "Токен-базований доступ (JWT), перехоплення запитів через Interceptor та розподіл ролей"
            )
        }

        if (uiState.session != null) {
            val session = uiState.session!!
            item {
                IndustrialCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "АКТИВНА СЕСІЯ: ${session.username}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            StatusBadge(
                                statusText = session.role.name,
                                isCritical = false,
                                isNormal = session.role == UserRole.ADMIN
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Роль: ${session.role.roleName}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "JWT Token: ${session.token.take(36)}...",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        IndustrialOutlinedButton(
                            onClick = { viewModel.logout() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("ЗАВЕРШИТИ СЕСІЮ (ВИХІД)")
                        }
                    }
                }
            }

            item {
                Text(
                    text = "ПАНЕЛЬ УПРАВЛІННЯ ЗГІДНО РОЛІ (RBAC)",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (session.role == UserRole.ADMIN) {
                item {
                    IndustrialCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "ЗАХИЩЕНІ ОПЕРАЦІЇ ГОЛОВНОГО ЕНЕРГЕТИКА (ADMIN)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            IndustrialButton(
                                onClick = { viewModel.executeSecureCommand("START_GENERATOR_DG50") },
                                isEmergency = true,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Power, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("ПРИМУСОВИЙ ПУСК ДИЗЕЛЬ-ГЕНЕРАТОРА ДГ-50")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            IndustrialButton(
                                onClick = { viewModel.executeSecureCommand("FORCE_ATS_SWITCH") },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("РУЧНЕ ПЕРЕМИКАННЯ ВВОДУ АВР НА РЕЗЕРВ")
                            }
                        }
                    }
                }
            } else {
                item {
                    IndustrialCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "ОБМЕЖЕНИЙ РЕЖИМ ОПЕРАТОРА (READ-ONLY)",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Для поточної ролі OPERATOR примусове керування автоматикою та пуск генераторів заблоковані політикою безпеки SCADA.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        } else {
            item {
                IndustrialCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "ФОРМА АВТОРИЗАЦІЇ ДИСПЕТЧЕРА СИСТЕМИ (MOCKED JWT)",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IndustrialTextField(
                            value = uiState.loginInput,
                            onValueChange = { viewModel.onLoginChanged(it) },
                            label = "Логін диспетчера"
                        )
                        IndustrialTextField(
                            value = uiState.passwordInput,
                            onValueChange = { viewModel.onPasswordChanged(it) },
                            label = "Пароль доступу"
                        )
                        Text(
                            text = "ОБЕРІТЬ РОЛЬ ДЛЯ СИМУЛЯЦІЇ ДОСТУПУ:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            UserRole.entries.forEach { r ->
                                FilterChip(
                                    selected = uiState.selectedRole == r,
                                    onClick = { viewModel.onRoleSelected(r) },
                                    label = { Text(r.name) },
                                    shape = RectShape
                                )
                            }
                        }
                        IndustrialButton(
                            onClick = { viewModel.login() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("УВІЙТИ В СИСТЕМУ")
                        }
                    }
                }
            }
        }

        if (uiState.actionMessage != null) {
            item {
                IndustrialCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "РЕЗУЛЬТАТ ОПЕРАЦІЇ:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = uiState.actionMessage!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        if (uiState.errorMessage != null) {
            item {
                IndustrialCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "ПОМИЛКА:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = uiState.errorMessage!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
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
