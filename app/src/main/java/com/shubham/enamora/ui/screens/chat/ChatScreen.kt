package com.shubham.enamora.ui.screens.chat

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shubham.enamora.ui.model.CharacterUiModel
import com.shubham.enamora.ui.model.ChatMessageContent
import com.shubham.enamora.ui.model.ChatMessageUiModel
import com.shubham.enamora.ui.model.MessageAuthor
import com.shubham.enamora.ui.model.MessageDeliveryState
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
fun ChatScreen(
    character: CharacterUiModel,
    messages: List<ChatMessageUiModel>,
    isTyping: Boolean,
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit,
    onVoiceCallClick: () -> Unit,
    onMoreClick: () -> Unit,
    onAttachmentClick: () -> Unit,
    onCameraClick: () -> Unit,
    onMicrophoneClick: () -> Unit,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier,
    lastSeenText: String = "recently"
) {
    var messageText by rememberSaveable(character.id) {
        mutableStateOf("")
    }

    val listState = rememberLazyListState()
    val focusManager = LocalFocusManager.current

    fun sendCurrentMessage() {
        val cleanMessage = messageText.trim()

        if (cleanMessage.isNotEmpty()) {
            onSendMessage(cleanMessage)
            messageText = ""
            focusManager.clearFocus()
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.scrollToItem(messages.lastIndex)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EnamoraObsidian)
    ) {
        ChatHeader(
            character = character,
            isTyping = isTyping,
            lastSeenText = lastSeenText,
            onBackClick = onBackClick,
            onProfileClick = onProfileClick,
            onVoiceCallClick = onVoiceCallClick,
            onMoreClick = onMoreClick
        )

        HorizontalDivider(
            color = EnamoraOutline.copy(alpha = 0.45f)
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(
                horizontal = 14.dp,
                vertical = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            items(
                items = messages,
                key = { message ->
                    message.id
                }
            ) { message ->
                ChatMessageBubble(
                    message = message
                )
            }
        }

        HorizontalDivider(
            color = EnamoraOutline.copy(alpha = 0.45f)
        )

        MessageComposer(
            messageText = messageText,
            characterName = character.name,
            onMessageTextChange = { newText ->
                if (newText.length <= 500) {
                    messageText = newText
                }
            },
            onAttachmentClick = onAttachmentClick,
            onCameraClick = onCameraClick,
            onMicrophoneClick = onMicrophoneClick,
            onSendClick = {
                sendCurrentMessage()
            },
            onKeyboardSend = {
                sendCurrentMessage()
            }
        )
    }
}

@Composable
private fun ChatHeader(
    character: CharacterUiModel,
    isTyping: Boolean,
    lastSeenText: String,
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit,
    onVoiceCallClick: () -> Unit,
    onMoreClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 78.dp)
            .padding(
                start = 4.dp,
                end = 4.dp,
                top = 7.dp,
                bottom = 7.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderActionButton(
            symbol = "‹",
            description = "Go back",
            onClick = onBackClick,
            fontSize = 42.sp
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(14.dp))
                .clickable(onClick = onProfileClick)
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(
                    id = character.portraitResId
                ),
                contentDescription =
                    "${character.name}'s profile photograph",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .border(
                        width = 1.dp,
                        color = EnamoraOutline,
                        shape = CircleShape
                    )
            )

            Spacer(
                modifier = Modifier.width(11.dp)
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
                    modifier = Modifier.height(3.dp)
                )

                CharacterPresenceStatus(
                    isOnline = character.isAvailable,
                    isTyping = isTyping,
                    lastSeenText = lastSeenText
                )
            }
        }

        HeaderActionButton(
            symbol = "☎",
            description = "Start voice call",
            onClick = onVoiceCallClick,
            fontSize = 23.sp
        )

        HeaderActionButton(
            symbol = "⋮",
            description = "More options",
            onClick = onMoreClick,
            fontSize = 28.sp
        )
    }
}

