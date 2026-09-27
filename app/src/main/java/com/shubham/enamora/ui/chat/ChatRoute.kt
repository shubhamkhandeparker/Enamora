package com.shubham.enamora.ui.screens.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shubham.enamora.EnamoraApplication
import com.shubham.enamora.data.remote.EnamoraAiClient
import com.shubham.enamora.data.reply.CloudflareCharacterReplyGenerator
import com.shubham.enamora.data.repository.ChatRepository
import com.shubham.enamora.data.repository.ConversationContextRepository
import com.shubham.enamora.ui.chat.ChatViewModel
import com.shubham.enamora.ui.chat.ChatViewModelFactory
import com.shubham.enamora.ui.model.CharacterUiModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun ChatRoute(
    character: CharacterUiModel,
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit,
    onVoiceCallClick: () -> Unit,
    onMoreClick: () -> Unit,
    onAttachmentClick: () -> Unit,
    onCameraClick: () -> Unit,
    onMicrophoneClick: () -> Unit,
    modifier: Modifier = Modifier,
    isTyping: Boolean = false,
    lastSeenText: String = "recently"
) {
    val application =
        LocalContext.current
            .applicationContext
                as EnamoraApplication

    val viewModelFactory =
        remember(application) {
            ChatViewModelFactory(
                chatRepository =
                    ChatRepository(
                        database =
                            application.database
                    ),
                characterReplyGenerator =
                    CloudflareCharacterReplyGenerator(
                        aiClient =
                            EnamoraAiClient()
                    ),
                conversationContextRepository =
                    ConversationContextRepository(
                        database =
                            application.database
                    )
            )
        }

    val chatViewModel: ChatViewModel =
        viewModel(
            key = "chat_${character.id}",
            factory = viewModelFactory
        )

    val uiState by
    chatViewModel.uiState
        .collectAsStateWithLifecycle()

    val displayedCharacter =
        character.copy(
            isAvailable =
                uiState.isCharacterOnline
        )

    val resolvedLastSeenText =
        uiState.characterLastSeenAt
            ?.let { timestamp ->
                formatLastSeenText(
                    timestamp = timestamp
                )
            }
            ?: lastSeenText

    ChatScreen(
        character = displayedCharacter,
        messages = uiState.messages,
        isTyping =
            isTyping ||
                    uiState.isCharacterTyping,
        onBackClick = onBackClick,
        onProfileClick = onProfileClick,
        onVoiceCallClick =
            onVoiceCallClick,
        onMoreClick = onMoreClick,
        onAttachmentClick =
            onAttachmentClick,
        onCameraClick = onCameraClick,
        onMicrophoneClick =
            onMicrophoneClick,
        onSendMessage = { messageText ->
            chatViewModel.sendMessage(
                messageText = messageText
            )
        },
        onRetryMessage = { messageId ->
            chatViewModel.retryMessage(
                messageId = messageId
            )
        },
        modifier = modifier,
        lastSeenText =
            resolvedLastSeenText
    )
}

private fun formatLastSeenText(
    timestamp: Long
): String {
    val locale =
        Locale.getDefault()

    val now =
        Calendar.getInstance()

    val lastSeen =
        Calendar.getInstance().apply {
            timeInMillis = timestamp
        }

    val formattedTime =
        SimpleDateFormat(
            "h:mm a",
            locale
        ).format(
            Date(timestamp)
        ).lowercase(locale)

    return when {
        now.isSameCalendarDay(
            other = lastSeen
        ) -> {
            "today at $formattedTime"
        }

        now.isYesterday(
            other = lastSeen
        ) -> {
            "yesterday at $formattedTime"
        }

        else -> {
            SimpleDateFormat(
                "d MMM 'at' h:mm a",
                locale
            ).format(
                Date(timestamp)
            ).lowercase(locale)
        }
    }
}

private fun Calendar.isSameCalendarDay(
    other: Calendar
): Boolean {
    return get(Calendar.ERA) ==
            other.get(Calendar.ERA) &&
            get(Calendar.YEAR) ==
            other.get(Calendar.YEAR) &&
            get(Calendar.DAY_OF_YEAR) ==
            other.get(Calendar.DAY_OF_YEAR)
}

private fun Calendar.isYesterday(
    other: Calendar
): Boolean {
    val yesterday =
        clone() as Calendar

    yesterday.add(
        Calendar.DAY_OF_YEAR,
        -1
    )

    return yesterday
        .isSameCalendarDay(
            other = other
        )
}