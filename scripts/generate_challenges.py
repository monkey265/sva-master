import json
import re

challenges = []

def clean_prop_name(name):
    # sanitize into a valid SystemVerilog identifier
    s = re.sub(r'[^a-zA-Z0-9_]', '_', name).lower()
    s = re.sub(r'_+', '_', s).strip('_')
    return s[:24]

def add(prompt, target, acceptable, tokens, distractors, explanation, category, difficulty="Medium", prop_name=None, clock="@(posedge clk)", reset="!rst_n"):
    idx = len(challenges) + 1
    p_id = f"p_{clean_prop_name(prop_name if prop_name else f'check_{idx:03d}')}"
    
    full_property = f"""property {p_id};
  {clock} disable iff ({reset})
  {target};
endproperty"""

    directive = f"assert property ({p_id});"

    # Also allow user to type the full assert expression or property body
    all_acceptable = set([target] + acceptable)
    all_acceptable.add(directive)
    all_acceptable.add(f"assert property ({target});")
    all_acceptable.add(f"assert property ({target})")

    challenges.append({
        "id": f"sva_chal_{idx:03d}",
        "prompt": prompt,
        "targetSva": target,
        "acceptableAnswers": list(all_acceptable),
        "tokens": tokens,
        "distractors": distractors,
        "explanation": explanation,
        "category": category,
        "difficulty": difficulty,
        "fullPropertyDefinition": full_property,
        "assertionDirective": directive
    })

# --- CHALLENGE 1: USER'S EXACT PROPERTY ---
add(
    prompt="Handshake stability: when valid is high and ready is low, valid must stay high and data must remain stable in the next cycle",
    target="(valid && !ready) |=> (valid && $stable(data))",
    acceptable=[
        "(valid && !ready) |=> (valid && $stable(data))",
        "valid && !ready |=> valid && $stable(data)",
        "(valid && !ready) |-> ##1 (valid && $stable(data))",
        "valid && !ready |-> ##1 valid && $stable(data)"
    ],
    tokens=["(", "valid", "&&", "!ready", ")", "|=>", "(", "valid", "&&", "$stable(data)", ")"],
    distractors=["|->", "##0", "$changed(data)", "$rose(valid)"],
    explanation="Standard AXI/Stream handshake hold rule: A sender asserting valid must hold valid high and keep data stable until ready is acknowledged.",
    category="IMPLICATION",
    difficulty="Medium",
    prop_name="valid_hold"
)

# --- Group 1: Consecutive Repetitions, Handshakes & Stability (Items 2 - 25) ---
add(
    prompt="Verify that valid and ready are both at 1 for 2 consecutive clock cycles",
    target="(valid && ready) [*2]",
    acceptable=["(valid & ready) [*2]", "valid && ready [*2]"],
    tokens=["(", "valid", "&&", "ready", ")", "[*2]"],
    distractors=["|->", "[->2]", "##1", "[=2]"],
    explanation="Consecutive repetition [*2] requires the boolean condition (valid && ready) to hold for 2 consecutive clock ticks.",
    category="SEQUENCES",
    difficulty="Basic",
    prop_name="valid_ready_2cyc"
)

add(
    prompt="Verify that reset signal rst_n remains low for at least 3 consecutive cycles",
    target="!rst_n [*3]",
    acceptable=["(!rst_n) [*3]", "~rst_n [*3]", "rst_n == 0 [*3]", "(rst_n == 0) [*3]"],
    tokens=["!rst_n", "[*3]"],
    distractors=["[*2]", "[->3]", "##3", "|=>"],
    explanation="The [*3] consecutive repetition ensures !rst_n is sampled asserted low across 3 successive clock edges.",
    category="SEQUENCES",
    difficulty="Basic",
    prop_name="reset_hold_3cyc"
)

