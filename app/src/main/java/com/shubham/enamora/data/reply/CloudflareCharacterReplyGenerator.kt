package com.shubham.enamora.data.reply

import com.shubham.enamora.data.remote.AiRecentMessage
import com.shubham.enamora.data.remote.EnamoraAiClient
import com.shubham.enamora.domain.chat.CharacterReplyGenerator
import com.shubham.enamora.domain.chat.CharacterReplyRequest

class CloudflareCharacterReplyGenerator(
    private val aiClient:
    EnamoraAiClient
) : CharacterReplyGenerator {

    override suspend fun generateReply(
        request: CharacterReplyRequest
    ): String {
        require(
            request.systemPrompt.isNotBlank()
        ) {
            "Character context is missing."
        }

        val recentMessages =
            request.recentMessages.map {
                    message ->
                AiRecentMessage(
                    role = message.role.name,
                    text = message.text
                )
            }

        return aiClient.generateReply(
            characterId =
                request.characterId,
            systemPrompt =
                request.systemPrompt,
            recentMessages =
                recentMessages,
            userMessage =
                request.userMessage
        )
    }
}