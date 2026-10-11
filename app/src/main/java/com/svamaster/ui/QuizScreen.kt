package com.svamaster.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.svamaster.data.SvaRepository
import com.svamaster.model.SvaQuizQuestion
import com.svamaster.ui.components.SvaCodeBlock

@Composable
fun QuizScreen() {
    val questions = remember { SvaRepository.quizQuestions }
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var isCompleted by remember { mutableStateOf(false) }

    val currentQuestion = questions[currentIndex]

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (isCompleted) {
            item {
                QuizCompletionCard(
                    score = score,
                    total = questions.size,
                    onRestart = {
                        currentIndex = 0
                        selectedOption = null
                        isSubmitted = false
                        score = 0
                        isCompleted = false
                    }
                )
            }
        } else {
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Question ${currentIndex + 1} of ${questions.size}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Score: $score",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { (currentIndex + 1).toFloat() / questions.size.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }
            }

            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                        ) {
                            Text(
                                text = "${currentQuestion.category.icon} ${currentQuestion.category.title}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = currentQuestion.question,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 22.sp
                        )

                        if (currentQuestion.codeSnippet != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            SvaCodeBlock(code = currentQuestion.codeSnippet)
                        }
                    }
                }
            }

            items(currentQuestion.options.size) { optIdx ->
                val optionText = currentQuestion.options[optIdx]
                val isSelected = selectedOption == optIdx
                val isCorrect = optIdx == currentQuestion.correctIndex

                val borderColor = when {
                    !isSubmitted && isSelected -> MaterialTheme.colorScheme.primary
                    isSubmitted && isCorrect -> Color(0xFF4CAF50)
                    isSubmitted && isSelected && !isCorrect -> Color(0xFFF44336)
                    else -> MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                }

                val containerColor = when {
                    !isSubmitted && isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    isSubmitted && isCorrect -> Color(0xFF4CAF50).copy(alpha = 0.15f)
                    isSubmitted && isSelected && !isCorrect -> Color(0xFFF44336).copy(alpha = 0.15f)
                    else -> MaterialTheme.colorScheme.surface
                }

                Card(
                    onClick = {
                        if (!isSubmitted) {
                            selectedOption = optIdx
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.5.dp, borderColor),
                    colors = CardDefaults.cardColors(containerColor = containerColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = ('A'.code + optIdx).toChar().toString(),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = optionText,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                AnimatedVisibility(visible = isSubmitted) {
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedOption == currentQuestion.correctIndex)
                                Color(0xFF4CAF50).copy(alpha = 0.12f)
                            else
                                Color(0xFFF44336).copy(alpha = 0.12f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (selectedOption == currentQuestion.correctIndex) "✓ Correct!" else "✗ Incorrect",
                                fontWeight = FontWeight.Bold,
                                color = if (selectedOption == currentQuestion.correctIndex) Color(0xFF2E7D32) else Color(0xFFC62828),
                                style = MaterialTheme.typography.titleSmall
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = currentQuestion.explanation,
                                style = MaterialTheme.typography.bodySmall,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (!isSubmitted) {
                        Button(
                            onClick = {
                                if (selectedOption != null) {
                                    isSubmitted = true
                                    if (selectedOption == currentQuestion.correctIndex) {
                                        score++
                                    }
                                }
                            },
                            enabled = selectedOption != null,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Verify Answer")
                        }
                    } else {
                        Button(
                            onClick = {
                                if (currentIndex < questions.size - 1) {
                                    currentIndex++
                                    selectedOption = null
                                    isSubmitted = false
                                } else {
                                    isCompleted = true
                                }
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(if (currentIndex < questions.size - 1) "Next Question →" else "View Results")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuizCompletionCard(score: Int, total: Int, onRestart: () -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🎉", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "SVA Mastery Quiz Complete!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Your Score: $score / $total (${(score.toFloat() / total * 100).toInt()}%)",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (score == total)
                    "Flawless! You possess complete mastery of IEEE 1800 SystemVerilog assertions!"
                else
                    "Great practice! Review the reference and cheatsheet tabs to hone weak areas.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onRestart,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Restart Quiz")
            }
        }
    }
}
