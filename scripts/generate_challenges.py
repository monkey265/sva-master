import json

# Generator for 150 diverse, precise, idiomatic SVA practice challenges
challenges = []

def add(prompt, target, acceptable, tokens, distractors, explanation, category, difficulty="Medium"):
    idx = len(challenges) + 1
    challenges.append({
        "id": f"sva_chal_{idx:03d}",
        "prompt": prompt,
        "targetSva": target,
        "acceptableAnswers": list(set([target] + acceptable)),
        "tokens": tokens,
        "distractors": distractors,
        "explanation": explanation,
        "category": category,
        "difficulty": difficulty
    })

# --- Group 1: Consecutive Repetitions & Handshakes (Items 1 - 25) ---
add(
    "Verify that valid and ready are both at 1 for 2 consecutive clock cycles",
    "(valid && ready) [*2]",
    ["(valid & ready) [*2]", "valid && ready [*2]"],
    ["(", "valid", "&&", "ready", ")", "[*2]"],
    ["|->", "[->2]", "##1", "[=2]"],
    "Consecutive repetition [*2] requires the boolean condition (valid && ready) to hold for 2 consecutive clock ticks.",
    "SEQUENCES", "Basic"
)

add(
    "Verify that reset signal rst_n remains low for at least 3 consecutive cycles",
    "!rst_n [*3]",
    ["(!rst_n) [*3]", "~rst_n [*3]", "rst_n == 0 [*3]", "(rst_n == 0) [*3]"],
    ["!rst_n", "[*3]"],
    ["[*2]", "[->3]", "##3", "|=>"],
    "The [*3] consecutive repetition ensures !rst_n is sampled asserted low across 3 successive clock edges.",
    "SEQUENCES", "Basic"
)

add(
    "When req is asserted, ack must be asserted 1 cycle later",
    "req |=> ack",
    ["req |-> ##1 ack"],
    ["req", "|=>", "ack"],
    ["|->", "##0", "ack [*1]"],
    "Non-overlapping implication |=> is equivalent to |-> ##1, evaluating ack on the immediate next clock tick.",
    "IMPLICATION", "Basic"
)

add(
    "When cs is high, addr must be within range [< 32'h1000] in the same cycle",
    "cs |-> (addr < 32'h1000)",
    ["cs |-> addr < 32'h1000"],
    ["cs", "|->", "(", "addr", "<", "32'h1000", ")"],
    ["|=>", "##1", ">"],
    "Overlapping implication |-> checks that the consequent condition evaluates in the same clock cycle as cs.",
    "IMPLICATION", "Basic"
)

add(
    "Verify that enable remains asserted for between 2 and 4 consecutive cycles",
    "enable [*2:4]",
    ["(enable) [*2:4]"],
    ["enable", "[*2:4]"],
    ["[->2:4]", "[=2:4]", "##[2:4]"],
    "Consecutive range repetition [*2:4] matches when enable is continuously high between 2 and 4 cycles.",
    "SEQUENCES", "Basic"
)

add(
    "When start is pulsed, busy must stay high until done arrives (strong)",
    "start |=> busy s_until done",
    ["start |-> ##1 (busy s_until done)"],
    ["start", "|=>", "busy", "s_until", "done"],
    ["until", "|->", "s_eventually"],
    "The strong s_until requires busy to hold on each cycle until done arrives, and guarantees done will eventually arrive.",
    "LTL", "Advanced"
)

add(
    "When req is asserted, ack must arrive within 1 to 3 clock cycles",
    "req |-> ##[1:3] ack",
    ["req |=> ##[0:2] ack"],
    ["req", "|->", "##[1:3]", "ack"],
    ["|=>", "##[1:3]", "[*1:3]"],
    "Bounded cycle delay ##[1:3] after req requires ack to assert between 1 and 3 cycles later.",
    "SEQUENCES", "Medium"
)

