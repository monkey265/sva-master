package com.svamaster.data.challenges

import com.svamaster.model.SvaCategory
import com.svamaster.model.SvaPracticeChallenge

object SvaChallengesPart0 {
    val list: List<SvaPracticeChallenge> = listOf(
        SvaPracticeChallenge(
            id = "sva_chal_001",
            prompt = "Verify that valid and ready are both at 1 for 2 consecutive clock cycles",
            targetSva = "(valid && ready) [*2]",
            acceptableAnswers = listOf("valid && ready [*2]", "(valid & ready) [*2]", "(valid && ready) [*2]"),
            tokens = listOf("(", "valid", "&&", "ready", ")", "[*2]"),
            distractors = listOf("|->", "[->2]", "##1", "[=2]"),
            explanation = "Consecutive repetition [*2] requires the boolean condition (valid && ready) to hold for 2 consecutive clock ticks.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_002",
            prompt = "Verify that reset signal rst_n remains low for at least 3 consecutive cycles",
            targetSva = "!rst_n [*3]",
            acceptableAnswers = listOf("!rst_n [*3]", "(rst_n == 0) [*3]", "rst_n == 0 [*3]", "~rst_n [*3]", "(!rst_n) [*3]"),
            tokens = listOf("!rst_n", "[*3]"),
            distractors = listOf("[*2]", "[->3]", "##3", "|=>"),
            explanation = "The [*3] consecutive repetition ensures !rst_n is sampled asserted low across 3 successive clock edges.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_003",
            prompt = "When req is asserted, ack must be asserted 1 cycle later",
            targetSva = "req |=> ack",
            acceptableAnswers = listOf("req |-> ##1 ack", "req |=> ack"),
            tokens = listOf("req", "|=>", "ack"),
            distractors = listOf("|->", "##0", "ack [*1]"),
            explanation = "Non-overlapping implication |=> is equivalent to |-> ##1, evaluating ack on the immediate next clock tick.",
            category = SvaCategory.IMPLICATION,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_004",
            prompt = "When cs is high, addr must be within range [< 32'h1000] in the same cycle",
            targetSva = "cs |-> (addr < 32'h1000)",
            acceptableAnswers = listOf("cs |-> addr < 32'h1000", "cs |-> (addr < 32'h1000)"),
            tokens = listOf("cs", "|->", "(", "addr", "<", "32'h1000", ")"),
            distractors = listOf("|=>", "##1", ">"),
            explanation = "Overlapping implication |-> checks that the consequent condition evaluates in the same clock cycle as cs.",
            category = SvaCategory.IMPLICATION,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_005",
            prompt = "Verify that enable remains asserted for between 2 and 4 consecutive cycles",
            targetSva = "enable [*2:4]",
            acceptableAnswers = listOf("enable [*2:4]", "(enable) [*2:4]"),
            tokens = listOf("enable", "[*2:4]"),
            distractors = listOf("[->2:4]", "[=2:4]", "##[2:4]"),
            explanation = "Consecutive range repetition [*2:4] matches when enable is continuously high between 2 and 4 cycles.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_006",
            prompt = "When start is pulsed, busy must stay high until done arrives (strong)",
            targetSva = "start |=> busy s_until done",
            acceptableAnswers = listOf("start |=> busy s_until done", "start |-> ##1 (busy s_until done)"),
            tokens = listOf("start", "|=>", "busy", "s_until", "done"),
            distractors = listOf("until", "|->", "s_eventually"),
            explanation = "The strong s_until requires busy to hold on each cycle until done arrives, and guarantees done will eventually arrive.",
            category = SvaCategory.LTL,
            difficulty = "Advanced"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_007",
            prompt = "When req is asserted, ack must arrive within 1 to 3 clock cycles",
            targetSva = "req |-> ##[1:3] ack",
            acceptableAnswers = listOf("req |-> ##[1:3] ack", "req |=> ##[0:2] ack"),
            tokens = listOf("req", "|->", "##[1:3]", "ack"),
            distractors = listOf("|=>", "##[1:3]", "[*1:3]"),
            explanation = "Bounded cycle delay ##[1:3] after req requires ack to assert between 1 and 3 cycles later.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_008",
            prompt = "Verify that state vector is always one-hot and never unknown",
            targetSva = "\$onehot(state) && !\$isunknown(state)",
            acceptableAnswers = listOf("\$onehot(state) and !\$isunknown(state)", "!\$isunknown(state) && \$onehot(state)", "\$onehot(state) && !\$isunknown(state)"),
            tokens = listOf("\$onehot(state)", "&&", "!\$isunknown(state)"),
            distractors = listOf("\$onehot0(state)", "##1", "\$countones(state)"),
            explanation = "\$onehot checks that exactly 1 bit is high, and !\$isunknown ensures no bits are X or Z.",
            category = SvaCategory.SYSTEM_FUNCTIONS,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_009",
            prompt = "When transaction starts, grant must be eventually guaranteed to occur before sim ends",
            targetSva = "req |-> s_eventually gnt",
            acceptableAnswers = listOf("req |=> s_eventually gnt", "req |-> s_eventually gnt"),
            tokens = listOf("req", "|->", "s_eventually", "gnt"),
            distractors = listOf("always", "|=>", "eventually"),
            explanation = "s_eventually provides strong liveness checking, catching hangs and deadlocks before simulation concludes.",
            category = SvaCategory.LTL,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_010",
            prompt = "Verify that burst_len occurs 3 times non-consecutively, ending strictly on the 3rd match",
            targetSva = "burst_start ##1 (data_valid [->3]) ##1 burst_end",
            acceptableAnswers = listOf("burst_start ##1 data_valid [->3] ##1 burst_end", "burst_start ##1 (data_valid [->3]) ##1 burst_end"),
            tokens = listOf("burst_start", "##1", "data_valid", "[->3]", "##1", "burst_end"),
            distractors = listOf("[*3]", "[=3]", "|->"),
            explanation = "Goto repetition [->3] matches when data_valid occurs 3 times non-consecutively, ending precisely on the 3rd occurrence.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Advanced"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_011",
            prompt = "Ensure signal data remains stable whenever valid is high but ready is low",
            targetSva = "(valid && !ready) |=> \$stable(data)",
            acceptableAnswers = listOf("(valid && !ready) |-> ##1 \$stable(data)", "valid && !ready |=> \$stable(data)", "(valid && !ready) |=> \$stable(data)"),
            tokens = listOf("(", "valid", "&&", "!ready", ")", "|=>", "\$stable(data)"),
            distractors = listOf("|->", "\$changed(data)", "\$rose(data)"),
            explanation = "Backpressure stability rule: if sender asserts valid and receiver isn't ready, data must remain stable in next cycle.",
            category = SvaCategory.SYSTEM_FUNCTIONS,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_012",
            prompt = "Check that ack was 0 in the previous cycle when req rises",
            targetSva = "\$rose(req) |-> \$past(ack) == 0",
            acceptableAnswers = listOf("\$rose(req) |-> \$past(ack) == 0", "\$rose(req) |-> (\$past(ack) == 1'b0)", "\$rose(req) |-> !\$past(ack)"),
            tokens = listOf("\$rose(req)", "|->", "\$past(ack)", "==", "0"),
            distractors = listOf("|=>", "\$rose(ack)", "\$fell(req)"),
            explanation = "\$rose detects rising edge of req, and \$past(ack) inspects ack value in previous cycle.",
            category = SvaCategory.SYSTEM_FUNCTIONS,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_013",
            prompt = "Verify that packet payload of 4 words is accompanied throughout by payload_valid",
            targetSva = "payload_valid throughout (pkt_start ##1 pkt_data[*4] ##1 pkt_end)",
            acceptableAnswers = listOf("payload_valid throughout pkt_start ##1 pkt_data[*4] ##1 pkt_end", "payload_valid throughout (pkt_start ##1 pkt_data[*4] ##1 pkt_end)"),
            tokens = listOf("payload_valid", "throughout", "(", "pkt_start", "##1", "pkt_data[*4]", "##1", "pkt_end", ")"),
            distractors = listOf("within", "intersect", "and"),
            explanation = "The throughout operator requires boolean payload_valid to remain asserted for the entire sequence duration.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Advanced"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_014",
            prompt = "Both tx_done and rx_done sequences must conclude on the exact same clock cycle",
            targetSva = "(tx_start ##[1:5] tx_done) intersect (rx_start ##[1:5] rx_done)",
            acceptableAnswers = listOf("(tx_start ##[1:5] tx_done) intersect (rx_start ##[1:5] rx_done)", "tx_start ##[1:5] tx_done intersect rx_start ##[1:5] rx_done"),
            tokens = listOf("(", "tx_start ##[1:5] tx_done", ")", "intersect", "(", "rx_start ##[1:5] rx_done", ")"),
            distractors = listOf("and", "or", "throughout"),
            explanation = "intersect requires both sequences to match and terminate on the exact same clock edge.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Advanced"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_015",
            prompt = "Take only the first match of req ##[1:5] ack before triggering data phase",
            targetSva = "first_match(req ##[1:5] ack) |=> data_phase",
            acceptableAnswers = listOf("first_match(req ##[1:5] ack) |=> data_phase", "first_match(req ##[1:5] ack) |-> ##1 data_phase"),
            tokens = listOf("first_match(", "req ##[1:5] ack", ")", "|=>", "data_phase"),
            distractors = listOf("|->", "intersect", "within"),
            explanation = "first_match discards later matches in the multi-match delay window, preventing duplicate threads.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Advanced"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_016",
            prompt = "Verify that frame_sync is followed by exactly 8 consecutive bytes of data",
            targetSva = "frame_sync |=> data_byte [*8]",
            acceptableAnswers = listOf("frame_sync |=> data_byte [*8]", "frame_sync |-> ##1 data_byte [*8]"),
            tokens = listOf("frame_sync", "|=>", "data_byte", "[*8]"),
            distractors = listOf("|->", "[->8]", "[=8]"),
            explanation = "frame_sync followed next cycle by 8 back-to-back cycles of data_byte.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_017",
            prompt = "Verify that interrupt line irq drops low on falling edge of clk_gate",
            targetSva = "\$fell(clk_gate) |-> !irq",
            acceptableAnswers = listOf("\$fell(clk_gate) |-> irq == 0", "\$fell(clk_gate) |-> !irq"),
            tokens = listOf("\$fell(clk_gate)", "|->", "!irq"),
            distractors = listOf("\$rose(clk_gate)", "|=>", "\$stable(irq)"),
            explanation = "\$fell checks high-to-low transition and overlapping |-> checks !irq in that cycle.",
            category = SvaCategory.SYSTEM_FUNCTIONS,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_018",
            prompt = "Verify that bus arbiter vector has at most one grant active (onehot0)",
            targetSva = "\$onehot0(gnt_bus)",
            acceptableAnswers = listOf("\$onehot0(gnt_bus)", "\$onehot0(gnt_bus) == 1"),
            tokens = listOf("\$onehot0(gnt_bus)"),
            distractors = listOf("\$onehot(gnt_bus)", "\$countones(gnt_bus) == 1", "!\$isunknown(gnt_bus)"),
            explanation = "\$onehot0 allows either 0 grants (idle) or exactly 1 grant active.",
            category = SvaCategory.SYSTEM_FUNCTIONS,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_019",
            prompt = "Verify that exactly 2 bits in mask vector are set to 1",
            targetSva = "\$countones(mask) == 2",
            acceptableAnswers = listOf("\$countones(mask) == 32'd2", "\$countones(mask) == 2"),
            tokens = listOf("\$countones(mask)", "==", "2"),
            distractors = listOf("\$onehot(mask)", "\$onehot0(mask)", "3"),
            explanation = "\$countones counts the total number of set bits in the bit-vector.",
            category = SvaCategory.SYSTEM_FUNCTIONS,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_020",
            prompt = "Whenever error_flag asserts, immediately reject assertion execution asynchronously",
            targetSva = "reject_on(error_flag) (req |=> ack)",
            acceptableAnswers = listOf("reject_on(error_flag) req |=> ack", "reject_on(error_flag) (req |=> ack)"),
            tokens = listOf("reject_on(", "error_flag", ")", "req", "|=>", "ack"),
            distractors = listOf("accept_on", "sync_reject_on", "disable iff"),
            explanation = "reject_on immediately aborts the assertion with failure as soon as error_flag goes high.",
            category = SvaCategory.ABORTS,
            difficulty = "Advanced"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_021",
            prompt = "Whenever soft_reset asserts, immediately accept assertion execution as passing",
            targetSva = "accept_on(soft_reset) (req |=> ##[1:10] ack)",
            acceptableAnswers = listOf("accept_on(soft_reset) (req |=> ##[1:10] ack)", "accept_on(soft_reset) req |=> ##[1:10] ack"),
            tokens = listOf("accept_on(", "soft_reset", ")", "req", "|=>", "##[1:10]", "ack"),
            distractors = listOf("reject_on", "sync_accept_on", "disable iff"),
            explanation = "accept_on triggers an immediate passing verdict when condition becomes true.",
            category = SvaCategory.ABORTS,
            difficulty = "Advanced"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_022",
            prompt = "On clock edge, synchronously abort with failure if fault is active",
            targetSva = "sync_reject_on(fault) (start |=> done)",
            acceptableAnswers = listOf("sync_reject_on(fault) (start |=> done)", "sync_reject_on(fault) start |=> done"),
            tokens = listOf("sync_reject_on(", "fault", ")", "start", "|=>", "done"),
            distractors = listOf("sync_accept_on", "reject_on", "accept_on"),
            explanation = "sync_reject_on samples abort on clock edge rather than purely asynchronously.",
            category = SvaCategory.ABORTS,
            difficulty = "Advanced"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_023",
            prompt = "When write mode is set, ack must come in 1 cycle, otherwise in 2 cycles",
            targetSva = "valid |-> if (wr_mode) ##1 wr_ack else ##2 rd_ack",
            acceptableAnswers = listOf("valid |-> (if (wr_mode) ##1 wr_ack else ##2 rd_ack)", "valid |-> if (wr_mode) ##1 wr_ack else ##2 rd_ack"),
            tokens = listOf("valid", "|->", "if (wr_mode)", "##1 wr_ack", "else", "##2 rd_ack"),
            distractors = listOf("|=>", "and", "or"),
            explanation = "Conditional property evaluation inside SVA using if-else construct.",
            category = SvaCategory.IMPLICATION,
            difficulty = "Advanced"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_024",
            prompt = "Ensure signal data_in was identical 2 cycles ago to current data_out",
            targetSva = "data_out == \$past(data_in, 2)",
            acceptableAnswers = listOf("data_out == \$past(data_in, 2)", "\$past(data_in, 2) == data_out"),
            tokens = listOf("data_out", "==", "\$past(data_in, 2)"),
            distractors = listOf("\$past(data_in)", "\$rose(data_in)", "\$stable(data_in)"),
            explanation = "\$past(sig, N) reads sampled value from N clock cycles in the past.",
            category = SvaCategory.SYSTEM_FUNCTIONS,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_025",
            prompt = "Verify that pulse is high for exactly 1 cycle (rising then falling edge)",
            targetSva = "\$rose(pulse) |=> \$fell(pulse)",
            acceptableAnswers = listOf("\$rose(pulse) |-> ##1 \$fell(pulse)", "\$rose(pulse) |=> \$fell(pulse)"),
            tokens = listOf("\$rose(pulse)", "|=>", "\$fell(pulse)"),
            distractors = listOf("\$stable(pulse)", "|->", "\$rose(pulse)"),
            explanation = "A 1-cycle strobe rises at cycle N and must fall at cycle N+1.",
            category = SvaCategory.SYSTEM_FUNCTIONS,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_026",
            prompt = "Verify that ready holds low for exactly 1 consecutive cycles after stall",
            targetSva = "stall |=> !ready [*1]",
            acceptableAnswers = listOf("stall |=> (!ready) [*1]", "stall |-> ##1 !ready [*1]", "stall |=> !ready [*1]"),
            tokens = listOf("stall", "|=>", "!ready", "[*1]"),
            distractors = listOf("|->", "[->1]", "[=1]"),
            explanation = "Consecutive repetition [*1] ensures !ready is sampled for 1 cycles after stall.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Medium"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_027",
            prompt = "When tx_req is asserted, tx_ack must arrive after exactly 2 clock cycles",
            targetSva = "tx_req |-> ##2 tx_ack",
            acceptableAnswers = listOf("tx_req |=> ##1 tx_ack", "tx_req |-> ##2 tx_ack"),
            tokens = listOf("tx_req", "|->", "##2", "tx_ack"),
            distractors = listOf("|=>", "[*2]", "##3"),
            explanation = "Fixed delay ##2 requires tx_ack exactly 2 cycles following tx_req.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_028",
            prompt = "Verify that heartbeat changed value from previous cycle at tick 3",
            targetSva = "\$changed(heartbeat)",
            acceptableAnswers = listOf("\$past(heartbeat) != heartbeat", "\$changed(heartbeat)", "heartbeat != \$past(heartbeat)"),
            tokens = listOf("\$changed(heartbeat)"),
            distractors = listOf("\$stable(heartbeat)", "\$rose(heartbeat)", "\$fell(heartbeat)"),
            explanation = "\$changed returns 1 if signal is not equal to its sampled value from the prior cycle.",
            category = SvaCategory.SYSTEM_FUNCTIONS,
            difficulty = "Basic"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_029",
            prompt = "Event trigger must happen 4 times non-consecutively before finish, allowing extra cycles after",
            targetSva = "trigger [=4] ##1 finish",
            acceptableAnswers = listOf("(trigger [=4]) ##1 finish", "trigger [=4] ##1 finish"),
            tokens = listOf("trigger", "[=4]", "##1", "finish"),
            distractors = listOf("[->4]", "[*4]", "|=>"),
            explanation = "Non-consecutive repetition [=4] allows delay after the 4-th occurrence before finish.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Advanced"
        ),
        SvaPracticeChallenge(
            id = "sva_chal_030",
            prompt = "Ensure sub-sequence sub_seq_5 occurs entirely within main_frame_5",
            targetSva = "sub_seq_5 within main_frame_5",
            acceptableAnswers = listOf("(sub_seq_5) within (main_frame_5)", "sub_seq_5 within main_frame_5"),
            tokens = listOf("sub_seq_5", "within", "main_frame_5"),
            distractors = listOf("throughout", "intersect", "and"),
            explanation = "within operator checks that sub-sequence occurs within the timeframe of main sequence.",
            category = SvaCategory.SEQUENCES,
            difficulty = "Advanced"
        ),
    )
}
