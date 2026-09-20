package com.shubham.enamora.ui.screens.explore

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shubham.enamora.ui.model.CharacterUiModel
import com.shubham.enamora.ui.theme.EnamoraDustyRose
import com.shubham.enamora.ui.theme.EnamoraObsidian
import com.shubham.enamora.ui.theme.EnamoraOutline
import com.shubham.enamora.ui.theme.EnamoraRose
import com.shubham.enamora.ui.theme.EnamoraRoseSoft
import com.shubham.enamora.ui.theme.EnamoraSurface
import com.shubham.enamora.ui.theme.EnamoraTextSecondary
import com.shubham.enamora.ui.theme.EnamoraWarmIvory
import com.shubham.enamora.ui.theme.EnamoraWineDeep
import kotlinx.coroutines.launch
import kotlin.math.abs

@Composable
fun ExploreScreen(
    characters: List<CharacterUiModel>,
    onViewProfile: (CharacterUiModel) -> Unit,
    onPass: (CharacterUiModel) -> Unit,
    onConnect: (CharacterUiModel) -> Unit,
    modifier: Modifier = Modifier,
    onFiltersClick: () -> Unit = {}
) {
    val characterListKey =
        characters.joinToString(
            separator = "|"
        ) { character ->
            character.id
        }

    var currentCharacterIndex by rememberSaveable(
        characterListKey
    ) {
        mutableStateOf(0)
    }

    val character =
        characters.getOrNull(
            currentCharacterIndex
        )

    if (character == null) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(EnamoraObsidian),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = 14.dp,
                end = 16.dp,
                bottom = 20.dp
            ),
            verticalArrangement =
                Arrangement.spacedBy(11.dp)
        ) {
            item {
                ExploreHeader(
                    onFiltersClick =
                        onFiltersClick
                )
            }

            item {
                ExploreEmptyState(
                    canReviewAgain =
                        characters.isNotEmpty(),
                    onReviewAgain = {
                        currentCharacterIndex = 0
                    }
                )
            }

            item {
                FictionalCharacterDisclosure()
            }
        }

        return
    }

    val coroutineScope =
        rememberCoroutineScope()

    val density =
        LocalDensity.current

    val swipeThreshold =
        with(density) {
            90.dp.toPx()
        }

    val exitDistance =
        with(density) {
            520.dp.toPx()
        }

    var cardOffsetX by remember(
        character.id
    ) {
        mutableFloatStateOf(0f)
    }

    var isAnimating by remember(
        character.id
    ) {
        mutableStateOf(false)
    }

    fun returnCardToCentre() {
        if (isAnimating) {
            return
        }

        isAnimating = true

        coroutineScope.launch {
            animate(
                initialValue = cardOffsetX,
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = 190
                )
            ) { value, _ ->
                cardOffsetX = value
            }

            cardOffsetX = 0f
            isAnimating = false
        }
    }

    fun completeSwipe(
        direction: Float,
        action: (CharacterUiModel) -> Unit
    ) {
        if (isAnimating) {
            return
        }

        val swipedCharacter =
            character

        isAnimating = true

        coroutineScope.launch {
            animate(
                initialValue = cardOffsetX,
                targetValue =
                    exitDistance * direction,
                animationSpec = tween(
                    durationMillis = 230
                )
            ) { value, _ ->
                cardOffsetX = value
            }

            cardOffsetX = 0f

            currentCharacterIndex =
                (currentCharacterIndex + 1)
                    .coerceAtMost(
                        characters.size
                    )

            isAnimating = false

            action(swipedCharacter)
        }
    }

    val horizontalDragModifier =
        if (isAnimating) {
            Modifier
        } else {
            Modifier.pointerInput(
                character.id
            ) {
                detectHorizontalDragGestures(
                    onDragCancel = {
                        returnCardToCentre()
                    },
                    onDragEnd = {
                        when {
                            cardOffsetX >=
                                    swipeThreshold -> {
                                completeSwipe(
                                    direction = 1f,
                                    action = onConnect
                                )
                            }

                            cardOffsetX <=
                                    -swipeThreshold -> {
                                completeSwipe(
                                    direction = -1f,
                                    action = onPass
                                )
                            }

                            else -> {
                                returnCardToCentre()
                            }
                        }
                    },
                    onHorizontalDrag = {
                            change,
                            dragAmount ->

                        change.consume()

                        cardOffsetX +=
                            dragAmount
                    }
                )
            }
        }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(EnamoraObsidian),
        contentPadding = PaddingValues(
            start = 16.dp,
            top = 14.dp,
            end = 16.dp,
            bottom = 20.dp
        ),
        verticalArrangement =
            Arrangement.spacedBy(11.dp)
    ) {
        item {
            ExploreHeader(
                onFiltersClick =
                    onFiltersClick
            )
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(430.dp)
                    .graphicsLayer {
                        translationX =
                            cardOffsetX

                        rotationZ =
                            (
                                    cardOffsetX /
                                            exitDistance
                                    ) * 11f
                    }
                    .then(
                        horizontalDragModifier
                    )
            ) {
                ExploreCharacterCard(
                    character = character,
                    modifier = Modifier.fillMaxSize()
                )

                val overlayAlignment =
                    if (cardOffsetX >= 0f) {
                        Alignment.TopStart
                    } else {
                        Alignment.TopEnd
                    }

                SwipeDecisionOverlay(
                    cardOffsetX =
                        cardOffsetX,
                    swipeThreshold =
                        swipeThreshold,
                    modifier = Modifier
                        .align(
                            overlayAlignment
                        )
                        .padding(
                            horizontal = 20.dp,
                            vertical = 55.dp
                        )
                )
            }
        }

        item {
            ExploreActions(
                character = character,
                onPass = {
                    completeSwipe(
                        direction = -1f,
                        action = onPass
                    )
                },
                onViewProfile =
                    onViewProfile,
                onConnect = {
                    completeSwipe(
                        direction = 1f,
                        action = onConnect
                    )
                }
            )
        }

        item {
            SwipeInstructions()
        }

        item {
            FictionalCharacterDisclosure()
        }
    }
}

