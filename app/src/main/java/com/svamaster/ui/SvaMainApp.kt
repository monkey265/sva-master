package com.svamaster.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.svamaster.theme.SvaTheme
import com.svamaster.theme.ThemeMode

enum class SvaNavigationTab(val label: String, val icon: String) {
    PRACTICE("Practice", "✍️"),
    CHEATSHEET("Cheatsheet", "📑"),
    REFERENCE("Reference", "📚"),
    SANDBOX("Sandbox", "⚡"),
    QUIZ("Quiz", "🎯")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SvaMainApp() {
    var currentTab by remember { mutableStateOf(SvaNavigationTab.PRACTICE) }
    var themeMode by remember { mutableStateOf(ThemeMode.DARK) }

    SvaTheme(themeMode = themeMode) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "SVA Master",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    actions = {
                        IconButton(onClick = {
                            themeMode = if (themeMode == ThemeMode.DARK) ThemeMode.LIGHT else ThemeMode.DARK
                        }) {
                            Text(
                                text = if (themeMode == ThemeMode.DARK) "☀️" else "🌙",
                                fontSize = 18.sp
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    SvaNavigationTab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = currentTab == tab,
                            onClick = { currentTab = tab },
                            icon = { Text(tab.icon, fontSize = 20.sp) },
                            label = { Text(tab.label) }
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Crossfade(targetState = currentTab, label = "tab_fade") { tab ->
                    when (tab) {
                        SvaNavigationTab.PRACTICE -> PracticeScreen()
                        SvaNavigationTab.CHEATSHEET -> CheatsheetScreen()
                        SvaNavigationTab.REFERENCE -> ReferenceScreen()
                        SvaNavigationTab.SANDBOX -> SandboxScreen()
                        SvaNavigationTab.QUIZ -> QuizScreen()
                    }
                }
            }
        }
    }
}