add(
    prompt="When req is asserted, ack must be asserted 1 cycle later",
    target="req |=> ack",
    acceptable=["req |-> ##1 ack"],
    tokens=["req", "|=>", "ack"],
    distractors=["|->", "##0", "ack [*1]"],
    explanation="Non-overlapping implication |=> is equivalent to |-> ##1, evaluating ack on the immediate next clock tick.",
    category="IMPLICATION",
    difficulty="Basic",
    prop_name="req_ack_1cyc"
)

add(
    prompt="When cs is high, addr must be within range [< 32'h1000] in the same cycle",
    target="cs |-> (addr < 32'h1000)",
    acceptable=["cs |-> addr < 32'h1000"],
    tokens=["cs", "|->", "(", "addr", "<", "32'h1000", ")"],
    distractors=["|=>", "##1", ">"],
    explanation="Overlapping implication |-> checks that the consequent condition evaluates in the same clock cycle as cs.",
    category="IMPLICATION",
    difficulty="Basic",
    prop_name="cs_addr_range"
)

add(
    prompt="Verify that enable remains asserted for between 2 and 4 consecutive cycles",
    target="enable [*2:4]",
    acceptable=["(enable) [*2:4]"],
    tokens=["enable", "[*2:4]"],
    distractors=["[->2:4]", "[=2:4]", "##[2:4]"],
    explanation="Consecutive range repetition [*2:4] matches when enable is continuously high between 2 and 4 cycles.",
    category="SEQUENCES",
    difficulty="Basic",
    prop_name="enable_range"
)

add(
    prompt="When start is pulsed, busy must stay high until done arrives (strong)",
    target="start |=> busy s_until done",
    acceptable=["start |-> ##1 (busy s_until done)"],
    tokens=["start", "|=>", "busy", "s_until", "done"],
    distractors=["until", "|->", "s_eventually"],
    explanation="The strong s_until requires busy to hold on each cycle until done arrives, and guarantees done will eventually arrive.",
    category="LTL",
    difficulty="Advanced",
    prop_name="busy_s_until_done"
)

add(
    prompt="When req is asserted, ack must arrive within 1 to 3 clock cycles",
    target="req |-> ##[1:3] ack",
    acceptable=["req |=> ##[0:2] ack"],
    tokens=["req", "|->", "##[1:3]", "ack"],
    distractors=["|=>", "##[1:3]", "[*1:3]"],
    explanation="Bounded cycle delay ##[1:3] after req requires ack to assert between 1 and 3 cycles later.",
    category="SEQUENCES",
    difficulty="Medium",
    prop_name="req_ack_window"
)

add(
    prompt="Verify that state vector is always one-hot and never unknown",
    target="$onehot(state) && !$isunknown(state)",
    acceptable=["$onehot(state) and !$isunknown(state)", "!$isunknown(state) && $onehot(state)"],
    tokens=["$onehot(state)", "&&", "!$isunknown(state)"],
    distractors=["$onehot0(state)", "##1", "$countones(state)"],
    explanation="$onehot checks that exactly 1 bit is high, and !$isunknown ensures no bits are X or Z.",
    category="SYSTEM_FUNCTIONS",
    difficulty="Medium",
    prop_name="onehot_legal_fsm"
)

add(
    prompt="When transaction starts, grant must be eventually guaranteed to occur before sim ends",
    target="req |-> s_eventually gnt",
    acceptable=["req |=> s_eventually gnt"],
    tokens=["req", "|->", "s_eventually", "gnt"],
    distractors=["always", "|=>", "eventually"],
    explanation="s_eventually provides strong liveness checking, catching hangs and deadlocks before simulation concludes.",
    category="LTL",
    difficulty="Medium",
    prop_name="req_eventual_gnt"
)

