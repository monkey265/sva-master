package com.svamaster.model

enum class SvaCategory(val title: String, val icon: String, val description: String) {
    DIRECTIVES(
        title = "Directives Overview",
        icon = "🎯",
        description = "assert, cover, assume, restrict - Dynamic vs Formal behaviors"
    ),
    ANATOMY(
        title = "Anatomy & Controls",
        icon = "⚙️",
        description = "Clocks, resets, disable iff, and assertion structure"
    ),
    IMPLICATION(
        title = "Implication & Branching",
        icon = "➡️",
        description = "Overlapping |->, non-overlapping |=>, conditional, iff, and logical combos"
    ),
    LTL(
        title = "LTL Operators",
        icon = "⏳",
        description = "Linear Temporal Logic: always, s_eventually, until, nexttime (Weak vs Strong)"
    ),
    ABORTS(
        title = "Aborts & Rejections",
        icon = "🛑",
        description = "accept_on, reject_on, sync_accept_on, sync_reject_on"
    ),
    SEQUENCES(
        title = "Sequences & Repetitions",
        icon = "🔁",
        description = "Delays ##N, [*N], [->N], [=N], intersect, throughout, within, first_match"
    ),
    SYSTEM_FUNCTIONS(
        title = "Sampled Value Functions",
        icon = "🔬",
        description = "\$past, \$rose, \$fell, \$stable, \$onehot, \$countones, \$isunknown"
    ),
    PROTOCOL_PACKS(
        title = "Protocol Packs",
        icon = "⚡",
        description = "AXI4, APB4, AHB5, FIFO, and Skid-Buffer interface assertions"
    )
}

data class SvaItem(
    val id: String,
    val category: SvaCategory,
    val name: String,
    val syntax: String,
    val summary: String,
    val dynamicBehavior: String? = null,
    val formalBehavior: String? = null,
    val fullExplanation: String,
    val codeExample: String,
    val waveDiagram: String? = null,
    val keyTips: List<String> = emptyList()
)

data class SvaQuizQuestion(
    val id: String,
    val question: String,
    val codeSnippet: String? = null,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val category: SvaCategory
)

data class SvaSandboxSample(
    val title: String,
    val description: String,
    val code: String,
    val testVerdict: String,
    val explanation: String
)

data class SvaBadge(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val targetCount: Int,
    val getProgress: (solvedIds: Set<String>, quizScore: Int, challenges: List<SvaPracticeChallenge>) -> Int,
    val isUnlocked: (solvedIds: Set<String>, quizScore: Int, challenges: List<SvaPracticeChallenge>) -> Boolean
)
