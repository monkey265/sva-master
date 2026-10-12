package com.svamaster.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.svamaster.data.SvaRepository
import com.svamaster.model.SvaWaveformScenarios
import com.svamaster.ui.components.SvaCodeBlock
import com.svamaster.ui.components.SvaWaveformVisualizer
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class SandboxTab {
    WAVEFORM_STUDIO,
    ASSERTION_SIMULATOR
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SandboxScreen() {
    var activeTab by remember { mutableStateOf(SandboxTab.WAVEFORM_STUDIO) }
    val haptic = LocalHapticFeedback.current

    // Waveform Studio State
    val scenarios = remember { SvaWaveformScenarios.all }
    var selectedScenarioIndex by remember { mutableIntStateOf(0) }
    var expandedWaveformDropdown by remember { mutableStateOf(false) }
    val currentScenario = scenarios[selectedScenarioIndex]

    // Code Sandbox State
    val samples = remember { SvaRepository.sandboxSamples }
    var selectedSampleIndex by remember { mutableIntStateOf(0) }
    var expandedSampleDropdown by remember { mutableStateOf(false) }
    var isSimulating by remember { mutableStateOf(false) }
    var hasRun by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val currentSample = samples[selectedSampleIndex]

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Screen Header
        item {
            Column {
                Text(
                    text = "SVA Interactive Studio",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Analyze timing waveforms cycle-by-cycle and simulate formal assertions.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Tab Selector: Waveform Studio vs UVM Code Sandbox
        item {
            PrimaryTabRow(
                selectedTabIndex = if (activeTab == SandboxTab.WAVEFORM_STUDIO) 0 else 1,
                containerColor = Color.Transparent
            ) {
                Tab(
                    selected = activeTab == SandboxTab.WAVEFORM_STUDIO,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        activeTab = SandboxTab.WAVEFORM_STUDIO
                    },
                    text = { Text("📈 Waveform Studio") }
                )
                Tab(
                    selected = activeTab == SandboxTab.ASSERTION_SIMULATOR,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        activeTab = SandboxTab.ASSERTION_SIMULATOR
                    },
                    text = { Text("⚡ Code Testbench") }
                )
            }
        }

        if (activeTab == SandboxTab.WAVEFORM_STUDIO) {
            // Scenario Quick Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    items(scenarios.indices.toList()) { idx ->
                        val sc = scenarios[idx]
                        FilterChip(
                            selected = selectedScenarioIndex == idx,
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                selectedScenarioIndex = idx
                            },
                            label = { Text("${sc.category.icon} ${sc.title}") }
                        )
                    }
                }
            }

            // Scenario Dropdown Box
            item {
                ExposedDropdownMenuBox(
                    expanded = expandedWaveformDropdown,
                    onExpandedChange = { expandedWaveformDropdown = !expandedWaveformDropdown }
                ) {
                    OutlinedTextField(
                        value = "${currentScenario.category.icon} ${currentScenario.title}",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select Timing Scenario") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedWaveformDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(androidx.compose.material3.ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(14.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = expandedWaveformDropdown,
                        onDismissRequest = { expandedWaveformDropdown = false }
                    ) {
                        scenarios.forEachIndexed { index, scenarioItem ->
                            DropdownMenuItem(
                                text = { Text("${scenarioItem.category.icon} ${scenarioItem.title}") },
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    selectedScenarioIndex = index
                                    expandedWaveformDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            // Interactive Waveform Visualizer Component
            item {
                SvaWaveformVisualizer(scenario = currentScenario)
            }

            // Scenario Engineering Details
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Temporal Behavior & Rules",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentScenario.description,
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        } else {
            // Existing UVM Assertion Code Sandbox
            item {
                ExposedDropdownMenuBox(
                    expanded = expandedSampleDropdown,
                    onExpandedChange = { expandedSampleDropdown = !expandedSampleDropdown }
                ) {
                    OutlinedTextField(
                        value = currentSample.title,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Select SVA Assertion Pattern") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSampleDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(androidx.compose.material3.ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                        shape = RoundedCornerShape(14.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = expandedSampleDropdown,
                        onDismissRequest = { expandedSampleDropdown = false }
                    ) {
                        samples.forEachIndexed { index, sample ->
                            DropdownMenuItem(
                                text = { Text(sample.title) },
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    selectedSampleIndex = index
                                    expandedSampleDropdown = false
                                    hasRun = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Description",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentSample.description,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "SystemVerilog Code",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        SvaCodeBlock(code = currentSample.code)

                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                coroutineScope.launch {
                                    isSimulating = true
                                    delay(300)
                                    isSimulating = false
                                    hasRun = true
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                            },
                            enabled = !isSimulating,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(if (isSimulating) "⚡ Simulating Assertion Trace..." else "▶ Simulate & Verify Pattern")
                        }
                    }
                }
            }

            item {
                AnimatedVisibility(visible = hasRun) {
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF2E7D32).copy(alpha = 0.15f)
                        ),
                        border = BorderStroke(1.5.dp, Color(0xFF4CAF50))
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF2E7D32)
                                ) {
                                    Text(
                                        text = "VERDICT: SUCCESS",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentSample.testVerdict,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSystemInDarkTheme()) Color(0xFFA5D6A7) else Color(0xFF1B5E20)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = currentSample.explanation,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
