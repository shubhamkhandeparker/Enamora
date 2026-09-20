package com.shubham.enamora.ui.model

import androidx.annotation.DrawableRes

enum class MessageAuthor {
    USER,
    CHARACTER
}

enum class MessageDeliveryState {
    SENDING,
    SENT,
    DELIVERED,
    READ
}

sealed interface ChatMessageContent {

    data class Text(
        val text: String
    ) : ChatMessageContent {
        init {
            require(text.isNotBlank()) {
                "A text message cannot be empty."
            }
        }
    }

    data class VoiceNote(
        val duration: String
    ) : ChatMessageContent {
        init {
            require(duration.isNotBlank()) {
                "A voice note must have a duration."
            }
        }
    }

    data class Photo(
        @DrawableRes val imageResId: Int,
        val caption: String = ""
    ) : ChatMessageContent
}

data class ChatMessageUiModel(
    val id: String,
    val author: MessageAuthor,
    val content: ChatMessageContent,
    val timestamp: String,
    val deliveryState: MessageDeliveryState? = null
) {
    init {
        require(id.isNotBlank()) {
            "Every chat message must have an ID."
        }

        require(timestamp.isNotBlank()) {
            "Every chat message must have a timestamp."
        }
    }
}