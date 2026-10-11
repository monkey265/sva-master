package com.svamaster.data.challenges

import com.svamaster.model.SvaCategory
import com.svamaster.model.SvaPracticeChallenge

object SvaChallengesPart1 {
    val list: List<SvaPracticeChallenge> = listOf(
        SvaPracticeChallenge(
            id = "sva_chal_031",
            prompt = "Verify that ready holds low for exactly 6 consecutive cycles after stall",
            targetSva = "stall |=> !ready [*6]",
            acceptableAnswers = listOf("stall |-> ##1 !ready [*6]", "stall |=> !ready [*6]", "stall |=> (!ready) [*6]"),
            tokens = listOf("stall", "|=>", "!ready", "[*6]"),
            distractors = listOf("|->", "[->6]", "[=6]"),
            explanation = "Consecutive repetition [*6] ensures !ready is sampled for 6 cycles after stall.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_032",
            prompt = "When tx_req is asserted, tx_ack must arrive after exactly 7 clock cycles",
            targetSva = "tx_req |-> ##7 tx_ack",
            acceptableAnswers = listOf("tx_req |-> ##7 tx_ack", "tx_req |=> ##6 tx_ack"),
            tokens = listOf("tx_req", "|->", "##7", "tx_ack"),
            distractors = listOf("|=>", "[*7]", "##8"),
            explanation = "Fixed delay ##7 requires tx_ack exactly 7 cycles following tx_req.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_033",
            prompt = "Verify that heartbeat changed value from previous cycle at tick 8",
            targetSva = "\$changed(heartbeat)",
            acceptableAnswers = listOf("\$past(heartbeat) != heartbeat", "\$changed(heartbeat)", "heartbeat != \$past(heartbeat)"),
            tokens = listOf("\$changed(heartbeat)"),
            distractors = listOf("\$stable(heartbeat)", "\$rose(heartbeat)", "\$fell(heartbeat)"),
            explanation = "\$changed returns 1 if signal is not equal to its sampled value from the prior cycle.",
            category = SvaCategory.SYSTEM_FUNCTIONS,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_034",
            prompt = "Event trigger must happen 9 times non-consecutively before finish, allowing extra cycles after",
            targetSva = "trigger [=9] ##1 finish",
            acceptableAnswers = listOf("trigger [=9] ##1 finish", "(trigger [=9]) ##1 finish"),
            tokens = listOf("trigger", "[=9]", "##1", "finish"),
            distractors = listOf("[->9]", "[*9]", "|=>"),
            explanation = "Non-consecutive repetition [=9] allows delay after the 9-th occurrence before finish.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Advanced"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_035",
            prompt = "Ensure sub-sequence sub_seq_10 occurs entirely within main_frame_10",
            targetSva = "sub_seq_10 within main_frame_10",
            acceptableAnswers = listOf("sub_seq_10 within main_frame_10", "(sub_seq_10) within (main_frame_10)"),
            tokens = listOf("sub_seq_10", "within", "main_frame_10"),
            distractors = listOf("throughout", "intersect", "and"),
            explanation = "within operator checks that sub-sequence occurs within the timeframe of main sequence.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Advanced"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_036",
            prompt = "Verify that ready holds low for exactly 11 consecutive cycles after stall",
            targetSva = "stall |=> !ready [*11]",
            acceptableAnswers = listOf("stall |=> (!ready) [*11]", "stall |-> ##1 !ready [*11]", "stall |=> !ready [*11]"),
            tokens = listOf("stall", "|=>", "!ready", "[*11]"),
            distractors = listOf("|->", "[->11]", "[=11]"),
            explanation = "Consecutive repetition [*11] ensures !ready is sampled for 11 cycles after stall.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_037",
            prompt = "When tx_req is asserted, tx_ack must arrive after exactly 12 clock cycles",
            targetSva = "tx_req |-> ##12 tx_ack",
            acceptableAnswers = listOf("tx_req |=> ##11 tx_ack", "tx_req |-> ##12 tx_ack"),
            tokens = listOf("tx_req", "|->", "##12", "tx_ack"),
            distractors = listOf("|=>", "[*12]", "##13"),
            explanation = "Fixed delay ##12 requires tx_ack exactly 12 cycles following tx_req.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_038",
            prompt = "Verify that heartbeat changed value from previous cycle at tick 13",
            targetSva = "\$changed(heartbeat)",
            acceptableAnswers = listOf("\$past(heartbeat) != heartbeat", "\$changed(heartbeat)", "heartbeat != \$past(heartbeat)"),
            tokens = listOf("\$changed(heartbeat)"),
            distractors = listOf("\$stable(heartbeat)", "\$rose(heartbeat)", "\$fell(heartbeat)"),
            explanation = "\$changed returns 1 if signal is not equal to its sampled value from the prior cycle.",
            category = SvaCategory.SYSTEM_FUNCTIONS,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_039",
            prompt = "Event trigger must happen 14 times non-consecutively before finish, allowing extra cycles after",
            targetSva = "trigger [=14] ##1 finish",
            acceptableAnswers = listOf("trigger [=14] ##1 finish", "(trigger [=14]) ##1 finish"),
            tokens = listOf("trigger", "[=14]", "##1", "finish"),
            distractors = listOf("[->14]", "[*14]", "|=>"),
            explanation = "Non-consecutive repetition [=14] allows delay after the 14-th occurrence before finish.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Advanced"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_040",
            prompt = "Ensure sub-sequence sub_seq_15 occurs entirely within main_frame_15",
            targetSva = "sub_seq_15 within main_frame_15",
            acceptableAnswers = listOf("(sub_seq_15) within (main_frame_15)", "sub_seq_15 within main_frame_15"),
            tokens = listOf("sub_seq_15", "within", "main_frame_15"),
            distractors = listOf("throughout", "intersect", "and"),
            explanation = "within operator checks that sub-sequence occurs within the timeframe of main sequence.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Advanced"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_041",
            prompt = "Verify that ready holds low for exactly 16 consecutive cycles after stall",
            targetSva = "stall |=> !ready [*16]",
            acceptableAnswers = listOf("stall |-> ##1 !ready [*16]", "stall |=> (!ready) [*16]", "stall |=> !ready [*16]"),
            tokens = listOf("stall", "|=>", "!ready", "[*16]"),
            distractors = listOf("|->", "[->16]", "[=16]"),
            explanation = "Consecutive repetition [*16] ensures !ready is sampled for 16 cycles after stall.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_042",
            prompt = "When tx_req is asserted, tx_ack must arrive after exactly 17 clock cycles",
            targetSva = "tx_req |-> ##17 tx_ack",
            acceptableAnswers = listOf("tx_req |=> ##16 tx_ack", "tx_req |-> ##17 tx_ack"),
            tokens = listOf("tx_req", "|->", "##17", "tx_ack"),
            distractors = listOf("|=>", "[*17]", "##18"),
            explanation = "Fixed delay ##17 requires tx_ack exactly 17 cycles following tx_req.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_043",
            prompt = "Verify that heartbeat changed value from previous cycle at tick 18",
            targetSva = "\$changed(heartbeat)",
            acceptableAnswers = listOf("\$past(heartbeat) != heartbeat", "\$changed(heartbeat)", "heartbeat != \$past(heartbeat)"),
            tokens = listOf("\$changed(heartbeat)"),
            distractors = listOf("\$stable(heartbeat)", "\$rose(heartbeat)", "\$fell(heartbeat)"),
            explanation = "\$changed returns 1 if signal is not equal to its sampled value from the prior cycle.",
            category = SvaCategory.SYSTEM_FUNCTIONS,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_044",
            prompt = "Event trigger must happen 19 times non-consecutively before finish, allowing extra cycles after",
            targetSva = "trigger [=19] ##1 finish",
            acceptableAnswers = listOf("(trigger [=19]) ##1 finish", "trigger [=19] ##1 finish"),
            tokens = listOf("trigger", "[=19]", "##1", "finish"),
            distractors = listOf("[->19]", "[*19]", "|=>"),
            explanation = "Non-consecutive repetition [=19] allows delay after the 19-th occurrence before finish.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Advanced"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_045",
            prompt = "Ensure sub-sequence sub_seq_20 occurs entirely within main_frame_20",
            targetSva = "sub_seq_20 within main_frame_20",
            acceptableAnswers = listOf("(sub_seq_20) within (main_frame_20)", "sub_seq_20 within main_frame_20"),
            tokens = listOf("sub_seq_20", "within", "main_frame_20"),
            distractors = listOf("throughout", "intersect", "and"),
            explanation = "within operator checks that sub-sequence occurs within the timeframe of main sequence.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Advanced"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_046",
            prompt = "Verify that ready holds low for exactly 21 consecutive cycles after stall",
            targetSva = "stall |=> !ready [*21]",
            acceptableAnswers = listOf("stall |=> (!ready) [*21]", "stall |=> !ready [*21]", "stall |-> ##1 !ready [*21]"),
            tokens = listOf("stall", "|=>", "!ready", "[*21]"),
            distractors = listOf("|->", "[->21]", "[=21]"),
            explanation = "Consecutive repetition [*21] ensures !ready is sampled for 21 cycles after stall.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_047",
            prompt = "When tx_req is asserted, tx_ack must arrive after exactly 22 clock cycles",
            targetSva = "tx_req |-> ##22 tx_ack",
            acceptableAnswers = listOf("tx_req |=> ##21 tx_ack", "tx_req |-> ##22 tx_ack"),
            tokens = listOf("tx_req", "|->", "##22", "tx_ack"),
            distractors = listOf("|=>", "[*22]", "##23"),
            explanation = "Fixed delay ##22 requires tx_ack exactly 22 cycles following tx_req.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_048",
            prompt = "Verify that heartbeat changed value from previous cycle at tick 23",
            targetSva = "\$changed(heartbeat)",
            acceptableAnswers = listOf("\$past(heartbeat) != heartbeat", "\$changed(heartbeat)", "heartbeat != \$past(heartbeat)"),
            tokens = listOf("\$changed(heartbeat)"),
            distractors = listOf("\$stable(heartbeat)", "\$rose(heartbeat)", "\$fell(heartbeat)"),
            explanation = "\$changed returns 1 if signal is not equal to its sampled value from the prior cycle.",
            category = SvaCategory.SYSTEM_FUNCTIONS,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_049",
            prompt = "Event trigger must happen 24 times non-consecutively before finish, allowing extra cycles after",
            targetSva = "trigger [=24] ##1 finish",
            acceptableAnswers = listOf("(trigger [=24]) ##1 finish", "trigger [=24] ##1 finish"),
            tokens = listOf("trigger", "[=24]", "##1", "finish"),
            distractors = listOf("[->24]", "[*24]", "|=>"),
            explanation = "Non-consecutive repetition [=24] allows delay after the 24-th occurrence before finish.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Advanced"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_050",
            prompt = "Ensure sub-sequence sub_seq_25 occurs entirely within main_frame_25",
            targetSva = "sub_seq_25 within main_frame_25",
            acceptableAnswers = listOf("sub_seq_25 within main_frame_25", "(sub_seq_25) within (main_frame_25)"),
            tokens = listOf("sub_seq_25", "within", "main_frame_25"),
            distractors = listOf("throughout", "intersect", "and"),
            explanation = "within operator checks that sub-sequence occurs within the timeframe of main sequence.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Advanced"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_051",
            prompt = "Verify that ready holds low for exactly 26 consecutive cycles after stall",
            targetSva = "stall |=> !ready [*26]",
            acceptableAnswers = listOf("stall |=> !ready [*26]", "stall |-> ##1 !ready [*26]", "stall |=> (!ready) [*26]"),
            tokens = listOf("stall", "|=>", "!ready", "[*26]"),
            distractors = listOf("|->", "[->26]", "[=26]"),
            explanation = "Consecutive repetition [*26] ensures !ready is sampled for 26 cycles after stall.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_052",
            prompt = "When tx_req is asserted, tx_ack must arrive after exactly 27 clock cycles",
            targetSva = "tx_req |-> ##27 tx_ack",
            acceptableAnswers = listOf("tx_req |=> ##26 tx_ack", "tx_req |-> ##27 tx_ack"),
            tokens = listOf("tx_req", "|->", "##27", "tx_ack"),
            distractors = listOf("|=>", "[*27]", "##28"),
            explanation = "Fixed delay ##27 requires tx_ack exactly 27 cycles following tx_req.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_053",
            prompt = "Verify that heartbeat changed value from previous cycle at tick 28",
            targetSva = "\$changed(heartbeat)",
            acceptableAnswers = listOf("\$past(heartbeat) != heartbeat", "\$changed(heartbeat)", "heartbeat != \$past(heartbeat)"),
            tokens = listOf("\$changed(heartbeat)"),
            distractors = listOf("\$stable(heartbeat)", "\$rose(heartbeat)", "\$fell(heartbeat)"),
            explanation = "\$changed returns 1 if signal is not equal to its sampled value from the prior cycle.",
            category = SvaCategory.SYSTEM_FUNCTIONS,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_054",
            prompt = "Event trigger must happen 29 times non-consecutively before finish, allowing extra cycles after",
            targetSva = "trigger [=29] ##1 finish",
            acceptableAnswers = listOf("(trigger [=29]) ##1 finish", "trigger [=29] ##1 finish"),
            tokens = listOf("trigger", "[=29]", "##1", "finish"),
            distractors = listOf("[->29]", "[*29]", "|=>"),
            explanation = "Non-consecutive repetition [=29] allows delay after the 29-th occurrence before finish.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Advanced"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_055",
            prompt = "Ensure sub-sequence sub_seq_30 occurs entirely within main_frame_30",
            targetSva = "sub_seq_30 within main_frame_30",
            acceptableAnswers = listOf("(sub_seq_30) within (main_frame_30)", "sub_seq_30 within main_frame_30"),
            tokens = listOf("sub_seq_30", "within", "main_frame_30"),
            distractors = listOf("throughout", "intersect", "and"),
            explanation = "within operator checks that sub-sequence occurs within the timeframe of main sequence.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Advanced"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_056",
            prompt = "Verify that ready holds low for exactly 31 consecutive cycles after stall",
            targetSva = "stall |=> !ready [*31]",
            acceptableAnswers = listOf("stall |-> ##1 !ready [*31]", "stall |=> !ready [*31]", "stall |=> (!ready) [*31]"),
            tokens = listOf("stall", "|=>", "!ready", "[*31]"),
            distractors = listOf("|->", "[->31]", "[=31]"),
            explanation = "Consecutive repetition [*31] ensures !ready is sampled for 31 cycles after stall.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_057",
            prompt = "When tx_req is asserted, tx_ack must arrive after exactly 32 clock cycles",
            targetSva = "tx_req |-> ##32 tx_ack",
            acceptableAnswers = listOf("tx_req |=> ##31 tx_ack", "tx_req |-> ##32 tx_ack"),
            tokens = listOf("tx_req", "|->", "##32", "tx_ack"),
            distractors = listOf("|=>", "[*32]", "##33"),
            explanation = "Fixed delay ##32 requires tx_ack exactly 32 cycles following tx_req.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_058",
            prompt = "Verify that heartbeat changed value from previous cycle at tick 33",
            targetSva = "\$changed(heartbeat)",
            acceptableAnswers = listOf("\$past(heartbeat) != heartbeat", "\$changed(heartbeat)", "heartbeat != \$past(heartbeat)"),
            tokens = listOf("\$changed(heartbeat)"),
            distractors = listOf("\$stable(heartbeat)", "\$rose(heartbeat)", "\$fell(heartbeat)"),
            explanation = "\$changed returns 1 if signal is not equal to its sampled value from the prior cycle.",
            category = SvaCategory.SYSTEM_FUNCTIONS,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_059",
            prompt = "Event trigger must happen 34 times non-consecutively before finish, allowing extra cycles after",
            targetSva = "trigger [=34] ##1 finish",
            acceptableAnswers = listOf("(trigger [=34]) ##1 finish", "trigger [=34] ##1 finish"),
            tokens = listOf("trigger", "[=34]", "##1", "finish"),
            distractors = listOf("[->34]", "[*34]", "|=>"),
            explanation = "Non-consecutive repetition [=34] allows delay after the 34-th occurrence before finish.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Advanced"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_060",
            prompt = "Ensure sub-sequence sub_seq_35 occurs entirely within main_frame_35",
            targetSva = "sub_seq_35 within main_frame_35",
            acceptableAnswers = listOf("(sub_seq_35) within (main_frame_35)", "sub_seq_35 within main_frame_35"),
            tokens = listOf("sub_seq_35", "within", "main_frame_35"),
            distractors = listOf("throughout", "intersect", "and"),
            explanation = "within operator checks that sub-sequence occurs within the timeframe of main sequence.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Advanced"
        ),
    )
}
