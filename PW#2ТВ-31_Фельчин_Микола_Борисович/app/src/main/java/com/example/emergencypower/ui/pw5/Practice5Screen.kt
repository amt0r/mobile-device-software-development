package com.example.emergencypower.ui.pw5

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.emergencypower.model.AlarmSeverity
import com.example.emergencypower.navigation.NavRoutes
import com.example.emergencypower.ui.components.EmergencyHeader
import com.example.emergencypower.ui.components.PrimaryActionButton
import com.example.emergencypower.ui.components.StatusBadge
import com.example.emergencypower.ui.components.ValidatedInputField

@Composable
fun Practice5Screen() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.MainMenu.route,
        modifier = Modifier.fillMaxSize()
    ) {

        composable(NavRoutes.MainMenu.route) {
            Practice5MainMenu(navController = navController)
        }

        composable(NavRoutes.Dashboard.route) {
            Practice5DashboardScreen(navController = navController)
        }

        composable(
            route = NavRoutes.BatteryDetail.route,
            arguments = listOf(
                navArgument("stationId") { type = NavType.StringType },
                navArgument("mode") {
                    type = NavType.StringType
                    defaultValue = "standby"
                }
            )
        ) { backStackEntry ->
            val stationId = backStackEntry.arguments?.getString("stationId") ?: "UPS-01"
            val mode = backStackEntry.arguments?.getString("mode") ?: "standby"
            Practice5BatteryDetailScreen(
                navController = navController,
                stationId = stationId,
                initialMode = mode
            )
        }

        composable(NavRoutes.GeneratorAts.route) {
            Practice5GeneratorAtsScreen(navController = navController)
        }

        composable(NavRoutes.Auth.route) {
            Practice5AuthScreen(navController = navController)
        }
    }
}

@Composable
fun Practice5MainMenu(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        EmergencyHeader(
            title = "ПЗ №5: Навігація в Jetpack Compose",
            subtitle = "Тема 18: Диспетчеризація системи аварійного електропостачання"
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Використано NavController і NavHost для декларативної навігації із передачею параметрів через URL-маршрути.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Text(
            text = "Розділи системи аварійного живлення:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        MenuNavigationCard(
            title = "1. Загальний моніторинг шини живлення",
            description = "Поточні параметри вводу, напруга, стан АВР та активне джерело",
            icon = Icons.Default.Dashboard,
            onClick = { navController.navigate(NavRoutes.Dashboard.route) }
        )

        MenuNavigationCard(
            title = "2. Акумулятори та Інверторна підсистема",
            description = "Деталі заряду АКБ з передачею ID станції (UPS-A102) через NavArguments",
            icon = Icons.Default.BatteryChargingFull,
            onClick = {
                navController.navigate(NavRoutes.BatteryDetail.createRoute("UPS-A102", "emergency_eco"))
            }
        )

        MenuNavigationCard(
            title = "3. Дизель-генератор та щит АВР",
            description = "Керування підігрівом двигуна, рівень палива та тест автозапуску",
            icon = Icons.Default.ElectricBolt,
            onClick = { navController.navigate(NavRoutes.GeneratorAts.route) }
        )

        MenuNavigationCard(
            title = "4. Авторизація диспетчера (Домашнє завдання)",
            description = "Форма входу та реєстрації інженера з валідацією полів",
            icon = Icons.Default.Security,
            onClick = { navController.navigate(NavRoutes.Auth.route) }
        )
    }
}

@Composable
fun MenuNavigationCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Перейти",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun Practice5DashboardScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
            }
            EmergencyHeader(
                title = "Панель моніторингу АЕП",
                subtitle = "Головний вузол електропостачання"
            )
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Статус міської мережі: 228.4 В", fontWeight = FontWeight.SemiBold)
                    StatusBadge(severity = AlarmSeverity.NORMAL, text = "В нормі")
                }
                HorizontalDivider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Щит АВР (Автомат введення):", fontWeight = FontWeight.SemiBold)
                    StatusBadge(severity = AlarmSeverity.INFO, text = "АВТО-ГОТОВНІСТЬ")
                }
                HorizontalDivider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Батарейний масив:", fontWeight = FontWeight.SemiBold)
                    Text("94% (Буферний підзаряд)", color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        PrimaryActionButton(
            text = "Повернутись до головного меню",
            onClick = { navController.popBackStack() }
        )
    }
}

