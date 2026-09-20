package com.shubham.enamora.ui.screens.chat

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shubham.enamora.ui.model.ChatThreadUiModel
import com.shubham.enamora.ui.theme.EnamoraDustyRose
import com.shubham.enamora.ui.theme.EnamoraObsidian
import com.shubham.enamora.ui.theme.EnamoraOutline
import com.shubham.enamora.ui.theme.EnamoraRose
import com.shubham.enamora.ui.theme.EnamoraSurface
import com.shubham.enamora.ui.theme.EnamoraTextSecondary
import com.shubham.enamora.ui.theme.EnamoraWarmIvory

@Composable
fun ChatListScreen(
    chatThreads: List<ChatThreadUiModel>,
    onChatClick: (ChatThreadUiModel) -> Unit,
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EnamoraObsidian)
    ) {
        ChatListHeader(
            onSearchClick = onSearchClick
        )

        if (chatThreads.isEmpty()) {
            EmptyChatList(
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 10.dp,
                    bottom = 22.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = chatThreads,
                    key = { thread ->
                        thread.character.id
                    }
                ) { thread ->
                    ChatThreadCard(
                        thread = thread,
                        onClick = {
                            onChatClick(thread)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatListHeader(
    onSearchClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 20.dp,
                end = 10.dp,
                top = 20.dp,
                bottom = 14.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Chats",
                color = EnamoraWarmIvory,
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(
                modifier = Modifier.height(2.dp)
            )

            Text(
                text = "Your conversations",
                color = EnamoraTextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        IconButton(
            onClick = onSearchClick,
            modifier = Modifier
                .size(50.dp)
                .semantics {
                    contentDescription = "Search conversations"
                }
        ) {
            SearchGlyph()
        }
    }
}

@Composable
private fun ChatThreadCard(
    thread: ChatThreadUiModel,
    onClick: () -> Unit
) {
    val character = thread.character
    val hasUnreadMessages = thread.unreadCount > 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(EnamoraSurface)
            .border(
                width = 1.dp,
                color = EnamoraOutline.copy(alpha = 0.65f),
                shape = RoundedCornerShape(22.dp)
            )
            .clickable(onClick = onClick)
            .padding(
                horizontal = 14.dp,
                vertical = 14.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CharacterAvatar(
            thread = thread
        )

        Spacer(
            modifier = Modifier.width(13.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = character.name,
                color = EnamoraWarmIvory,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            Text(
                text = thread.latestMessage
                    ?: "Start a conversation",
                color =
                    if (hasUnreadMessages) {
                        EnamoraWarmIvory
                    } else {
                        EnamoraTextSecondary
                    },
                style = MaterialTheme.typography.bodyMedium,
                fontWeight =
                    if (hasUnreadMessages) {
                        FontWeight.Medium
                    } else {
                        FontWeight.Normal
                    },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(
            modifier = Modifier.width(10.dp)
        )

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Center
        ) {
            if (thread.latestMessageTimestamp != null) {
                Text(
                    text = thread.latestMessageTimestamp,
                    color =
                        if (hasUnreadMessages) {
                            EnamoraDustyRose
                        } else {
                            EnamoraTextSecondary
                        },
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight =
                        if (hasUnreadMessages) {
                            FontWeight.SemiBold
                        } else {
                            FontWeight.Normal
                        }
                )
            }

            if (hasUnreadMessages) {
                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                UnreadCountBadge(
                    unreadCount = thread.unreadCount
                )
            }
        }
    }
}

@Composable
private fun CharacterAvatar(
    thread: ChatThreadUiModel
) {
    val character = thread.character

    Box(
        modifier = Modifier.size(64.dp)
    ) {
        Image(
            painter = painterResource(
                id = character.portraitResId
            ),
            contentDescription =
                "${character.name}'s profile photograph",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.Center)
                .clip(CircleShape)
                .border(
                    width = 1.dp,
                    color = EnamoraOutline,
                    shape = CircleShape
                )
        )

        if (character.isAvailable) {
            Box(
                modifier = Modifier
                    .size(15.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(Color(0xFF68CA8B))
                    .border(
                        width = 2.dp,
                        color = EnamoraSurface,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
private fun UnreadCountBadge(
    unreadCount: Int
) {
    Box(
        modifier = Modifier
            .heightIn(min = 24.dp)
            .widthIn(min = 24.dp)
            .clip(CircleShape)
            .background(EnamoraRose)
            .padding(
                horizontal = 7.dp,
                vertical = 3.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text =
                if (unreadCount > 99) {
                    "99+"
                } else {
                    unreadCount.toString()
                },
            color = EnamoraObsidian,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun EmptyChatList(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 34.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(EnamoraSurface)
                    .border(
                        width = 1.dp,
                        color = EnamoraOutline,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "♡",
                    color = EnamoraRose,
                    style = MaterialTheme.typography.headlineLarge
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(
                text = "No conversations yet",
                color = EnamoraWarmIvory,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text =
                    "Characters you connect with will appear here.",
                color = EnamoraTextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun SearchGlyph() {
    Canvas(
        modifier = Modifier.size(24.dp)
    ) {
        val strokeWidth = 2.dp.toPx()

        drawCircle(
            color = EnamoraWarmIvory,
            radius = size.minDimension * 0.28f,
            center = Offset(
                x = size.width * 0.42f,
                y = size.height * 0.42f
            ),
            style = Stroke(
                width = strokeWidth
            )
        )

        drawLine(
            color = EnamoraWarmIvory,
            start = Offset(
                x = size.width * 0.62f,
                y = size.height * 0.62f
            ),
            end = Offset(
                x = size.width * 0.86f,
                y = size.height * 0.86f
            ),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}