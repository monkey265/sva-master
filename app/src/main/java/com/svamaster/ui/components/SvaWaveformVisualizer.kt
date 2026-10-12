package com.svamaster.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.svamaster.model.VerdictStatus
import com.svamaster.model.WaveformScenario
import com.svamaster.model.WaveformSignal
import com.svamaster.model.WaveformVerdict
import kotlin.random.Random

@Composable
fun SvaWaveformVisualizer(
    scenario: WaveformScenario,
    modifier: Modifier = Modifier,
    onVerdictChange: ((WaveformVerdict) -> Unit)? = null
) {
    val haptic = LocalHapticFeedback.current

    // Signal state editable per signal name -> List<Int>
    val signalStates = remember(scenario.id) {
        mutableStateMapOf<String, List<Int>>().apply {
            scenario.signals.forEach { sig ->
                put(sig.name, sig.values.toList())
            }
        }
    }

    var selectedCycle by remember(scenario.id) { mutableIntStateOf(scenario.antecedentCycle ?: 0) }

    // Re-evaluate assertion whenever signal states change
    val currentVerdict = remember(signalStates.toMap(), scenario) {
        scenario.evaluate(signalStates.toMap())
    }

    LaunchedEffect(currentVerdict) {
        onVerdictChange?.invoke(currentVerdict)
    }

    val numCycles = scenario.numCycles
    val cycleWidth = 48.dp
    val rowHeight = 44.dp
    val labelWidth = 84.dp

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Scenario Info & Assertion
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${scenario.category.icon} ${scenario.title}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (currentVerdict.status) {
                        VerdictStatus.PASS -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                        VerdictStatus.FAIL -> Color(0xFFF44336).copy(alpha = 0.2f)
                        VerdictStatus.VACUOUS -> Color(0xFF9C27B0).copy(alpha = 0.2f)
                    }
                ) {
                    Text(
                        text = when (currentVerdict.status) {
                            VerdictStatus.PASS -> "PASS"
                            VerdictStatus.FAIL -> "VIOLATION"
                            VerdictStatus.VACUOUS -> "VACUOUS"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = when (currentVerdict.status) {
                            VerdictStatus.PASS -> Color(0xFF2E7D32)
                            VerdictStatus.FAIL -> Color(0xFFC62828)
                            VerdictStatus.VACUOUS -> Color(0xFF7B1FA2)
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Assertion expression display
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF0F172A),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = scenario.assertionExpr,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = Color(0xFF38BDF8),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Waveform Interactive Display Area
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFF090D16),
                border = BorderStroke(1.dp, Color(0xFF1E293B)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    // Left Column: Signal Names
                    Column(
                        modifier = Modifier
                            .width(labelWidth)
                            .padding(vertical = 6.dp)
                    ) {
                        // Header spacer matching cycle timeline
                        Box(
                            modifier = Modifier
                                .height(28.dp)
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = "SIGNAL",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B)
                            )
                        }

                        // Each signal label
                        scenario.signals.forEach { sig ->
                            Box(
                                modifier = Modifier
                                    .height(rowHeight)
                                    .padding(horizontal = 8.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (sig.isClock) Color(0xFF38BDF8) else Color(0xFF10B981),
                                        modifier = Modifier.size(6.dp)
                                    ) {}
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = sig.name,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFF1F5F9),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    // Right Scrollable Canvas Area
                    val scrollState = rememberScrollState()
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .horizontalScroll(scrollState)
                            .padding(vertical = 6.dp)
                    ) {
                        Column {
                            // Cycle Header Row (C0, C1, ... C7)
                            Row(modifier = Modifier.height(28.dp)) {
                                (0 until numCycles).forEach { c ->
                                    val isSelected = selectedCycle == c
                                    val isTrigger = scenario.antecedentCycle == c
                                    val isConsequent = scenario.consequentMinCycle != null &&
                                        scenario.consequentMaxCycle != null &&
                                        c in scenario.consequentMinCycle..scenario.consequentMaxCycle

                                    Box(
                                        modifier = Modifier
                                            .width(cycleWidth)
                                            .height(28.dp)
                                            .clickable {
                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                selectedCycle = c
                                            }
                                            .background(
                                                when {
                                                    isSelected -> Color(0xFF334155).copy(alpha = 0.6f)
                                                    isTrigger -> Color(0xFFF59E0B).copy(alpha = 0.2f)
                                                    isConsequent -> Color(0xFF38BDF8).copy(alpha = 0.15f)
                                                    else -> Color.Transparent
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "C$c",
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected || isTrigger) FontWeight.Bold else FontWeight.Medium,
                                                color = when {
                                                    isSelected -> Color(0xFFF8FAFC)
                                                    isTrigger -> Color(0xFFFBBF24)
                                                    isConsequent -> Color(0xFF38BDF8)
                                                    else -> Color(0xFF94A3B8)
                                                }
                                            )
                                            Text(
                                                text = "▲",
                                                fontSize = 7.sp,
                                                color = Color(0xFF38BDF8)
                                            )
                                        }
                                    }
                                }
                            }

                            // Signals Rows (Waveform Canvas & Tap Toggle Targets)
                            scenario.signals.forEach { sig ->
                                val currentValues = signalStates[sig.name] ?: sig.values

                                Box(
                                    modifier = Modifier
                                        .height(rowHeight)
                                        .width(cycleWidth * numCycles)
                                ) {
                                    // Custom Waveform Drawing
                                    Canvas(
                                        modifier = Modifier
                                            .matchParentSize()
                                    ) {
                                        drawSignalWave(
                                            signal = sig,
                                            values = currentValues,
                                            numCycles = numCycles,
                                            cycleWidthPx = cycleWidth.toPx(),
                                            rowHeightPx = rowHeight.toPx(),
                                            selectedCycle = selectedCycle,
                                            triggerCycle = scenario.antecedentCycle,
                                            matchCycle = currentVerdict.matchCycle,
                                            failCycle = currentVerdict.failCycle
                                        )
                                    }

                                    // Interactive Tap Layer: Each Cycle is a Clickable Toggle
                                    Row(modifier = Modifier.matchParentSize()) {
                                        (0 until numCycles).forEach { c ->
                                            Box(
                                                modifier = Modifier
                                                    .width(cycleWidth)
                                                    .height(rowHeight)
                                                    .clickable {
                                                        if (!sig.isClock) {
                                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                            val updated = currentValues.toMutableList()
                                                            val currentVal = updated.getOrElse(c) { 0 }
                                                            updated[c] = if (currentVal == 1) 0 else 1
                                                            signalStates[sig.name] = updated
                                                            selectedCycle = c
                                                        }
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                val bit = currentValues.getOrElse(c) { 0 }
                                                if (!sig.isClock && !sig.isBus) {
                                                    Surface(
                                                        shape = CircleShape,
                                                        color = if (bit == 1) Color(0xFF10B981).copy(alpha = 0.25f) else Color.Transparent,
                                                        modifier = Modifier.size(18.dp)
                                                    ) {
                                                        Box(contentAlignment = Alignment.Center) {
                                                            Text(
                                                                text = "$bit",
                                                                fontSize = 10.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                fontFamily = FontFamily.Monospace,
                                                                color = if (bit == 1) Color(0xFF34D399) else Color(0xFF64748B)
                                                            )
                                                        }
                                                    }
                                                } else if (sig.isBus) {
                                                    val hex = sig.busHexValues.getOrElse(c) { if (bit == 1) "0xA5" else "0x00" }
                                                    Text(
                                                        text = hex,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        fontFamily = FontFamily.Monospace,
                                                        color = if (bit == 1) Color(0xFFA78BFA) else Color(0xFF64748B)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Live Temporal Verdict Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (currentVerdict.status) {
                        VerdictStatus.PASS -> Color(0xFF2E7D32).copy(alpha = 0.15f)
                        VerdictStatus.FAIL -> Color(0xFFC62828).copy(alpha = 0.15f)
                        VerdictStatus.VACUOUS -> Color(0xFF6A1B9A).copy(alpha = 0.15f)
                    }
                ),
                border = BorderStroke(
                    1.dp,
                    when (currentVerdict.status) {
                        VerdictStatus.PASS -> Color(0xFF4CAF50)
                        VerdictStatus.FAIL -> Color(0xFFE53935)
                        VerdictStatus.VACUOUS -> Color(0xFFAB47BC)
                    }
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = when (currentVerdict.status) {
                                VerdictStatus.PASS -> "✅"
                                VerdictStatus.FAIL -> "❌"
                                VerdictStatus.VACUOUS -> "⚪"
                            },
                            fontSize = 14.sp
                        )
                        Text(
                            text = currentVerdict.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = when (currentVerdict.status) {
                                VerdictStatus.PASS -> Color(0xFF2E7D32)
                                VerdictStatus.FAIL -> Color(0xFFC62828)
                                VerdictStatus.VACUOUS -> Color(0xFF7B1FA2)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentVerdict.explanation,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Interactive Controls: Randomize Trace, Reset to Golden Trace, Tap Hint
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "💡 Tap any bit to toggle trace",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            scenario.signals.forEach { sig ->
                                signalStates[sig.name] = sig.values.toList()
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Reset", fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            scenario.signals.forEach { sig ->
                                if (!sig.isClock) {
                                    val randomized = List(numCycles) { Random.nextInt(0, 2) }
                                    signalStates[sig.name] = randomized
                                }
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("🎲 Random Trace", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawSignalWave(
    signal: WaveformSignal,
    values: List<Int>,
    numCycles: Int,
    cycleWidthPx: Float,
    rowHeightPx: Float,
    selectedCycle: Int,
    triggerCycle: Int?,
    matchCycle: Int?,
    failCycle: Int?
) {
    val yTop = 6f
    val yBottom = rowHeightPx - 8f
    val yMid = (yTop + yBottom) / 2f
    val wavePath = Path()

    val waveColor = if (signal.isClock) Color(0xFF38BDF8) else Color(0xFF10B981)
    val strokeWidth = 2.5f

    // Draw background column tint for active annotations
    for (c in 0 until numCycles) {
        val xStart = c * cycleWidthPx
        if (triggerCycle == c) {
            drawRect(
                color = Color(0xFFF59E0B).copy(alpha = 0.08f),
                topLeft = Offset(xStart, 0f),
                size = Size(cycleWidthPx, rowHeightPx)
            )
        }
        if (matchCycle == c) {
            drawRect(
                color = Color(0xFF10B981).copy(alpha = 0.12f),
                topLeft = Offset(xStart, 0f),
                size = Size(cycleWidthPx, rowHeightPx)
            )
        }
        if (failCycle == c) {
            drawRect(
                color = Color(0xFFEF4444).copy(alpha = 0.12f),
                topLeft = Offset(xStart, 0f),
                size = Size(cycleWidthPx, rowHeightPx)
            )
        }
    }

    if (signal.isClock) {
        // Draw oscillating square clock wave
        for (c in 0 until numCycles) {
            val xStart = c * cycleWidthPx
            val xHalf = xStart + cycleWidthPx / 2f
            val xEnd = (c + 1) * cycleWidthPx

            if (c == 0) {
                wavePath.moveTo(xStart, yBottom)
            }

            // Posedge transition
            wavePath.lineTo(xStart, yTop)
            // High level
            wavePath.lineTo(xHalf, yTop)
            // Negedge transition
            wavePath.lineTo(xHalf, yBottom)
            // Low level
            wavePath.lineTo(xEnd, yBottom)
        }
        drawPath(
            path = wavePath,
            color = waveColor,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )
    } else if (signal.isBus) {
        // Draw bus crossover envelopes
        for (c in 0 until numCycles) {
            val xStart = c * cycleWidthPx
            val xEnd = (c + 1) * cycleWidthPx
            val bit = values.getOrElse(c) { 0 }
            val busColor = if (bit == 1) Color(0xFFA78BFA) else Color(0xFF64748B)

            // Top rail
            drawLine(
                color = busColor,
                start = Offset(xStart + 4f, yTop),
                end = Offset(xEnd - 4f, yTop),
                strokeWidth = 2f
            )
            // Bottom rail
            drawLine(
                color = busColor,
                start = Offset(xStart + 4f, yBottom),
                end = Offset(xEnd - 4f, yBottom),
                strokeWidth = 2f
            )
            // Transition crossover lines
            drawLine(
                color = busColor,
                start = Offset(xStart - 4f, yTop),
                end = Offset(xStart + 4f, yBottom),
                strokeWidth = 1.5f
            )
            drawLine(
                color = busColor,
                start = Offset(xStart - 4f, yBottom),
                end = Offset(xStart + 4f, yTop),
                strokeWidth = 1.5f
            )
        }
    } else {
        // Digital 1-bit waveform
        for (c in 0 until numCycles) {
            val xStart = c * cycleWidthPx
            val xEnd = (c + 1) * cycleWidthPx
            val bit = values.getOrElse(c) { 0 }
            val prevBit = if (c > 0) values.getOrElse(c - 1) { 0 } else bit

            val targetY = if (bit == 1) yTop else yBottom

            if (c == 0) {
                wavePath.moveTo(xStart, targetY)
            } else if (bit != prevBit) {
                // Vertical edge transition
                wavePath.lineTo(xStart, targetY)
            }

            // Horizontal level line
            wavePath.lineTo(xEnd, targetY)
        }

        drawPath(
            path = wavePath,
            color = waveColor,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Draw soft shaded fill under high level
        for (c in 0 until numCycles) {
            val bit = values.getOrElse(c) { 0 }
            if (bit == 1) {
                drawRect(
                    color = Color(0xFF10B981).copy(alpha = 0.10f),
                    topLeft = Offset(c * cycleWidthPx, yTop),
                    size = Size(cycleWidthPx, yBottom - yTop)
                )
            }
        }
    }
}
