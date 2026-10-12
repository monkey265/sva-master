package com.svamaster.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.zIndex
import com.svamaster.data.SvaRepository
import com.svamaster.model.SvaBadge
import com.svamaster.model.SvaCategory
import com.svamaster.model.SvaPracticeChallenge
import com.svamaster.ui.components.SvaCodeBlock

enum class PracticeInputMode {
    TYPE_SYNTAX,
    TOKEN_ASSEMBLY
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PracticeScreen() {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val progressManager = remember(context) { com.svamaster.data.SvaProgressManager.getInstance(context) }
    val allChallenges = remember { SvaRepository.challenges }
    val badges = remember { SvaRepository.badges }
    val currentChallengeIndex by progressManager.currentChallengeIndex.collectAsState()
    val solvedIds by progressManager.solvedIds.collectAsState()
    val quizScore by progressManager.quizScore.collectAsState()

    var inputMode by remember { mutableStateOf(PracticeInputMode.TYPE_SYNTAX) }
    var typedCode by remember { mutableStateOf("") }
    var checkVerdict by remember { mutableStateOf<Boolean?>(null) }
    var showHint by remember { mutableStateOf(false) }

    var selectedCategoryFilter by remember { mutableStateOf<SvaCategory?>(null) }
    var filterUnsolvedOnly by remember { mutableStateOf(false) }
    var showJumpDialog by remember { mutableStateOf(false) }
    var showBadgesDialog by remember { mutableStateOf(false) }

    val filteredChallenges = remember(allChallenges, selectedCategoryFilter, filterUnsolvedOnly, solvedIds) {
        var list = allChallenges
        if (selectedCategoryFilter != null) {
            list = list.filter { it.category == selectedCategoryFilter }
        }
        if (filterUnsolvedOnly) {
            val unsolved = list.filter { !solvedIds.contains(it.id) }
            if (unsolved.isNotEmpty()) {
                list = unsolved
            }
        }
        list
    }

    val safeIndex = currentChallengeIndex.coerceIn(0, (allChallenges.size - 1).coerceAtLeast(0))
    val currentFilteredIndex = remember(filteredChallenges, safeIndex) {
        val found = filteredChallenges.indexOfFirst { it.id == allChallenges[safeIndex].id }
        if (found >= 0) found else 0
    }
    val currentChallenge = filteredChallenges.getOrElse(currentFilteredIndex) { allChallenges[safeIndex] }

    val unlockedBadgesCount = remember(solvedIds, quizScore, allChallenges) {
        badges.count { it.isUnlocked(solvedIds, quizScore, allChallenges) }
    }

    // Token Assembly State
    val selectedTokens = remember(currentChallenge.id) { mutableStateListOf<Pair<Int, String>>() }
    val poolTokens = remember(currentChallenge.id) {
        val combined = (currentChallenge.tokens + currentChallenge.distractors).shuffled()
        mutableStateListOf<Pair<Int, String>>().apply {
            combined.forEachIndexed { i, token -> add(Pair(i, token)) }
        }
    }

    // Drag & Reorder State
    var canvasCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var draggingToken by remember { mutableStateOf<Pair<Int, String>?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var targetDropIndex by remember { mutableIntStateOf(-1) }
    val tileCoordinates = remember(currentChallenge.id) { mutableStateMapOf<Int, Rect>() }

    val onDragUpdate: (Pair<Int, String>, Offset) -> Unit = { token, amount ->
        dragOffset += amount
        val startRect = tileCoordinates[token.first]
        if (startRect != null) {
            val currentCenter = startRect.center + dragOffset
            var closestDist = Float.MAX_VALUE
            var closestIdx = -1
            selectedTokens.forEachIndexed { i, otherPair ->
                val otherRect = tileCoordinates[otherPair.first]
                if (otherRect != null) {
                    val dx = currentCenter.x - otherRect.center.x
                    val dy = currentCenter.y - otherRect.center.y
                    val dist = dx * dx + dy * dy
                    if (dist < closestDist) {
                        closestDist = dist
                        closestIdx = i
                    }
                }
            }
            if (closestIdx != -1 && closestIdx != targetDropIndex) {
                targetDropIndex = closestIdx
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
        }
    }

    val onDragFinish: () -> Unit = {
        val currentDragging = draggingToken
        if (currentDragging != null && targetDropIndex in 0 until selectedTokens.size) {
            val fromIdx = selectedTokens.indexOf(currentDragging)
            if (fromIdx != -1 && fromIdx != targetDropIndex) {
                val item = selectedTokens.removeAt(fromIdx)
                selectedTokens.add(targetDropIndex, item)
                checkVerdict = null
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
        }
        draggingToken = null
        dragOffset = Offset.Zero
        targetDropIndex = -1
    }

    val currentDragUpdate by rememberUpdatedState(onDragUpdate)
    val currentDragFinish by rememberUpdatedState(onDragFinish)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Progress & Stats Header
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${allChallenges.indexOfFirst { it.id == currentChallenge.id } + 1}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Challenge ${currentFilteredIndex + 1} of ${filteredChallenges.size}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (selectedCategoryFilter != null || filterUnsolvedOnly) {
                                Text(
                                    text = "${selectedCategoryFilter?.title ?: "Unsolved"} Filter Active",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f),
                            modifier = Modifier.clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                showBadgesDialog = true
                            }
                        ) {
                            Text(
                                text = "🏆 $unlockedBadgesCount/${badges.size}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                showJumpDialog = true
                            }
                        ) {
                            Text(
                                text = "🔍 Jump",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            )
                        }
                    }
                }

                LinearProgressIndicator(
                    progress = { (solvedIds.size.toFloat() / allChallenges.size.toFloat()).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total Solved: ${solvedIds.size} / ${allChallenges.size}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(solvedIds.size * 100) / allChallenges.size}% Mastery",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Category & Filter Picker Chips Row
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategoryFilter == null && !filterUnsolvedOnly,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedCategoryFilter = null
                            filterUnsolvedOnly = false
                        },
                        label = { Text("All (${allChallenges.size})") }
                    )
                }

                item {
                    val unsolvedCount = allChallenges.count { !solvedIds.contains(it.id) }
                    FilterChip(
                        selected = filterUnsolvedOnly,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            filterUnsolvedOnly = !filterUnsolvedOnly
                            if (filterUnsolvedOnly) {
                                val firstUnsolved = allChallenges.firstOrNull { !solvedIds.contains(it.id) }
                                if (firstUnsolved != null) {
                                    val idx = allChallenges.indexOfFirst { it.id == firstUnsolved.id }
                                    progressManager.setCurrentChallengeIndex(idx)
                                }
                            }
                        },
                        label = { Text("⭐ Unsolved ($unsolvedCount)") }
                    )
                }

                items(SvaCategory.entries) { category ->
                    val count = allChallenges.count { it.category == category }
                    FilterChip(
                        selected = selectedCategoryFilter == category && !filterUnsolvedOnly,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            filterUnsolvedOnly = false
                            if (selectedCategoryFilter == category) {
                                selectedCategoryFilter = null
                            } else {
                                selectedCategoryFilter = category
                                if (currentChallenge.category != category) {
                                    val firstInCat = allChallenges.firstOrNull { it.category == category }
                                    if (firstInCat != null) {
                                        val idx = allChallenges.indexOfFirst { it.id == firstInCat.id }
                                        progressManager.setCurrentChallengeIndex(idx)
                                        typedCode = ""
                                        checkVerdict = null
                                        showHint = false
                                        draggingToken = null
                                        dragOffset = Offset.Zero
                                        targetDropIndex = -1
                                    }
                                }
                            }
                        },
                        label = { Text("${category.icon} ${category.title} ($count)") }
                    )
                }
            }
        }

        // Challenge Prompt Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${currentChallenge.category.icon} ${currentChallenge.category.title}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (currentChallenge.difficulty) {
                                "Basic" -> Color(0xFF4CAF50).copy(alpha = 0.2f)
                                "Medium" -> Color(0xFFFF9800).copy(alpha = 0.2f)
                                else -> Color(0xFFF44336).copy(alpha = 0.2f)
                            }
                        ) {
                            Text(
                                text = currentChallenge.difficulty,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = when (currentChallenge.difficulty) {
                                    "Basic" -> Color(0xFF2E7D32)
                                    "Medium" -> Color(0xFFEF6C00)
                                    else -> Color(0xFFC62828)
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "REQUIREMENT / SCENARIO",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentChallenge.prompt,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 24.sp
                    )
                }
            }
        }

        // Mode Selector: Type Syntax vs Token Assembly (Langobard style)
        item {
            PrimaryTabRow(
                selectedTabIndex = if (inputMode == PracticeInputMode.TYPE_SYNTAX) 0 else 1,
                containerColor = Color.Transparent
            ) {
                Tab(
                    selected = inputMode == PracticeInputMode.TYPE_SYNTAX,
                    onClick = {
                        inputMode = PracticeInputMode.TYPE_SYNTAX
                        checkVerdict = null
                    },
                    text = { Text("⌨️ Type SVA Syntax") }
                )
                Tab(
                    selected = inputMode == PracticeInputMode.TOKEN_ASSEMBLY,
                    onClick = {
                        inputMode = PracticeInputMode.TOKEN_ASSEMBLY
                        checkVerdict = null
                    },
                    text = { Text("🧩 Assemble Tiles") }
                )
            }
        }

        // Interactive Construction Canvas
        if (inputMode == PracticeInputMode.TYPE_SYNTAX) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = typedCode,
                        onValueChange = {
                            typedCode = it
                            checkVerdict = null
                        },
                        placeholder = { Text("e.g. (valid && ready) [*2]") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        singleLine = false,
                        maxLines = 4,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.SemiBold
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                        )
                    )
                }
            }
        } else {
            // Token Assembly Canvas (Langobard drop & pool UI)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "YOUR SVA ASSEMBLY",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (selectedTokens.isNotEmpty()) {
                                Text(
                                    text = "Drag ⠿ to reorder • ✕ to remove",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        if (selectedTokens.isNotEmpty()) {
                            TextButton(
                                onClick = {
                                    poolTokens.addAll(selectedTokens)
                                    selectedTokens.clear()
                                    tileCoordinates.clear()
                                    checkVerdict = null
                                },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                            ) {
                                Text("Clear", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 120.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                                .onGloballyPositioned { canvasCoordinates = it }
                        ) {
                            if (selectedTokens.isEmpty()) {
                                Text(
                                    text = "Tap tokens below to construct SVA expression",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            } else {
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    selectedTokens.forEachIndexed { index, tokenPair ->
                                        key(tokenPair.first) {
                                            val isDragging = draggingToken?.first == tokenPair.first
                                            val isDropTarget = targetDropIndex == index && draggingToken != null && !isDragging

                                            Surface(
                                                shape = RoundedCornerShape(10.dp),
                                                color = when {
                                                    isDragging -> MaterialTheme.colorScheme.primaryContainer
                                                    isDropTarget -> MaterialTheme.colorScheme.secondaryContainer
                                                    else -> MaterialTheme.colorScheme.primary
                                                },
                                                border = when {
                                                    isDragging -> BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                                                    isDropTarget -> BorderStroke(2.dp, MaterialTheme.colorScheme.secondary)
                                                    else -> null
                                                },
                                                shadowElevation = if (isDragging) 8.dp else 1.dp,
                                                modifier = Modifier
                                                    .zIndex(if (isDragging) 10f else 1f)
                                                    .onGloballyPositioned { coords ->
                                                        val parent = canvasCoordinates
                                                        if (parent != null && coords.isAttached) {
                                                            val topLeft = parent.localPositionOf(coords, Offset.Zero)
                                                            tileCoordinates[tokenPair.first] = Rect(topLeft, coords.size.toSize())
                                                        }
                                                    }
                                                    .graphicsLayer {
                                                        if (isDragging) {
                                                            translationX = dragOffset.x
                                                            translationY = dragOffset.y
                                                            scaleX = 1.08f
                                                            scaleY = 1.08f
                                                        }
                                                    }
                                                    .pointerInput(tokenPair.first) {
                                                        detectDragGesturesAfterLongPress(
                                                            onDragStart = {
                                                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                                draggingToken = tokenPair
                                                                dragOffset = Offset.Zero
                                                                targetDropIndex = selectedTokens.indexOf(tokenPair)
                                                            },
                                                            onDrag = { change, dragAmount ->
                                                                change.consume()
                                                                currentDragUpdate(tokenPair, dragAmount)
                                                            },
                                                            onDragEnd = { currentDragFinish() },
                                                            onDragCancel = {
                                                                draggingToken = null
                                                                dragOffset = Offset.Zero
                                                                targetDropIndex = -1
                                                            }
                                                        )
                                                    }
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    // Draggable handle with immediate gesture detection
                                                    Box(
                                                        modifier = Modifier
                                                            .pointerInput(tokenPair.first) {
                                                                detectDragGestures(
                                                                    onDragStart = {
                                                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                                                        draggingToken = tokenPair
                                                                        dragOffset = Offset.Zero
                                                                        targetDropIndex = selectedTokens.indexOf(tokenPair)
                                                                    },
                                                                    onDrag = { change, dragAmount ->
                                                                        change.consume()
                                                                        currentDragUpdate(tokenPair, dragAmount)
                                                                    },
                                                                    onDragEnd = { currentDragFinish() },
                                                                    onDragCancel = {
                                                                        draggingToken = null
                                                                        dragOffset = Offset.Zero
                                                                        targetDropIndex = -1
                                                                    }
                                                                )
                                                            }
                                                            .padding(end = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = "⠿",
                                                            fontSize = 12.sp,
                                                            color = if (isDragging || isDropTarget) {
                                                                MaterialTheme.colorScheme.onPrimaryContainer
                                                            } else {
                                                                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f)
                                                            }
                                                        )
                                                    }

                                                    Text(
                                                        text = tokenPair.second,
                                                        fontFamily = FontFamily.Monospace,
                                                        style = MaterialTheme.typography.labelMedium,
                                                        fontWeight = FontWeight.Bold,
                                                        color = when {
                                                            isDragging -> MaterialTheme.colorScheme.onPrimaryContainer
                                                            isDropTarget -> MaterialTheme.colorScheme.onSecondaryContainer
                                                            else -> MaterialTheme.colorScheme.onPrimary
                                                        }
                                                    )

                                                    if (selectedTokens.size > 1) {
                                                        if (index > 0) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .clickable {
                                                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                                        val item = selectedTokens.removeAt(index)
                                                                        selectedTokens.add(index - 1, item)
                                                                        checkVerdict = null
                                                                    }
                                                                    .padding(horizontal = 2.dp)
                                                            ) {
                                                                Text(
                                                                    text = "◀",
                                                                    fontSize = 10.sp,
                                                                    color = if (isDragging || isDropTarget) {
                                                                        MaterialTheme.colorScheme.onPrimaryContainer
                                                                    } else {
                                                                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f)
                                                                    }
                                                                )
                                                            }
                                                        }
                                                        if (index < selectedTokens.lastIndex) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .clickable {
                                                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                                        val item = selectedTokens.removeAt(index)
                                                                        selectedTokens.add(index + 1, item)
                                                                        checkVerdict = null
                                                                    }
                                                                    .padding(horizontal = 2.dp)
                                                            ) {
                                                                Text(
                                                                    text = "▶",
                                                                    fontSize = 10.sp,
                                                                    color = if (isDragging || isDropTarget) {
                                                                        MaterialTheme.colorScheme.onPrimaryContainer
                                                                    } else {
                                                                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f)
                                                                    }
                                                                )
                                                            }
                                                        }
                                                    }

                                                    Box(
                                                        modifier = Modifier
                                                            .clickable {
                                                                if (checkVerdict != true) {
                                                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                                    selectedTokens.remove(tokenPair)
                                                                    poolTokens.add(tokenPair)
                                                                    tileCoordinates.remove(tokenPair.first)
                                                                    checkVerdict = null
                                                                }
                                                            }
                                                            .padding(start = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = "✕",
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isDragging || isDropTarget) {
                                                                MaterialTheme.colorScheme.onPrimaryContainer
                                                            } else {
                                                                MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                                                            }
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Text(
                        text = "AVAILABLE TILES",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        poolTokens.forEach { tokenPair ->
                            key(tokenPair.first) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outlineVariant),
                                    modifier = Modifier.clickable {
                                        if (checkVerdict != true) {
                                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            poolTokens.remove(tokenPair)
                                            selectedTokens.add(tokenPair)
                                            checkVerdict = null
                                        }
                                    }
                                ) {
                                    Text(
                                        text = tokenPair.second,
                                        fontFamily = FontFamily.Monospace,
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Feedback & Verdict Banner
        item {
            AnimatedVisibility(visible = checkVerdict != null) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (checkVerdict == true)
                            Color(0xFF4CAF50).copy(alpha = 0.15f)
                        else
                            Color(0xFFF44336).copy(alpha = 0.15f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (checkVerdict == true) "🎉 Valid SVA Match!" else "❌ Syntax Mismatch",
                                fontWeight = FontWeight.Bold,
                                color = if (checkVerdict == true) Color(0xFF2E7D32) else Color(0xFFC62828),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (checkVerdict == true) {
                                "Canonical Assertion: ${currentChallenge.targetSva}\n\n${currentChallenge.explanation}"
                            } else {
                                "Try again or tap 'Show SVA Hint' below for canonical short SVA syntax."
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }

        // Canonical Hint (Short assert expression)
        item {
            AnimatedVisibility(visible = showHint) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "💡 Short SVA Syntax:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        SvaCodeBlock(code = currentChallenge.targetSva)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = currentChallenge.explanation,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Action Buttons (Verify Answer, Hint, Prev, Next)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            showHint = !showHint
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (showHint) "Hide Hint" else "💡 Hint")
                    }

                    Button(
                        onClick = {
                            val userAssembly = if (inputMode == PracticeInputMode.TYPE_SYNTAX) {
                                typedCode
                            } else {
                                selectedTokens.joinToString(" ") { it.second }
                            }

                            val normalizedUser = normalizeSva(userAssembly)
                            val isMatch = currentChallenge.acceptableAnswers.any {
                                normalizeSva(it) == normalizedUser
                            }

                            checkVerdict = isMatch
                            if (isMatch) {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                progressManager.markChallengeSolved(currentChallenge.id)
                            } else {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Verify SVA")
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            val prevFilteredIndex = if (currentFilteredIndex > 0) currentFilteredIndex - 1 else filteredChallenges.lastIndex
                            val prevChal = filteredChallenges[prevFilteredIndex]
                            val prevIndex = allChallenges.indexOfFirst { it.id == prevChal.id }
                            progressManager.setCurrentChallengeIndex(prevIndex)
                            typedCode = ""
                            checkVerdict = null
                            showHint = false
                            draggingToken = null
                            dragOffset = Offset.Zero
                            targetDropIndex = -1
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("← Prev")
                    }

                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            val nextFilteredIndex = (currentFilteredIndex + 1) % filteredChallenges.size
                            val nextChal = filteredChallenges[nextFilteredIndex]
                            val nextIndex = allChallenges.indexOfFirst { it.id == nextChal.id }
                            progressManager.setCurrentChallengeIndex(nextIndex)
                            typedCode = ""
                            checkVerdict = null
                            showHint = false
                            draggingToken = null
                            dragOffset = Offset.Zero
                            targetDropIndex = -1
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text("Next →")
                    }
                }
            }
        }
    }

    // Modal: Jump to Challenge Navigator
    if (showJumpDialog) {
        AlertDialog(
            onDismissRequest = { showJumpDialog = false },
            title = {
                Text(
                    text = "🔍 Jump to Challenge",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Showing ${filteredChallenges.size} challenges in active filter:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 380.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredChallenges) { challenge ->
                            val globalIndex = allChallenges.indexOfFirst { it.id == challenge.id }
                            val isSolved = solvedIds.contains(challenge.id)
                            val isCurrent = challenge.id == currentChallenge.id

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = when {
                                    isCurrent -> MaterialTheme.colorScheme.primaryContainer
                                    isSolved -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    else -> MaterialTheme.colorScheme.surface
                                },
                                border = BorderStroke(
                                    if (isCurrent) 2.dp else 1.dp,
                                    if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        progressManager.setCurrentChallengeIndex(globalIndex)
                                        typedCode = ""
                                        checkVerdict = null
                                        showHint = false
                                        draggingToken = null
                                        dragOffset = Offset.Zero
                                        targetDropIndex = -1
                                        showJumpDialog = false
                                    }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = "#${globalIndex + 1}",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.width(36.dp)
                                        )
                                        Text(
                                            text = challenge.category.icon,
                                            fontSize = 14.sp,
                                            modifier = Modifier.padding(end = 6.dp)
                                        )
                                        Column {
                                            Text(
                                                text = challenge.prompt,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                                maxLines = 1,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = challenge.targetSva,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.outline,
                                                maxLines = 1
                                            )
                                        }
                                    }

                                    if (isSolved) {
                                        Surface(
                                            shape = CircleShape,
                                            color = Color(0xFF4CAF50).copy(alpha = 0.2f),
                                            modifier = Modifier.size(22.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "✓",
                                                    fontSize = 12.sp,
                                                    color = Color(0xFF2E7D32),
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showJumpDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Modal: Badges & Mastery Dialog
    if (showBadgesDialog) {
        AlertDialog(
            onDismissRequest = { showBadgesDialog = false },
            title = {
                Text(
                    text = "🏆 Verification Mastery",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "$unlockedBadgesCount of ${badges.size} Badges Unlocked",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Earn badges by solving assertions across categories and acing quizzes.",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 380.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(badges) { badge ->
                            val isUnlocked = badge.isUnlocked(solvedIds, quizScore, allChallenges)
                            val progress = badge.getProgress(solvedIds, quizScore, allChallenges)

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isUnlocked)
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                                else
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                                border = BorderStroke(
                                    1.dp,
                                    if (isUnlocked) Color(0xFFFFB300) else MaterialTheme.colorScheme.outlineVariant
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (isUnlocked) Color(0xFFFFE082) else MaterialTheme.colorScheme.surfaceVariant,
                                        modifier = Modifier.size(42.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = badge.icon,
                                                fontSize = 20.sp
                                            )
                                        }
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = badge.title,
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = if (isUnlocked) Color(0xFF4CAF50).copy(alpha = 0.2f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                                            ) {
                                                Text(
                                                    text = if (isUnlocked) "UNLOCKED" else "LOCKED",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 9.sp,
                                                    color = if (isUnlocked) Color(0xFF2E7D32) else MaterialTheme.colorScheme.outline,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = badge.description,
                                            style = MaterialTheme.typography.bodySmall,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            LinearProgressIndicator(
                                                progress = { (progress.toFloat() / badge.targetCount.toFloat()).coerceIn(0f, 1f) },
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(6.dp)
                                                    .clip(RoundedCornerShape(3.dp)),
                                                color = if (isUnlocked) Color(0xFFFFB300) else MaterialTheme.colorScheme.primary
                                            )
                                            Text(
                                                text = "$progress / ${badge.targetCount}",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showBadgesDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

fun normalizeSva(raw: String): String {
    return raw.replace("\\s+".toRegex(), "")
        .replace("1'b0", "0")
        .replace("1'b1", "1")
        .trim()
        .trimEnd(';')
}
