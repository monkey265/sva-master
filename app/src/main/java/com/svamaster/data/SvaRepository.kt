package com.svamaster.data

import com.svamaster.data.challenges.SvaChallengesPart0
import com.svamaster.data.challenges.SvaChallengesPart1
import com.svamaster.data.challenges.SvaChallengesPart2
import com.svamaster.data.challenges.SvaChallengesPart3
import com.svamaster.data.challenges.SvaChallengesPart4
import com.svamaster.data.challenges.SvaProtocolChallenges
import com.svamaster.model.SvaBadge
import com.svamaster.model.SvaCategory
import com.svamaster.model.SvaItem
import com.svamaster.model.SvaPracticeChallenge
import com.svamaster.model.SvaQuizQuestion
import com.svamaster.model.SvaSandboxSample

object SvaRepository {

    val challenges: List<SvaPracticeChallenge> by lazy {
        SvaChallengesPart0.list +
        SvaChallengesPart1.list +
        SvaChallengesPart2.list +
        SvaChallengesPart3.list +
        SvaChallengesPart4.list +
        SvaProtocolChallenges.list
    }

    val items: List<SvaItem> = listOf(
        // Section 1: Directives Overview
        SvaItem(
            id = "dir_assert",
            category = SvaCategory.DIRECTIVES,
            name = "assert property",
            syntax = "a_name: assert property (p_expr) else `uvm_error(\"ID\", \"msg\");",
            summary = "Verifies design correctness continuously during dynamic simulation and formal exploration.",
            dynamicBehavior = "Fails with error / severity task on violation during simulation.",
            formalBehavior = "Prover searches for a counterexample trace violating the property.",
            fullExplanation = "The primary directive used to check safety and liveness properties. If the property antecedents match and the consequent fails, an assertion violation is fired.",
            codeExample = """
                property p_req_ack;
                  @(posedge clk) disable iff (rst)
                  req |-> ##[1:3] ack;
                endproperty
                a_req_ack: assert property (p_req_ack)
                  else `uvm_error("SVA", "Ack not received within 1-3 cycles of Req!");
            """.trimIndent(),
            waveDiagram = "clk : _|~|_|~|_|~|_\nreq : __|~~~~|_____\nack : _____|~~~~|___",
            keyTips = listOf(
                "Always add an action block (else `uvm_error / \$error) for debugging.",
                "Can be placed inside design modules, interfaces, or bind files."
            )
        ),
        SvaItem(
            id = "dir_cover",
            category = SvaCategory.DIRECTIVES,
            name = "cover property",
            syntax = "c_name: cover property (p_expr);",
            summary = "Monitors occurrence of scenarios/protocols to verify functional coverage.",
            dynamicBehavior = "Records hit counts each time property holds.",
            formalBehavior = "Finds witness trace reaching scenario (proves reachability).",
            fullExplanation = "Used to ensure that functional scenarios actually occurred during simulation, or to ask formal solvers to generate a witness wave demonstrating that a protocol sequence can be completed.",
            codeExample = """
                property p_burst_complete;
                  @(posedge clk) disable iff (rst)
                  burst_start ##1 (burst_data[*4]) ##1 burst_end;
                endproperty
                c_burst: cover property (p_burst_complete);
            """.trimIndent(),
            keyTips = listOf(
                "Crucial in formal verification to ensure assumptions didn't overconstrain and make logic unreachable.",
                "In dynamic simulation, feeds functional coverage metrics."
            )
        ),
        SvaItem(
            id = "dir_assume",
            category = SvaCategory.DIRECTIVES,
            name = "assume property",
            syntax = "u_name: assume property (p_expr);",
            summary = "Constraints on environment or primary inputs.",
            dynamicBehavior = "Fails with error like assert if violated by driver/TB.",
            formalBehavior = "Restricts the state-space of inputs; solver will not violate it.",
            fullExplanation = "Declares environmental constraints. In formal tools, assumptions define what the external environment is permitted to do, preventing false counterexamples.",
            codeExample = """
                property p_stable_inputs;
                  @(posedge clk) disable iff (rst)
                  !ready_i |-> ${'$'}stable(data_i);
                endproperty
                u_inputs: assume property (p_stable_inputs);
            """.trimIndent(),
            keyTips = listOf(
                "Beware of over-constraining in formal: over-assumption causes vacuous proofs.",
                "In simulation, assume behaves as a checker on inputs."
            )
        ),
        SvaItem(
            id = "dir_restrict",
            category = SvaCategory.DIRECTIVES,
            name = "restrict property",
            syntax = "`ifdef FORMAL\n  r_name: restrict property (p_expr);\n`endif",
            summary = "Prunes state space specifically for formal solvers without affecting simulation semantics.",
            dynamicBehavior = "Ignored in simulation (guard with `ifdef FORMAL).",
            formalBehavior = "Constrains proof exploration without creating assumptions for composition.",
            fullExplanation = "Directs formal tools to optimize the proof space for specific sub-proofs or case-splits without asserting environmental constraints across composition boundaries.",
            codeExample = """
                `ifdef FORMAL
                // Restrict formal engine to standard burst lengths for quick bounded proof
                r_burst_len: restrict property (
                  @(posedge clk) burst_len inside {4, 8, 16}
                );
                `endif
            """.trimIndent(),
            keyTips = listOf(
                "Always wrap in `ifdef FORMAL if tool complains in dynamic sim.",
                "Great for slicing large formal problems into manageable proofs."
            )
        ),

        // Section 2: Anatomy & Controls
        SvaItem(
            id = "anat_clk_reset",
            category = SvaCategory.ANATOMY,
            name = "Anatomy & Controls",
            syntax = "property p_name;\n  @(posedge clk) disable iff (rst)\n  sequence_expr |=> property_expr;\nendproperty",
            summary = "Defines sampling clock, asynchronous reset, and clean assertion scoping.",
            dynamicBehavior = "Property is evaluated on clock edge and disabled asynchronously when reset is high.",
            formalBehavior = "Solver respects clocking domain and suppresses checks during active reset.",
            fullExplanation = "Every concurrent assertion requires a clock domain to sample signals (in the Preponed region) and a reset expression. The `disable iff (condition)` clause aborts evaluation without triggering an assertion failure.",
            codeExample = """
                default clocking def_clk @(posedge clk); endclocking
                default disable iff (rst_n == 1'b0);

                property p_handshake;
                  valid_i && !ready_o |=> valid_i;
                endproperty
                a_handshake: assert property (p_handshake);
            """.trimIndent(),
            keyTips = listOf(
                "`default clocking` and `default disable iff` drastically reduce boilerplate across a module or interface.",
                "Signals in assertions are sampled in the Preponed region to avoid race conditions with RTL Active region updates."
            )
        ),

        // Section 3: Implication & Branching
        SvaItem(
            id = "imp_overlapping",
            category = SvaCategory.IMPLICATION,
            name = "Overlapping Implication (|->)",
            syntax = "antecedent |-> consequent",
            summary = "Consequent begins evaluation in the SAME cycle antecedent matches (##0).",
            dynamicBehavior = "If antecedent matches at cycle N, consequent must start matching at cycle N.",
            formalBehavior = "Formal verifies consequent on cycle N upon antecedent match at cycle N.",
            fullExplanation = "The overlapping implication `|->` requires the consequent to evaluate starting on the very same clock tick where the antecedent matches. If the antecedent is false, the assertion succeeds vacuously.",
            codeExample = """
                property p_write_enable;
                  @(posedge clk) disable iff (rst)
                  (wr_en && cs) |-> (addr < 32'h1000);
                endproperty
                a_write_addr: assert property (p_write_enable);
            """.trimIndent(),
            waveDiagram = "clk  : _|~|_|~|_|~|_\nwr_en: __|~~~~|_____\naddr : __[<0x1000]__",
            keyTips = listOf(
                "Use |-> when the condition is combinational or synchronous in the same cycle.",
                "Vacuous success: if antecedent is 0, the property passes automatically."
            )
        ),
        SvaItem(
            id = "imp_non_overlapping",
            category = SvaCategory.IMPLICATION,
            name = "Non-overlapping Implication (|=>)",
            syntax = "antecedent |=> consequent",
            summary = "Consequent begins evaluation in the NEXT cycle after antecedent matches (equivalent to |-> ##1).",
            dynamicBehavior = "If antecedent matches at cycle N, consequent starts evaluating at cycle N+1.",
            formalBehavior = "Solver proves consequent starting at N+1 after match at cycle N.",
            fullExplanation = "The non-overlapping implication `|=>` is shorthand for `|-> ##1`. It is the most common operator for registered RTL designs where an action causes an effect on the following clock cycle.",
            codeExample = """
                property p_req_ack;
                  @(posedge clk) disable iff (rst)
                  req |=> ack;
                endproperty
                a_req_ack: assert property (p_req_ack);
            """.trimIndent(),
            waveDiagram = "clk: _|~|_|~|_|~|_\nreq: _|~~|________\nack: _____|~~|____",
            keyTips = listOf(
                "Remember: `A |=> B` is identical to `A |-> ##1 B`.",
                "Standard for registered outputs."
            )
        ),
        SvaItem(
            id = "imp_conditional",
            category = SvaCategory.IMPLICATION,
            name = "Conditional (if-else in property)",
            syntax = "if (cond) P1 else P2",
            summary = "Branching evaluation inside property based on boolean expression.",
            dynamicBehavior = "Evaluates property P1 if cond is true; otherwise evaluates P2.",
            formalBehavior = "Prover splits analysis based on boolean state of condition.",
            fullExplanation = "Allows selecting between alternative properties based on the state of a control signal.",
            codeExample = """
                property p_rw_check;
                  @(posedge clk) disable iff (rst)
                  valid |-> if (write_mode) ##1 wr_ack else ##2 rd_ack;
                endproperty
                a_rw: assert property (p_rw_check);
            """.trimIndent()
        ),
        SvaItem(
            id = "imp_logic_ops",
            category = SvaCategory.IMPLICATION,
            name = "Logic Combinators (and, or, not, implies, iff)",
            syntax = "P1 and P2 | P1 or P2 | not P | P1 implies P2 | P1 iff P2",
            summary = "Boolean and temporal property conjunction, disjunction, negation, and equivalence.",
            dynamicBehavior = "Combines properties under standard truth tables for concurrent sequences.",
            formalBehavior = "Dual-rail and boolean lattice operations evaluated across all state trees.",
            fullExplanation = "IEEE 1800 provides property combinators: `P1 implies P2` is equivalent to `not P1 or P2`. `P1 iff P2` requires both properties to have identical evaluation truth values.",
            codeExample = """
                property p_dual_check;
                  @(posedge clk) disable iff (rst)
                  (req_a |=> ack_a) and (req_b |=> ack_b);
                endproperty
            """.trimIndent()
        ),

        // Section 4: LTL Operators
        SvaItem(
            id = "ltl_always",
            category = SvaCategory.LTL,
            name = "always / always [min:max]",
            syntax = "always property_expr | always [min:max] property_expr",
            summary = "Weak LTL operator: property must hold in every cycle from now until end.",
            dynamicBehavior = "Continuously checks condition every cycle. Weak: passing at sim end is ok.",
            formalBehavior = "Invariance proof across all states reachable by solver.",
            fullExplanation = "Specifies an invariant property. Bounded form `always [min:max] P` requires P to hold during the specified window of cycles.",
            codeExample = """
                property p_safe_state;
                  @(posedge clk) disable iff (rst)
                  always (!error_flag);
                endproperty
                a_safe: assert property (p_safe_state);
            """.trimIndent()
        ),
        SvaItem(
            id = "ltl_s_eventually",
            category = SvaCategory.LTL,
            name = "s_eventually (Strong Liveness)",
            syntax = "s_eventually P | s_eventually [min:max] P",
            summary = "Strong operator: P MUST occur at least once before simulation ends.",
            dynamicBehavior = "If simulation ends and P has never occurred, assertion fails (catches hangs/deadlocks).",
            formalBehavior = "Formal prover must prove liveness; failure if solver finds a loop where P never occurs.",
            fullExplanation = "The `s_` prefix designates STRONG semantics. Unlike weak operators which quietly pass if simulation terminates, strong operators flag an error if the requirement is unfulfilled when time runs out.",
            codeExample = """
                property p_eventual_grant;
                  @(posedge clk) disable iff (rst)
                  req |-> s_eventually grant;
                endproperty
                a_grant: assert property (p_eventual_grant)
                  else `uvm_fatal("DEADLOCK", "Request was never granted!");
            """.trimIndent(),
            keyTips = listOf(
                "Use s_eventually to detect system lockups, bus hangs, and infinite starvation loops.",
                "Essential for formal liveness proofs."
            )
        ),
        SvaItem(
            id = "ltl_until",
            category = SvaCategory.LTL,
            name = "until / s_until / until_with / s_until_with",
            syntax = "P1 until P2 | P1 s_until P2 | P1 until_with P2 | P1 s_until_with P2",
            summary = "Temporal persistence: P1 holds continuously until P2 becomes true.",
            dynamicBehavior = "Weak until: P2 never arriving is allowed if P1 holds forever. Strong s_until: P2 is guaranteed to arrive.",
            formalBehavior = "Verifies persistence and terminal trigger conditions.",
            fullExplanation = "- `until`: P1 holds each cycle until P2 is true (P2 optional if P1 never stops).\n- `s_until`: P1 holds until P2, AND P2 is guaranteed to happen.\n- `until_with`: Overlapping: P1 also holds in the cycle P2 becomes true.",
            codeExample = """
                property p_busy_until_done;
                  @(posedge clk) disable iff (rst)
                  start |=> busy s_until done;
                endproperty
                a_busy: assert property (p_busy_until_done);
            """.trimIndent()
        ),
        SvaItem(
            id = "ltl_nexttime",
            category = SvaCategory.LTL,
            name = "nexttime / s_nexttime",
            syntax = "nexttime P | s_nexttime P",
            summary = "P must hold in the next cycle (weak or strong).",
            dynamicBehavior = "s_nexttime fails if current cycle is the final cycle of simulation.",
            formalBehavior = "Step operator into the subsequent state transition.",
            fullExplanation = "Equivalent to `##1 P` but used in property contexts where temporal logic semantics are preferred.",
            codeExample = """
                property p_next;
                  @(posedge clk) disable iff (rst)
                  trigger |-> s_nexttime status_ok;
                endproperty
            """.trimIndent()
        ),

        // Section 5: Aborts
        SvaItem(
            id = "abort_ops",
            category = SvaCategory.ABORTS,
            name = "accept_on / reject_on / sync aborts",
            syntax = "accept_on (cond) P | reject_on (cond) P | sync_accept_on (cond) P | sync_reject_on (cond) P",
            summary = "Asynchronous or synchronous abortion with immediate success or failure.",
            dynamicBehavior = "accept_on causes instant true verdict; reject_on causes instant violation.",
            formalBehavior = "Prunes paths or introduces forced termination conditions for property evaluation.",
            fullExplanation = "- `accept_on(cond)`: Asynchronous immediate success when cond becomes true.\n- `reject_on(cond)`: Asynchronous immediate failure when cond becomes true.\n- `sync_accept_on(cond)`: Evaluated synchronously on clock edge.\n- `sync_reject_on(cond)`: Evaluated synchronously on clock edge.",
            codeExample = """
                property p_with_abort;
                  @(posedge clk)
                  accept_on (soft_reset)
                  reject_on (hard_fault)
                  (start |=> ##[1:10] complete);
                endproperty
                a_abort: assert property (p_with_abort);
            """.trimIndent()
        ),

        // Section 6: Sequences & Repetitions
        SvaItem(
            id = "seq_delay",
            category = SvaCategory.SEQUENCES,
            name = "Delay (##N, ##[min:max], ##[1:$])",
            syntax = "S1 ##N S2 | S1 ##[min:max] S2 | S1 ##[1:${'$'}] S2",
            summary = "Specifies clock cycle delays between sequence expressions.",
            dynamicBehavior = "Evaluates subsequent sequence after exact or bounded number of clock ticks.",
            formalBehavior = "Expands search depth across bounded cycle unrolling.",
            fullExplanation = "- `##N`: Exact N clock cycles delay.\n- `##[min:max]`: Any delay between min and max inclusive.\n- `##[1:$]`: Bounded by end of simulation / arbitrary future tick.",
            codeExample = """
                sequence s_burst;
                  header ##1 payload ##[1:4] crc;
                endsequence
            """.trimIndent()
        ),
        SvaItem(
            id = "seq_repetitions",
            category = SvaCategory.SEQUENCES,
            name = "Repetitions ([*N], [->N], [=N])",
            syntax = "expr [*N] | expr [*min:max] | expr [->N] | expr [=N]",
            summary = "Consecutive, goto, and non-consecutive repetitions.",
            dynamicBehavior = "Matches expression occurrences consecutively or across intermittent intervals.",
            formalBehavior = "Expands state space corresponding to loop iterations and token matching.",
            fullExplanation = """
                - Consecutive `[*N]`: `expr` matches for exactly N consecutive clock cycles. E.g. `rst [*2] ##1 !rst`.
                - Range `[*min:max]`: Consecutive match between min and max times.
                - Goto `[->N]`: `expr` occurs N times non-consecutively; sequence terminates PRECISELY on the N-th occurrence.
                - Non-consecutive `[=N]`: `expr` occurs N times non-consecutively; extra clock cycles permitted AFTER the N-th occurrence.
            """.trimIndent(),
            codeExample = """
                // Consecutive: 3 back-to-back clock pulses of valid
                sequence s_consec;
                  valid [*3];
                endsequence

                // Goto: ends exactly when 3rd pulse arrives
                sequence s_goto;
                  start ##1 event_a [->3] ##1 complete;
                endsequence

                // Non-consecutive: event_b occurs twice before end
                sequence s_non_consec;
                  event_b [=2] ##1 finished;
                endsequence
            """.trimIndent(),
            keyTips = listOf(
                "Use `[->1]` for synchronization points: wait for event without knowing exact delay.",
                "`[*0]` creates an empty match sequence."
            )
        ),
        SvaItem(
            id = "seq_combinators",
            category = SvaCategory.SEQUENCES,
            name = "Sequence Combinators (and, intersect, or, first_match)",
            syntax = "S1 and S2 | S1 intersect S2 | S1 or S2 | first_match(S)",
            summary = "Advanced sequence composition: multi-branching and synchronized termination.",
            dynamicBehavior = "and allows differing lengths; intersect requires exact simultaneous finish.",
            formalBehavior = "Simultaneous path unification and earliest match pruning.",
            fullExplanation = """
                - `S1 and S2`: Both sequences match; can finish at DIFFERENT cycles.
                - `S1 intersect S2`: Both sequences match and MUST END IN THE EXACT SAME CYCLE.
                - `S1 or S2`: At least one sequence matches.
                - `first_match(S)`: Selects only the earliest match of multi-match sequence S to avoid redundant evaluations.
            """.trimIndent(),
            codeExample = """
                // Both sub-protocols must conclude together
                sequence s_synced;
                  (rx_done ##[1:5] rx_ack) intersect (tx_done ##[1:5] tx_ack);
                endsequence

                // Avoid multiple thread explosions in dynamic sim
                property p_earliest;
                  @(posedge clk)
                  first_match(req ##[1:10] ack) |-> data_valid;
                endproperty
            """.trimIndent(),
            keyTips = listOf(
                "Use `first_match()` in implications to prevent multiple parallel threads from launching consequent checks."
            )
        ),
        SvaItem(
            id = "seq_throughout_within",
            category = SvaCategory.SEQUENCES,
            name = "throughout & within",
            syntax = "b throughout S | S1 within S2",
            summary = "Conditions spanning sequence execution or containment.",
            dynamicBehavior = "throughout fails if boolean b drops low at any clock cycle while S runs.",
            formalBehavior = "Continuously constrains candidate traces over sequence duration.",
            fullExplanation = """
                - `b throughout S`: Boolean expression `b` must remain TRUE for the entire duration of sequence S.
                - `S1 within S2`: Sequence S1 must start after S2 starts and finish before or when S2 finishes.
            """.trimIndent(),
            codeExample = """
                property p_enable_throughout;
                  @(posedge clk) disable iff (rst)
                  en throughout (start ##1 data_valid[*4] ##1 stop);
                endproperty
                a_en: assert property (p_enable_throughout);
            """.trimIndent()
        ),

        // Section 7: Sampled Value System Functions
        SvaItem(
            id = "sys_past_edges",
            category = SvaCategory.SYSTEM_FUNCTIONS,
            name = "\$past, \$rose, \$fell, \$stable, \$changed",
            syntax = "${'$'}past(sig, N) | ${'$'}rose(sig) | ${'$'}fell(sig) | ${'$'}stable(sig) | ${'$'}changed(sig)",
            summary = "Temporal introspection on sampled signal history and transitions.",
            dynamicBehavior = "Queries values sampled in the Preponed region of earlier clock cycles.",
            formalBehavior = "Accesses previous cycle state variables.",
            fullExplanation = """
                - `${'$'}past(sig, N)`: Value of signal `sig` exactly N clock ticks ago (default N=1).
                - `${'$'}rose(sig)`: Evaluates true if LSB changed from 0/X/Z to 1.
                - `${'$'}fell(sig)`: Evaluates true if LSB changed from 1/X/Z to 0.
                - `${'$'}stable(sig)`: Returns 1 if `sig == ${'$'}past(sig)`.
                - `${'$'}changed(sig)`: Returns 1 if `sig != ${'$'}past(sig)`.
            """.trimIndent(),
            codeExample = """
                property p_ack_pulse;
                  @(posedge clk) disable iff (rst)
                  ${'$'}rose(ack) |=> ${'$'}fell(ack);
                endproperty

                property p_held_stable;
                  @(posedge clk) disable iff (rst)
                  req && !gnt |=> ${'$'}stable(data);
                endproperty
            """.trimIndent(),
            keyTips = listOf(
                "Values are sampled from the Preponed region (before NBA updates).",
                "\$past(sig) requires clock gating consideration during initial simulation ticks."
            )
        ),
        SvaItem(
            id = "sys_bit_checks",
            category = SvaCategory.SYSTEM_FUNCTIONS,
            name = "\$onehot, \$onehot0, \$countones, \$isunknown",
            syntax = "${'$'}onehot(sig) | ${'$'}onehot0(sig) | ${'$'}countones(sig) | !${'$'}isunknown(sig)",
            summary = "Bitwise integrity and X/Z unknown validation.",
            dynamicBehavior = "Fast hardware validation functions for state machines, arbiters, and data buses.",
            formalBehavior = "Translated to optimized bit-vector arithmetic in solvers.",
            fullExplanation = """
                - `${'$'}onehot(sig)`: Returns 1 if EXACTLY ONE bit is high (vital for one-hot FSMs).
                - `${'$'}onehot0(sig)`: Returns 1 if ZERO OR ONE bit is high.
                - `${'$'}countones(sig)`: Returns the integer count of bits set to 1.
                - `${'$'}isunknown(sig)`: Returns 1 if any bit is 'X' or 'Z'.
                - `!${'$'}isunknown(sig)`: Returns 1 if all bits are cleanly known (0 or 1).
            """.trimIndent(),
            codeExample = """
                property p_onehot_fsm;
                  @(posedge clk) disable iff (rst)
                  ${'$'}onehot(state_vec) and !${'$'}isunknown(state_vec);
                endproperty
                a_fsm: assert property (p_onehot_fsm);
            """.trimIndent(),
            keyTips = listOf(
                "Use `!\$isunknown(bus)` at interfaces to trap reset propagation or uninitialized flops.",
                "`\$onehot` is standard in one-hot state machine safety checks."
            )
        ),

        // Section: Protocol Packs
        SvaItem(
            id = "proto_axi4_stream",
            category = SvaCategory.PROTOCOL_PACKS,
            name = "AXI4-Stream Handshake Hold",
            syntax = "(tvalid && !tready) |=> (tvalid && \$stable(tdata));",
            summary = "Enforces that once TVALID is asserted, it cannot drop and TDATA must remain stable until TREADY is acknowledged.",
            dynamicBehavior = "Fails immediately if sender changes data or drops valid before receiver acknowledges.",
            formalBehavior = "Constrains or asserts master-slave interface adherence across all possible backpressure delays.",
            fullExplanation = "The core AMBA AXI4-Stream specification dictates that once TVALID is asserted by the source, it must not be deasserted until the transfer finishes on a cycle when both TVALID and TREADY are high. Furthermore, payload data (TDATA, TKEEP, TUSER) must stay stable.",
            codeExample = """
                property p_axi_stream_hold;
                  @(posedge aclk) disable iff (!aresetn)
                  (tvalid && !tready) |=> (tvalid && ${'$'}stable(tdata));
                endproperty
                a_axi_hold: assert property (p_axi_stream_hold)
                  else `uvm_error("AXIS", "Protocol violation: TVALID/TDATA changed before TREADY!");
            """.trimIndent(),
            waveDiagram = "aclk   : _|~|_|~|_|~|_|~|_\ntvalid : ___|~~~~~~~~~~|_\ntready : _________|~~~~|_\ntdata  : ___|===D0=====|_",
            keyTips = listOf(
                "Never allow master logic to depend combinatorially on TREADY to assert TVALID (breaks timing loops).",
                "Applies equally to AXI4 full and AXI-Lite channels (AW, W, AR, R, B)."
            )
        ),
        SvaItem(
            id = "proto_apb4_transfer",
            category = SvaCategory.PROTOCOL_PACKS,
            name = "APB4 Two-Phase Transfer",
            syntax = "(psel && !penable) |=> (psel && penable);",
            summary = "Enforces APB protocol state progression from Setup phase (PSEL=1, PENABLE=0) to Access phase (PSEL=1, PENABLE=1).",
            dynamicBehavior = "Detects illegal multi-cycle setup states or premature deassertions.",
            formalBehavior = "Proves APB bridge FSM transitions without deadlock or skipped access cycles.",
            fullExplanation = "ARM APB transfers start with the Setup phase where PSEL is high and PENABLE is low. In the following cycle, PENABLE must go high into the Access phase. During Access, PADDR and PWRITE must remain unchanged until PREADY goes high.",
            codeExample = """
                property p_apb_phases;
                  @(posedge pclk) disable iff (!presetn)
                  (psel && !penable) |=> (psel && penable && ${'$'}stable(paddr));
                endproperty
                a_apb_phase: assert property (p_apb_phases)
                  else `uvm_error("APB", "Illegal APB transfer setup sequence!");
            """.trimIndent(),
            waveDiagram = "pclk    : _|~|_|~|_|~|_\npsel    : ___|~~~~~~~|_\npenable : _______|~~~|_\npready  : _______|~~~|_",
            keyTips = listOf(
                "PADDR and PWDATA must remain strictly stable during the entire access phase.",
                "PENABLE must deassert in the cycle immediately following PREADY assertion."
            )
        ),
        SvaItem(
            id = "proto_fifo_invariants",
            category = SvaCategory.PROTOCOL_PACKS,
            name = "Synchronous FIFO Invariants",
            syntax = "(fifo_full && !pop) |-> !push;  // No overflow",
            summary = "Guarantees FIFO pointer integrity, preventing push on full and pop on empty.",
            dynamicBehavior = "Flags illegal writes/reads before corrupting memory arrays or pointers.",
            formalBehavior = "Exhaustively proves no overflow or underflow can occur under any valid interface traffic.",
            fullExplanation = "Synchronous FIFOs require strict boundary invariants. Pushing when full without popping in the same cycle corrupts unread items. Popping when empty corrupts read pointers and returns invalid data.",
            codeExample = """
                property p_fifo_no_overflow;
                  @(posedge clk) disable iff (!rst_n)
                  (fifo_full && !pop) |-> !push;
                endproperty
                property p_fifo_no_underflow;
                  @(posedge clk) disable iff (!rst_n)
                  fifo_empty |-> !pop;
                endproperty
                a_no_overflow:  assert property (p_fifo_no_overflow);
                a_no_underflow: assert property (p_fifo_no_underflow);
            """.trimIndent(),
            keyTips = listOf(
                "For dual-clock asynchronous FIFOs, also verify that gray-coded pointers only toggle 1 bit per cycle.",
                "Use almost_full and almost_empty flags to throttle upstream transmitters."
            )
        )
    )

