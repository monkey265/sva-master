package com.svamaster.data.challenges

import com.svamaster.model.SvaCategory
import com.svamaster.model.SvaPracticeChallenge

object SvaChallengesPart4 {
    val list: List<SvaPracticeChallenge> = listOf(
        SvaPracticeChallenge(
            id = "sva_chal_121",
            prompt = "Verify that invariant safe_mode_16 always holds throughout execution",
            targetSva = "always safe_mode_16",
            acceptableAnswers = listOf("always (safe_mode_16)", "always safe_mode_16"),
            tokens = listOf("always", "safe_mode_16"),
            distractors = listOf("s_eventually", "nexttime", "until"),
            explanation = "always operator declares an invariant that must evaluate true at every clock tick.",
            category = SvaCategory.LTL,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_122",
            prompt = "Verify that bus request bus_req_17 is eventually granted within 19 cycles",
            targetSva = "bus_req_17 |-> s_eventually [1:19] bus_gnt_17",
            acceptableAnswers = listOf("bus_req_17 |-> s_eventually [1:19] bus_gnt_17", "bus_req_17 |-> s_eventually[1:19] bus_gnt_17"),
            tokens = listOf("bus_req_17", "|->", "s_eventually", "[1:19]", "bus_gnt_17"),
            distractors = listOf("always", "|=>", "until"),
            explanation = "Bounded strong liveness operator s_eventually [1:19] enforces grant window.",
            category = SvaCategory.LTL,
            difficulty = "Advanced"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_123",
            prompt = "Signal lock_18 must hold until unlock_18 arrives, overlapping cycle of unlock",
            targetSva = "lock_18 until_with unlock_18",
            acceptableAnswers = listOf("lock_18 until_with unlock_18", "(lock_18) until_with (unlock_18)"),
            tokens = listOf("lock_18", "until_with", "unlock_18"),
            distractors = listOf("until", "s_until", "throughout"),
            explanation = "until_with requires lock to hold up to and including the cycle where unlock asserts.",
            category = SvaCategory.LTL,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_124",
            prompt = "When trigger_19 occurs, check status in next clock cycle using nexttime",
            targetSva = "trigger_19 |-> nexttime status_ok_19",
            acceptableAnswers = listOf("trigger_19 |-> s_nexttime status_ok_19", "trigger_19 |-> nexttime status_ok_19", "trigger_19 |=> status_ok_19"),
            tokens = listOf("trigger_19", "|->", "nexttime", "status_ok_19"),
            distractors = listOf("always", "|=>", "until"),
            explanation = "nexttime P evaluates property P in the immediate next clock cycle.",
            category = SvaCategory.LTL,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_125",
            prompt = "Verify that invariant safe_mode_20 always holds throughout execution",
            targetSva = "always safe_mode_20",
            acceptableAnswers = listOf("always (safe_mode_20)", "always safe_mode_20"),
            tokens = listOf("always", "safe_mode_20"),
            distractors = listOf("s_eventually", "nexttime", "until"),
            explanation = "always operator declares an invariant that must evaluate true at every clock tick.",
            category = SvaCategory.LTL,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_126",
            prompt = "Verify that bus request bus_req_21 is eventually granted within 23 cycles",
            targetSva = "bus_req_21 |-> s_eventually [1:23] bus_gnt_21",
            acceptableAnswers = listOf("bus_req_21 |-> s_eventually[1:23] bus_gnt_21", "bus_req_21 |-> s_eventually [1:23] bus_gnt_21"),
            tokens = listOf("bus_req_21", "|->", "s_eventually", "[1:23]", "bus_gnt_21"),
            distractors = listOf("always", "|=>", "until"),
            explanation = "Bounded strong liveness operator s_eventually [1:23] enforces grant window.",
            category = SvaCategory.LTL,
            difficulty = "Advanced"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_127",
            prompt = "Signal lock_22 must hold until unlock_22 arrives, overlapping cycle of unlock",
            targetSva = "lock_22 until_with unlock_22",
            acceptableAnswers = listOf("lock_22 until_with unlock_22", "(lock_22) until_with (unlock_22)"),
            tokens = listOf("lock_22", "until_with", "unlock_22"),
            distractors = listOf("until", "s_until", "throughout"),
            explanation = "until_with requires lock to hold up to and including the cycle where unlock asserts.",
            category = SvaCategory.LTL,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_128",
            prompt = "When trigger_23 occurs, check status in next clock cycle using nexttime",
            targetSva = "trigger_23 |-> nexttime status_ok_23",
            acceptableAnswers = listOf("trigger_23 |-> s_nexttime status_ok_23", "trigger_23 |=> status_ok_23", "trigger_23 |-> nexttime status_ok_23"),
            tokens = listOf("trigger_23", "|->", "nexttime", "status_ok_23"),
            distractors = listOf("always", "|=>", "until"),
            explanation = "nexttime P evaluates property P in the immediate next clock cycle.",
            category = SvaCategory.LTL,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_129",
            prompt = "Verify that invariant safe_mode_24 always holds throughout execution",
            targetSva = "always safe_mode_24",
            acceptableAnswers = listOf("always (safe_mode_24)", "always safe_mode_24"),
            tokens = listOf("always", "safe_mode_24"),
            distractors = listOf("s_eventually", "nexttime", "until"),
            explanation = "always operator declares an invariant that must evaluate true at every clock tick.",
            category = SvaCategory.LTL,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_130",
            prompt = "Verify that bus request bus_req_25 is eventually granted within 27 cycles",
            targetSva = "bus_req_25 |-> s_eventually [1:27] bus_gnt_25",
            acceptableAnswers = listOf("bus_req_25 |-> s_eventually [1:27] bus_gnt_25", "bus_req_25 |-> s_eventually[1:27] bus_gnt_25"),
            tokens = listOf("bus_req_25", "|->", "s_eventually", "[1:27]", "bus_gnt_25"),
            distractors = listOf("always", "|=>", "until"),
            explanation = "Bounded strong liveness operator s_eventually [1:27] enforces grant window.",
            category = SvaCategory.LTL,
            difficulty = "Advanced"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_131",
            prompt = "Signal lock_26 must hold until unlock_26 arrives, overlapping cycle of unlock",
            targetSva = "lock_26 until_with unlock_26",
            acceptableAnswers = listOf("(lock_26) until_with (unlock_26)", "lock_26 until_with unlock_26"),
            tokens = listOf("lock_26", "until_with", "unlock_26"),
            distractors = listOf("until", "s_until", "throughout"),
            explanation = "until_with requires lock to hold up to and including the cycle where unlock asserts.",
            category = SvaCategory.LTL,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_132",
            prompt = "When trigger_27 occurs, check status in next clock cycle using nexttime",
            targetSva = "trigger_27 |-> nexttime status_ok_27",
            acceptableAnswers = listOf("trigger_27 |=> status_ok_27", "trigger_27 |-> nexttime status_ok_27", "trigger_27 |-> s_nexttime status_ok_27"),
            tokens = listOf("trigger_27", "|->", "nexttime", "status_ok_27"),
            distractors = listOf("always", "|=>", "until"),
            explanation = "nexttime P evaluates property P in the immediate next clock cycle.",
            category = SvaCategory.LTL,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_133",
            prompt = "Verify that invariant safe_mode_28 always holds throughout execution",
            targetSva = "always safe_mode_28",
            acceptableAnswers = listOf("always safe_mode_28", "always (safe_mode_28)"),
            tokens = listOf("always", "safe_mode_28"),
            distractors = listOf("s_eventually", "nexttime", "until"),
            explanation = "always operator declares an invariant that must evaluate true at every clock tick.",
            category = SvaCategory.LTL,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_134",
            prompt = "Verify that bus request bus_req_29 is eventually granted within 31 cycles",
            targetSva = "bus_req_29 |-> s_eventually [1:31] bus_gnt_29",
            acceptableAnswers = listOf("bus_req_29 |-> s_eventually[1:31] bus_gnt_29", "bus_req_29 |-> s_eventually [1:31] bus_gnt_29"),
            tokens = listOf("bus_req_29", "|->", "s_eventually", "[1:31]", "bus_gnt_29"),
            distractors = listOf("always", "|=>", "until"),
            explanation = "Bounded strong liveness operator s_eventually [1:31] enforces grant window.",
            category = SvaCategory.LTL,
            difficulty = "Advanced"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_135",
            prompt = "Signal lock_30 must hold until unlock_30 arrives, overlapping cycle of unlock",
            targetSva = "lock_30 until_with unlock_30",
            acceptableAnswers = listOf("lock_30 until_with unlock_30", "(lock_30) until_with (unlock_30)"),
            tokens = listOf("lock_30", "until_with", "unlock_30"),
            distractors = listOf("until", "s_until", "throughout"),
            explanation = "until_with requires lock to hold up to and including the cycle where unlock asserts.",
            category = SvaCategory.LTL,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_136",
            prompt = "When trigger_31 occurs, check status in next clock cycle using nexttime",
            targetSva = "trigger_31 |-> nexttime status_ok_31",
            acceptableAnswers = listOf("trigger_31 |-> nexttime status_ok_31", "trigger_31 |-> s_nexttime status_ok_31", "trigger_31 |=> status_ok_31"),
            tokens = listOf("trigger_31", "|->", "nexttime", "status_ok_31"),
            distractors = listOf("always", "|=>", "until"),
            explanation = "nexttime P evaluates property P in the immediate next clock cycle.",
            category = SvaCategory.LTL,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_137",
            prompt = "Verify that invariant safe_mode_32 always holds throughout execution",
            targetSva = "always safe_mode_32",
            acceptableAnswers = listOf("always safe_mode_32", "always (safe_mode_32)"),
            tokens = listOf("always", "safe_mode_32"),
            distractors = listOf("s_eventually", "nexttime", "until"),
            explanation = "always operator declares an invariant that must evaluate true at every clock tick.",
            category = SvaCategory.LTL,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_138",
            prompt = "Verify that bus request bus_req_33 is eventually granted within 35 cycles",
            targetSva = "bus_req_33 |-> s_eventually [1:35] bus_gnt_33",
            acceptableAnswers = listOf("bus_req_33 |-> s_eventually[1:35] bus_gnt_33", "bus_req_33 |-> s_eventually [1:35] bus_gnt_33"),
            tokens = listOf("bus_req_33", "|->", "s_eventually", "[1:35]", "bus_gnt_33"),
            distractors = listOf("always", "|=>", "until"),
            explanation = "Bounded strong liveness operator s_eventually [1:35] enforces grant window.",
            category = SvaCategory.LTL,
            difficulty = "Advanced"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_139",
            prompt = "Signal lock_34 must hold until unlock_34 arrives, overlapping cycle of unlock",
            targetSva = "lock_34 until_with unlock_34",
            acceptableAnswers = listOf("lock_34 until_with unlock_34", "(lock_34) until_with (unlock_34)"),
            tokens = listOf("lock_34", "until_with", "unlock_34"),
            distractors = listOf("until", "s_until", "throughout"),
            explanation = "until_with requires lock to hold up to and including the cycle where unlock asserts.",
            category = SvaCategory.LTL,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_140",
            prompt = "When trigger_35 occurs, check status in next clock cycle using nexttime",
            targetSva = "trigger_35 |-> nexttime status_ok_35",
            acceptableAnswers = listOf("trigger_35 |-> nexttime status_ok_35", "trigger_35 |=> status_ok_35", "trigger_35 |-> s_nexttime status_ok_35"),
            tokens = listOf("trigger_35", "|->", "nexttime", "status_ok_35"),
            distractors = listOf("always", "|=>", "until"),
            explanation = "nexttime P evaluates property P in the immediate next clock cycle.",
            category = SvaCategory.LTL,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_141",
            prompt = "Verify that invariant safe_mode_36 always holds throughout execution",
            targetSva = "always safe_mode_36",
            acceptableAnswers = listOf("always safe_mode_36", "always (safe_mode_36)"),
            tokens = listOf("always", "safe_mode_36"),
            distractors = listOf("s_eventually", "nexttime", "until"),
            explanation = "always operator declares an invariant that must evaluate true at every clock tick.",
            category = SvaCategory.LTL,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_142",
            prompt = "Verify that bus request bus_req_37 is eventually granted within 39 cycles",
            targetSva = "bus_req_37 |-> s_eventually [1:39] bus_gnt_37",
            acceptableAnswers = listOf("bus_req_37 |-> s_eventually[1:39] bus_gnt_37", "bus_req_37 |-> s_eventually [1:39] bus_gnt_37"),
            tokens = listOf("bus_req_37", "|->", "s_eventually", "[1:39]", "bus_gnt_37"),
            distractors = listOf("always", "|=>", "until"),
            explanation = "Bounded strong liveness operator s_eventually [1:39] enforces grant window.",
            category = SvaCategory.LTL,
            difficulty = "Advanced"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_143",
            prompt = "Signal lock_38 must hold until unlock_38 arrives, overlapping cycle of unlock",
            targetSva = "lock_38 until_with unlock_38",
            acceptableAnswers = listOf("lock_38 until_with unlock_38", "(lock_38) until_with (unlock_38)"),
            tokens = listOf("lock_38", "until_with", "unlock_38"),
            distractors = listOf("until", "s_until", "throughout"),
            explanation = "until_with requires lock to hold up to and including the cycle where unlock asserts.",
            category = SvaCategory.LTL,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_144",
            prompt = "When trigger_39 occurs, check status in next clock cycle using nexttime",
            targetSva = "trigger_39 |-> nexttime status_ok_39",
            acceptableAnswers = listOf("trigger_39 |=> status_ok_39", "trigger_39 |-> nexttime status_ok_39", "trigger_39 |-> s_nexttime status_ok_39"),
            tokens = listOf("trigger_39", "|->", "nexttime", "status_ok_39"),
            distractors = listOf("always", "|=>", "until"),
            explanation = "nexttime P evaluates property P in the immediate next clock cycle.",
            category = SvaCategory.LTL,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_145",
            prompt = "Verify that invariant safe_mode_40 always holds throughout execution",
            targetSva = "always safe_mode_40",
            acceptableAnswers = listOf("always safe_mode_40", "always (safe_mode_40)"),
            tokens = listOf("always", "safe_mode_40"),
            distractors = listOf("s_eventually", "nexttime", "until"),
            explanation = "always operator declares an invariant that must evaluate true at every clock tick.",
            category = SvaCategory.LTL,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_146",
            prompt = "Verify that bus request bus_req_41 is eventually granted within 43 cycles",
            targetSva = "bus_req_41 |-> s_eventually [1:43] bus_gnt_41",
            acceptableAnswers = listOf("bus_req_41 |-> s_eventually[1:43] bus_gnt_41", "bus_req_41 |-> s_eventually [1:43] bus_gnt_41"),
            tokens = listOf("bus_req_41", "|->", "s_eventually", "[1:43]", "bus_gnt_41"),
            distractors = listOf("always", "|=>", "until"),
            explanation = "Bounded strong liveness operator s_eventually [1:43] enforces grant window.",
            category = SvaCategory.LTL,
            difficulty = "Advanced"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_147",
            prompt = "Signal lock_42 must hold until unlock_42 arrives, overlapping cycle of unlock",
            targetSva = "lock_42 until_with unlock_42",
            acceptableAnswers = listOf("lock_42 until_with unlock_42", "(lock_42) until_with (unlock_42)"),
            tokens = listOf("lock_42", "until_with", "unlock_42"),
            distractors = listOf("until", "s_until", "throughout"),
            explanation = "until_with requires lock to hold up to and including the cycle where unlock asserts.",
            category = SvaCategory.LTL,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_148",
            prompt = "When trigger_43 occurs, check status in next clock cycle using nexttime",
            targetSva = "trigger_43 |-> nexttime status_ok_43",
            acceptableAnswers = listOf("trigger_43 |=> status_ok_43", "trigger_43 |-> nexttime status_ok_43", "trigger_43 |-> s_nexttime status_ok_43"),
            tokens = listOf("trigger_43", "|->", "nexttime", "status_ok_43"),
            distractors = listOf("always", "|=>", "until"),
            explanation = "nexttime P evaluates property P in the immediate next clock cycle.",
            category = SvaCategory.LTL,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_149",
            prompt = "Verify that invariant safe_mode_44 always holds throughout execution",
            targetSva = "always safe_mode_44",
            acceptableAnswers = listOf("always (safe_mode_44)", "always safe_mode_44"),
            tokens = listOf("always", "safe_mode_44"),
            distractors = listOf("s_eventually", "nexttime", "until"),
            explanation = "always operator declares an invariant that must evaluate true at every clock tick.",
            category = SvaCategory.LTL,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_150",
            prompt = "Verify that bus request bus_req_45 is eventually granted within 47 cycles",
            targetSva = "bus_req_45 |-> s_eventually [1:47] bus_gnt_45",
            acceptableAnswers = listOf("bus_req_45 |-> s_eventually [1:47] bus_gnt_45", "bus_req_45 |-> s_eventually[1:47] bus_gnt_45"),
            tokens = listOf("bus_req_45", "|->", "s_eventually", "[1:47]", "bus_gnt_45"),
            distractors = listOf("always", "|=>", "until"),
            explanation = "Bounded strong liveness operator s_eventually [1:47] enforces grant window.",
            category = SvaCategory.LTL,
            difficulty = "Advanced"
        ),
    )
}