add(
    "Verify that state vector is always one-hot and never unknown",
    "$onehot(state) && !$isunknown(state)",
    ["$onehot(state) and !$isunknown(state)", "!$isunknown(state) && $onehot(state)"],
    ["$onehot(state)", "&&", "!$isunknown(state)"],
    ["$onehot0(state)", "##1", "$countones(state)"],
    "$onehot checks that exactly 1 bit is high, and !$isunknown ensures no bits are X or Z.",
    "SYSTEM_FUNCTIONS", "Medium"
)

add(
    "When transaction starts, grant must be eventually guaranteed to occur before sim ends",
    "req |-> s_eventually gnt",
    ["req |=> s_eventually gnt"],
    ["req", "|->", "s_eventually", "gnt"],
    ["always", "|=>", "eventually"],
    "s_eventually provides strong liveness checking, catching hangs and deadlocks before simulation concludes.",
    "LTL", "Medium"
)

add(
    "Verify that burst_len occurs 3 times non-consecutively, ending strictly on the 3rd match",
    "burst_start ##1 (data_valid [->3]) ##1 burst_end",
    ["burst_start ##1 data_valid [->3] ##1 burst_end"],
    ["burst_start", "##1", "data_valid", "[->3]", "##1", "burst_end"],
    ["[*3]", "[=3]", "|->"],
    "Goto repetition [->3] matches when data_valid occurs 3 times non-consecutively, ending precisely on the 3rd occurrence.",
    "SEQUENCES", "Advanced"
)

add(
    "Ensure signal data remains stable whenever valid is high but ready is low",
    "(valid && !ready) |=> $stable(data)",
    ["valid && !ready |=> $stable(data)", "(valid && !ready) |-> ##1 $stable(data)"],
    ["(", "valid", "&&", "!ready", ")", "|=>", "$stable(data)"],
    ["|->", "$changed(data)", "$rose(data)"],
    "Backpressure stability rule: if sender asserts valid and receiver isn't ready, data must remain stable in next cycle.",
    "SYSTEM_FUNCTIONS", "Medium"
)

add(
    "Check that ack was 0 in the previous cycle when req rises",
    "$rose(req) |-> $past(ack) == 0",
    ["$rose(req) |-> !$past(ack)", "$rose(req) |-> ($past(ack) == 1'b0)"],
    ["$rose(req)", "|->", "$past(ack)", "==", "0"],
    ["|=>", "$rose(ack)", "$fell(req)"],
    "$rose detects rising edge of req, and $past(ack) inspects ack value in previous cycle.",
    "SYSTEM_FUNCTIONS", "Medium"
)

add(
    "Verify that packet payload of 4 words is accompanied throughout by payload_valid",
    "payload_valid throughout (pkt_start ##1 pkt_data[*4] ##1 pkt_end)",
    ["payload_valid throughout pkt_start ##1 pkt_data[*4] ##1 pkt_end"],
    ["payload_valid", "throughout", "(", "pkt_start", "##1", "pkt_data[*4]", "##1", "pkt_end", ")"],
    ["within", "intersect", "and"],
    "The throughout operator requires boolean payload_valid to remain asserted for the entire sequence duration.",
    "SEQUENCES", "Advanced"
)

add(
    "Both tx_done and rx_done sequences must conclude on the exact same clock cycle",
    "(tx_start ##[1:5] tx_done) intersect (rx_start ##[1:5] rx_done)",
    ["tx_start ##[1:5] tx_done intersect rx_start ##[1:5] rx_done"],
    ["(", "tx_start ##[1:5] tx_done", ")", "intersect", "(", "rx_start ##[1:5] rx_done", ")"],
    ["and", "or", "throughout"],
    "intersect requires both sequences to match and terminate on the exact same clock edge.",
    "SEQUENCES", "Advanced"
)

add(
    "Take only the first match of req ##[1:5] ack before triggering data phase",
    "first_match(req ##[1:5] ack) |=> data_phase",
    ["first_match(req ##[1:5] ack) |-> ##1 data_phase"],
    ["first_match(", "req ##[1:5] ack", ")", "|=>", "data_phase"],
    ["|->", "intersect", "within"],
    "first_match discards later matches in the multi-match delay window, preventing duplicate threads.",
    "SEQUENCES", "Advanced"
)

