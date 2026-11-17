package com.example.emergencypower.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.emergencypower.ui.pw21.Practice21Screen
import com.example.emergencypower.ui.pw21.Practice21ViewModel
import com.example.emergencypower.ui.pw22.Practice22Screen
import com.example.emergencypower.ui.pw22.Practice22ViewModel
import com.example.emergencypower.ui.pw23.Practice23Screen
import com.example.emergencypower.ui.pw23.Practice23ViewModel
import com.example.emergencypower.ui.pw24.Practice24Screen
import com.example.emergencypower.ui.pw24.Practice24ViewModel
import com.example.emergencypower.ui.pw25.Practice25Screen
import com.example.emergencypower.ui.pw25.Practice25ViewModel
import com.example.emergencypower.ui.pw26.Practice26Screen
import com.example.emergencypower.ui.pw26.Practice26ViewModel
import com.example.emergencypower.ui.pw27.Practice27Screen
import com.example.emergencypower.ui.pw27.Practice27ViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        "ПЗ 21: АНІМАЦІЇ",
        "ПЗ 22: CSV / JSON",
        "ПЗ 23: ФОТО ТА ДОКИ",
        "ПЗ 24: ЗВІТИ PDF",
        "ПЗ 25: ТЕСТИ",
        "ПЗ 26: РЕСУРСИ",
        "ПЗ 27: РЕЛІЗ"
    )

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "САЕ-18 • ПРАКТИЧНА №6 (ПЗ 21-27)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "Анімації, Файли, PDF, Тестування, Оптимізація та Реліз",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )

                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    edgePadding = 8.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) MaterialTheme.colorScheme.primary else Color.Gray
                                )
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> {
                    val vm: Practice21ViewModel = viewModel()
                    Practice21Screen(viewModel = vm)
                }
                1 -> {
                    val vm: Practice22ViewModel = viewModel()
                    Practice22Screen(viewModel = vm)
                }
                2 -> {
                    val vm: Practice23ViewModel = viewModel()
                    Practice23Screen(viewModel = vm)
                }
                3 -> {
                    val vm: Practice24ViewModel = viewModel()
                    Practice24Screen(viewModel = vm)
                }
                4 -> {
                    val vm: Practice25ViewModel = viewModel()
                    Practice25Screen(viewModel = vm)
                }
                5 -> {
                    val vm: Practice26ViewModel = viewModel()
                    Practice26Screen(viewModel = vm)
                }
                6 -> {
                    val vm: Practice27ViewModel = viewModel()
                    Practice27Screen(viewModel = vm)
                }
            }
        }
    }
}