add(
    prompt="Verify that data_valid occurs 3 times non-consecutively, ending strictly on the 3rd match",
    target="burst_start ##1 (data_valid [->3]) ##1 burst_end",
    acceptable=["burst_start ##1 data_valid [->3] ##1 burst_end"],
    tokens=["burst_start", "##1", "(", "data_valid", "[->3]", ")", "##1", "burst_end"],
    distractors=["[*3]", "[=3]", "|->"],
    explanation="Goto repetition [->3] matches when data_valid occurs 3 times non-consecutively, ending precisely on the 3rd occurrence.",
    category="SEQUENCES",
    difficulty="Advanced",
    prop_name="goto_3_burst"
)

add(
    prompt="Ensure signal data remains stable whenever valid is high but ready is low",
    target="(valid && !ready) |=> $stable(data)",
    acceptable=["valid && !ready |=> $stable(data)", "(valid && !ready) |-> ##1 $stable(data)"],
    tokens=["(", "valid", "&&", "!ready", ")", "|=>", "$stable(data)"],
    distractors=["|->", "$changed(data)", "$rose(data)"],
    explanation="Backpressure stability rule: if sender asserts valid and receiver isn't ready, data must remain stable in next cycle.",
    category="SYSTEM_FUNCTIONS",
    difficulty="Medium",
    prop_name="data_stable_hold"
)

add(
    prompt="Check that ack was 0 in the previous cycle when req rises",
    target="$rose(req) |-> $past(ack) == 0",
    acceptable=["$rose(req) |-> !$past(ack)", "$rose(req) |-> ($past(ack) == 1'b0)"],
    tokens=["$rose(req)", "|->", "$past(ack)", "==", "0"],
    distractors=["|=>", "$rose(ack)", "$fell(req)"],
    explanation="$rose detects rising edge of req, and $past(ack) inspects ack value in previous cycle.",
    category="SYSTEM_FUNCTIONS",
    difficulty="Medium",
    prop_name="past_ack_zero"
)

add(
    prompt="Verify that packet payload of 4 words is accompanied throughout by payload_valid",
    target="payload_valid throughout (pkt_start ##1 pkt_data[*4] ##1 pkt_end)",
    acceptable=["payload_valid throughout pkt_start ##1 pkt_data[*4] ##1 pkt_end"],
    tokens=["payload_valid", "throughout", "(", "pkt_start", "##1", "pkt_data[*4]", "##1", "pkt_end", ")"],
    distractors=["within", "intersect", "and"],
    explanation="The throughout operator requires boolean payload_valid to remain asserted for the entire sequence duration.",
    category="SEQUENCES",
    difficulty="Advanced",
    prop_name="payload_valid_throughout"
)

add(
    prompt="Both tx_done and rx_done sequences must conclude on the exact same clock cycle",
    target="(tx_start ##[1:5] tx_done) intersect (rx_start ##[1:5] rx_done)",
    acceptable=["tx_start ##[1:5] tx_done intersect rx_start ##[1:5] rx_done"],
    tokens=["(", "tx_start ##[1:5] tx_done", ")", "intersect", "(", "rx_start ##[1:5] rx_done", ")"],
    distractors=["and", "or", "throughout"],
    explanation="intersect requires both sequences to match and terminate on the exact same clock edge.",
    category="SEQUENCES",
    difficulty="Advanced",
    prop_name="synced_intersect"
)

add(
    prompt="Take only the first match of req ##[1:5] ack before triggering data phase",
    target="first_match(req ##[1:5] ack) |=> data_phase",
    acceptable=["first_match(req ##[1:5] ack) |-> ##1 data_phase"],
    tokens=["first_match(", "req ##[1:5] ack", ")", "|=>", "data_phase"],
    distractors=["|->", "intersect", "within"],
    explanation="first_match discards later matches in the multi-match delay window, preventing duplicate threads.",
    category="SEQUENCES",
    difficulty="Advanced",
    prop_name="first_match_handshake"
)

add(
    prompt="Verify that frame_sync is followed by exactly 8 consecutive bytes of data",
    target="frame_sync |=> data_byte [*8]",
    acceptable=["frame_sync |-> ##1 data_byte [*8]"],
    tokens=["frame_sync", "|=>", "data_byte", "[*8]"],
    distractors=["|->", "[->8]", "[=8]"],
    explanation="frame_sync followed next cycle by 8 back-to-back cycles of data_byte.",
    category="SEQUENCES",
    difficulty="Medium",
    prop_name="frame_8_bytes"
)