add(
    "Verify that frame_sync is followed by exactly 8 consecutive bytes of data",
    "frame_sync |=> data_byte [*8]",
    ["frame_sync |-> ##1 data_byte [*8]"],
    ["frame_sync", "|=>", "data_byte", "[*8]"],
    ["|->", "[->8]", "[=8]"],
    "frame_sync followed next cycle by 8 back-to-back cycles of data_byte.",
    "SEQUENCES", "Medium"
)

add(
    "Verify that interrupt line irq drops low on falling edge of clk_gate",
    "$fell(clk_gate) |-> !irq",
    ["$fell(clk_gate) |-> irq == 0"],
    ["$fell(clk_gate)", "|->", "!irq"],
    ["$rose(clk_gate)", "|=>", "$stable(irq)"],
    "$fell checks high-to-low transition and overlapping |-> checks !irq in that cycle.",
    "SYSTEM_FUNCTIONS", "Medium"
)

add(
    "Verify that bus arbiter vector has at most one grant active (onehot0)",
    "$onehot0(gnt_bus)",
    ["$onehot0(gnt_bus) == 1"],
    ["$onehot0(gnt_bus)"],
    ["$onehot(gnt_bus)", "$countones(gnt_bus) == 1", "!$isunknown(gnt_bus)"],
    "$onehot0 allows either 0 grants (idle) or exactly 1 grant active.",
    "SYSTEM_FUNCTIONS", "Basic"
)

add(
    "Verify that exactly 2 bits in mask vector are set to 1",
    "$countones(mask) == 2",
    ["$countones(mask) == 32'd2"],
    ["$countones(mask)", "==", "2"],
    ["$onehot(mask)", "$onehot0(mask)", "3"],
    "$countones counts the total number of set bits in the bit-vector.",
    "SYSTEM_FUNCTIONS", "Basic"
)

add(
    "Whenever error_flag asserts, immediately reject assertion execution asynchronously",
    "reject_on(error_flag) (req |=> ack)",
    ["reject_on(error_flag) req |=> ack"],
    ["reject_on(", "error_flag", ")", "req", "|=>", "ack"],
    ["accept_on", "sync_reject_on", "disable iff"],
    "reject_on immediately aborts the assertion with failure as soon as error_flag goes high.",
    "ABORTS", "Advanced"
)

add(
    "Whenever soft_reset asserts, immediately accept assertion execution as passing",
    "accept_on(soft_reset) (req |=> ##[1:10] ack)",
    ["accept_on(soft_reset) req |=> ##[1:10] ack"],
    ["accept_on(", "soft_reset", ")", "req", "|=>", "##[1:10]", "ack"],
    ["reject_on", "sync_accept_on", "disable iff"],
    "accept_on triggers an immediate passing verdict when condition becomes true.",
    "ABORTS", "Advanced"
)

add(
    "On clock edge, synchronously abort with failure if fault is active",
    "sync_reject_on(fault) (start |=> done)",
    ["sync_reject_on(fault) start |=> done"],
    ["sync_reject_on(", "fault", ")", "start", "|=>", "done"],
    ["sync_accept_on", "reject_on", "accept_on"],
    "sync_reject_on samples abort on clock edge rather than purely asynchronously.",
    "ABORTS", "Advanced"
)

add(
    "When write mode is set, ack must come in 1 cycle, otherwise in 2 cycles",
    "valid |-> if (wr_mode) ##1 wr_ack else ##2 rd_ack",
    ["valid |-> (if (wr_mode) ##1 wr_ack else ##2 rd_ack)"],
    ["valid", "|->", "if (wr_mode)", "##1 wr_ack", "else", "##2 rd_ack"],
    ["|=>", "and", "or"],
    "Conditional property evaluation inside SVA using if-else construct.",
    "IMPLICATION", "Advanced"
)

add(
    "Ensure signal data_in was identical 2 cycles ago to current data_out",
    "data_out == $past(data_in, 2)",
    ["$past(data_in, 2) == data_out"],
    ["data_out", "==", "$past(data_in, 2)"],
    ["$past(data_in)", "$rose(data_in)", "$stable(data_in)"],
    "$past(sig, N) reads sampled value from N clock cycles in the past.",
    "SYSTEM_FUNCTIONS", "Medium"
)

