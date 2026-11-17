package com.example.emergencypower.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.emergencypower.ui.components.RectShape
import com.example.emergencypower.ui.pw17.Practice17Screen
import com.example.emergencypower.ui.pw18.Practice18Screen
import com.example.emergencypower.ui.pw19.Practice19Screen
import com.example.emergencypower.ui.pw20.Practice20Screen

@Composable
fun MainScreen() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("ПЗ 17: SENSORS", "ПЗ 18: NOTIFY", "ПЗ 19: CHARTS", "ПЗ 20: GAUGES")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        PrimaryTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = {
                TabRowDefaults.PrimaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(selectedTabIndex),
                    shape = RectShape,
                    color = MaterialTheme.colorScheme.primary,
                    height = 3.dp
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTabIndex) {
                0 -> Practice17Screen()
                1 -> Practice18Screen()
                2 -> Practice19Screen()
                3 -> Practice20Screen()
            }
        }
    }
}