@Composable
private fun ExploreHeader(
    onFiltersClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Explore",
                color = EnamoraWarmIvory,
                style =
                    MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Meet unique AI characters",
                color = EnamoraTextSecondary,
                style =
                    MaterialTheme.typography.bodySmall
            )
        }

        IconButton(
            onClick = onFiltersClick,
            modifier = Modifier.size(42.dp)
        ) {
            FilterIcon()
        }
    }
}

@Composable
private fun ExploreCharacterCard(
    character: CharacterUiModel,
    modifier: Modifier = Modifier
) {
    val displayedPhoto =
        character.photoResIds.firstOrNull()
            ?: character.portraitResId

    val prompt =
        character.prompts.firstOrNull()

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = EnamoraSurface
        ),
        border = BorderStroke(
            width = 1.dp,
            color =
                EnamoraOutline.copy(
                    alpha = 0.85f
                )
        ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(
                    displayedPhoto
                ),
                contentDescription =
                    "${character.name}, fictional AI character",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0f to
                                        Color.Black.copy(
                                            alpha = 0.06f
                                        ),
                                0.42f to
                                        Color.Transparent,
                                1f to
                                        Color.Black.copy(
                                            alpha = 0.95f
                                        )
                            )
                        )
                    )
            )

            PhotoIndicators(
                photoCount =
                    character.photoResIds.size,
                modifier = Modifier
                    .align(
                        Alignment.TopCenter
                    )
                    .padding(top = 12.dp)
            )

            Column(
                modifier = Modifier
                    .align(
                        Alignment.BottomStart
                    )
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text =
                        "${character.name}, ${character.age}",
                    color = EnamoraWarmIvory,
                    style =
                        MaterialTheme.typography.headlineMedium
                )

                Text(
                    text = character.tagline,
                    color = EnamoraWarmIvory,
                    style =
                        MaterialTheme.typography.bodyMedium
                )

                if (prompt != null) {
                    CharacterPromptCard(
                        question =
                            prompt.question,
                        answer =
                            prompt.answer
                    )
                }

                CharacterInterests(
                    interests =
                        character.interests
                )
            }
        }
    }
}

@Composable
private fun SwipeDecisionOverlay(
    cardOffsetX: Float,
    swipeThreshold: Float,
    modifier: Modifier = Modifier
) {
    if (cardOffsetX == 0f) {
        return
    }

    val isConnecting =
        cardOffsetX > 0f

    val progress =
        (
                abs(cardOffsetX) /
                        swipeThreshold
                ).coerceIn(
                minimumValue = 0f,
                maximumValue = 1f
            )

    val borderColor =
        if (isConnecting) {
            EnamoraRose
        } else {
            EnamoraWarmIvory
        }

    Surface(
        modifier = modifier
            .graphicsLayer {
                alpha = progress

                rotationZ =
                    if (isConnecting) {
                        -7f
                    } else {
                        7f
                    }
            },
        shape = RoundedCornerShape(12.dp),
        color =
            EnamoraObsidian.copy(
                alpha = 0.78f
            ),
        border = BorderStroke(
            width = 2.dp,
            color = borderColor
        )
    ) {
        Text(
            text =
                if (isConnecting) {
                    "CONNECT"
                } else {
                    "PASS"
                },
            color = borderColor,
            style =
                MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 8.dp
            )
        )
    }
}