    val badges: List<SvaBadge> = listOf(
        SvaBadge(
            id = "badge_first",
            title = "First Assertion",
            description = "Solve your very first SVA challenge",
            icon = "🌱",
            targetCount = 1,
            getProgress = { solvedIds, _, _ -> solvedIds.size.coerceAtMost(1) },
            isUnlocked = { solvedIds, _, _ -> solvedIds.isNotEmpty() }
        ),
        SvaBadge(
            id = "badge_handshake",
            title = "Handshake Guardian",
            description = "Master 5 handshake or protocol assertions",
            icon = "🤝",
            targetCount = 5,
            getProgress = { solvedIds, _, challenges ->
                solvedIds.count { id ->
                    val chal = challenges.find { it.id == id }
                    chal?.category == SvaCategory.PROTOCOL_PACKS || chal?.targetSva?.contains("ready") == true
                }.coerceAtMost(5)
            },
            isUnlocked = { solvedIds, _, challenges ->
                solvedIds.count { id ->
                    val chal = challenges.find { it.id == id }
                    chal?.category == SvaCategory.PROTOCOL_PACKS || chal?.targetSva?.contains("ready") == true
                } >= 5
            }
        ),
        SvaBadge(
            id = "badge_sequences",
            title = "Sequence Architect",
            description = "Solve 10 Sequence or Repetition challenges",
            icon = "🔁",
            targetCount = 10,
            getProgress = { solvedIds, _, challenges ->
                solvedIds.count { id -> challenges.find { it.id == id }?.category == SvaCategory.SEQUENCES }.coerceAtMost(10)
            },
            isUnlocked = { solvedIds, _, challenges ->
                solvedIds.count { id -> challenges.find { it.id == id }?.category == SvaCategory.SEQUENCES } >= 10
            }
        ),
        SvaBadge(
            id = "badge_ltl",
            title = "LTL Temporal Master",
            description = "Solve 5 Linear Temporal Logic challenges (always, s_eventually, until)",
            icon = "⏳",
            targetCount = 5,
            getProgress = { solvedIds, _, challenges ->
                solvedIds.count { id -> challenges.find { it.id == id }?.category == SvaCategory.LTL }.coerceAtMost(5)
            },
            isUnlocked = { solvedIds, _, challenges ->
                solvedIds.count { id -> challenges.find { it.id == id }?.category == SvaCategory.LTL } >= 5
            }
        ),
        SvaBadge(
            id = "badge_introspection",
            title = "Introspection Pro",
            description = "Solve 5 System Function (\$past, \$rose, \$onehot) assertions",
            icon = "🔬",
            targetCount = 5,
            getProgress = { solvedIds, _, challenges ->
                solvedIds.count { id -> challenges.find { it.id == id }?.category == SvaCategory.SYSTEM_FUNCTIONS }.coerceAtMost(5)
            },
            isUnlocked = { solvedIds, _, challenges ->
                solvedIds.count { id -> challenges.find { it.id == id }?.category == SvaCategory.SYSTEM_FUNCTIONS } >= 5
            }
        ),
        SvaBadge(
            id = "badge_protocols",
            title = "Bus Protocol Master",
            description = "Solve 5 AXI4, APB4, or FIFO Protocol Pack challenges",
            icon = "⚡",
            targetCount = 5,
            getProgress = { solvedIds, _, challenges ->
                solvedIds.count { id -> challenges.find { it.id == id }?.category == SvaCategory.PROTOCOL_PACKS }.coerceAtMost(5)
            },
            isUnlocked = { solvedIds, _, challenges ->
                solvedIds.count { id -> challenges.find { it.id == id }?.category == SvaCategory.PROTOCOL_PACKS } >= 5
            }
        ),
        SvaBadge(
            id = "badge_quiz_master",
            title = "Quiz Ace",
            description = "Score 5 or more points in the SVA Quiz",
            icon = "🎯",
            targetCount = 5,
            getProgress = { _, quizScore, _ -> quizScore.coerceAtMost(5) },
            isUnlocked = { _, quizScore, _ -> quizScore >= 5 }
        ),
        SvaBadge(
            id = "badge_veteran_25",
            title = "Verification Specialist",
            description = "Solve 25+ total SVA assertions",
            icon = "🛡️",
            targetCount = 25,
            getProgress = { solvedIds, _, _ -> solvedIds.size.coerceAtMost(25) },
            isUnlocked = { solvedIds, _, _ -> solvedIds.size >= 25 }
        ),
        SvaBadge(
            id = "badge_grandmaster_50",
            title = "SVA Grandmaster",
            description = "Solve 50+ total SVA assertions",
            icon = "🌟",
            targetCount = 50,
            getProgress = { solvedIds, _, _ -> solvedIds.size.coerceAtMost(50) },
            isUnlocked = { solvedIds, _, _ -> solvedIds.size >= 50 }
        ),
        SvaBadge(
            id = "badge_centurion_100",
            title = "Centurion",
            description = "Solve 100+ total SVA assertions",
            icon = "👑",
            targetCount = 100,
            getProgress = { solvedIds, _, _ -> solvedIds.size.coerceAtMost(100) },
            isUnlocked = { solvedIds, _, _ -> solvedIds.size >= 100 }
        )
    )