add(
    prompt="Verify that interrupt line irq drops low on falling edge of clk_gate",
    target="$fell(clk_gate) |-> !irq",
    acceptable=["$fell(clk_gate) |-> irq == 0"],
    tokens=["$fell(clk_gate)", "|->", "!irq"],
    distractors=["$rose(clk_gate)", "|=>", "$stable(irq)"],
    explanation="$fell checks high-to-low transition and overlapping |-> checks !irq in that cycle.",
    category="SYSTEM_FUNCTIONS",
    difficulty="Medium",
    prop_name="irq_fell_clk"
)

add(
    prompt="Verify that bus arbiter vector has at most one grant active (onehot0)",
    target="$onehot0(gnt_bus)",
    acceptable=["$onehot0(gnt_bus) == 1"],
    tokens=["$onehot0(gnt_bus)"],
    distractors=["$onehot(gnt_bus)", "$countones(gnt_bus) == 1", "!$isunknown(gnt_bus)"],
    explanation="$onehot0 allows either 0 grants (idle) or exactly 1 grant active.",
    category="SYSTEM_FUNCTIONS",
    difficulty="Basic",
    prop_name="arbiter_onehot0"
)

add(
    prompt="Verify that exactly 2 bits in mask vector are set to 1",
    target="$countones(mask) == 2",
    acceptable=["$countones(mask) == 32'd2"],
    tokens=["$countones(mask)", "==", "2"],
    distractors=["$onehot(mask)", "$onehot0(mask)", "3"],
    explanation="$countones counts the total number of set bits in the bit-vector.",
    category="SYSTEM_FUNCTIONS",
    difficulty="Basic",
    prop_name="countones_mask_2"
)

add(
    prompt="Whenever error_flag asserts, immediately reject assertion execution asynchronously",
    target="reject_on(error_flag) (req |=> ack)",
    acceptable=["reject_on(error_flag) req |=> ack"],
    tokens=["reject_on(", "error_flag", ")", "req", "|=>", "ack"],
    distractors=["accept_on", "sync_reject_on", "disable iff"],
    explanation="reject_on immediately aborts the assertion with failure as soon as error_flag goes high.",
    category="ABORTS",
    difficulty="Advanced",
    prop_name="reject_on_error"
)

add(
    prompt="Whenever soft_reset asserts, immediately accept assertion execution as passing",
    target="accept_on(soft_reset) (req |=> ##[1:10] ack)",
    acceptable=["accept_on(soft_reset) req |=> ##[1:10] ack"],
    tokens=["accept_on(", "soft_reset", ")", "req", "|=>", "##[1:10]", "ack"],
    distractors=["reject_on", "sync_accept_on", "disable iff"],
    explanation="accept_on triggers an immediate passing verdict when condition becomes true.",
    category="ABORTS",
    difficulty="Advanced",
    prop_name="accept_on_reset"
)

add(
    prompt="On clock edge, synchronously abort with failure if fault is active",
    target="sync_reject_on(fault) (start |=> done)",
    acceptable=["sync_reject_on(fault) start |=> done"],
    tokens=["sync_reject_on(", "fault", ")", "start", "|=>", "done"],
    distractors=["sync_accept_on", "reject_on", "accept_on"],
    explanation="sync_reject_on samples abort on clock edge rather than purely asynchronously.",
    category="ABORTS",
    difficulty="Advanced",
    prop_name="sync_reject_fault"
)

