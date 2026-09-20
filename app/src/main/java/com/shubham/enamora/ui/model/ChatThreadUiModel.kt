package com.shubham.enamora.ui.model

data class ChatThreadUiModel(
    val character: CharacterUiModel,
    val latestMessage: String? = null,
    val latestMessageTimestamp: String? = null,
    val unreadCount: Int = 0
) {
    init {
        require(unreadCount >= 0) {
            "Unread message count cannot be negative."
        }

        require(
            (latestMessage == null) ==
                    (latestMessageTimestamp == null)
        ) {
            "A latest message and its timestamp must be provided together."
        }
    }

    val hasMessages: Boolean
        get() = latestMessage != null
}