@Composable
fun Practice5BatteryDetailScreen(
    navController: NavController,
    stationId: String,
    initialMode: String
) {
    var currentMode by remember { mutableStateOf(initialMode) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
            }
            EmergencyHeader(
                title = "Інверторна станція $stationId",
                subtitle = "Дані отримано через NavController Arguments"
            )
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Ідентифікатор обладнання: $stationId", fontWeight = FontWeight.Bold)
                Text("Поточний режим інвертора: $currentMode")
                Text("Напруга акумуляторної шини: 51.8 В DC (LiFePO4 16S)")
                Text("Температура батарейного блоку: +23.5 °C")
                Text("Ефективність перетворення: 94.2%")

                Spacer(modifier = Modifier.height(8.dp))
                Text("Зміна режиму роботи інвертора:", fontWeight = FontWeight.Medium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { currentMode = "eco_saving" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentMode == "eco_saving") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Text("ЕКО")
                    }
                    Button(
                        onClick = { currentMode = "max_backup" },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentMode == "max_backup") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Text("Макс. резерв")
                    }
                }
            }
        }

        PrimaryActionButton(
            text = "Застосувати та повернутись назад",
            onClick = { navController.popBackStack() }
        )
    }
}

@Composable
fun Practice5GeneratorAtsScreen(navController: NavController) {
    var isTestRunning by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
            }
            EmergencyHeader(
                title = "Дизель-генератор ДГ-50",
                subtitle = "Резервне автономне джерело 3-ї категорії"
            )
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Рівень дизельного пального: 185 л (82%)")
                Text("Підігрів картера: Активний (+45 °C)")
                Text("Напруга стартерної батареї: 26.8 В")
                Text("Мотогодини до ТО: 142 год")

                if (testResult != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = testResult!!,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }
        }

        PrimaryActionButton(
            text = if (isTestRunning) "Виконується тестовий прокрут..." else "Запустити 10-сек тест автозапуску",
            isLoading = isTestRunning,
            onClick = {
                isTestRunning = true
                testResult = "Тест успішний: стартер спрацював, тиск масла в нормі (4.2 бар)."
                isTestRunning = false
            }
        )

        OutlinedButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Назад до меню")
        }
    }
}

@Composable
fun Practice5AuthScreen(navController: NavController) {
    var isRegisterMode by remember { mutableStateOf(false) }

    var operatorId by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
            }
            EmergencyHeader(
                title = if (isRegisterMode) "Реєстрація диспетчера" else "Вхід у систему АЕП",
                subtitle = "Контроль доступу чергового персоналу"
            )
        }

        ValidatedInputField(
            value = operatorId,
            onValueChange = { operatorId = it },
            label = "Позивний / Табельний номер"
        )

        if (isRegisterMode) {
            ValidatedInputField(
                value = email,
                onValueChange = { email = it },
                label = "Службовий Email"
            )
        }

        ValidatedInputField(
            value = password,
            onValueChange = { password = it },
            label = "Пароль доступу",
            isPassword = true
        )

        if (isRegisterMode) {
            ValidatedInputField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = "Підтвердження пароля",
                isPassword = true
            )
        }

        PrimaryActionButton(
            text = if (isRegisterMode) "Зареєструвати диспетчера" else "Увійти в панель",
            onClick = {
                if (operatorId.isBlank() || password.isBlank()) {
                    isError = true
                    feedbackMessage = "Будь ласка, заповніть усі обов'язкові поля!"
                } else if (isRegisterMode && password != confirmPassword) {
                    isError = true
                    feedbackMessage = "Паролі не співпадають!"
                } else {
                    isError = false
                    feedbackMessage = if (isRegisterMode) {
                        "Диспетчера $operatorId успішно зареєстровано в системі АЕП."
                    } else {
                        "Авторизація успішна! Доступ до керування АЕП надано."
                    }
                }
            }
        )

        TextButton(
            onClick = {
                isRegisterMode = !isRegisterMode
                feedbackMessage = null
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = if (isRegisterMode) "Вже маєте доступ? Увійти" else "Новий співробітник? Зареєструватись"
            )
        }

        feedbackMessage?.let { msg ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isError) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = msg,
                    color = if (isError) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(14.dp),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
