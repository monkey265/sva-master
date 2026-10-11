package com.svamaster.model

data class SvaPracticeChallenge(
    val id: String,
    val prompt: String,                         // e.g. "Verify that valid and ready are both at 1 for 2 clock cycles"
    val targetSva: String,                      // Canonical SVA solution: "(valid && ready) [*2]"
    val acceptableAnswers: List<String>,        // Normalized variants that are also strictly correct
    val tokens: List<String>,                   // Token tiles for construction: ["(", "valid", "&&", "ready", ")", "[*2]"]
    val distractors: List<String> = emptyList(), // Extra distractor tiles (e.g. ["|->", "[->2]"])
    val explanation: String,                    // Why this SVA expression works
    val category: SvaCategory,
    val difficulty: String = "Medium"           // "Basic", "Medium", "Advanced"
)