    val quizQuestions: List<SvaQuizQuestion> = listOf(
        SvaQuizQuestion(
            id = "q1",
            question = "Which operator indicates that the consequent evaluates starting in the SAME cycle as the antecedent?",
            codeSnippet = "req |-> ack   vs   req |=> ack",
            options = listOf(
                "|-> (Overlapping implication)",
                "|=> (Non-overlapping implication)",
                "##1",
                "nexttime"
            ),
            correctIndex = 0,
            explanation = "Overlapping implication `|->` begins evaluation in the same clock cycle (##0) where the antecedent matches.",
            category = SvaCategory.IMPLICATION
        ),
        SvaQuizQuestion(
            id = "q2",
            question = "What happens in dynamic simulation if an assertion uses `s_eventually P` and simulation finishes before P ever occurs?",
            codeSnippet = "a_live: assert property (@(posedge clk) req |-> s_eventually ack);",
            options = listOf(
                "It passes silently (weak semantics)",
                "It fails with an assertion violation (strong semantics)",
                "Simulation cannot exit until P occurs",
                "It produces a compile-time warning"
            ),
            correctIndex = 1,
            explanation = "The `s_` prefix designates strong temporal operators. If simulation ends without the condition being fulfilled, the assertion reports an error. Weak operators would pass.",
            category = SvaCategory.LTL
        ),
        SvaQuizQuestion(
            id = "q3",
            question = "What is the difference between `S1 and S2` versus `S1 intersect S2`?",
            codeSnippet = null,
            options = listOf(
                "`intersect` requires both sequences to match and finish at the EXACT SAME clock cycle",
                "`and` requires identical length while `intersect` does not",
                "`intersect` only works for boolean expressions",
                "There is no difference in IEEE 1800"
            ),
            correctIndex = 0,
            explanation = "While `and` succeeds if both sequences match even if they terminate at different cycles, `intersect` enforces that both must end in the exact same cycle.",
            category = SvaCategory.SEQUENCES
        ),
        SvaQuizQuestion(
            id = "q4",
            question = "Which function evaluates true if a signal transitioned from 0/X/Z to 1?",
            codeSnippet = "@(posedge clk) ...",
            options = listOf(
                "\$past(sig)",
                "\$rose(sig)",
                "\$changed(sig)",
                "\$onehot(sig)"
            ),
            correctIndex = 1,
            explanation = "`${'$'}rose(sig)` evaluates true when the sampled value of the signal's LSB has transitioned to 1 from 0, X, or Z.",
            category = SvaCategory.SYSTEM_FUNCTIONS
        ),
        SvaQuizQuestion(
            id = "q5",
            question = "What is the purpose of `first_match(seq)` when used in the antecedent of an assertion?",
            codeSnippet = "first_match(req ##[1:5] ready) |-> data_valid;",
            options = listOf(
                "It forces dynamic simulation to choose the latest match",
                "It discards all subsequent matches of the sequence, preventing duplicate concurrent evaluation threads",
                "It checks only cycle 0",
                "It makes the property weak instead of strong"
            ),
            correctIndex = 1,
            explanation = "Without `first_match`, a sequence matching multiple times launches multiple consequent evaluation threads. `first_match` selects only the earliest matching trace.",
            category = SvaCategory.SEQUENCES
        ),
        SvaQuizQuestion(
            id = "q6",
            question = "In Formal Verification, what is the effect of `assume property`?",
            codeSnippet = "u_in: assume property (@(posedge clk) req |-> !reset);",
            options = listOf(
                "It searches for counterexamples violating the assumption",
                "It prunes the solver search space by constraining inputs/environment",
                "It produces warning logs without altering proofs",
                "It forces the solver to ignore clock gating"
            ),
            correctIndex = 1,
            explanation = "`assume property` restricts the formal engine's input space so the solver will never produce a counterexample where the environment violates the assumption.",
            category = SvaCategory.DIRECTIVES
        ),
        SvaQuizQuestion(
            id = "q7",
            question = "What does the goto repetition operator `expr [->N]` mean?",
            codeSnippet = "start ##1 event_x [->3] ##1 done;",
            options = listOf(
                "`expr` must occur 3 times consecutively",
                "`expr` occurs 3 times non-consecutively, and the sequence ends PRECISELY on the 3rd occurrence",
                "`expr` can occur 3 or more times with delay after",
                "Jump directly to clock cycle 3"
            ),
            correctIndex = 1,
            explanation = "Goto repetition `[->N]` matches when the boolean occurs N times non-consecutively, with the match point occurring strictly on the exact clock cycle of the Nth occurrence.",
            category = SvaCategory.SEQUENCES
        ),
        SvaQuizQuestion(
            id = "q8",
            question = "Which system function checks that exactly one bit of a vector is asserted high?",
            codeSnippet = "assert property (@(posedge clk) ... (fsm_state));",
            options = listOf(
                "\$countones(sig)",
                "\$onehot0(sig)",
                "\$onehot(sig)",
                "\$isunknown(sig)"
            ),
            correctIndex = 2,
            explanation = "`${'$'}onehot(sig)` returns 1 if exactly one bit is 1. `${'$'}onehot0` allows zero or one bit.",
            category = SvaCategory.SYSTEM_FUNCTIONS
        )
    )

