package com.example.emergencypower.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.emergencypower.ui.components.RectShape
import com.example.emergencypower.ui.pw10.Practice10Screen
import com.example.emergencypower.ui.pw11.Practice11Screen
import com.example.emergencypower.ui.pw12.Practice12Screen
import com.example.emergencypower.ui.pw9.Practice9Screen

@Composable
fun MainScreen() {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("ПЗ 9: REPO", "ПЗ 10: REST", "ПЗ 11: AUTH", "ПЗ 12: ERRORS")

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
                0 -> Practice9Screen()
                1 -> Practice10Screen()
                2 -> Practice11Screen()
                3 -> Practice12Screen()
            }
        }
    }
}
