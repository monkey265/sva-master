package com.svamaster.data.challenges

import com.svamaster.model.SvaCategory
import com.svamaster.model.SvaPracticeChallenge

object SvaChallengesPart2 {
    val list: List<SvaPracticeChallenge> = listOf(
        SvaPracticeChallenge(
            id = "sva_chal_061",
            prompt = "Verify that ready holds low for exactly 36 consecutive cycles after stall",
            targetSva = "stall |=> !ready [*36]",
            acceptableAnswers = listOf("stall |=> (!ready) [*36]", "stall |=> !ready [*36]", "stall |-> ##1 !ready [*36]"),
            tokens = listOf("stall", "|=>", "!ready", "[*36]"),
            distractors = listOf("|->", "[->36]", "[=36]"),
            explanation = "Consecutive repetition [*36] ensures !ready is sampled for 36 cycles after stall.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_062",
            prompt = "When tx_req is asserted, tx_ack must arrive after exactly 37 clock cycles",
            targetSva = "tx_req |-> ##37 tx_ack",
            acceptableAnswers = listOf("tx_req |-> ##37 tx_ack", "tx_req |=> ##36 tx_ack"),
            tokens = listOf("tx_req", "|->", "##37", "tx_ack"),
            distractors = listOf("|=>", "[*37]", "##38"),
            explanation = "Fixed delay ##37 requires tx_ack exactly 37 cycles following tx_req.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_063",
            prompt = "Verify that heartbeat changed value from previous cycle at tick 38",
            targetSva = "\$changed(heartbeat)",
            acceptableAnswers = listOf("\$past(heartbeat) != heartbeat", "\$changed(heartbeat)", "heartbeat != \$past(heartbeat)"),
            tokens = listOf("\$changed(heartbeat)"),
            distractors = listOf("\$stable(heartbeat)", "\$rose(heartbeat)", "\$fell(heartbeat)"),
            explanation = "\$changed returns 1 if signal is not equal to its sampled value from the prior cycle.",
            category = SvaCategory.SYSTEM_FUNCTIONS,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_064",
            prompt = "Event trigger must happen 39 times non-consecutively before finish, allowing extra cycles after",
            targetSva = "trigger [=39] ##1 finish",
            acceptableAnswers = listOf("trigger [=39] ##1 finish", "(trigger [=39]) ##1 finish"),
            tokens = listOf("trigger", "[=39]", "##1", "finish"),
            distractors = listOf("[->39]", "[*39]", "|=>"),
            explanation = "Non-consecutive repetition [=39] allows delay after the 39-th occurrence before finish.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Advanced"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_065",
            prompt = "Ensure sub-sequence sub_seq_40 occurs entirely within main_frame_40",
            targetSva = "sub_seq_40 within main_frame_40",
            acceptableAnswers = listOf("(sub_seq_40) within (main_frame_40)", "sub_seq_40 within main_frame_40"),
            tokens = listOf("sub_seq_40", "within", "main_frame_40"),
            distractors = listOf("throughout", "intersect", "and"),
            explanation = "within operator checks that sub-sequence occurs within the timeframe of main sequence.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Advanced"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_066",
            prompt = "Constrain formal input in_data_1 to remain stable when in_valid_1 is asserted",
            targetSva = "in_valid_1 |-> \$stable(in_data_1)",
            acceptableAnswers = listOf("(in_valid_1) |-> \$stable(in_data_1)", "in_valid_1 |-> \$stable(in_data_1)"),
            tokens = listOf("in_valid_1", "|->", "\$stable(in_data_1)"),
            distractors = listOf("|=>", "\$changed(in_data)", "always"),
            explanation = "Environmental assumption/check: in_data_1 must not glitch when in_valid_1 is high.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_067",
            prompt = "Cover scenario where burst length is 2 cycles followed by eop",
            targetSva = "burst_en [* 2] ##1 eop",
            acceptableAnswers = listOf("(burst_en [*2]) ##1 eop", "burst_en [* 2] ##1 eop", "burst_en [*2] ##1 eop"),
            tokens = listOf("burst_en", "[*2]", "##1", "eop"),
            distractors = listOf("[->2]", "[=2]", "|->"),
            explanation = "Cover property sequence tracking witness trace for 2 bursts ending with eop.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_068",
            prompt = "Verify that fifo_count_3 never exceeds threshold of 19",
            targetSva = "fifo_count_3 <= 19",
            acceptableAnswers = listOf("fifo_count_3 <= 19", "fifo_count_3 < 20", "(fifo_count_3 <= 19)"),
            tokens = listOf("fifo_count_3", "<=", "19"),
            distractors = listOf(">", "==", "##1"),
            explanation = "Invariance safety check ensuring FIFO level doesn't overflow limit of 19.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_069",
            prompt = "Prune formal search space to enforce mode_4 inside valid configuration set {0, 1, 3}",
            targetSva = "mode_4 inside {0, 1, 3}",
            acceptableAnswers = listOf("mode_4 inside {0, 1, 3}", "(mode_4 inside {0, 1, 3})"),
            tokens = listOf("mode_4", "inside", "{0, 1, 3}"),
            distractors = listOf("==", "!=", "intersect"),
            explanation = "Restrict property operator constraining formal verification solver exploration.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_070",
            prompt = "Constrain formal input in_data_5 to remain stable when in_valid_5 is asserted",
            targetSva = "in_valid_5 |-> \$stable(in_data_5)",
            acceptableAnswers = listOf("(in_valid_5) |-> \$stable(in_data_5)", "in_valid_5 |-> \$stable(in_data_5)"),
            tokens = listOf("in_valid_5", "|->", "\$stable(in_data_5)"),
            distractors = listOf("|=>", "\$changed(in_data)", "always"),
            explanation = "Environmental assumption/check: in_data_5 must not glitch when in_valid_5 is high.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_071",
            prompt = "Cover scenario where burst length is 6 cycles followed by eop",
            targetSva = "burst_en [* 6] ##1 eop",
            acceptableAnswers = listOf("burst_en [*6] ##1 eop", "(burst_en [*6]) ##1 eop", "burst_en [* 6] ##1 eop"),
            tokens = listOf("burst_en", "[*6]", "##1", "eop"),
            distractors = listOf("[->6]", "[=6]", "|->"),
            explanation = "Cover property sequence tracking witness trace for 6 bursts ending with eop.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_072",
            prompt = "Verify that fifo_count_7 never exceeds threshold of 23",
            targetSva = "fifo_count_7 <= 23",
            acceptableAnswers = listOf("fifo_count_7 <= 23", "(fifo_count_7 <= 23)", "fifo_count_7 < 24"),
            tokens = listOf("fifo_count_7", "<=", "23"),
            distractors = listOf(">", "==", "##1"),
            explanation = "Invariance safety check ensuring FIFO level doesn't overflow limit of 23.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_073",
            prompt = "Prune formal search space to enforce mode_8 inside valid configuration set {0, 1, 3}",
            targetSva = "mode_8 inside {0, 1, 3}",
            acceptableAnswers = listOf("mode_8 inside {0, 1, 3}", "(mode_8 inside {0, 1, 3})"),
            tokens = listOf("mode_8", "inside", "{0, 1, 3}"),
            distractors = listOf("==", "!=", "intersect"),
            explanation = "Restrict property operator constraining formal verification solver exploration.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_074",
            prompt = "Constrain formal input in_data_9 to remain stable when in_valid_9 is asserted",
            targetSva = "in_valid_9 |-> \$stable(in_data_9)",
            acceptableAnswers = listOf("in_valid_9 |-> \$stable(in_data_9)", "(in_valid_9) |-> \$stable(in_data_9)"),
            tokens = listOf("in_valid_9", "|->", "\$stable(in_data_9)"),
            distractors = listOf("|=>", "\$changed(in_data)", "always"),
            explanation = "Environmental assumption/check: in_data_9 must not glitch when in_valid_9 is high.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_075",
            prompt = "Cover scenario where burst length is 10 cycles followed by eop",
            targetSva = "burst_en [* 10] ##1 eop",
            acceptableAnswers = listOf("burst_en [* 10] ##1 eop", "(burst_en [*10]) ##1 eop", "burst_en [*10] ##1 eop"),
            tokens = listOf("burst_en", "[*10]", "##1", "eop"),
            distractors = listOf("[->10]", "[=10]", "|->"),
            explanation = "Cover property sequence tracking witness trace for 10 bursts ending with eop.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_076",
            prompt = "Verify that fifo_count_11 never exceeds threshold of 27",
            targetSva = "fifo_count_11 <= 27",
            acceptableAnswers = listOf("fifo_count_11 < 28", "fifo_count_11 <= 27", "(fifo_count_11 <= 27)"),
            tokens = listOf("fifo_count_11", "<=", "27"),
            distractors = listOf(">", "==", "##1"),
            explanation = "Invariance safety check ensuring FIFO level doesn't overflow limit of 27.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_077",
            prompt = "Prune formal search space to enforce mode_12 inside valid configuration set {0, 1, 3}",
            targetSva = "mode_12 inside {0, 1, 3}",
            acceptableAnswers = listOf("mode_12 inside {0, 1, 3}", "(mode_12 inside {0, 1, 3})"),
            tokens = listOf("mode_12", "inside", "{0, 1, 3}"),
            distractors = listOf("==", "!=", "intersect"),
            explanation = "Restrict property operator constraining formal verification solver exploration.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_078",
            prompt = "Constrain formal input in_data_13 to remain stable when in_valid_13 is asserted",
            targetSva = "in_valid_13 |-> \$stable(in_data_13)",
            acceptableAnswers = listOf("in_valid_13 |-> \$stable(in_data_13)", "(in_valid_13) |-> \$stable(in_data_13)"),
            tokens = listOf("in_valid_13", "|->", "\$stable(in_data_13)"),
            distractors = listOf("|=>", "\$changed(in_data)", "always"),
            explanation = "Environmental assumption/check: in_data_13 must not glitch when in_valid_13 is high.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_079",
            prompt = "Cover scenario where burst length is 14 cycles followed by eop",
            targetSva = "burst_en [* 14] ##1 eop",
            acceptableAnswers = listOf("(burst_en [*14]) ##1 eop", "burst_en [* 14] ##1 eop", "burst_en [*14] ##1 eop"),
            tokens = listOf("burst_en", "[*14]", "##1", "eop"),
            distractors = listOf("[->14]", "[=14]", "|->"),
            explanation = "Cover property sequence tracking witness trace for 14 bursts ending with eop.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_080",
            prompt = "Verify that fifo_count_15 never exceeds threshold of 31",
            targetSva = "fifo_count_15 <= 31",
            acceptableAnswers = listOf("fifo_count_15 < 32", "(fifo_count_15 <= 31)", "fifo_count_15 <= 31"),
            tokens = listOf("fifo_count_15", "<=", "31"),
            distractors = listOf(">", "==", "##1"),
            explanation = "Invariance safety check ensuring FIFO level doesn't overflow limit of 31.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_081",
            prompt = "Prune formal search space to enforce mode_16 inside valid configuration set {0, 1, 3}",
            targetSva = "mode_16 inside {0, 1, 3}",
            acceptableAnswers = listOf("(mode_16 inside {0, 1, 3})", "mode_16 inside {0, 1, 3}"),
            tokens = listOf("mode_16", "inside", "{0, 1, 3}"),
            distractors = listOf("==", "!=", "intersect"),
            explanation = "Restrict property operator constraining formal verification solver exploration.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_082",
            prompt = "Constrain formal input in_data_17 to remain stable when in_valid_17 is asserted",
            targetSva = "in_valid_17 |-> \$stable(in_data_17)",
            acceptableAnswers = listOf("(in_valid_17) |-> \$stable(in_data_17)", "in_valid_17 |-> \$stable(in_data_17)"),
            tokens = listOf("in_valid_17", "|->", "\$stable(in_data_17)"),
            distractors = listOf("|=>", "\$changed(in_data)", "always"),
            explanation = "Environmental assumption/check: in_data_17 must not glitch when in_valid_17 is high.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_083",
            prompt = "Cover scenario where burst length is 18 cycles followed by eop",
            targetSva = "burst_en [* 18] ##1 eop",
            acceptableAnswers = listOf("burst_en [* 18] ##1 eop", "(burst_en [*18]) ##1 eop", "burst_en [*18] ##1 eop"),
            tokens = listOf("burst_en", "[*18]", "##1", "eop"),
            distractors = listOf("[->18]", "[=18]", "|->"),
            explanation = "Cover property sequence tracking witness trace for 18 bursts ending with eop.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_084",
            prompt = "Verify that fifo_count_19 never exceeds threshold of 35",
            targetSva = "fifo_count_19 <= 35",
            acceptableAnswers = listOf("fifo_count_19 < 36", "(fifo_count_19 <= 35)", "fifo_count_19 <= 35"),
            tokens = listOf("fifo_count_19", "<=", "35"),
            distractors = listOf(">", "==", "##1"),
            explanation = "Invariance safety check ensuring FIFO level doesn't overflow limit of 35.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_085",
            prompt = "Prune formal search space to enforce mode_20 inside valid configuration set {0, 1, 3}",
            targetSva = "mode_20 inside {0, 1, 3}",
            acceptableAnswers = listOf("mode_20 inside {0, 1, 3}", "(mode_20 inside {0, 1, 3})"),
            tokens = listOf("mode_20", "inside", "{0, 1, 3}"),
            distractors = listOf("==", "!=", "intersect"),
            explanation = "Restrict property operator constraining formal verification solver exploration.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_086",
            prompt = "Constrain formal input in_data_21 to remain stable when in_valid_21 is asserted",
            targetSva = "in_valid_21 |-> \$stable(in_data_21)",
            acceptableAnswers = listOf("in_valid_21 |-> \$stable(in_data_21)", "(in_valid_21) |-> \$stable(in_data_21)"),
            tokens = listOf("in_valid_21", "|->", "\$stable(in_data_21)"),
            distractors = listOf("|=>", "\$changed(in_data)", "always"),
            explanation = "Environmental assumption/check: in_data_21 must not glitch when in_valid_21 is high.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_087",
            prompt = "Cover scenario where burst length is 22 cycles followed by eop",
            targetSva = "burst_en [* 22] ##1 eop",
            acceptableAnswers = listOf("(burst_en [*22]) ##1 eop", "burst_en [*22] ##1 eop", "burst_en [* 22] ##1 eop"),
            tokens = listOf("burst_en", "[*22]", "##1", "eop"),
            distractors = listOf("[->22]", "[=22]", "|->"),
            explanation = "Cover property sequence tracking witness trace for 22 bursts ending with eop.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_088",
            prompt = "Verify that fifo_count_23 never exceeds threshold of 39",
            targetSva = "fifo_count_23 <= 39",
            acceptableAnswers = listOf("fifo_count_23 < 40", "(fifo_count_23 <= 39)", "fifo_count_23 <= 39"),
            tokens = listOf("fifo_count_23", "<=", "39"),
            distractors = listOf(">", "==", "##1"),
            explanation = "Invariance safety check ensuring FIFO level doesn't overflow limit of 39.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_089",
            prompt = "Prune formal search space to enforce mode_24 inside valid configuration set {0, 1, 3}",
            targetSva = "mode_24 inside {0, 1, 3}",
            acceptableAnswers = listOf("(mode_24 inside {0, 1, 3})", "mode_24 inside {0, 1, 3}"),
            tokens = listOf("mode_24", "inside", "{0, 1, 3}"),
            distractors = listOf("==", "!=", "intersect"),
            explanation = "Restrict property operator constraining formal verification solver exploration.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_090",
            prompt = "Constrain formal input in_data_25 to remain stable when in_valid_25 is asserted",
            targetSva = "in_valid_25 |-> \$stable(in_data_25)",
            acceptableAnswers = listOf("in_valid_25 |-> \$stable(in_data_25)", "(in_valid_25) |-> \$stable(in_data_25)"),
            tokens = listOf("in_valid_25", "|->", "\$stable(in_data_25)"),
            distractors = listOf("|=>", "\$changed(in_data)", "always"),
            explanation = "Environmental assumption/check: in_data_25 must not glitch when in_valid_25 is high.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Medium"
        ),
    )
}
