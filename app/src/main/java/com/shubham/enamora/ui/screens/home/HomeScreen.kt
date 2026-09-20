package com.shubham.enamora.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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

@Composable
fun HomeScreen(
    userName: String,
    character: CharacterUiModel,
    voiceNoteDuration: String,
    sharedMemoryTitle: String,
    onContinueConversation: () -> Unit,
    modifier: Modifier = Modifier,
    onNotificationsClick: () -> Unit = {},
    onVoiceNoteClick: () -> Unit = {},
    onMemoryClick: () -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(EnamoraObsidian),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 16.dp,
            top = 14.dp,
            end = 16.dp,
            bottom = 20.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            HomeHeader(
                userName = userName,
                onNotificationsClick = onNotificationsClick
            )
        }

        item {
            CharacterHeroCard(character = character)
        }

        item {
            ContinueConversationButton(
                onClick = onContinueConversation
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                VoiceNoteCard(
                    characterName = character.name,
                    duration = voiceNoteDuration,
                    onClick = onVoiceNoteClick,
                    modifier = Modifier.weight(1f)
                )

                SharedMemoryCard(
                    title = sharedMemoryTitle,
                    onClick = onMemoryClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun HomeHeader(
    userName: String,
    onNotificationsClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Enamora",
                color = EnamoraRoseSoft,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = onNotificationsClick,
                modifier = Modifier.size(40.dp)
            ) {
                NotificationBell()
            }
        }

        Text(
            text = "Good evening, $userName",
            color = EnamoraRoseSoft,
            style = MaterialTheme.typography.titleLarge
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Text(
                text = "⌁",
                color = EnamoraDustyRose,
                fontSize = 20.sp
            )

            Text(
                text = "Good to have you back",
                color = EnamoraTextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun CharacterHeroCard(
    character: CharacterUiModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = EnamoraSurface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = EnamoraOutline.copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(character.portraitResId),
                contentDescription = "${character.name}, fictional AI character",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0f to Color.Transparent,
                                0.48f to Color.Transparent,
                                1f to Color.Black.copy(alpha = 0.92f)
                            )
                        )
                    )
            )

            AvailabilityPill(
                text = character.availabilityText,
                isAvailable = character.isAvailable,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
            )

            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = character.name,
                        color = EnamoraWarmIvory,
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Text(
                        text = character.recentMessagePreview,
                        color = EnamoraWarmIvory,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    modifier = Modifier.size(38.dp),
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.48f),
                    border = BorderStroke(
                        width = 1.dp,
                        color = EnamoraWarmIvory.copy(alpha = 0.18f)
                    )
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "›",
                            color = EnamoraWarmIvory,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Light
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AvailabilityPill(
    text: String,
    isAvailable: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = Color.Black.copy(alpha = 0.60f),
        border = BorderStroke(
            width = 1.dp,
            color = Color.White.copy(alpha = 0.08f)
        )
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 7.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .background(
                        color = if (isAvailable) {
                            Color(0xFF72C786)
                        } else {
                            EnamoraTextSecondary
                        },
                        shape = CircleShape
                    )
            )

            Text(
                text = text,
                color = EnamoraWarmIvory,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ContinueConversationButton(
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(13.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = EnamoraRose,
            contentColor = EnamoraObsidian
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 0.dp
        )
    ) {
        ConversationBubbleIcon(
            color = EnamoraObsidian
        )

        Spacer(modifier = Modifier.size(9.dp))

        Text(
            text = "Continue conversation",
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
private fun VoiceNoteCard(
    characterName: String,
    duration: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(158.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = EnamoraSurface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = EnamoraOutline.copy(alpha = 0.75f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(13.dp)
        ) {
            Text(
                text = "A voice note\nfrom $characterName",
                color = EnamoraWarmIvory,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                PlayButton()

                Spacer(modifier = Modifier.size(9.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    VoiceWaveform(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(34.dp)
                    )

                    Text(
                        text = duration,
                        color = EnamoraTextSecondary,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
private fun SharedMemoryCard(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(158.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = EnamoraSurface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = EnamoraOutline.copy(alpha = 0.75f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(13.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Text(
                text = "A memory\nfrom this week",
                color = EnamoraWarmIvory,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 18.sp
            )

            MemoryArtwork(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            )

            Text(
                text = title,
                color = EnamoraWarmIvory,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun NotificationBell() {
    Canvas(
        modifier = Modifier.size(24.dp)
    ) {
        val outline = Stroke(
            width = 1.7.dp.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )

        val bell = Path().apply {
            moveTo(size.width * 0.25f, size.height * 0.72f)
            quadraticBezierTo(
                size.width * 0.32f,
                size.height * 0.62f,
                size.width * 0.32f,
                size.height * 0.47f
            )
            quadraticBezierTo(
                size.width * 0.32f,
                size.height * 0.25f,
                size.width * 0.50f,
                size.height * 0.20f
            )
            quadraticBezierTo(
                size.width * 0.68f,
                size.height * 0.25f,
                size.width * 0.68f,
                size.height * 0.47f
            )
            quadraticBezierTo(
                size.width * 0.68f,
                size.height * 0.62f,
                size.width * 0.75f,
                size.height * 0.72f
            )
            close()
        }

        drawPath(
            path = bell,
            color = EnamoraWarmIvory,
            style = outline
        )

        drawArc(
            color = EnamoraWarmIvory,
            startAngle = 10f,
            sweepAngle = 160f,
            useCenter = false,
            topLeft = Offset(
                size.width * 0.42f,
                size.height * 0.70f
            ),
            size = Size(
                size.width * 0.16f,
                size.height * 0.16f
            ),
            style = outline
        )
    }
}

@Composable
private fun ConversationBubbleIcon(
    color: Color
) {
    Canvas(
        modifier = Modifier.size(18.dp)
    ) {
        val outline = Stroke(
            width = 1.5.dp.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )

        drawRoundRect(
            color = color,
            topLeft = Offset(
                size.width * 0.08f,
                size.height * 0.08f
            ),
            size = Size(
                size.width * 0.84f,
                size.height * 0.68f
            ),
            cornerRadius = CornerRadius(
                size.width * 0.22f,
                size.width * 0.22f
            ),
            style = outline
        )

        val tail = Path().apply {
            moveTo(size.width * 0.32f, size.height * 0.75f)
            lineTo(size.width * 0.22f, size.height * 0.90f)
            lineTo(size.width * 0.50f, size.height * 0.76f)
        }

        drawPath(
            path = tail,
            color = color,
            style = outline
        )
    }
}

@Composable
private fun PlayButton() {
    Canvas(
        modifier = Modifier.size(42.dp)
    ) {
        drawCircle(
            color = EnamoraDustyRose,
            style = Stroke(
                width = 1.5.dp.toPx()
            )
        )

        val triangle = Path().apply {
            moveTo(size.width * 0.42f, size.height * 0.34f)
            lineTo(size.width * 0.42f, size.height * 0.66f)
            lineTo(size.width * 0.66f, size.height * 0.50f)
            close()
        }

        drawPath(
            path = triangle,
            color = EnamoraDustyRose
        )
    }
}

@Composable
private fun VoiceWaveform(
    modifier: Modifier = Modifier
) {
    val amplitudes = listOf(
        0.24f, 0.42f, 0.72f, 0.38f, 0.84f,
        0.55f, 0.92f, 0.48f, 0.70f, 0.34f,
        0.80f, 0.52f, 0.94f, 0.44f, 0.64f,
        0.30f, 0.56f, 0.40f
    )

    Canvas(modifier = modifier) {
        val spacing = size.width / amplitudes.size
        val strokeWidth = 1.35.dp.toPx()

        amplitudes.forEachIndexed { index, amplitude ->
            val x = spacing * index + spacing / 2f
            val barHeight = size.height * amplitude

            drawLine(
                color = EnamoraTextSecondary,
                start = Offset(
                    x = x,
                    y = (size.height - barHeight) / 2f
                ),
                end = Offset(
                    x = x,
                    y = (size.height + barHeight) / 2f
                ),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun MemoryArtwork(
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier.clip(
            RoundedCornerShape(9.dp)
        )
    ) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    EnamoraWineDeep,
                    EnamoraDustyRose,
                    Color(0xFF473A4B)
                )
            )
        )

        drawCircle(
            color = EnamoraRoseSoft.copy(alpha = 0.85f),
            radius = size.minDimension * 0.12f,
            center = Offset(
                x = size.width * 0.70f,
                y = size.height * 0.37f
            )
        )

        drawLine(
            color = EnamoraWarmIvory.copy(alpha = 0.40f),
            start = Offset(
                x = 0f,
                y = size.height * 0.68f
            ),
            end = Offset(
                x = size.width,
                y = size.height * 0.68f
            ),
            strokeWidth = 1.dp.toPx()
        )

        drawLine(
            color = EnamoraWarmIvory.copy(alpha = 0.18f),
            start = Offset(
                x = size.width * 0.10f,
                y = size.height * 0.80f
            ),
            end = Offset(
                x = size.width * 0.88f,
                y = size.height * 0.80f
            ),
            strokeWidth = 1.dp.toPx()
        )
    }
}