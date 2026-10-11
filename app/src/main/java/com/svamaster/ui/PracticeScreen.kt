package com.svamaster.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.svamaster.data.SvaRepository
import com.svamaster.model.SvaPracticeChallenge
import com.svamaster.ui.components.SvaCodeBlock

enum class PracticeInputMode {
    TYPE_SYNTAX,
    TOKEN_ASSEMBLY
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PracticeScreen() {
    val allChallenges = remember { SvaRepository.challenges }
    var currentChallengeIndex by remember { mutableIntStateOf(0) }
    var inputMode by remember { mutableStateOf(PracticeInputMode.TYPE_SYNTAX) }
    var typedCode by remember { mutableStateOf("") }
    var checkVerdict by remember { mutableStateOf<Boolean?>(null) }
    var solvedCount by remember { mutableIntStateOf(0) }
    var showHint by remember { mutableStateOf(false) }

    val currentChallenge = allChallenges[currentChallengeIndex]

    // Token Assembly State
    val selectedTokens = remember(currentChallengeIndex) { mutableStateListOf<Pair<Int, String>>() }
    val poolTokens = remember(currentChallengeIndex, currentChallenge) {
        val combined = (currentChallenge.tokens + currentChallenge.distractors).shuffled()
        mutableStateListOf<Pair<Int, String>>().apply {
            combined.forEachIndexed { i, token -> add(Pair(i, token)) }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Progress & Stats Header
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${currentChallengeIndex + 1}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Challenge ${currentChallengeIndex + 1} of ${allChallenges.size}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = "Solved: $solvedCount / ${allChallenges.size}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { (currentChallengeIndex + 1).toFloat() / allChallenges.size.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
        }

        // Challenge Prompt Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
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
                                text = "${currentChallenge.category.icon} ${currentChallenge.category.title}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (currentChallenge.difficulty) {
                                "Basic" -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                                "Medium" -> Color(0xFFFF9800).copy(alpha = 0.2f)
                                else -> Color(0xFFF44336).copy(alpha = 0.2f)
                            }
                        ) {
                            Text(
                                text = currentChallenge.difficulty,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = when (currentChallenge.difficulty) {
                                    "Basic" -> Color(0xFF2E7D32)
                                    "Medium" -> Color(0xFFEF6C00)
                                    else -> Color(0xFFC62828)
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "REQUIREMENT / SCENARIO",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentChallenge.prompt,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 24.sp
                    )
                }
            }
        }

        // Mode Selector: Type Syntax vs Token Assembly (Langobard style)
        item {
            PrimaryTabRow(
                selectedTabIndex = if (inputMode == PracticeInputMode.TYPE_SYNTAX) 0 else 1,
                containerColor = Color.Transparent
            ) {
                Tab(
                    selected = inputMode == PracticeInputMode.TYPE_SYNTAX,
                    onClick = {
                        inputMode = PracticeInputMode.TYPE_SYNTAX
                        checkVerdict = null
                    },
                    text = { Text("⌨️ Type SVA Syntax") }
                )
                Tab(
                    selected = inputMode == PracticeInputMode.TOKEN_ASSEMBLY,
                    onClick = {
                        inputMode = PracticeInputMode.TOKEN_ASSEMBLY
                        checkVerdict = null
                    },
                    text = { Text("🧩 Assemble Tiles") }
                )
            }
        }

        // Interactive Construction Canvas
        if (inputMode == PracticeInputMode.TYPE_SYNTAX) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = typedCode,
                        onValueChange = {
                            typedCode = it
                            checkVerdict = null
                        },
                        placeholder = { Text("e.g. (valid && ready) [*2]") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = false,
                        maxLines = 4,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                        )
                    )
                }
            }
        } else {
            // Token Assembly Canvas (Langobard drop & pool UI)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "YOUR SVA ASSEMBLY",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp)
                        ) {
                            if (selectedTokens.isEmpty()) {
                                Text(
                                    text = "Tap tokens below to construct SVA expression",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            } else {
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    selectedTokens.forEach { tokenPair ->
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.clickable {
                                                if (checkVerdict != true) {
                                                    selectedTokens.remove(tokenPair)
                                                    poolTokens.add(tokenPair)
                                                    checkVerdict = null
                                                }
                                            }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Text(
                                                    text = tokenPair.second,
                                                    fontFamily = FontFamily.Monospace,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onPrimary
                                                )
                                                Text(
                                                    text = "✕",
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Text(
                        text = "AVAILABLE TILES",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        poolTokens.forEach { tokenPair ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier.clickable {
                                    if (checkVerdict != true) {
                                        poolTokens.remove(tokenPair)
                                        selectedTokens.add(tokenPair)
                                        checkVerdict = null
                                    }
                                }
                            ) {
                                Text(
                                    text = tokenPair.second,
                                    fontFamily = FontFamily.Monospace,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Feedback & Verdict Banner
        item {
            AnimatedVisibility(visible = checkVerdict != null) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (checkVerdict == true)
                            Color(0xFF4CAF50).copy(alpha = 0.15f)
                        else
                            Color(0xFFF44336).copy(alpha = 0.15f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (checkVerdict == true) "🎉 Valid SVA Match!" else "❌ Syntax Mismatch",
                                fontWeight = FontWeight.Bold,
                                color = if (checkVerdict == true) Color(0xFF2E7D32) else Color(0xFFC62828),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (checkVerdict == true) {
                                "Expected: ${currentChallenge.targetSva}\n\n${currentChallenge.explanation}"
                            } else {
                                "Try again or tap 'Show SVA Hint' below for canonical IEEE 1800 syntax."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }

        // Canonical Hint
        item {
            AnimatedVisibility(visible = showHint) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "💡 Canonical SVA Solution:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        SvaCodeBlock(code = currentChallenge.targetSva)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = currentChallenge.explanation,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Action Buttons (Verify Answer, Hint, Next)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { showHint = !showHint },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (showHint) "Hide Hint" else "💡 Hint")
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            val userAssembly = if (inputMode == PracticeInputMode.TYPE_SYNTAX) {
                                typedCode
                            } else {
                                selectedTokens.joinToString(" ") { it.second }
                            }

                            val normalizedUser = normalizeSva(userAssembly)
                            val isMatch = currentChallenge.acceptableAnswers.any {
                                normalizeSva(it) == normalizedUser
                            }

                            checkVerdict = isMatch
                            if (isMatch) {
                                solvedCount++
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Verify SVA")
                    }

                    Button(
                        onClick = {
                            if (currentChallengeIndex < allChallenges.size - 1) {
                                currentChallengeIndex++
                            } else {
                                currentChallengeIndex = 0
                            }
                            typedCode = ""
                            checkVerdict = null
                            showHint = false
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Next →")
                    }
                }
            }
        }
    }
}

fun normalizeSva(raw: String): String {
    return raw.replace("\\s+".toRegex(), "")
        .replace("1'b0", "0")
        .replace("1'b1", "1")
        .trim()
}
