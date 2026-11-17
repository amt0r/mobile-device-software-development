package com.example.emergencypower.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.emergencypower.ui.components.RectShape
import com.example.emergencypower.ui.pw13.Practice13Screen
import com.example.emergencypower.ui.pw14.Practice14Screen
import com.example.emergencypower.ui.pw15.Practice15Screen
import com.example.emergencypower.ui.pw16.Practice16Screen

@Composable
fun MainScreen() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("ПЗ 13: FLOW", "ПЗ 14: MAP", "ПЗ 15: SEARCH", "ПЗ 16: GPS")

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
                0 -> Practice13Screen()
                1 -> Practice14Screen()
                2 -> Practice15Screen()
                3 -> Practice16Screen()
            }
        }
    }
}