add(
    "Verify that pulse is high for exactly 1 cycle (rising then falling edge)",
    "$rose(pulse) |=> $fell(pulse)",
    ["$rose(pulse) |-> ##1 $fell(pulse)"],
    ["$rose(pulse)", "|=>", "$fell(pulse)"],
    ["$stable(pulse)", "|->", "$rose(pulse)"],
    "A 1-cycle strobe rises at cycle N and must fall at cycle N+1.",
    "SYSTEM_FUNCTIONS", "Medium"
)

# --- Group 2: Temporal Repetition, Delays, Protocols (Items 26 - 65) ---
for i in range(26, 66):
    n = i - 25
    if n % 5 == 1:
        add(
            f"Verify that ready holds low for exactly {n} consecutive cycles after stall",
            f"stall |=> !ready [*{n}]",
            [f"stall |-> ##1 !ready [*{n}]", f"stall |=> (!ready) [*{n}]"],
            ["stall", "|=>", "!ready", f"[*{n}]"],
            ["|->", f"[->{n}]", f"[={n}]"],
            f"Consecutive repetition [*{n}] ensures !ready is sampled for {n} cycles after stall.",
            "SEQUENCES", "Medium"
        )
    elif n % 5 == 2:
        add(
            f"When tx_req is asserted, tx_ack must arrive after exactly {n} clock cycles",
            f"tx_req |-> ##{n} tx_ack",
            [f"tx_req |=> ##{n-1} tx_ack" if n > 1 else f"tx_req |=> tx_ack"],
            ["tx_req", "|->", f"##{n}", "tx_ack"],
            ["|=>", f"[*{n}]", f"##{n+1}"],
            f"Fixed delay ##{n} requires tx_ack exactly {n} cycles following tx_req.",
            "SEQUENCES", "Basic"
        )
    elif n % 5 == 3:
        add(
            f"Verify that heartbeat changed value from previous cycle at tick {n}",
            f"$changed(heartbeat)",
            ["heartbeat != $past(heartbeat)", "$past(heartbeat) != heartbeat"],
            ["$changed(heartbeat)"],
            ["$stable(heartbeat)", "$rose(heartbeat)", "$fell(heartbeat)"],
            "$changed returns 1 if signal is not equal to its sampled value from the prior cycle.",
            "SYSTEM_FUNCTIONS", "Basic"
        )
    elif n % 5 == 4:
        add(
            f"Event trigger must happen {n} times non-consecutively before finish, allowing extra cycles after",
            f"trigger [={n}] ##1 finish",
            [f"(trigger [={n}]) ##1 finish"],
            ["trigger", f"[={n}]", "##1", "finish"],
            [f"[->{n}]", f"[*{n}]", "|=>"],
            f"Non-consecutive repetition [={n}] allows delay after the {n}-th occurrence before finish.",
            "SEQUENCES", "Advanced"
        )
    else:
        add(
            f"Ensure sub-sequence sub_seq_{n} occurs entirely within main_frame_{n}",
            f"sub_seq_{n} within main_frame_{n}",
            [f"(sub_seq_{n}) within (main_frame_{n})"],
            [f"sub_seq_{n}", "within", f"main_frame_{n}"],
            ["throughout", "intersect", "and"],
            "within operator checks that sub-sequence occurs within the timeframe of main sequence.",
            "SEQUENCES", "Advanced"
        )

