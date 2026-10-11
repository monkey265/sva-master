package com.svamaster.model

data class SvaPracticeChallenge(
    val id: String,
    val prompt: String,                         // e.g. "Verify that valid and ready are both at 1 for 2 clock cycles"
    val targetSva: String,                      // Core expression or full SVA
    val acceptableAnswers: List<String>,        // Normalized variants that are also strictly correct
    val tokens: List<String>,                   // Token tiles for construction
    val distractors: List<String> = emptyList(), // Extra distractor tiles
    val explanation: String,                    // Why this SVA expression works
    val category: SvaCategory,
    val difficulty: String = "Medium",          // "Basic", "Medium", "Advanced"
    val fullPropertyDefinition: String? = null, // e.g. "property p_valid_hold;\n  @(posedge clk) disable iff (!rst_n)\n  (valid && !ready) |=> (valid && $stable(data));\nendproperty"
    val assertionDirective: String? = null      // e.g. "assert property (p_valid_hold);"
)
