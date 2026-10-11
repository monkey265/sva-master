package com.svamaster.data.challenges

import com.svamaster.model.SvaCategory
import com.svamaster.model.SvaPracticeChallenge

object SvaChallengesPart3 {
    val list: List<SvaPracticeChallenge> = listOf(
        SvaPracticeChallenge(
            id = "sva_chal_091",
            prompt = "Cover scenario where burst length is 26 cycles followed by eop",
            targetSva = "burst_en [* 26] ##1 eop",
            acceptableAnswers = listOf("burst_en [* 26] ##1 eop", "(burst_en [*26]) ##1 eop", "burst_en [*26] ##1 eop"),
            tokens = listOf("burst_en", "[*26]", "##1", "eop"),
            distractors = listOf("[->26]", "[=26]", "|->"),
            explanation = "Cover property sequence tracking witness trace for 26 bursts ending with eop.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_092",
            prompt = "Verify that fifo_count_27 never exceeds threshold of 43",
            targetSva = "fifo_count_27 <= 43",
            acceptableAnswers = listOf("fifo_count_27 <= 43", "fifo_count_27 < 44", "(fifo_count_27 <= 43)"),
            tokens = listOf("fifo_count_27", "<=", "43"),
            distractors = listOf(">", "==", "##1"),
            explanation = "Invariance safety check ensuring FIFO level doesn't overflow limit of 43.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_093",
            prompt = "Prune formal search space to enforce mode_28 inside valid configuration set {0, 1, 3}",
            targetSva = "mode_28 inside {0, 1, 3}",
            acceptableAnswers = listOf("(mode_28 inside {0, 1, 3})", "mode_28 inside {0, 1, 3}"),
            tokens = listOf("mode_28", "inside", "{0, 1, 3}"),
            distractors = listOf("==", "!=", "intersect"),
            explanation = "Restrict property operator constraining formal verification solver exploration.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_094",
            prompt = "Constrain formal input in_data_29 to remain stable when in_valid_29 is asserted",
            targetSva = "in_valid_29 |-> \$stable(in_data_29)",
            acceptableAnswers = listOf("(in_valid_29) |-> \$stable(in_data_29)", "in_valid_29 |-> \$stable(in_data_29)"),
            tokens = listOf("in_valid_29", "|->", "\$stable(in_data_29)"),
            distractors = listOf("|=>", "\$changed(in_data)", "always"),
            explanation = "Environmental assumption/check: in_data_29 must not glitch when in_valid_29 is high.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_095",
            prompt = "Cover scenario where burst length is 30 cycles followed by eop",
            targetSva = "burst_en [* 30] ##1 eop",
            acceptableAnswers = listOf("burst_en [*30] ##1 eop", "(burst_en [*30]) ##1 eop", "burst_en [* 30] ##1 eop"),
            tokens = listOf("burst_en", "[*30]", "##1", "eop"),
            distractors = listOf("[->30]", "[=30]", "|->"),
            explanation = "Cover property sequence tracking witness trace for 30 bursts ending with eop.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_096",
            prompt = "Verify that fifo_count_31 never exceeds threshold of 47",
            targetSva = "fifo_count_31 <= 47",
            acceptableAnswers = listOf("fifo_count_31 < 48", "(fifo_count_31 <= 47)", "fifo_count_31 <= 47"),
            tokens = listOf("fifo_count_31", "<=", "47"),
            distractors = listOf(">", "==", "##1"),
            explanation = "Invariance safety check ensuring FIFO level doesn't overflow limit of 47.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_097",
            prompt = "Prune formal search space to enforce mode_32 inside valid configuration set {0, 1, 3}",
            targetSva = "mode_32 inside {0, 1, 3}",
            acceptableAnswers = listOf("(mode_32 inside {0, 1, 3})", "mode_32 inside {0, 1, 3}"),
            tokens = listOf("mode_32", "inside", "{0, 1, 3}"),
            distractors = listOf("==", "!=", "intersect"),
            explanation = "Restrict property operator constraining formal verification solver exploration.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_098",
            prompt = "Constrain formal input in_data_33 to remain stable when in_valid_33 is asserted",
            targetSva = "in_valid_33 |-> \$stable(in_data_33)",
            acceptableAnswers = listOf("in_valid_33 |-> \$stable(in_data_33)", "(in_valid_33) |-> \$stable(in_data_33)"),
            tokens = listOf("in_valid_33", "|->", "\$stable(in_data_33)"),
            distractors = listOf("|=>", "\$changed(in_data)", "always"),
            explanation = "Environmental assumption/check: in_data_33 must not glitch when in_valid_33 is high.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_099",
            prompt = "Cover scenario where burst length is 34 cycles followed by eop",
            targetSva = "burst_en [* 34] ##1 eop",
            acceptableAnswers = listOf("burst_en [*34] ##1 eop", "burst_en [* 34] ##1 eop", "(burst_en [*34]) ##1 eop"),
            tokens = listOf("burst_en", "[*34]", "##1", "eop"),
            distractors = listOf("[->34]", "[=34]", "|->"),
            explanation = "Cover property sequence tracking witness trace for 34 bursts ending with eop.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_100",
            prompt = "Verify that fifo_count_35 never exceeds threshold of 51",
            targetSva = "fifo_count_35 <= 51",
            acceptableAnswers = listOf("fifo_count_35 <= 51", "(fifo_count_35 <= 51)", "fifo_count_35 < 52"),
            tokens = listOf("fifo_count_35", "<=", "51"),
            distractors = listOf(">", "==", "##1"),
            explanation = "Invariance safety check ensuring FIFO level doesn't overflow limit of 51.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_101",
            prompt = "Prune formal search space to enforce mode_36 inside valid configuration set {0, 1, 3}",
            targetSva = "mode_36 inside {0, 1, 3}",
            acceptableAnswers = listOf("(mode_36 inside {0, 1, 3})", "mode_36 inside {0, 1, 3}"),
            tokens = listOf("mode_36", "inside", "{0, 1, 3}"),
            distractors = listOf("==", "!=", "intersect"),
            explanation = "Restrict property operator constraining formal verification solver exploration.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_102",
            prompt = "Constrain formal input in_data_37 to remain stable when in_valid_37 is asserted",
            targetSva = "in_valid_37 |-> \$stable(in_data_37)",
            acceptableAnswers = listOf("(in_valid_37) |-> \$stable(in_data_37)", "in_valid_37 |-> \$stable(in_data_37)"),
            tokens = listOf("in_valid_37", "|->", "\$stable(in_data_37)"),
            distractors = listOf("|=>", "\$changed(in_data)", "always"),
            explanation = "Environmental assumption/check: in_data_37 must not glitch when in_valid_37 is high.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_103",
            prompt = "Cover scenario where burst length is 38 cycles followed by eop",
            targetSva = "burst_en [* 38] ##1 eop",
            acceptableAnswers = listOf("burst_en [*38] ##1 eop", "burst_en [* 38] ##1 eop", "(burst_en [*38]) ##1 eop"),
            tokens = listOf("burst_en", "[*38]", "##1", "eop"),
            distractors = listOf("[->38]", "[=38]", "|->"),
            explanation = "Cover property sequence tracking witness trace for 38 bursts ending with eop.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_104",
            prompt = "Verify that fifo_count_39 never exceeds threshold of 55",
            targetSva = "fifo_count_39 <= 55",
            acceptableAnswers = listOf("(fifo_count_39 <= 55)", "fifo_count_39 <= 55", "fifo_count_39 < 56"),
            tokens = listOf("fifo_count_39", "<=", "55"),
            distractors = listOf(">", "==", "##1"),
            explanation = "Invariance safety check ensuring FIFO level doesn't overflow limit of 55.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_105",
            prompt = "Prune formal search space to enforce mode_40 inside valid configuration set {0, 1, 3}",
            targetSva = "mode_40 inside {0, 1, 3}",
            acceptableAnswers = listOf("mode_40 inside {0, 1, 3}", "(mode_40 inside {0, 1, 3})"),
            tokens = listOf("mode_40", "inside", "{0, 1, 3}"),
            distractors = listOf("==", "!=", "intersect"),
            explanation = "Restrict property operator constraining formal verification solver exploration.",
            category = SvaCategory.DIRECTIVES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_106",
            prompt = "Verify that bus request bus_req_1 is eventually granted within 3 cycles",
            targetSva = "bus_req_1 |-> s_eventually [1:3] bus_gnt_1",
            acceptableAnswers = listOf("bus_req_1 |-> s_eventually[1:3] bus_gnt_1", "bus_req_1 |-> s_eventually [1:3] bus_gnt_1"),
            tokens = listOf("bus_req_1", "|->", "s_eventually", "[1:3]", "bus_gnt_1"),
            distractors = listOf("always", "|=>", "until"),
            explanation = "Bounded strong liveness operator s_eventually [1:3] enforces grant window.",
            category = SvaCategory.LTL,
            difficulty = "Advanced"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_107",
            prompt = "Signal lock_2 must hold until unlock_2 arrives, overlapping cycle of unlock",
            targetSva = "lock_2 until_with unlock_2",
            acceptableAnswers = listOf("(lock_2) until_with (unlock_2)", "lock_2 until_with unlock_2"),
            tokens = listOf("lock_2", "until_with", "unlock_2"),
            distractors = listOf("until", "s_until", "throughout"),
            explanation = "until_with requires lock to hold up to and including the cycle where unlock asserts.",
            category = SvaCategory.LTL,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_108",
            prompt = "When trigger_3 occurs, check status in next clock cycle using nexttime",
            targetSva = "trigger_3 |-> nexttime status_ok_3",
            acceptableAnswers = listOf("trigger_3 |-> nexttime status_ok_3", "trigger_3 |-> s_nexttime status_ok_3", "trigger_3 |=> status_ok_3"),
            tokens = listOf("trigger_3", "|->", "nexttime", "status_ok_3"),
            distractors = listOf("always", "|=>", "until"),
            explanation = "nexttime P evaluates property P in the immediate next clock cycle.",
            category = SvaCategory.LTL,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_109",
            prompt = "Verify that invariant safe_mode_4 always holds throughout execution",
            targetSva = "always safe_mode_4",
            acceptableAnswers = listOf("always safe_mode_4", "always (safe_mode_4)"),
            tokens = listOf("always", "safe_mode_4"),
            distractors = listOf("s_eventually", "nexttime", "until"),
            explanation = "always operator declares an invariant that must evaluate true at every clock tick.",
            category = SvaCategory.LTL,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_110",
            prompt = "Verify that bus request bus_req_5 is eventually granted within 7 cycles",
            targetSva = "bus_req_5 |-> s_eventually [1:7] bus_gnt_5",
            acceptableAnswers = listOf("bus_req_5 |-> s_eventually[1:7] bus_gnt_5", "bus_req_5 |-> s_eventually [1:7] bus_gnt_5"),
            tokens = listOf("bus_req_5", "|->", "s_eventually", "[1:7]", "bus_gnt_5"),
            distractors = listOf("always", "|=>", "until"),
            explanation = "Bounded strong liveness operator s_eventually [1:7] enforces grant window.",
            category = SvaCategory.LTL,
            difficulty = "Advanced"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_111",
            prompt = "Signal lock_6 must hold until unlock_6 arrives, overlapping cycle of unlock",
            targetSva = "lock_6 until_with unlock_6",
            acceptableAnswers = listOf("(lock_6) until_with (unlock_6)", "lock_6 until_with unlock_6"),
            tokens = listOf("lock_6", "until_with", "unlock_6"),
            distractors = listOf("until", "s_until", "throughout"),
            explanation = "until_with requires lock to hold up to and including the cycle where unlock asserts.",
            category = SvaCategory.LTL,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_112",
            prompt = "When trigger_7 occurs, check status in next clock cycle using nexttime",
            targetSva = "trigger_7 |-> nexttime status_ok_7",
            acceptableAnswers = listOf("trigger_7 |=> status_ok_7", "trigger_7 |-> nexttime status_ok_7", "trigger_7 |-> s_nexttime status_ok_7"),
            tokens = listOf("trigger_7", "|->", "nexttime", "status_ok_7"),
            distractors = listOf("always", "|=>", "until"),
            explanation = "nexttime P evaluates property P in the immediate next clock cycle.",
            category = SvaCategory.LTL,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_113",
            prompt = "Verify that invariant safe_mode_8 always holds throughout execution",
            targetSva = "always safe_mode_8",
            acceptableAnswers = listOf("always safe_mode_8", "always (safe_mode_8)"),
            tokens = listOf("always", "safe_mode_8"),
            distractors = listOf("s_eventually", "nexttime", "until"),
            explanation = "always operator declares an invariant that must evaluate true at every clock tick.",
            category = SvaCategory.LTL,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_114",
            prompt = "Verify that bus request bus_req_9 is eventually granted within 11 cycles",
            targetSva = "bus_req_9 |-> s_eventually [1:11] bus_gnt_9",
            acceptableAnswers = listOf("bus_req_9 |-> s_eventually[1:11] bus_gnt_9", "bus_req_9 |-> s_eventually [1:11] bus_gnt_9"),
            tokens = listOf("bus_req_9", "|->", "s_eventually", "[1:11]", "bus_gnt_9"),
            distractors = listOf("always", "|=>", "until"),
            explanation = "Bounded strong liveness operator s_eventually [1:11] enforces grant window.",
            category = SvaCategory.LTL,
            difficulty = "Advanced"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_115",
            prompt = "Signal lock_10 must hold until unlock_10 arrives, overlapping cycle of unlock",
            targetSva = "lock_10 until_with unlock_10",
            acceptableAnswers = listOf("lock_10 until_with unlock_10", "(lock_10) until_with (unlock_10)"),
            tokens = listOf("lock_10", "until_with", "unlock_10"),
            distractors = listOf("until", "s_until", "throughout"),
            explanation = "until_with requires lock to hold up to and including the cycle where unlock asserts.",
            category = SvaCategory.LTL,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_116",
            prompt = "When trigger_11 occurs, check status in next clock cycle using nexttime",
            targetSva = "trigger_11 |-> nexttime status_ok_11",
            acceptableAnswers = listOf("trigger_11 |-> s_nexttime status_ok_11", "trigger_11 |-> nexttime status_ok_11", "trigger_11 |=> status_ok_11"),
            tokens = listOf("trigger_11", "|->", "nexttime", "status_ok_11"),
            distractors = listOf("always", "|=>", "until"),
            explanation = "nexttime P evaluates property P in the immediate next clock cycle.",
            category = SvaCategory.LTL,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_117",
            prompt = "Verify that invariant safe_mode_12 always holds throughout execution",
            targetSva = "always safe_mode_12",
            acceptableAnswers = listOf("always (safe_mode_12)", "always safe_mode_12"),
            tokens = listOf("always", "safe_mode_12"),
            distractors = listOf("s_eventually", "nexttime", "until"),
            explanation = "always operator declares an invariant that must evaluate true at every clock tick.",
            category = SvaCategory.LTL,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_118",
            prompt = "Verify that bus request bus_req_13 is eventually granted within 15 cycles",
            targetSva = "bus_req_13 |-> s_eventually [1:15] bus_gnt_13",
            acceptableAnswers = listOf("bus_req_13 |-> s_eventually[1:15] bus_gnt_13", "bus_req_13 |-> s_eventually [1:15] bus_gnt_13"),
            tokens = listOf("bus_req_13", "|->", "s_eventually", "[1:15]", "bus_gnt_13"),
            distractors = listOf("always", "|=>", "until"),
            explanation = "Bounded strong liveness operator s_eventually [1:15] enforces grant window.",
            category = SvaCategory.LTL,
            difficulty = "Advanced"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_119",
            prompt = "Signal lock_14 must hold until unlock_14 arrives, overlapping cycle of unlock",
            targetSva = "lock_14 until_with unlock_14",
            acceptableAnswers = listOf("lock_14 until_with unlock_14", "(lock_14) until_with (unlock_14)"),
            tokens = listOf("lock_14", "until_with", "unlock_14"),
            distractors = listOf("until", "s_until", "throughout"),
            explanation = "until_with requires lock to hold up to and including the cycle where unlock asserts.",
            category = SvaCategory.LTL,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_120",
            prompt = "When trigger_15 occurs, check status in next clock cycle using nexttime",
            targetSva = "trigger_15 |-> nexttime status_ok_15",
            acceptableAnswers = listOf("trigger_15 |=> status_ok_15", "trigger_15 |-> s_nexttime status_ok_15", "trigger_15 |-> nexttime status_ok_15"),
            tokens = listOf("trigger_15", "|->", "nexttime", "status_ok_15"),
            distractors = listOf("always", "|=>", "until"),
            explanation = "nexttime P evaluates property P in the immediate next clock cycle.",
            category = SvaCategory.LTL,
            difficulty = "Medium"
        ),
    )
}