# --- Group 3: Formal & Directives Checks (Items 66 - 105) ---
for i in range(66, 106):
    n = i - 65
    if n % 4 == 1:
        add(
            f"Constrain formal input in_data_{n} to remain stable when in_valid_{n} is asserted",
            f"in_valid_{n} |-> $stable(in_data_{n})",
            [f"(in_valid_{n}) |-> $stable(in_data_{n})"],
            [f"in_valid_{n}", "|->", f"$stable(in_data_{n})"],
            ["|=>", "$changed(in_data)", "always"],
            f"Environmental assumption/check: in_data_{n} must not glitch when in_valid_{n} is high.",
            "DIRECTIVES", "Medium"
        )
    elif n % 4 == 2:
        add(
            f"Cover scenario where burst length is {n} cycles followed by eop",
            f"burst_en [* {n}] ##1 eop",
            [f"burst_en [*{n}] ##1 eop", f"(burst_en [*{n}]) ##1 eop"],
            ["burst_en", f"[*{n}]", "##1", "eop"],
            [f"[->{n}]", f"[={n}]", "|->"],
            f"Cover property sequence tracking witness trace for {n} bursts ending with eop.",
            "DIRECTIVES", "Medium"
        )
    elif n % 4 == 3:
        add(
            f"Verify that fifo_count_{n} never exceeds threshold of {16 + n}",
            f"fifo_count_{n} <= {16 + n}",
            [f"fifo_count_{n} < {17 + n}", f"(fifo_count_{n} <= {16 + n})"],
            [f"fifo_count_{n}", "<=", f"{16 + n}"],
            [">", "==", "##1"],
            f"Invariance safety check ensuring FIFO level doesn't overflow limit of {16 + n}.",
            "DIRECTIVES", "Basic"
        )
    else:
        add(
            f"Prune formal search space to enforce mode_{n} inside valid configuration set {{0, 1, 3}}",
            f"mode_{n} inside {{0, 1, 3}}",
            [f"(mode_{n} inside {{0, 1, 3}})"],
            [f"mode_{n}", "inside", "{0, 1, 3}"],
            ["==", "!=", "intersect"],
            "Restrict property operator constraining formal verification solver exploration.",
            "DIRECTIVES", "Medium"
        )

# --- Group 4: LTL & Implication Protocols (Items 106 - 150) ---
for i in range(106, 151):
    n = i - 105
    if n % 4 == 1:
        add(
            f"Verify that bus request bus_req_{n} is eventually granted within {n+2} cycles",
            f"bus_req_{n} |-> s_eventually [1:{n+2}] bus_gnt_{n}",
            [f"bus_req_{n} |-> s_eventually[1:{n+2}] bus_gnt_{n}"],
            [f"bus_req_{n}", "|->", "s_eventually", f"[1:{n+2}]", f"bus_gnt_{n}"],
            ["always", "|=>", "until"],
            f"Bounded strong liveness operator s_eventually [1:{n+2}] enforces grant window.",
            "LTL", "Advanced"
        )
    elif n % 4 == 2:
        add(
            f"Signal lock_{n} must hold until unlock_{n} arrives, overlapping cycle of unlock",
            f"lock_{n} until_with unlock_{n}",
            [f"(lock_{n}) until_with (unlock_{n})"],
            [f"lock_{n}", "until_with", f"unlock_{n}"],
            ["until", "s_until", "throughout"],
            "until_with requires lock to hold up to and including the cycle where unlock asserts.",
            "LTL", "Medium"
        )
    elif n % 4 == 3:
        add(
            f"When trigger_{n} occurs, check status in next clock cycle using nexttime",
            f"trigger_{n} |-> nexttime status_ok_{n}",
            [f"trigger_{n} |=> status_ok_{n}", f"trigger_{n} |-> s_nexttime status_ok_{n}"],
            [f"trigger_{n}", "|->", "nexttime", f"status_ok_{n}"],
            ["always", "|=>", "until"],
            "nexttime P evaluates property P in the immediate next clock cycle.",
            "LTL", "Medium"
        )
    else:
        add(
            f"Verify that invariant safe_mode_{n} always holds throughout execution",
            f"always safe_mode_{n}",
            [f"always (safe_mode_{n})"],
            ["always", f"safe_mode_{n}"],
            ["s_eventually", "nexttime", "until"],
            "always operator declares an invariant that must evaluate true at every clock tick.",
            "LTL", "Basic"
        )

print(f"Total challenges generated: {len(challenges)}")
with open("challenges.json", "w") as f:
    json.dump(challenges, f, indent=2)
