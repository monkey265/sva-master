package com.svamaster.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.svamaster.ui.components.SvaCodeBlock

@Composable
fun CheatsheetScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Section 1
        item {
            CheatsheetSectionHeader(
                title = "1. Directives Overview",
                subtitle = "Dynamic Simulation vs Formal Verification"
            )
            DirectivesTable()
        }

        // Section 2
        item {
            CheatsheetSectionHeader(
                title = "2. Anatomy & Controls",
                subtitle = "Clocking, Asynchronous Resets, and Scoping"
            )
            AnatomySnippet()
        }

        // Section 3
        item {
            CheatsheetSectionHeader(
                title = "3. Property & Temporal Operators",
                subtitle = "Implication, Branching, and LTL"
            )
            ImplicationTable()
            Spacer(modifier = Modifier.height(10.dp))
            LtlTable()
            Spacer(modifier = Modifier.height(10.dp))
            AbortsSnippet()
        }

        // Section 4
        item {
            CheatsheetSectionHeader(
                title = "4. Sequence Operators & Repetitions",
                subtitle = "Delays, Consecutive [*N], Goto [->N], Non-consecutive [=N]"
            )
            SequencesTable()
        }

        // Section 5
        item {
            CheatsheetSectionHeader(
                title = "5. Sampled Value System Functions",
                subtitle = "Signal History, Transition Detection, and Vector Introspection"
            )
            SampledFunctionsTable()
        }
    }
}

@Composable
fun CheatsheetSectionHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(bottom = 6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun DirectivesTable() {
    val rows = listOf(
        Triple("assert property", "Fails with error on violation", "Searches for counterexample trace"),
        Triple("cover property", "Records hit counts", "Finds witness trace reaching scenario"),
        Triple("assume property", "Fails with error (like assert)", "Restricts state-space of inputs"),
        Triple("restrict property", "Ignored (guard with `ifdef FORMAL)", "Prunes solver exploration")
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Directive", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.1f), style = MaterialTheme.typography.labelMedium)
                Text("Dynamic Sim", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.1f), style = MaterialTheme.typography.labelMedium)
                Text("Formal Verif", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f), style = MaterialTheme.typography.labelMedium)
            }
            HorizontalDivider()
            rows.forEach { (dir, sim, formal) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        dir,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1.1f)
                    )
                    Text(
                        sim,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        modifier = Modifier.weight(1.1f)
                    )
                    Text(
                        formal,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1.2f)
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
            }
        }
    }
}

@Composable
fun AnatomySnippet() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            SvaCodeBlock(
                code = """
                    property p_example;
                      @(posedge clk) disable iff (rst)
                      valid_i |=> valid_o;
                    endproperty

                    a_example: assert property (p_example)
                      else `uvm_error("SVA", "Assertion failed!");
                """.trimIndent()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "• @(posedge clk) / default clocking @(posedge clk); – defines sampling clock.\n" +
                "• disable iff (rst) / default disable iff (rst); – asynchronous reset suppression.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ImplicationTable() {
    val items = listOf(
        Pair("A |-> P", "Overlapping Implication: P begins evaluation in same cycle (##0)"),
        Pair("A |=> P", "Non-overlapping Implication: P begins evaluation in next cycle (##1)"),
        Pair("if (c) P1 else P2", "Conditional branch between P1 and P2 based on boolean c"),
        Pair("P1 and P2", "Conjunction: both properties must hold"),
        Pair("P1 or P2", "Disjunction: at least one property must hold"),
        Pair("not P", "Negation: property P must not occur"),
        Pair("P1 implies P2", "Logical implication: equivalent to not P1 or P2"),
        Pair("P1 iff P2", "Equivalence: both properties have identical truth value")
    )

    CompactTableCard(title = "Implication & Branching", items = items)
}

@Composable
fun LtlTable() {
    val items = listOf(
        Pair("always P", "Weak: P must hold in every cycle from now until sim end"),
        Pair("always [min:max] P", "Weak: P holds in every cycle within range"),
        Pair("s_eventually P", "Strong: P must occur at least once before sim end"),
        Pair("s_eventually [min:max] P", "Strong: P must occur within cycle window [min:max]"),
        Pair("P1 until P2", "Weak: P1 holds continuously until P2 becomes true"),
        Pair("P1 s_until P2", "Strong: P1 holds until P2, and P2 is guaranteed to happen"),
        Pair("P1 until_with P2", "Weak: P1 holds until and including cycle P2 occurs"),
        Pair("P1 s_until_with P2", "Strong: P1 holds through P2 occurrence, P2 guaranteed"),
        Pair("nexttime P / s_nexttime P", "Weak / Strong next clock cycle evaluation")
    )

    CompactTableCard(title = "Linear Temporal Logic (LTL) Operators (Weak vs Strong)", items = items)
}

@Composable
fun AbortsSnippet() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Aborts (Asynchronous / Synchronous)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(6.dp))
            SvaCodeBlock(
                code = """
                    accept_on (abort_cond) property_expr      // async immediate success
                    reject_on (abort_cond) property_expr      // async immediate failure
                    sync_accept_on (abort_cond) property_expr // sync success on clock edge
                    sync_reject_on (abort_cond) property_expr // sync failure on clock edge
                """.trimIndent()
            )
        }
    }
}

@Composable
fun SequencesTable() {
    val items = listOf(
        Pair("##N / ##[min:max]", "Cycle delay (exact N cycles or range min..max)"),
        Pair("expr [*N]", "Consecutive repetition: exactly N cycles in a row"),
        Pair("expr [*min:max]", "Consecutive repetition within min and max occurrences"),
        Pair("expr [->N]", "Goto repetition: N times non-consecutively; ends strictly on Nth match"),
        Pair("expr [=N]", "Non-consecutive: N times, allows extra delay after Nth match"),
        Pair("S1 and S2", "Both match; may conclude at different clock cycles"),
        Pair("S1 intersect S2", "Both match and must finish on the exact same cycle"),
        Pair("S1 or S2", "At least one sequence matches"),
        Pair("first_match(S)", "Selects only earliest match of multi-match sequence"),
        Pair("b throughout S", "Boolean b must remain true throughout the entire duration of S"),
        Pair("S1 within S2", "Sequence S1 occurs entirely inside the timespan of S2")
    )

    CompactTableCard(title = "Sequence Operators & Repetitions", items = items)
}

@Composable
fun SampledFunctionsTable() {
    val items = listOf(
        Pair("\$past(sig, N)", "Sampled value of sig N clock cycles ago (default N=1)"),
        Pair("\$rose(sig)", "Rising edge transition (0/X/Z -> 1)"),
        Pair("\$fell(sig)", "Falling edge transition (1/X/Z -> 0)"),
        Pair("\$stable(sig)", "Signal remained unchanged: sig == \$past(sig)"),
        Pair("\$changed(sig)", "Signal changed value from previous cycle"),
        Pair("\$isunknown(sig)", "Returns 1 if any bit in vector is 'X' or 'Z'"),
        Pair("!\$isunknown(sig)", "Returns 1 if all bits are cleanly 0 or 1"),
        Pair("\$onehot(sig)", "Returns 1 if exactly one bit is high (FSM verification)"),
        Pair("\$onehot0(sig)", "Returns 1 if zero or one bit is high"),
        Pair("\$countones(sig)", "Returns total count of bits set to 1")
    )

    CompactTableCard(title = "Sampled Value System Functions", items = items)
}

@Composable
fun CompactTableCard(title: String, items: List<Pair<String, String>>) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            items.forEach { (syntax, desc) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = syntax,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1.4f)
                    )
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
            }
        }
    }
}