    val sandboxSamples: List<SvaSandboxSample> = listOf(
        SvaSandboxSample(
            title = "Request-Acknowledge Handshake",
            description = "Standard bus handshake: ack must follow within 1 to 3 cycles of req.",
            code = """
                property p_req_ack;
                  @(posedge clk) disable iff (!rst_n)
                  req |-> ##[1:3] ack;
                endproperty
                a_req_ack: assert property (p_req_ack)
                  else `uvm_error("SVA", "Ack missing!");
            """.trimIndent(),
            testVerdict = "PASS (ack arrives ##2 after req)",
            explanation = "Overlapping implication checks that whenever req is 1, ack arrives in the window of 1 to 3 clock cycles."
        ),
        SvaSandboxSample(
            title = "One-Hot State Machine Invariant",
            description = "Safety assertion ensuring FSM state is never corrupted or in an unknown state.",
            code = """
                property p_fsm_legal;
                  @(posedge clk) disable iff (!rst_n)
                  ${'$'}onehot(current_state) && !${'$'}isunknown(current_state);
                endproperty
                a_fsm_legal: assert property (p_fsm_legal);
            """.trimIndent(),
            testVerdict = "PASS (state always has exactly 1 bit high without X/Z)",
            explanation = "Guards against multi-hot glitches, uninitialized state vectors, and X-propagation."
        ),
        SvaSandboxSample(
            title = "Strong Liveness with s_eventually",
            description = "Proves system will never hang indefinitely after enter_low_power.",
            code = """
                property p_wakeup;
                  @(posedge clk) disable iff (!rst_n)
                  enter_sleep |-> s_eventually wakeup_ready;
                endproperty
                a_wakeup: assert property (p_wakeup)
                  else `uvm_fatal("DEADLOCK", "Device never woke up!");
            """.trimIndent(),
            testVerdict = "PASS (wakeup occurred at tick 124)",
            explanation = "Strong operator flags an error if simulation completes without wakeup occurring."
        )
    )
}
