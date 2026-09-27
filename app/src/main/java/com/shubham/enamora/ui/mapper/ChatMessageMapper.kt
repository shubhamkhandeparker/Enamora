package com.shubham.enamora.ui.mapper

import com.shubham.enamora.data.local.entity.MessageEntity
import com.shubham.enamora.ui.model.ChatMessageContent
import com.shubham.enamora.ui.model.ChatMessageUiModel
import com.shubham.enamora.ui.model.MessageAuthor
import com.shubham.enamora.ui.model.MessageDeliveryState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun MessageEntity.toChatMessageUiModel():
        ChatMessageUiModel? {

    val messageAuthor =
        when (senderType.uppercase(Locale.ROOT)) {
            "USER" -> MessageAuthor.USER

            "CHARACTER" ->
                MessageAuthor.CHARACTER

            else -> return null
        }

    val messageContent =
        when (contentType.uppercase(Locale.ROOT)) {
            "TEXT" -> {
                val text =
                    bodyText?.trim()
                        ?.takeIf {
                            it.isNotEmpty()
                        }
                        ?: return null

                ChatMessageContent.Text(
                    text = text
                )
            }

            "VOICE" -> {
                val duration =
                    mediaDurationMs
                        ?: return null

                ChatMessageContent.VoiceNote(
                    duration =
                        formatDuration(duration)
                )
            }

            else -> return null
        }

    val messageDeliveryState =
        if (messageAuthor == MessageAuthor.USER) {
            deliveryStatus
                .toMessageDeliveryState()
        } else {
            null
        }

    return ChatMessageUiModel(
        id = id,
        author = messageAuthor,
        content = messageContent,
        timestamp = formatMessageTime(
            timestamp = createdAt
        ),
        deliveryState =
            messageDeliveryState
    )
}

private fun String.toMessageDeliveryState():
        MessageDeliveryState? {

    return when (uppercase(Locale.ROOT)) {
        "PENDING" ->
            MessageDeliveryState.SENDING

        "SENT" ->
            MessageDeliveryState.SENT

        "DELIVERED" ->
            MessageDeliveryState.DELIVERED

        "READ" ->
            MessageDeliveryState.READ

        "FAILED" ->
            MessageDeliveryState.FAILED

        else -> null
    }
}

private fun formatMessageTime(
    timestamp: Long
): String {
    val currentLocale =
        Locale.getDefault()

    return SimpleDateFormat(
        "h:mm a",
        currentLocale
    ).format(
        Date(timestamp)
    ).lowercase(currentLocale)
}

private fun formatDuration(
    durationMilliseconds: Long
): String {
    val totalSeconds =
        durationMilliseconds / 1000L

    val minutes =
        totalSeconds / 60L

    val seconds =
        totalSeconds % 60L

    return String.format(
        Locale.getDefault(),
        "%d:%02d",
        minutes,
        seconds
    )
}