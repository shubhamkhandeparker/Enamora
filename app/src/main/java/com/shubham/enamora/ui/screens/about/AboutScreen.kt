package com.shubham.enamora.ui.screens.about

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shubham.enamora.ui.model.CharacterUiModel
import com.shubham.enamora.ui.model.ImportantPersonUiModel
import com.shubham.enamora.ui.model.SharedMemoryUiModel
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
fun AboutScreen(
    character: CharacterUiModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onViewMemoriesClick: () -> Unit = {},
    onConversationPreferencesClick: () -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(EnamoraObsidian),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ProfileHero(
                character = character,
                onBackClick = onBackClick
            )
        }

        item {
            ProfileInformationGrid(
                character = character,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item {
            ImportantPeopleSection(
                characterName = character.name,
                people = character.importantPeople
            )
        }

        item {
            SharedMemoriesSection(
                memories = character.sharedMemories
            )
        }

        item {
            ProfileActionRow(
                badgeText = "M",
                title = "View memories",
                onClick = onViewMemoriesClick,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item {
            ProfileActionRow(
                badgeText = "P",
                title = "Conversation preferences",
                onClick = onConversationPreferencesClick,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        item {
            FictionalAiDisclosure(
                characterName = character.name,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun ProfileHero(
    character: CharacterUiModel,
    onBackClick: () -> Unit
) {
    val displayedPhoto =
        character.photoResIds.firstOrNull() ?: character.portraitResId

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(310.dp)
            .clip(
                RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomEnd = 26.dp,
                    bottomStart = 26.dp
                )
            )
    ) {
        Image(
            painter = painterResource(displayedPhoto),
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
                            0f to Color.Black.copy(alpha = 0.12f),
                            0.48f to Color.Transparent,
                            1f to Color.Black.copy(alpha = 0.94f)
                        )
                    )
                )
        )

        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .size(42.dp)
                .background(
                    color = Color.Black.copy(alpha = 0.42f),
                    shape = CircleShape
                )
        ) {
            BackArrowIcon()
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    bottom = 20.dp
                ),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = "About ${character.name}",
                color = EnamoraWarmIvory,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "The person you're getting to know",
                color = EnamoraWarmIvory.copy(alpha = 0.86f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun ProfileInformationGrid(
    character: CharacterUiModel,
    modifier: Modifier = Modifier
) {
    val worldText = listOf(
        character.occupation,
        character.dailyLife
    )
        .filter { it.isNotBlank() }
        .joinToString(separator = " ")

    val interestsText =
        character.interests.joinToString(separator = ", ")

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ProfileInformationCard(
                title = "Personality",
                value = character.personality.ifBlank {
                    "More details will appear as you get to know ${character.name}."
                },
                modifier = Modifier.weight(1f)
            )

            ProfileInformationCard(
                title = "${character.name}'s world",
                value = worldText.ifBlank {
                    "Daily-life details will be added soon."
                },
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ProfileInformationCard(
                title = "Interests",
                value = interestsText.ifBlank {
                    "Interests will be added soon."
                },
                modifier = Modifier.weight(1f)
            )

            ProfileInformationCard(
                title = "Currently into",
                value = character.currentActivity.ifBlank {
                    "Nothing shared yet."
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ProfileInformationCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(154.dp),
        shape = RoundedCornerShape(16.dp),
        color = EnamoraSurface,
        border = BorderStroke(
            width = 1.dp,
            color = EnamoraOutline.copy(alpha = 0.75f)
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(
                            color = EnamoraRose,
                            shape = CircleShape
                        )
                )

                Text(
                    text = title,
                    color = EnamoraRoseSoft,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = value,
                color = EnamoraWarmIvory,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 6,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ImportantPeopleSection(
    characterName: String,
    people: List<ImportantPersonUiModel>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ProfileSectionTitle(
            title = "People in ${characterName}'s life",
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        if (people.isEmpty()) {
            EmptyProfileSection(
                text = "Important people will appear here.",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = people,
                    key = { person -> person.id }
                ) { person ->
                    ImportantPersonCard(person = person)
                }
            }
        }
    }
}

@Composable
private fun ImportantPersonCard(
    person: ImportantPersonUiModel
) {
    Surface(
        modifier = Modifier
            .width(112.dp)
            .height(126.dp),
        shape = RoundedCornerShape(16.dp),
        color = EnamoraSurface,
        border = BorderStroke(
            width = 1.dp,
            color = EnamoraOutline.copy(alpha = 0.72f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            PersonAvatar(person = person)

            Text(
                text = person.name,
                color = EnamoraWarmIvory,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = person.relationship,
                color = EnamoraTextSecondary,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun PersonAvatar(
    person: ImportantPersonUiModel
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(EnamoraWineDeep),
        contentAlignment = Alignment.Center
    ) {
        val photoResId = person.photoResId

        if (photoResId != null) {
            Image(
                painter = painterResource(photoResId),
                contentDescription = person.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = person.name
                    .firstOrNull()
                    ?.uppercaseChar()
                    ?.toString()
                    ?: "?",
                color = EnamoraRoseSoft,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun SharedMemoriesSection(
    memories: List<SharedMemoryUiModel>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ProfileSectionTitle(
            title = "Your shared memories",
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        if (memories.isEmpty()) {
            EmptyProfileSection(
                text = "Your shared memories will appear here.",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = memories,
                    key = { memory -> memory.id }
                ) { memory ->
                    SharedMemoryCard(memory = memory)
                }
            }
        }
    }
}

@Composable
private fun SharedMemoryCard(
    memory: SharedMemoryUiModel
) {
    Surface(
        modifier = Modifier
            .width(196.dp)
            .height(118.dp),
        shape = RoundedCornerShape(16.dp),
        color = EnamoraSurface,
        border = BorderStroke(
            width = 1.dp,
            color = EnamoraOutline.copy(alpha = 0.72f)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            val imageResId = memory.imageResId

            if (imageResId != null) {
                Image(
                    painter = painterResource(imageResId),
                    contentDescription = memory.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                MemoryPlaceholderArtwork()
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.88f)
                            )
                        )
                    )
            )

            Text(
                text = memory.title,
                color = EnamoraWarmIvory,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun MemoryPlaceholderArtwork() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        EnamoraWineDeep,
                        EnamoraDustyRose.copy(alpha = 0.72f),
                        EnamoraSurface
                    )
                )
            )
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawCircle(
                color = EnamoraRoseSoft.copy(alpha = 0.50f),
                radius = size.minDimension * 0.14f,
                center = Offset(
                    x = size.width * 0.76f,
                    y = size.height * 0.30f
                )
            )

            drawLine(
                color = EnamoraWarmIvory.copy(alpha = 0.28f),
                start = Offset(
                    x = size.width * 0.12f,
                    y = size.height * 0.68f
                ),
                end = Offset(
                    x = size.width * 0.88f,
                    y = size.height * 0.68f
                ),
                strokeWidth = 1.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun ProfileActionRow(
    badgeText: String,
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = EnamoraSurface,
        border = BorderStroke(
            width = 1.dp,
            color = EnamoraOutline.copy(alpha = 0.72f)
        )
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 14.dp,
                vertical = 13.dp
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(34.dp),
                shape = CircleShape,
                color = Color.Transparent,
                border = BorderStroke(
                    width = 1.dp,
                    color = EnamoraDustyRose
                )
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badgeText,
                        color = EnamoraRoseSoft,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = title,
                color = EnamoraWarmIvory,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )

            ChevronIcon()
        }
    }
}

@Composable
private fun ProfileSectionTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        color = EnamoraWarmIvory,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
    )
}

@Composable
private fun EmptyProfileSection(
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = EnamoraSurface,
        border = BorderStroke(
            width = 1.dp,
            color = EnamoraOutline.copy(alpha = 0.70f)
        )
    ) {
        Text(
            text = text,
            color = EnamoraTextSecondary,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun FictionalAiDisclosure(
    characterName: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(22.dp),
            shape = CircleShape,
            color = Color.Transparent,
            border = BorderStroke(
                width = 1.dp,
                color = EnamoraOutline
            )
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "AI",
                    color = EnamoraTextSecondary,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        Text(
            text = "$characterName is a fictional AI character",
            color = EnamoraTextSecondary,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
private fun BackArrowIcon() {
    Canvas(
        modifier = Modifier.size(22.dp)
    ) {
        val strokeWidth = 1.8.dp.toPx()

        drawLine(
            color = EnamoraWarmIvory,
            start = Offset(
                x = size.width * 0.72f,
                y = size.height * 0.18f
            ),
            end = Offset(
                x = size.width * 0.30f,
                y = size.height * 0.50f
            ),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = EnamoraWarmIvory,
            start = Offset(
                x = size.width * 0.30f,
                y = size.height * 0.50f
            ),
            end = Offset(
                x = size.width * 0.72f,
                y = size.height * 0.82f
            ),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun ChevronIcon() {
    Canvas(
        modifier = Modifier.size(18.dp)
    ) {
        val strokeWidth = 1.5.dp.toPx()

        drawLine(
            color = EnamoraTextSecondary,
            start = Offset(
                x = size.width * 0.35f,
                y = size.height * 0.22f
            ),
            end = Offset(
                x = size.width * 0.66f,
                y = size.height * 0.50f
            ),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )

        drawLine(
            color = EnamoraTextSecondary,
            start = Offset(
                x = size.width * 0.66f,
                y = size.height * 0.50f
            ),
            end = Offset(
                x = size.width * 0.35f,
                y = size.height * 0.78f
            ),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}