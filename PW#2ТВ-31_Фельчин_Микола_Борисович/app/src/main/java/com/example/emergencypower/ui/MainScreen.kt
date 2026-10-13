package com.example.emergencypower.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.emergencypower.ui.pw5.Practice5Screen
import com.example.emergencypower.ui.pw6.Practice6Screen
import com.example.emergencypower.ui.pw7.Practice7Screen
import com.example.emergencypower.ui.pw8.Practice8Screen

@Composable
fun MainScreen() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("ПЗ 5", "ПЗ 6", "ПЗ 7", "ПЗ 8")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        PrimaryTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTabIndex) {
                0 -> Practice5Screen()
                1 -> Practice6Screen()
                2 -> Practice7Screen()
                3 -> Practice8Screen()
            }
        }
    }
}