@Composable
private fun CharacterPresenceStatus(
    isOnline: Boolean,
    isTyping: Boolean,
    lastSeenText: String
) {
    val onlineColor = Color(0xFF68CA8B)

    when {
        isTyping -> {
            Text(
                text = "typing…",
                color = EnamoraDustyRose,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium
            )
        }

        isOnline -> {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(onlineColor)
                )

                Spacer(
                    modifier = Modifier.width(6.dp)
                )

                Text(
                    text = "online",
                    color = onlineColor,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        else -> {
            Text(
                text = "last seen $lastSeenText",
                color = EnamoraTextSecondary,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun HeaderActionButton(
    symbol: String,
    description: String,
    onClick: () -> Unit,
    fontSize: androidx.compose.ui.unit.TextUnit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(46.dp)
            .semantics {
                contentDescription = description
            }
    ) {
        Text(
            text = symbol,
            color = EnamoraWarmIvory,
            fontSize = fontSize,
            fontWeight = FontWeight.Light
        )
    }
}

@Composable
private fun ChatMessageBubble(
    message: ChatMessageUiModel
) {
    val isUserMessage =
        message.author == MessageAuthor.USER

    val bubbleShape =
        if (isUserMessage) {
            RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomEnd = 5.dp,
                bottomStart = 18.dp
            )
        } else {
            RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomEnd = 18.dp,
                bottomStart = 5.dp
            )
        }

    val backgroundColor =
        if (isUserMessage) {
            EnamoraWineDeep
        } else {
            EnamoraSurface
        }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            if (isUserMessage) {
                Arrangement.End
            } else {
                Arrangement.Start
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.84f)
                .clip(bubbleShape)
                .background(backgroundColor)
                .border(
                    width = 1.dp,
                    color = EnamoraOutline.copy(alpha = 0.55f),
                    shape = bubbleShape
                )
                .padding(11.dp)
        ) {
            when (val content = message.content) {
                is ChatMessageContent.Text -> {
                    Text(
                        text = content.text,
                        color = EnamoraWarmIvory,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 21.sp
                    )
                }

                is ChatMessageContent.VoiceNote -> {
                    VoiceNoteContent(
                        duration = content.duration
                    )
                }

                is ChatMessageContent.Photo -> {
                    PhotoMessageContent(
                        imageResId = content.imageResId,
                        caption = content.caption
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(5.dp)
            )

            MessageMetadata(
                timestamp = message.timestamp,
                deliveryState = message.deliveryState
            )
        }
    }
}

@Composable
private fun VoiceNoteContent(
    duration: String
) {
    val waveformHeights = listOf(
        10,
        17,
        25,
        14,
        30,
        20,
        11,
        27,
        35,
        18,
        29,
        13,
        23,
        32,
        16,
        26,
        12,
        21
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(EnamoraRose),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "▶",
                color = EnamoraObsidian,
                fontSize = 17.sp
            )
        }

        Spacer(
            modifier = Modifier.width(11.dp)
        )

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            waveformHeights.forEach { height ->
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(height.dp)
                        .clip(CircleShape)
                        .background(EnamoraTextSecondary)
                )
            }
        }

        Spacer(
            modifier = Modifier.width(8.dp)
        )

        Text(
            text = duration,
            color = EnamoraWarmIvory,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun PhotoMessageContent(
    imageResId: Int,
    caption: String
) {
    Image(
        painter = painterResource(
            id = imageResId
        ),
        contentDescription = "Photo shared in the conversation",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
            .clip(RoundedCornerShape(13.dp))
    )

    if (caption.isNotBlank()) {
        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = caption,
            color = EnamoraWarmIvory,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun MessageMetadata(
    timestamp: String,
    deliveryState: MessageDeliveryState?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = timestamp,
            color = EnamoraTextSecondary,
            style = MaterialTheme.typography.labelSmall
        )

        if (deliveryState != null) {
            Spacer(
                modifier = Modifier.width(5.dp)
            )

            Text(
                text = deliveryStateSymbol(
                    deliveryState = deliveryState
                ),
                color =
                    if (deliveryState == MessageDeliveryState.READ) {
                        EnamoraRoseSoft
                    } else {
                        EnamoraTextSecondary
                    },
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun deliveryStateSymbol(
    deliveryState: MessageDeliveryState
): String {
    return when (deliveryState) {
        MessageDeliveryState.SENDING -> "◷"
        MessageDeliveryState.SENT -> "✓"
        MessageDeliveryState.DELIVERED -> "✓✓"
        MessageDeliveryState.READ -> "✓✓"
    }
}

@Composable
private fun MessageComposer(
    messageText: String,
    characterName: String,
    onMessageTextChange: (String) -> Unit,
    onAttachmentClick: () -> Unit,
    onCameraClick: () -> Unit,
    onMicrophoneClick: () -> Unit,
    onSendClick: () -> Unit,
    onKeyboardSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(EnamoraObsidian)
            .padding(
                horizontal = 9.dp,
                vertical = 9.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ComposerCircleButton(
            symbol = "+",
            description = "Add attachment",
            backgroundColor = EnamoraSurface,
            contentColor = EnamoraWarmIvory,
            onClick = onAttachmentClick
        )

        Spacer(
            modifier = Modifier.width(7.dp)
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 52.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(EnamoraSurface)
                .border(
                    width = 1.dp,
                    color = EnamoraOutline.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(start = 17.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = messageText,
                onValueChange = onMessageTextChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 13.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = EnamoraWarmIvory
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        onKeyboardSend()
                    }
                ),
                cursorBrush =
                    androidx.compose.ui.graphics.SolidColor(
                        EnamoraRose
                    ),
                decorationBox = { innerTextField ->
                    Box(
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (messageText.isBlank()) {
                            Text(
                                text = "Message $characterName...",
                                color = EnamoraTextSecondary,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        innerTextField()
                    }
                }
            )

            IconButton(
                onClick = onCameraClick,
                modifier = Modifier
                    .size(48.dp)
                    .semantics {
                        contentDescription = "Open camera"
                    }
            ) {
                Text(
                    text = "▣",
                    color = EnamoraWarmIvory,
                    fontSize = 21.sp
                )
            }
        }

        Spacer(
            modifier = Modifier.width(7.dp)
        )

        if (messageText.isBlank()) {
            ComposerCircleButton(
                symbol = "●",
                description = "Record voice note",
                backgroundColor = EnamoraRose,
                contentColor = Color.White,
                onClick = onMicrophoneClick
            )
        } else {
            ComposerCircleButton(
                symbol = "↑",
                description = "Send message",
                backgroundColor = EnamoraRose,
                contentColor = EnamoraObsidian,
                onClick = onSendClick
            )
        }
    }
}

@Composable
private fun ComposerCircleButton(
    symbol: String,
    description: String,
    backgroundColor: Color,
    contentColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = EnamoraOutline.copy(alpha = 0.65f),
                shape = CircleShape
            )
            .clickable(onClick = onClick)
            .semantics {
                contentDescription = description
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            color = contentColor,
            fontSize = 25.sp,
            fontWeight = FontWeight.Medium
        )
    }
}