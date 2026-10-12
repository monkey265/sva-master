package com.svamaster.model

enum class VerdictStatus {
    PASS,
    FAIL,
    VACUOUS
}

data class WaveformVerdict(
    val status: VerdictStatus,
    val title: String,
    val explanation: String,
    val triggerCycle: Int? = null,
    val matchCycle: Int? = null,
    val failCycle: Int? = null
)

data class WaveformSignal(
    val name: String,
    val values: List<Int>,
    val isClock: Boolean = false,
    val isBus: Boolean = false,
    val busHexValues: List<String> = emptyList()
)

data class WaveformScenario(
    val id: String,
    val title: String,
    val assertionExpr: String,
    val category: SvaCategory,
    val description: String,
    val numCycles: Int = 8,
    val signals: List<WaveformSignal>,
    val antecedentCycle: Int? = 1,
    val consequentMinCycle: Int? = 2,
    val consequentMaxCycle: Int? = 4,
    val evaluate: (signals: Map<String, List<Int>>) -> WaveformVerdict
)

object SvaWaveformScenarios {

    val all: List<WaveformScenario> = listOf(
        // 1. Handshake with Bounded Delay ##[1:3]
        WaveformScenario(
            id = "wave_handshake_bounded",
            title = "Bounded Handshake (##[1:3])",
            assertionExpr = "req |-> ##[1:3] ack;",
            category = SvaCategory.IMPLICATION,
            description = "Whenever req is asserted, ack must follow within 1 to 3 clock cycles.",
            numCycles = 8,
            antecedentCycle = 1,
            consequentMinCycle = 2,
            consequentMaxCycle = 4,
            signals = listOf(
                WaveformSignal("clk", listOf(1, 1, 1, 1, 1, 1, 1, 1), isClock = true),
                WaveformSignal("req", listOf(0, 1, 0, 0, 0, 0, 0, 0)),
                WaveformSignal("ack", listOf(0, 0, 0, 1, 0, 0, 0, 0))
            ),
            evaluate = { sigs ->
                val req = sigs["req"] ?: emptyList()
                val ack = sigs["ack"] ?: emptyList()
                val trigger = req.indexOfFirst { it == 1 }
                if (trigger == -1) {
                    WaveformVerdict(
                        status = VerdictStatus.VACUOUS,
                        title = "Vacuous Pass (No Trigger)",
                        explanation = "Antecedent `req` never asserted high; assertion passes vacuously without exercising the consequent."
                    )
                } else {
                    val minC = trigger + 1
                    val maxC = (trigger + 3).coerceAtMost(req.size - 1)
                    val ackIdx = (minC..maxC).firstOrNull { it < ack.size && ack[it] == 1 }
                    if (ackIdx != null) {
                        val latency = ackIdx - trigger
                        WaveformVerdict(
                            status = VerdictStatus.PASS,
                            title = "Assertion Passed (Matched ##$latency)",
                            explanation = "Triggered by `req` at Cycle $trigger. Consequent `ack` asserted at Cycle $ackIdx within the ##[1:3] deadline window.",
                            triggerCycle = trigger,
                            matchCycle = ackIdx
                        )
                    } else {
                        WaveformVerdict(
                            status = VerdictStatus.FAIL,
                            title = "Assertion Violation (Ack Timeout)",
                            explanation = "`req` asserted at Cycle $trigger, but `ack` remained low through cycles $minC..$maxC. Window exceeded without acknowledgment!",
                            triggerCycle = trigger,
                            failCycle = maxC
                        )
                    }
                }
            }
        ),

        // 2. AXI4-Stream Valid & Data Stability
        WaveformScenario(
            id = "wave_axi_stream_hold",
            title = "AXI4-Stream Handshake Hold",
            assertionExpr = "(tvalid && !tready) |=> (tvalid && \$stable(tdata));",
            category = SvaCategory.PROTOCOL_PACKS,
            description = "When stalled, TVALID must remain asserted and TDATA must stay stable until acknowledged.",
            numCycles = 8,
            antecedentCycle = 1,
            consequentMinCycle = 2,
            consequentMaxCycle = 2,
            signals = listOf(
                WaveformSignal("aclk", listOf(1, 1, 1, 1, 1, 1, 1, 1), isClock = true),
                WaveformSignal("tvalid", listOf(0, 1, 1, 0, 0, 0, 0, 0)),
                WaveformSignal("tready", listOf(0, 0, 1, 0, 0, 0, 0, 0)),
                WaveformSignal(
                    "tdata",
                    listOf(0, 1, 1, 0, 0, 0, 0, 0),
                    isBus = true,
                    busHexValues = listOf("xx", "0xA5", "0xA5", "0x00", "0x00", "0x00", "0x00", "0x00")
                )
            ),
            evaluate = { sigs ->
                val tvalid = sigs["tvalid"] ?: emptyList()
                val tready = sigs["tready"] ?: emptyList()
                val tdata = sigs["tdata"] ?: emptyList()
                val stallIdx = (0 until tvalid.size - 1).firstOrNull { tvalid[it] == 1 && tready[it] == 0 }
                if (stallIdx == null) {
                    WaveformVerdict(
                        status = VerdictStatus.VACUOUS,
                        title = "Vacuous Pass (No Stall Phase)",
                        explanation = "No cycle encountered `tvalid=1 && tready=0`. Handshake was either idle or completed immediately."
                    )
                } else {
                    val nextC = stallIdx + 1
                    val validHeld = nextC < tvalid.size && tvalid[nextC] == 1
                    val dataHeld = nextC < tdata.size && tdata[nextC] == tdata[stallIdx]
                    if (!validHeld) {
                        WaveformVerdict(
                            status = VerdictStatus.FAIL,
                            title = "Protocol Violation: TVALID Dropped",
                            explanation = "TVALID dropped to 0 at Cycle $nextC before TREADY was acknowledged. AMBA specification violated!",
                            triggerCycle = stallIdx,
                            failCycle = nextC
                        )
                    } else if (!dataHeld) {
                        WaveformVerdict(
                            status = VerdictStatus.FAIL,
                            title = "Protocol Violation: TDATA Changed",
                            explanation = "TDATA payload changed at Cycle $nextC while transfer was waiting for backpressure acknowledgment!",
                            triggerCycle = stallIdx,
                            failCycle = nextC
                        )
                    } else {
                        WaveformVerdict(
                            status = VerdictStatus.PASS,
                            title = "AXI Protocol Adherence Verified",
                            explanation = "TVALID and TDATA remained strictly invariant across stall cycle $stallIdx into cycle $nextC.",
                            triggerCycle = stallIdx,
                            matchCycle = nextC
                        )
                    }
                }
            }
        ),

        // 3. APB4 Setup to Access Phase
        WaveformScenario(
            id = "wave_apb4_transfer",
            title = "APB4 Setup to Access Progression",
            assertionExpr = "(psel && !penable) |=> (psel && penable);",
            category = SvaCategory.PROTOCOL_PACKS,
            description = "Setup phase (PSEL=1, PENABLE=0) must transition to Access phase (PSEL=1, PENABLE=1) next cycle.",
            numCycles = 8,
            antecedentCycle = 1,
            consequentMinCycle = 2,
            consequentMaxCycle = 2,
            signals = listOf(
                WaveformSignal("pclk", listOf(1, 1, 1, 1, 1, 1, 1, 1), isClock = true),
                WaveformSignal("psel", listOf(0, 1, 1, 0, 0, 0, 0, 0)),
                WaveformSignal("penable", listOf(0, 0, 1, 0, 0, 0, 0, 0)),
                WaveformSignal("pready", listOf(0, 0, 1, 0, 0, 0, 0, 0))
            ),
            evaluate = { sigs ->
                val psel = sigs["psel"] ?: emptyList()
                val penable = sigs["penable"] ?: emptyList()
                val setupIdx = (0 until psel.size - 1).firstOrNull { psel[it] == 1 && penable[it] == 0 }
                if (setupIdx == null) {
                    WaveformVerdict(
                        status = VerdictStatus.VACUOUS,
                        title = "Vacuous Pass (No Setup Phase)",
                        explanation = "No APB Setup phase (`psel=1, penable=0`) was initiated."
                    )
                } else {
                    val nextC = setupIdx + 1
                    val pselNext = nextC < psel.size && psel[nextC] == 1
                    val penableNext = nextC < penable.size && penable[nextC] == 1
                    if (pselNext && penableNext) {
                        WaveformVerdict(
                            status = VerdictStatus.PASS,
                            title = "APB Two-Phase Transition Valid",
                            explanation = "Setup at Cycle $setupIdx correctly transitioned to Access phase (`psel=1, penable=1`) at Cycle $nextC.",
                            triggerCycle = setupIdx,
                            matchCycle = nextC
                        )
                    } else {
                        WaveformVerdict(
                            status = VerdictStatus.FAIL,
                            title = "Illegal APB Phase Transition",
                            explanation = "Setup at Cycle $setupIdx failed to establish Access phase (`psel=1, penable=1`) at Cycle $nextC.",
                            triggerCycle = setupIdx,
                            failCycle = nextC
                        )
                    }
                }
            }
        ),

        // 4. Overlapping Implication (|->)
        WaveformScenario(
            id = "wave_overlapping",
            title = "Overlapping Implication (|->)",
            assertionExpr = "req |-> ack;",
            category = SvaCategory.IMPLICATION,
            description = "Evaluates whether ack is high in the EXACT SAME clock cycle as req.",
            numCycles = 8,
            antecedentCycle = 2,
            consequentMinCycle = 2,
            consequentMaxCycle = 2,
            signals = listOf(
                WaveformSignal("clk", listOf(1, 1, 1, 1, 1, 1, 1, 1), isClock = true),
                WaveformSignal("req", listOf(0, 0, 1, 0, 0, 0, 0, 0)),
                WaveformSignal("ack", listOf(0, 0, 1, 0, 0, 0, 0, 0))
            ),
            evaluate = { sigs ->
                val req = sigs["req"] ?: emptyList()
                val ack = sigs["ack"] ?: emptyList()
                val trigger = req.indexOfFirst { it == 1 }
                if (trigger == -1) {
                    WaveformVerdict(
                        status = VerdictStatus.VACUOUS,
                        title = "Vacuous Pass",
                        explanation = "Antecedent `req` never asserted high."
                    )
                } else {
                    if (trigger < ack.size && ack[trigger] == 1) {
                        WaveformVerdict(
                            status = VerdictStatus.PASS,
                            title = "Overlapping Match (Same Cycle ##0)",
                            explanation = "Both `req` and `ack` are 1 at Cycle $trigger. Same-cycle implication satisfied.",
                            triggerCycle = trigger,
                            matchCycle = trigger
                        )
                    } else {
                        WaveformVerdict(
                            status = VerdictStatus.FAIL,
                            title = "Overlapping Violation at Cycle $trigger",
                            explanation = "`req=1` at Cycle $trigger, but `ack` is 0 in the same cycle. Overlapping implication failed!",
                            triggerCycle = trigger,
                            failCycle = trigger
                        )
                    }
                }
            }
        ),

        // 5. Non-Overlapping Implication (|=>)
        WaveformScenario(
            id = "wave_non_overlapping",
            title = "Non-Overlapping Implication (|=>)",
            assertionExpr = "req |=> ack;",
            category = SvaCategory.IMPLICATION,
            description = "Evaluates whether ack is high in the cycle immediately following req (##1 delay).",
            numCycles = 8,
            antecedentCycle = 1,
            consequentMinCycle = 2,
            consequentMaxCycle = 2,
            signals = listOf(
                WaveformSignal("clk", listOf(1, 1, 1, 1, 1, 1, 1, 1), isClock = true),
                WaveformSignal("req", listOf(0, 1, 0, 0, 0, 0, 0, 0)),
                WaveformSignal("ack", listOf(0, 0, 1, 0, 0, 0, 0, 0))
            ),
            evaluate = { sigs ->
                val req = sigs["req"] ?: emptyList()
                val ack = sigs["ack"] ?: emptyList()
                val trigger = req.indexOfFirst { it == 1 }
                if (trigger == -1) {
                    WaveformVerdict(
                        status = VerdictStatus.VACUOUS,
                        title = "Vacuous Pass",
                        explanation = "`req` was never asserted."
                    )
                } else {
                    val nextC = trigger + 1
                    if (nextC < ack.size && ack[nextC] == 1) {
                        WaveformVerdict(
                            status = VerdictStatus.PASS,
                            title = "Next-Cycle Implication Satisfied",
                            explanation = "`req` at Cycle $trigger followed by `ack=1` at Cycle $nextC (##1).",
                            triggerCycle = trigger,
                            matchCycle = nextC
                        )
                    } else {
                        WaveformVerdict(
                            status = VerdictStatus.FAIL,
                            title = "Violation at Cycle $nextC",
                            explanation = "`req` asserted at Cycle $trigger, but `ack` was 0 at Cycle $nextC.",
                            triggerCycle = trigger,
                            failCycle = nextC
                        )
                    }
                }
            }
        ),

        // 6. Synchronous FIFO Overflow Guard
        WaveformScenario(
            id = "wave_fifo_overflow",
            title = "Synchronous FIFO Overflow Guard",
            assertionExpr = "(fifo_full && !pop) |-> !push;",
            category = SvaCategory.PROTOCOL_PACKS,
            description = "When FIFO is full without simultaneous pop, push is strictly prohibited.",
            numCycles = 8,
            antecedentCycle = 2,
            consequentMinCycle = 2,
            consequentMaxCycle = 2,
            signals = listOf(
                WaveformSignal("clk", listOf(1, 1, 1, 1, 1, 1, 1, 1), isClock = true),
                WaveformSignal("fifo_full", listOf(0, 0, 1, 1, 0, 0, 0, 0)),
                WaveformSignal("pop", listOf(0, 0, 0, 1, 0, 0, 0, 0)),
                WaveformSignal("push", listOf(1, 1, 0, 0, 1, 0, 0, 0))
            ),
            evaluate = { sigs ->
                val full = sigs["fifo_full"] ?: emptyList()
                val pop = sigs["pop"] ?: emptyList()
                val push = sigs["push"] ?: emptyList()
                val boundaryCycle = (0 until full.size).firstOrNull { full[it] == 1 && (it >= pop.size || pop[it] == 0) }
                if (boundaryCycle == null) {
                    WaveformVerdict(
                        status = VerdictStatus.VACUOUS,
                        title = "Vacuous Pass (Never Full Without Pop)",
                        explanation = "FIFO was never in full state without a concurrent read pop."
                    )
                } else {
                    val isPush = boundaryCycle < push.size && push[boundaryCycle] == 1
                    if (isPush) {
                        WaveformVerdict(
                            status = VerdictStatus.FAIL,
                            title = "FATAL: FIFO Overflow Violation!",
                            explanation = "Illegal write! `push=1` attempted at Cycle $boundaryCycle while FIFO was full with `pop=0`. Data corrupted!",
                            triggerCycle = boundaryCycle,
                            failCycle = boundaryCycle
                        )
                    } else {
                        WaveformVerdict(
                            status = VerdictStatus.PASS,
                            title = "FIFO Overflow Invariant Preserved",
                            explanation = "At Cycle $boundaryCycle (FIFO full and pop=0), `push` was correctly suppressed (0).",
                            triggerCycle = boundaryCycle,
                            matchCycle = boundaryCycle
                        )
                    }
                }
            }
        ),

        // 7. Consecutive Repetition [*2]
        WaveformScenario(
            id = "wave_repetition",
            title = "Consecutive Repetition [*2]",
            assertionExpr = "start ##1 ready [*2] ##1 done;",
            category = SvaCategory.SEQUENCES,
            description = "Start followed by ready high for 2 consecutive cycles, followed by done.",
            numCycles = 8,
            antecedentCycle = 1,
            consequentMinCycle = 2,
            consequentMaxCycle = 4,
            signals = listOf(
                WaveformSignal("clk", listOf(1, 1, 1, 1, 1, 1, 1, 1), isClock = true),
                WaveformSignal("start", listOf(0, 1, 0, 0, 0, 0, 0, 0)),
                WaveformSignal("ready", listOf(0, 0, 1, 1, 0, 0, 0, 0)),
                WaveformSignal("done", listOf(0, 0, 0, 0, 1, 0, 0, 0))
            ),
            evaluate = { sigs ->
                val start = sigs["start"] ?: emptyList()
                val ready = sigs["ready"] ?: emptyList()
                val done = sigs["done"] ?: emptyList()
                val trigger = start.indexOfFirst { it == 1 }
                if (trigger == -1) {
                    WaveformVerdict(
                        status = VerdictStatus.VACUOUS,
                        title = "Vacuous Pass",
                        explanation = "`start` never occurred."
                    )
                } else {
                    val r1 = trigger + 1
                    val r2 = trigger + 2
                    val d = trigger + 3
                    val r1Ok = r1 < ready.size && ready[r1] == 1
                    val r2Ok = r2 < ready.size && ready[r2] == 1
                    val dOk = d < done.size && done[d] == 1
                    if (!r1Ok || !r2Ok) {
                        val failC = if (!r1Ok) r1 else r2
                        WaveformVerdict(
                            status = VerdictStatus.FAIL,
                            title = "Repetition Interrupted",
                            explanation = "`ready` was not consecutively asserted across cycles $r1 and $r2.",
                            triggerCycle = trigger,
                            failCycle = failC
                        )
                    } else if (!dOk) {
                        WaveformVerdict(
                            status = VerdictStatus.FAIL,
                            title = "Missing Sequence Terminator",
                            explanation = "`ready[*2]` matched, but `done` was not asserted at Cycle $d.",
                            triggerCycle = trigger,
                            failCycle = d
                        )
                    } else {
                        WaveformVerdict(
                            status = VerdictStatus.PASS,
                            title = "Sequence Matched Cleanly",
                            explanation = "`start` (C$trigger) -> `ready[*2]` (C$r1, C$r2) -> `done` (C$d) completed successfully.",
                            triggerCycle = trigger,
                            matchCycle = d
                        )
                    }
                }
            }
        ),

        // 8. One-Hot Grant Arbiter
        WaveformScenario(
            id = "wave_onehot_arb",
            title = "Arbiter One-Hot Grant Safety",
            assertionExpr = "(|req) |=> \$onehot(gnt);",
            category = SvaCategory.SYSTEM_FUNCTIONS,
            description = "Whenever any requester asserts (|req), gnt must be strictly one-hot next cycle.",
            numCycles = 8,
            antecedentCycle = 1,
            consequentMinCycle = 2,
            consequentMaxCycle = 2,
            signals = listOf(
                WaveformSignal("clk", listOf(1, 1, 1, 1, 1, 1, 1, 1), isClock = true),
                WaveformSignal("req0", listOf(0, 1, 0, 0, 0, 0, 0, 0)),
                WaveformSignal("req1", listOf(0, 1, 0, 0, 0, 0, 0, 0)),
                WaveformSignal("gnt0", listOf(0, 0, 1, 0, 0, 0, 0, 0)),
                WaveformSignal("gnt1", listOf(0, 0, 0, 0, 0, 0, 0, 0))
            ),
            evaluate = { sigs ->
                val r0 = sigs["req0"] ?: emptyList()
                val r1 = sigs["req1"] ?: emptyList()
                val g0 = sigs["gnt0"] ?: emptyList()
                val g1 = sigs["gnt1"] ?: emptyList()
                val trigger = (0 until r0.size - 1).firstOrNull { (r0.getOrElse(it) { 0 } == 1) || (r1.getOrElse(it) { 0 } == 1) }
                if (trigger == null) {
                    WaveformVerdict(
                        status = VerdictStatus.VACUOUS,
                        title = "Vacuous Pass",
                        explanation = "No bus requests occurred."
                    )
                } else {
                    val nextC = trigger + 1
                    val grants = (if (g0.getOrElse(nextC) { 0 } == 1) 1 else 0) + (if (g1.getOrElse(nextC) { 0 } == 1) 1 else 0)
                    if (grants == 1) {
                        WaveformVerdict(
                            status = VerdictStatus.PASS,
                            title = "One-Hot Mutual Exclusion Enforced",
                            explanation = "Exactly 1 requester granted at Cycle $nextC following competing requests at Cycle $trigger.",
                            triggerCycle = trigger,
                            matchCycle = nextC
                        )
                    } else {
                        WaveformVerdict(
                            status = VerdictStatus.FAIL,
                            title = "Safety Breach: Not One-Hot ($grants granted)",
                            explanation = "At Cycle $nextC, $grants grants were asserted simultaneously! Mutual exclusion broken.",
                            triggerCycle = trigger,
                            failCycle = nextC
                        )
                    }
                }
            }
        )
    )
}