@Composable
private fun PhotoIndicators(
    photoCount: Int,
    modifier: Modifier = Modifier
) {
    val safePhotoCount =
        photoCount.coerceAtLeast(1)

    Row(
        modifier = modifier,
        horizontalArrangement =
            Arrangement.spacedBy(7.dp),
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        repeat(safePhotoCount) { index ->
            Box(
                modifier = Modifier
                    .width(
                        if (index == 0) {
                            30.dp
                        } else {
                            16.dp
                        }
                    )
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(
                        if (index == 0) {
                            EnamoraRose
                        } else {
                            EnamoraWarmIvory.copy(
                                alpha = 0.70f
                            )
                        }
                    )
            )
        }
    }
}

@Composable
private fun CharacterPromptCard(
    question: String,
    answer: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(13.dp),
        color =
            EnamoraSurface.copy(
                alpha = 0.90f
            ),
        border = BorderStroke(
            width = 1.dp,
            color =
                EnamoraWarmIvory.copy(
                    alpha = 0.10f
                )
        )
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 13.dp,
                vertical = 10.dp
            ),
            verticalArrangement =
                Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = question,
                color = EnamoraTextSecondary,
                style =
                    MaterialTheme.typography.bodySmall
            )

            Text(
                text = answer,
                color = EnamoraWarmIvory,
                style =
                    MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun CharacterInterests(
    interests: List<String>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.spacedBy(7.dp)
    ) {
        interests.take(3).forEach { interest ->
            Surface(
                shape = CircleShape,
                color =
                    EnamoraSurface.copy(
                        alpha = 0.90f
                    ),
                border = BorderStroke(
                    width = 1.dp,
                    color =
                        EnamoraWarmIvory.copy(
                            alpha = 0.14f
                        )
                )
            ) {
                Text(
                    text = interest,
                    color = EnamoraWarmIvory,
                    style =
                        MaterialTheme.typography.labelSmall,
                    modifier =
                        Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 7.dp
                        )
                )
            }
        }
    }
}

@Composable
private fun ExploreActions(
    character: CharacterUiModel,
    onPass: (CharacterUiModel) -> Unit,
    onViewProfile: (CharacterUiModel) -> Unit,
    onConnect: (CharacterUiModel) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.spacedBy(12.dp),
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        CircleActionButton(
            onClick = {
                onPass(character)
            },
            backgroundColor =
                Color.Transparent,
            borderColor =
                EnamoraOutline
        ) {
            PassIcon()
        }

        OutlinedButton(
            onClick = {
                onViewProfile(character)
            },
            modifier = Modifier
                .weight(1f)
                .height(54.dp),
            shape = CircleShape,
            border = BorderStroke(
                width = 1.dp,
                color = EnamoraDustyRose
            ),
            colors =
                ButtonDefaults.outlinedButtonColors(
                    contentColor =
                        EnamoraWarmIvory
                )
        ) {
            Text(
                text = "View profile",
                style =
                    MaterialTheme.typography.labelLarge
            )
        }

        CircleActionButton(
            onClick = {
                onConnect(character)
            },
            backgroundColor =
                EnamoraWineDeep,
            borderColor =
                EnamoraDustyRose.copy(
                    alpha = 0.75f
                )
        ) {
            ConnectHeartIcon()
        }
    }
}

@Composable
private fun CircleActionButton(
    onClick: () -> Unit,
    backgroundColor: Color,
    borderColor: Color,
    content: @Composable () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.size(54.dp),
        shape = CircleShape,
        color = backgroundColor,
        border = BorderStroke(
            width = 1.dp,
            color = borderColor
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment =
                Alignment.Center
        ) {
            content()
        }
    }
}

