package com.shubham.enamora.domain.chat

enum class CharacterMessageRole {
    USER,
    CHARACTER
}

data class CharacterContextMessage(
    val role: CharacterMessageRole,
    val text: String
)

data class CharacterReplyRequest(
    val threadId: String,
    val characterId: String,
    val userMessage: String,
    val systemPrompt: String = "",
    val recentMessages:
    List<CharacterContextMessage> =
        emptyList()
)

fun interface CharacterReplyGenerator {

    suspend fun generateReply(
        request: CharacterReplyRequest
    ): String
}