add(
    prompt="When write mode is set, ack must come in 1 cycle, otherwise in 2 cycles",
    target="valid |-> if (wr_mode) ##1 wr_ack else ##2 rd_ack",
    acceptable=["valid |-> (if (wr_mode) ##1 wr_ack else ##2 rd_ack)"],
    tokens=["valid", "|->", "if (wr_mode)", "##1 wr_ack", "else", "##2 rd_ack"],
    distractors=["|=>", "and", "or"],
    explanation="Conditional property evaluation inside SVA using if-else construct.",
    category="IMPLICATION",
    difficulty="Advanced",
    prop_name="conditional_wr_rd"
)

add(
    prompt="Ensure signal data_in was identical 2 cycles ago to current data_out",
    target="data_out == $past(data_in, 2)",
    acceptable=["$past(data_in, 2) == data_out"],
    tokens=["data_out", "==", "$past(data_in, 2)"],
    distractors=["$past(data_in)", "$rose(data_in)", "$stable(data_in)"],
    explanation="$past(sig, N) reads sampled value from N clock cycles in the past.",
    category="SYSTEM_FUNCTIONS",
    difficulty="Medium",
    prop_name="past_data_pipe"
)

add(
    prompt="Verify that pulse is high for exactly 1 cycle (rising then falling edge)",
    target="$rose(pulse) |=> $fell(pulse)",
    acceptable=["$rose(pulse) |-> ##1 $fell(pulse)"],
    tokens=["$rose(pulse)", "|=>", "$fell(pulse)"],
    distractors=["$stable(pulse)", "|->", "$rose(pulse)"],
    explanation="A 1-cycle strobe rises at cycle N and must fall at cycle N+1.",
    category="SYSTEM_FUNCTIONS",
    difficulty="Medium",
    prop_name="single_cycle_pulse"
)

# --- Group 2: Protocol Handshakes, AXI hold rules, and Delays (Items 26 - 65) ---
for i in range(26, 66):
    n = i - 25
    if n % 5 == 1:
        # AXI / Handshake hold pattern variation
        add(
            prompt=f"AXI hold rule {n}: when ch{n}_valid && !ch{n}_ready, hold valid and stable data for channel {n}",
            target=f"(ch{n}_valid && !ch{n}_ready) |=> (ch{n}_valid && $stable(ch{n}_data))",
            acceptable=[
                f"ch{n}_valid && !ch{n}_ready |=> ch{n}_valid && $stable(ch{n}_data)",
                f"(ch{n}_valid && !ch{n}_ready) |-> ##1 (ch{n}_valid && $stable(ch{n}_data))"
            ],
            tokens=["(", f"ch{n}_valid", "&&", f"!ch{n}_ready", ")", "|=>", "(", f"ch{n}_valid", "&&", f"$stable(ch{n}_data)", ")"],
            distractors=["|->", "##0", "$changed(data)", f"$rose(ch{n}_valid)"],
            explanation=f"Valid-ready hold property: if channel {n} valid is unacknowledged, valid and data payload must hold.",
            category="IMPLICATION",
            difficulty="Medium",
            prop_name=f"ch{n}_valid_hold"
        )
    elif n % 5 == 2:
        add(
            prompt=f"When tx_req_{n} is asserted, tx_ack_{n} must arrive after exactly {n} clock cycles",
            target=f"tx_req_{n} |-> ##{n} tx_ack_{n}",
            acceptable=[f"tx_req_{n} |=> ##{n-1} tx_ack_{n}" if n > 1 else f"tx_req_{n} |=> tx_ack_{n}"],
            tokens=[f"tx_req_{n}", "|->", f"##{n}", f"tx_ack_{n}"],
            distractors=["|=>", f"[*{n}]", f"##{n+1}"],
            explanation=f"Fixed delay ##{n} requires tx_ack_{n} exactly {n} cycles following tx_req_{n}.",
            category="SEQUENCES",
            difficulty="Basic",
            prop_name=f"tx_delay_{n}"
        )
    elif n % 5 == 3:
        add(
            prompt=f"Verify that heartbeat_{n} changed value from previous cycle at tick {n}",
            target=f"$changed(heartbeat_{n})",
            acceptable=[f"heartbeat_{n} != $past(heartbeat_{n})", f"$past(heartbeat_{n}) != heartbeat_{n}"],
            tokens=[f"$changed(heartbeat_{n})"],
            distractors=[f"$stable(heartbeat_{n})", f"$rose(heartbeat_{n})", f"$fell(heartbeat_{n})"],
            explanation=f"$changed returns 1 if signal is not equal to its sampled value from the prior cycle.",
            category="SYSTEM_FUNCTIONS",
            difficulty="Basic",
            prop_name=f"heartbeat_toggle_{n}"
        )
    elif n % 5 == 4:
        add(
            prompt=f"Event trigger_{n} must happen {n} times non-consecutively before finish, allowing extra cycles after",
            target=f"trigger_{n} [={n}] ##1 finish_{n}",
            acceptable=[f"(trigger_{n} [={n}]) ##1 finish_{n}"],
            tokens=[f"trigger_{n}", f"[={n}]", "##1", f"finish_{n}"],
            distractors=[f"[->{n}]", f"[*{n}]", "|=>"],
            explanation=f"Non-consecutive repetition [={n}] allows delay after the {n}-th occurrence before finish_{n}.",
            category="SEQUENCES",
            difficulty="Advanced",
            prop_name=f"nonconsec_event_{n}"
        )
    else:
        add(
            prompt=f"Ensure sub-sequence sub_seq_{n} occurs entirely within main_frame_{n}",
            target=f"sub_seq_{n} within main_frame_{n}",
            acceptable=[f"(sub_seq_{n}) within (main_frame_{n})"],
            tokens=[f"sub_seq_{n}", "within", f"main_frame_{n}"],
            distractors=["throughout", "intersect", "and"],
            explanation="within operator checks that sub-sequence occurs within the timeframe of main sequence.",
            category="SEQUENCES",
            difficulty="Advanced",
            prop_name=f"within_frame_{n}"
        )