@Composable
private fun SwipeInstructions() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.SpaceBetween,
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Text(
            text = "←  Swipe left to pass",
            color = EnamoraTextSecondary,
            style =
                MaterialTheme.typography.labelSmall
        )

        Text(
            text =
                "Swipe right to connect  →",
            color = EnamoraTextSecondary,
            style =
                MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun FictionalCharacterDisclosure() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = EnamoraSurface,
        border = BorderStroke(
            width = 1.dp,
            color =
                EnamoraOutline.copy(
                    alpha = 0.70f
                )
        )
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 13.dp,
                vertical = 11.dp
            ),
            verticalAlignment =
                Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.spacedBy(11.dp)
        ) {
            Surface(
                modifier = Modifier.size(30.dp),
                shape = CircleShape,
                color = Color.Transparent,
                border = BorderStroke(
                    width = 1.dp,
                    color =
                        EnamoraDustyRose
                )
            ) {
                Box(
                    contentAlignment =
                        Alignment.Center
                ) {
                    Text(
                        text = "AI",
                        color =
                            EnamoraRoseSoft,
                        fontSize = 9.sp,
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }

            Text(
                text =
                    "All characters here are fictional AI characters created for meaningful conversations.",
                color = EnamoraTextSecondary,
                style =
                    MaterialTheme.typography.bodySmall,
                modifier =
                    Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ExploreEmptyState(
    canReviewAgain: Boolean,
    onReviewAgain: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(430.dp)
            .clip(
                RoundedCornerShape(22.dp)
            )
            .background(EnamoraSurface)
            .padding(28.dp),
        horizontalAlignment =
            Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.Center
    ) {
        Text(
            text = "You’ve met everyone for now",
            color = EnamoraWarmIvory,
            style =
                MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Text(
            text =
                "More fictional characters will appear here as they become available.",
            color = EnamoraTextSecondary,
            style =
                MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        if (canReviewAgain) {
            Spacer(
                modifier = Modifier.height(24.dp)
            )

            OutlinedButton(
                onClick = onReviewAgain,
                shape = CircleShape,
                border = BorderStroke(
                    width = 1.dp,
                    color =
                        EnamoraDustyRose
                ),
                colors =
                    ButtonDefaults.outlinedButtonColors(
                        contentColor =
                            EnamoraWarmIvory
                    )
            ) {
                Text(
                    text =
                        "Review characters again"
                )
            }
        }
    }
}

@Composable
private fun FilterIcon() {
    Canvas(
        modifier = Modifier.size(24.dp)
    ) {
        val lineColor =
            EnamoraWarmIvory

        val lineWidth =
            1.5.dp.toPx()

        drawLine(
            color = lineColor,
            start = Offset(
                size.width * 0.12f,
                size.height * 0.25f
            ),
            end = Offset(
                size.width * 0.88f,
                size.height * 0.25f
            ),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round
        )

        drawCircle(
            color = EnamoraObsidian,
            radius = 3.dp.toPx(),
            center = Offset(
                size.width * 0.38f,
                size.height * 0.25f
            ),
            style = Stroke(
                width = lineWidth
            )
        )

        drawLine(
            color = lineColor,
            start = Offset(
                size.width * 0.12f,
                size.height * 0.50f
            ),
            end = Offset(
                size.width * 0.88f,
                size.height * 0.50f
            ),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round
        )

        drawCircle(
            color = EnamoraObsidian,
            radius = 3.dp.toPx(),
            center = Offset(
                size.width * 0.68f,
                size.height * 0.50f
            ),
            style = Stroke(
                width = lineWidth
            )
        )

        drawLine(
            color = lineColor,
            start = Offset(
                size.width * 0.12f,
                size.height * 0.75f
            ),
            end = Offset(
                size.width * 0.88f,
                size.height * 0.75f
            ),
            strokeWidth = lineWidth,
            cap = StrokeCap.Round
        )

        drawCircle(
            color = EnamoraObsidian,
            radius = 3.dp.toPx(),
            center = Offset(
                size.width * 0.46f,
                size.height * 0.75f
            ),
            style = Stroke(
                width = lineWidth
            )
        )
    }
}

@Composable
private fun PassIcon() {
    Canvas(
        modifier = Modifier.size(22.dp)
    ) {
        val strokeWidth =
            1.8.dp.toPx()

        drawLine(
            color = EnamoraWarmIvory,
            start = Offset(
                size.width * 0.22f,
                size.height * 0.22f
            ),
            end = Offset(
                size.width * 0.78f,
                size.height * 0.78f
            ),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = EnamoraWarmIvory,
            start = Offset(
                size.width * 0.78f,
                size.height * 0.22f
            ),
            end = Offset(
                size.width * 0.22f,
                size.height * 0.78f
            ),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun ConnectHeartIcon() {
    Canvas(
        modifier = Modifier.size(23.dp)
    ) {
        val heart = Path().apply {
            moveTo(
                size.width * 0.50f,
                size.height * 0.84f
            )

            cubicTo(
                size.width * 0.16f,
                size.height * 0.62f,
                size.width * 0.08f,
                size.height * 0.36f,
                size.width * 0.25f,
                size.height * 0.22f
            )

            cubicTo(
                size.width * 0.38f,
                size.height * 0.11f,
                size.width * 0.48f,
                size.height * 0.20f,
                size.width * 0.50f,
                size.height * 0.29f
            )

            cubicTo(
                size.width * 0.52f,
                size.height * 0.20f,
                size.width * 0.62f,
                size.height * 0.11f,
                size.width * 0.75f,
                size.height * 0.22f
            )

            cubicTo(
                size.width * 0.92f,
                size.height * 0.36f,
                size.width * 0.84f,
                size.height * 0.62f,
                size.width * 0.50f,
                size.height * 0.84f
            )

            close()
        }

        drawPath(
            path = heart,
            color = EnamoraWarmIvory,
            style = Stroke(
                width = 1.7.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}