package com.svamaster.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.svamaster.theme.CodeComment
import com.svamaster.theme.CodeFunction
import com.svamaster.theme.CodeKeyword
import com.svamaster.theme.CodeOperator
import com.svamaster.theme.CodeString

@Composable
fun SvaCodeBlock(
    code: String,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF14181B))
            .padding(14.dp)
            .horizontalScroll(scrollState)
    ) {
        Text(
            text = highlightSvaSyntax(code),
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            color = Color(0xFFCFD8DC)
        )
    }
}

fun highlightSvaSyntax(code: String) = buildAnnotatedString {
    val lines = code.lines()
    val keywords = setOf(
        "assert", "property", "endproperty", "cover", "assume", "restrict",
        "sequence", "endsequence", "disable", "iff", "posedge", "negedge",
        "default", "clocking", "endclocking", "always", "s_eventually", "until",
        "s_until", "until_with", "s_until_with", "nexttime", "s_nexttime",
        "accept_on", "reject_on", "sync_accept_on", "sync_reject_on",
        "and", "or", "not", "implies", "inside", "if", "else", "first_match",
        "throughout", "within", "input", "output"
    )
    val sysFunctions = setOf(
        "\$past", "\$rose", "\$fell", "\$stable", "\$changed",
        "\$isunknown", "\$onehot", "\$onehot0", "\$countones", "\$error", "\$fatal"
    )

    lines.forEachIndexed { lineIdx, line ->
        if (line.trim().startsWith("//") || line.trim().startsWith("`ifdef") || line.trim().startsWith("`endif")) {
            withStyle(SpanStyle(color = CodeComment, fontWeight = FontWeight.Normal)) {
                append(line)
            }
        } else {
            var i = 0
            while (i < line.length) {
                if (line.startsWith("`uvm_", i)) {
                    val end = line.indexOfAny(charArrayOf(' ', '(', ';'), i).let { if (it == -1) line.length else it }
                    withStyle(SpanStyle(color = CodeFunction, fontWeight = FontWeight.Bold)) {
                        append(line.substring(i, end))
                    }
                    i = end
                } else if (line[i] == '"') {
                    val nextQuote = line.indexOf('"', i + 1)
                    val end = if (nextQuote == -1) line.length else nextQuote + 1
                    withStyle(SpanStyle(color = CodeString)) {
                        append(line.substring(i, end))
                    }
                    i = end
                } else if (line.startsWith("|->", i) || line.startsWith("|=>", i) || line.startsWith("##", i)) {
                    val opLength = if (line.startsWith("##", i)) 2 else 3
                    withStyle(SpanStyle(color = CodeOperator, fontWeight = FontWeight.Bold)) {
                        append(line.substring(i, i + opLength))
                    }
                    i += opLength
                } else if (line[i].isLetter() || line[i] == '$' || line[i] == '_') {
                    var end = i
                    while (end < line.length && (line[end].isLetterOrDigit() || line[end] == '_' || line[end] == '$')) {
                        end++
                    }
                    val word = line.substring(i, end)
                    when {
                        keywords.contains(word) -> {
                            withStyle(SpanStyle(color = CodeKeyword, fontWeight = FontWeight.Bold)) {
                                append(word)
                            }
                        }
                        sysFunctions.contains(word) -> {
                            withStyle(SpanStyle(color = CodeFunction, fontWeight = FontWeight.Bold)) {
                                append(word)
                            }
                        }
                        else -> {
                            append(word)
                        }
                    }
                    i = end
                } else {
                    append(line[i])
                    i++
                }
            }
        }
        if (lineIdx < lines.size - 1) {
            append("\n")
        }
    }
}