# --- Group 3: Formal Constraints, Assumes, Restricts & Coverage (Items 66 - 105) ---
for i in range(66, 106):
    n = i - 65
    if n % 4 == 1:
        add(
            prompt=f"Environmental stability: in_valid_{n} high forces in_data_{n} to remain stable in next cycle",
            target=f"(in_valid_{n} && !in_ready_{n}) |=> $stable(in_data_{n})",
            acceptable=[
                f"in_valid_{n} && !in_ready_{n} |=> $stable(in_data_{n})",
                f"(in_valid_{n} && !in_ready_{n}) |-> ##1 $stable(in_data_{n})"
            ],
            tokens=["(", f"in_valid_{n}", "&&", f"!in_ready_{n}", ")", "|=>", f"$stable(in_data_{n})"],
            distractors=["|->", "$changed(data)", "always"],
            explanation=f"Environmental assumption/check: in_data_{n} must not glitch when unacknowledged.",
            category="DIRECTIVES",
            difficulty="Medium",
            prop_name=f"env_stable_{n}"
        )
    elif n % 4 == 2:
        add(
            prompt=f"Cover scenario where burst length is {n} cycles followed by eop_{n}",
            target=f"burst_en_{n} [*{n}] ##1 eop_{n}",
            acceptable=[f"burst_en_{n} [* {n}] ##1 eop_{n}", f"(burst_en_{n} [*{n}]) ##1 eop_{n}"],
            tokens=[f"burst_en_{n}", f"[*{n}]", "##1", f"eop_{n}"],
            distractors=[f"[->{n}]", f"[={n}]", "|->"],
            explanation=f"Cover property sequence tracking witness trace for {n} bursts ending with eop.",
            category="DIRECTIVES",
            difficulty="Medium",
            prop_name=f"cover_burst_{n}"
        )
    elif n % 4 == 3:
        add(
            prompt=f"Verify that fifo_count_{n} never exceeds threshold of {16 + n}",
            target=f"fifo_count_{n} <= {16 + n}",
            acceptable=[f"fifo_count_{n} < {17 + n}", f"(fifo_count_{n} <= {16 + n})"],
            tokens=[f"fifo_count_{n}", "<=", f"{16 + n}"],
            distractors=[">", "==", "##1"],
            explanation=f"Invariance safety check ensuring FIFO level doesn't overflow limit of {16 + n}.",
            category="DIRECTIVES",
            difficulty="Basic",
            prop_name=f"fifo_max_{n}"
        )
    else:
        add(
            prompt=f"Prune formal search space to enforce mode_{n} inside valid configuration set {{0, 1, 3}}",
            target=f"mode_{n} inside {{0, 1, 3}}",
            acceptable=[f"(mode_{n} inside {{0, 1, 3}})"],
            tokens=[f"mode_{n}", "inside", "{0, 1, 3}"],
            distractors=["==", "!=", "intersect"],
            explanation="Restrict property operator constraining formal verification solver exploration.",
            category="DIRECTIVES",
            difficulty="Medium",
            prop_name=f"restrict_mode_{n}"
        )

