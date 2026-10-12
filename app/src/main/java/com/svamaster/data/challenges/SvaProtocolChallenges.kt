package com.svamaster.data.challenges

import com.svamaster.model.SvaCategory
import com.svamaster.model.SvaPracticeChallenge

object SvaProtocolChallenges {
    val list: List<SvaPracticeChallenge> = listOf(
        SvaPracticeChallenge(
            id = "sva_proto_001",
            prompt = "AXI-Stream TVALID Persistence: Once tvalid is asserted, if tready is low, tvalid must remain asserted in the next cycle",
            targetSva = "(tvalid && !tready) |=> tvalid",
            acceptableAnswers = listOf(
                "(tvalid && !tready) |=> tvalid",
                "tvalid && !tready |=> tvalid",
                "(tvalid && !tready) |-> ##1 tvalid",
                "assert property ((tvalid && !tready) |=> tvalid);",
                "assert property ((tvalid && !tready) |=> tvalid)"
            ),
            tokens = listOf("(", "tvalid", "&&", "!tready", ")", "|=>", "tvalid"),
            distractors = listOf("|->", "##0", "!tvalid", "\$fell(tvalid)"),
            explanation = "AXI4-Stream standard: Once TVALID is asserted, it MUST remain asserted until the handshake completes (TREADY is high).",
            category = SvaCategory.PROTOCOL_PACKS,
            difficulty = "Basic",
            fullPropertyDefinition = "property p_axis_valid_hold;\n  @(posedge clk) disable iff (!rst_n)\n  (tvalid && !tready) |=> tvalid;\nendproperty",
            assertionDirective = "assert property (p_axis_valid_hold);"
        ),
        SvaPracticeChallenge(
            id = "sva_proto_002",
            prompt = "AXI-Stream Payload Stability: When tvalid is high and tready is low, tdata must remain unchanged in the next cycle",
            targetSva = "(tvalid && !tready) |=> \$stable(tdata)",
            acceptableAnswers = listOf(
                "(tvalid && !tready) |=> \$stable(tdata)",
                "tvalid && !tready |=> \$stable(tdata)",
                "(tvalid && !tready) |-> ##1 \$stable(tdata)",
                "assert property ((tvalid && !tready) |=> \$stable(tdata));",
                "assert property ((tvalid && !tready) |=> \$stable(tdata))"
            ),
            tokens = listOf("(", "tvalid", "&&", "!tready", ")", "|=>", "\$stable(tdata)"),
            distractors = listOf("|->", "\$changed(tdata)", "\$rose(tdata)", "##0"),
            explanation = "AXI4 specification: Payload signals (TDATA, TSTRB, TKEEP, TUSER) must not change while TVALID is high waiting for TREADY.",
            category = SvaCategory.PROTOCOL_PACKS,
            difficulty = "Basic",
            fullPropertyDefinition = "property p_axis_data_stable;\n  @(posedge clk) disable iff (!rst_n)\n  (tvalid && !tready) |=> \$stable(tdata);\nendproperty",
            assertionDirective = "assert property (p_axis_data_stable);"
        ),
        SvaPracticeChallenge(
            id = "sva_proto_003",
            prompt = "AXI-Stream Full Handshake Hold: Stalled transfers must hold both tvalid and stable tdata across clock ticks",
            targetSva = "(tvalid && !tready) |=> (tvalid && \$stable(tdata))",
            acceptableAnswers = listOf(
                "(tvalid && !tready) |=> (tvalid && \$stable(tdata))",
                "tvalid && !tready |=> tvalid && \$stable(tdata)",
                "(tvalid && !tready) |-> ##1 (tvalid && \$stable(tdata))",
                "assert property ((tvalid && !tready) |=> (tvalid && \$stable(tdata)));",
                "assert property ((tvalid && !tready) |=> (tvalid && \$stable(tdata)))"
            ),
            tokens = listOf("(", "tvalid", "&&", "!tready", ")", "|=>", "(", "tvalid", "&&", "\$stable(tdata)", ")"),
            distractors = listOf("|->", "##0", "\$fell(tvalid)", "\$changed(tdata)"),
            explanation = "Canonical ARM AMBA handshake rule combining master validation and payload stability invariance.",
            category = SvaCategory.PROTOCOL_PACKS,
            difficulty = "Medium",
            fullPropertyDefinition = "property p_axis_handshake_hold;\n  @(posedge clk) disable iff (!rst_n)\n  (tvalid && !tready) |=> (tvalid && \$stable(tdata));\nendproperty",
            assertionDirective = "assert property (p_axis_handshake_hold);"
        ),
        SvaPracticeChallenge(
            id = "sva_proto_004",
            prompt = "AXI4 Write Response Latency: After awvalid && awready handshake, write response bvalid must assert within 1 to 16 cycles",
            targetSva = "(awvalid && awready) |-> ##[1:16] bvalid",
            acceptableAnswers = listOf(
                "(awvalid && awready) |-> ##[1:16] bvalid",
                "awvalid && awready |-> ##[1:16] bvalid",
                "(awvalid && awready) |=> ##[0:15] bvalid",
                "assert property ((awvalid && awready) |-> ##[1:16] bvalid);",
                "assert property ((awvalid && awready) |-> ##[1:16] bvalid)"
            ),
            tokens = listOf("(", "awvalid", "&&", "awready", ")", "|->", "##[1:16]", "bvalid"),
            distractors = listOf("|=>", "##[0:16]", "!bvalid", "bready"),
            explanation = "Bounded write completion: Once the write address is accepted, the slave must produce BVALID within the negotiated timeout window.",
            category = SvaCategory.PROTOCOL_PACKS,
            difficulty = "Medium",
            fullPropertyDefinition = "property p_axi_bvalid_timeout;\n  @(posedge clk) disable iff (!rst_n)\n  (awvalid && awready) |-> ##[1:16] bvalid;\nendproperty",
            assertionDirective = "assert property (p_axi_bvalid_timeout);"
        ),
        SvaPracticeChallenge(
            id = "sva_proto_005",
            prompt = "AXI4 Read Response Latency: After read address arvalid && arready handshake, read data rvalid must arrive within 1 to 32 cycles",
            targetSva = "(arvalid && arready) |-> ##[1:32] rvalid",
            acceptableAnswers = listOf(
                "(arvalid && arready) |-> ##[1:32] rvalid",
                "arvalid && arready |-> ##[1:32] rvalid",
                "(arvalid && arready) |=> ##[0:31] rvalid",
                "assert property ((arvalid && arready) |-> ##[1:32] rvalid);",
                "assert property ((arvalid && arready) |-> ##[1:32] rvalid)"
            ),
            tokens = listOf("(", "arvalid", "&&", "arready", ")", "|->", "##[1:32]", "rvalid"),
            distractors = listOf("|=>", "##32", "!rvalid", "rready"),
            explanation = "Read latency bounding ensures memory controllers return read data before downstream bus lockup.",
            category = SvaCategory.PROTOCOL_PACKS,
            difficulty = "Medium",
            fullPropertyDefinition = "property p_axi_rvalid_latency;\n  @(posedge clk) disable iff (!rst_n)\n  (arvalid && arready) |-> ##[1:32] rvalid;\nendproperty",
            assertionDirective = "assert property (p_axi_rvalid_latency);"
        ),
        SvaPracticeChallenge(
            id = "sva_proto_006",
            prompt = "APB4 Setup to Access Phase: When psel is asserted and penable is low, in the next cycle psel and penable must both be high",
            targetSva = "(psel && !penable) |=> (psel && penable)",
            acceptableAnswers = listOf(
                "(psel && !penable) |=> (psel && penable)",
                "psel && !penable |=> psel && penable",
                "(psel && !penable) |-> ##1 (psel && penable)",
                "assert property ((psel && !penable) |=> (psel && penable));",
                "assert property ((psel && !penable) |=> (psel && penable))"
            ),
            tokens = listOf("(", "psel", "&&", "!penable", ")", "|=>", "(", "psel", "&&", "penable", ")"),
            distractors = listOf("|->", "pready", "!psel", "##0"),
            explanation = "ARM APB state machine: Setup phase (psel high, penable low) is followed exactly one cycle later by Access phase (psel and penable both high).",
            category = SvaCategory.PROTOCOL_PACKS,
            difficulty = "Medium",
            fullPropertyDefinition = "property p_apb_setup_to_access;\n  @(posedge clk) disable iff (!rst_n)\n  (psel && !penable) |=> (psel && penable);\nendproperty",
            assertionDirective = "assert property (p_apb_setup_to_access);"
        ),
        SvaPracticeChallenge(
            id = "sva_proto_007",
            prompt = "APB4 Access Phase Termination: When psel, penable, and pready are all asserted, penable must deassert in the next cycle",
            targetSva = "(psel && penable && pready) |=> !penable",
            acceptableAnswers = listOf(
                "(psel && penable && pready) |=> !penable",
                "psel && penable && pready |=> !penable",
                "(psel && penable && pready) |-> ##1 !penable",
                "assert property ((psel && penable && pready) |=> !penable);",
                "assert property ((psel && penable && pready) |=> !penable)"
            ),
            tokens = listOf("(", "psel", "&&", "penable", "&&", "pready", ")", "|=>", "!penable"),
            distractors = listOf("|->", "penable", "##0", "!psel"),
            explanation = "When PREADY indicates slave completion, the APB access phase ends and PENABLE drops low.",
            category = SvaCategory.PROTOCOL_PACKS,
            difficulty = "Medium",
            fullPropertyDefinition = "property p_apb_access_terminate;\n  @(posedge clk) disable iff (!rst_n)\n  (psel && penable && pready) |=> !penable;\nendproperty",
            assertionDirective = "assert property (p_apb_access_terminate);"
        ),
        SvaPracticeChallenge(
            id = "sva_proto_008",
            prompt = "APB4 Address Stability: During the transition from Setup to Access phase, paddr must remain unchanged",
            targetSva = "(psel && !penable) |=> \$stable(paddr)",
            acceptableAnswers = listOf(
                "(psel && !penable) |=> \$stable(paddr)",
                "psel && !penable |=> \$stable(paddr)",
                "(psel && !penable) |-> ##1 \$stable(paddr)",
                "assert property ((psel && !penable) |=> \$stable(paddr));",
                "assert property ((psel && !penable) |=> \$stable(paddr))"
            ),
            tokens = listOf("(", "psel", "&&", "!penable", ")", "|=>", "\$stable(paddr)"),
            distractors = listOf("|->", "\$changed(paddr)", "##0", "!psel"),
            explanation = "APB requires address PADDR to remain valid and stable throughout the entire transfer cycle.",
            category = SvaCategory.PROTOCOL_PACKS,
            difficulty = "Basic",
            fullPropertyDefinition = "property p_apb_addr_stable;\n  @(posedge clk) disable iff (!rst_n)\n  (psel && !penable) |=> \$stable(paddr);\nendproperty",
            assertionDirective = "assert property (p_apb_addr_stable);"
        ),
        SvaPracticeChallenge(
            id = "sva_proto_009",
            prompt = "Synchronous FIFO Overflow Guard: When fifo_full is asserted and pop is low, push must not be asserted",
            targetSva = "(fifo_full && !pop) |-> !push",
            acceptableAnswers = listOf(
                "(fifo_full && !pop) |-> !push",
                "fifo_full && !pop |-> !push",
                "(fifo_full && !pop) |-> (push == 0)",
                "assert property ((fifo_full && !pop) |-> !push);",
                "assert property ((fifo_full && !pop) |-> !push)"
            ),
            tokens = listOf("(", "fifo_full", "&&", "!pop", ")", "|->", "!push"),
            distractors = listOf("|=>", "push", "##1", "fifo_empty"),
            explanation = "Fundamental FIFO safety rule: Writing into a full FIFO when no read is occurring causes data corruption / overflow.",
            category = SvaCategory.PROTOCOL_PACKS,
            difficulty = "Basic",
            fullPropertyDefinition = "property p_fifo_no_overflow;\n  @(posedge clk) disable iff (!rst_n)\n  (fifo_full && !pop) |-> !push;\nendproperty",
            assertionDirective = "assert property (p_fifo_no_overflow);"
        ),
        SvaPracticeChallenge(
            id = "sva_proto_010",
            prompt = "Synchronous FIFO Underflow Guard: When fifo_empty is asserted, pop must not be asserted in that cycle",
            targetSva = "fifo_empty |-> !pop",
            acceptableAnswers = listOf(
                "fifo_empty |-> !pop",
                "(fifo_empty) |-> !pop",
                "fifo_empty |-> (pop == 0)",
                "assert property (fifo_empty |-> !pop);",
                "assert property (fifo_empty |-> !pop)"
            ),
            tokens = listOf("fifo_empty", "|->", "!pop"),
            distractors = listOf("|=>", "pop", "##1", "!push"),
            explanation = "Attempting to read from an empty FIFO returns invalid garbage data and corrupts read pointers.",
            category = SvaCategory.PROTOCOL_PACKS,
            difficulty = "Basic",
            fullPropertyDefinition = "property p_fifo_no_underflow;\n  @(posedge clk) disable iff (!rst_n)\n  fifo_empty |-> !pop;\nendproperty",
            assertionDirective = "assert property (p_fifo_no_underflow);"
        ),
        SvaPracticeChallenge(
            id = "sva_proto_011",
            prompt = "FIFO High Watermark Assertion: When fifo_count is greater than or equal to ALMOST_FULL, almost_full must be high",
            targetSva = "(fifo_count >= ALMOST_FULL) |-> almost_full",
            acceptableAnswers = listOf(
                "(fifo_count >= ALMOST_FULL) |-> almost_full",
                "fifo_count >= ALMOST_FULL |-> almost_full",
                "assert property ((fifo_count >= ALMOST_FULL) |-> almost_full);",
                "assert property ((fifo_count >= ALMOST_FULL) |-> almost_full)"
            ),
            tokens = listOf("(", "fifo_count >= ALMOST_FULL", ")", "|->", "almost_full"),
            distractors = listOf("|=>", "!almost_full", "##1", "fifo_full"),
            explanation = "Watermark thresholds warn upstream transmitters to throttle bandwidth before full backpressure occurs.",
            category = SvaCategory.PROTOCOL_PACKS,
            difficulty = "Medium",
            fullPropertyDefinition = "property p_fifo_almost_full;\n  @(posedge clk) disable iff (!rst_n)\n  (fifo_count >= ALMOST_FULL) |-> almost_full;\nendproperty",
            assertionDirective = "assert property (p_fifo_almost_full);"
        ),
        SvaPracticeChallenge(
            id = "sva_proto_012",
            prompt = "Skid Buffer Full Invariant: When upstream sends valid_i but downstream is stalled (!ready_o), skid_full must assert in the next cycle",
            targetSva = "(valid_i && !ready_o) |=> skid_full",
            acceptableAnswers = listOf(
                "(valid_i && !ready_o) |=> skid_full",
                "valid_i && !ready_o |=> skid_full",
                "(valid_i && !ready_o) |-> ##1 skid_full",
                "assert property ((valid_i && !ready_o) |=> skid_full);",
                "assert property ((valid_i && !ready_o) |=> skid_full)"
            ),
            tokens = listOf("(", "valid_i", "&&", "!ready_o", ")", "|=>", "skid_full"),
            distractors = listOf("|->", "!skid_full", "##0", "ready_o"),
            explanation = "A skid buffer absorbs 1 cycle of pipeline latency when backpressure occurs to break timing loops.",
            category = SvaCategory.PROTOCOL_PACKS,
            difficulty = "Medium",
            fullPropertyDefinition = "property p_skid_buffer_occupancy;\n  @(posedge clk) disable iff (!rst_n)\n  (valid_i && !ready_o) |=> skid_full;\nendproperty",
            assertionDirective = "assert property (p_skid_buffer_occupancy);"
        ),
        SvaPracticeChallenge(
            id = "sva_proto_013",
            prompt = "Arbiter One-Hot Grant Check: Whenever any request is asserted (|req), the grant vector gnt must be strictly one-hot in the next cycle",
            targetSva = "(|req) |=> \$onehot(gnt)",
            acceptableAnswers = listOf(
                "(|req) |=> \$onehot(gnt)",
                "|req |=> \$onehot(gnt)",
                "(|req) |-> ##1 \$onehot(gnt)",
                "assert property ((|req) |=> \$onehot(gnt));",
                "assert property ((|req) |=> \$onehot(gnt))"
            ),
            tokens = listOf("(", "|req", ")", "|=>", "\$onehot(gnt)"),
            distractors = listOf("|->", "\$onehot0(gnt)", "\$countones(gnt)", "##0"),
            explanation = "Arbiters must guarantee mutual exclusion—exactly one requester is granted the shared bus resource.",
            category = SvaCategory.PROTOCOL_PACKS,
            difficulty = "Medium",
            fullPropertyDefinition = "property p_arb_onehot_grant;\n  @(posedge clk) disable iff (!rst_n)\n  (|req) |=> \$onehot(gnt);\nendproperty",
            assertionDirective = "assert property (p_arb_onehot_grant);"
        ),
        SvaPracticeChallenge(
            id = "sva_proto_014",
            prompt = "Fixed Priority Arbiter Rule: When highest priority requester req[0] is asserted, gnt[0] must be issued in the next cycle",
            targetSva = "req[0] |=> gnt[0]",
            acceptableAnswers = listOf(
                "req[0] |=> gnt[0]",
                "(req[0]) |=> gnt[0]",
                "req[0] |-> ##1 gnt[0]",
                "assert property (req[0] |=> gnt[0]);",
                "assert property (req[0] |=> gnt[0])"
            ),
            tokens = listOf("req[0]", "|=>", "gnt[0]"),
            distractors = listOf("|->", "!gnt[0]", "##0", "req[1]"),
            explanation = "Fixed-priority arbitration mandates that request 0 always preempts all higher-index requesters.",
            category = SvaCategory.PROTOCOL_PACKS,
            difficulty = "Basic",
            fullPropertyDefinition = "property p_fixed_priority_0;\n  @(posedge clk) disable iff (!rst_n)\n  req[0] |=> gnt[0];\nendproperty",
            assertionDirective = "assert property (p_fixed_priority_0);"
        ),
        SvaPracticeChallenge(
            id = "sva_proto_015",
            prompt = "Credit-Based Flow Control: When the transmit credit counter is 0, tx_valid must not be asserted in that cycle",
            targetSva = "(credit_count == 0) |-> !tx_valid",
            acceptableAnswers = listOf(
                "(credit_count == 0) |-> !tx_valid",
                "credit_count == 0 |-> !tx_valid",
                "assert property ((credit_count == 0) |-> !tx_valid);",
                "assert property ((credit_count == 0) |-> !tx_valid)"
            ),
            tokens = listOf("(", "credit_count == 0", ")", "|->", "!tx_valid"),
            distractors = listOf("|=>", "tx_valid", "##1", "credit_count > 0"),
            explanation = "Credit-based flow control (common in PCIe, NoC) forbids transmission when receiver credit allocation is exhausted.",
            category = SvaCategory.PROTOCOL_PACKS,
            difficulty = "Medium",
            fullPropertyDefinition = "property p_credit_exhausted;\n  @(posedge clk) disable iff (!rst_n)\n  (credit_count == 0) |-> !tx_valid;\nendproperty",
            assertionDirective = "assert property (p_credit_exhausted);"
        )
    )
}