# --- Group 4: LTL & Advanced Implication Protocols (Items 106 - 150) ---
for i in range(106, 150):
    n = i - 105
    if n % 4 == 1:
        add(
            prompt=f"Verify that bus request bus_req_{n} is eventually granted within {n+2} cycles",
            target=f"bus_req_{n} |-> s_eventually [1:{n+2}] bus_gnt_{n}",
            acceptable=[f"bus_req_{n} |-> s_eventually[1:{n+2}] bus_gnt_{n}"],
            tokens=[f"bus_req_{n}", "|->", "s_eventually", f"[1:{n+2}]", f"bus_gnt_{n}"],
            distractors=["always", "|=>", "until"],
            explanation=f"Bounded strong liveness operator s_eventually [1:{n+2}] enforces grant window.",
            category="LTL",
            difficulty="Advanced",
            prop_name=f"eventual_grant_{n}"
        )
    elif n % 4 == 2:
        add(
            prompt=f"Signal lock_{n} must hold until unlock_{n} arrives, overlapping cycle of unlock",
            target=f"lock_{n} until_with unlock_{n}",
            acceptable=[f"(lock_{n}) until_with (unlock_{n})"],
            tokens=[f"lock_{n}", "until_with", f"unlock_{n}"],
            distractors=["until", "s_until", "throughout"],
            explanation="until_with requires lock to hold up to and including the cycle where unlock asserts.",
            category="LTL",
            difficulty="Medium",
            prop_name=f"lock_until_with_{n}"
        )
    elif n % 4 == 3:
        add(
            prompt=f"When trigger_{n} occurs, check status in next clock cycle using nexttime",
            target=f"trigger_{n} |-> nexttime status_ok_{n}",
            acceptable=[f"trigger_{n} |=> status_ok_{n}", f"trigger_{n} |-> s_nexttime status_ok_{n}"],
            tokens=[f"trigger_{n}", "|->", "nexttime", f"status_ok_{n}"],
            distractors=["always", "|=>", "until"],
            explanation="nexttime P evaluates property P in the immediate next clock cycle.",
            category="LTL",
            difficulty="Medium",
            prop_name=f"nexttime_status_{n}"
        )
    else:
        add(
            prompt=f"Verify that invariant safe_mode_{n} always holds throughout execution",
            target=f"always safe_mode_{n}",
            acceptable=[f"always (safe_mode_{n})"],
            tokens=["always", f"safe_mode_{n}"],
            distractors=["s_eventually", "nexttime", "until"],
            explanation="always operator declares an invariant that must evaluate true at every clock tick.",
            category="LTL",
            difficulty="Basic",
            prop_name=f"invariant_safe_{n}"
        )

print(f"Total challenges generated: {len(challenges)}")
with open("challenges.json", "w") as f:
    json.dump(challenges, f, indent=2)
