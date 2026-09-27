package com.shubham.enamora.data.reply

import com.shubham.enamora.data.local.seed.InitialCharacterData
import com.shubham.enamora.domain.chat.CharacterReplyGenerator
import com.shubham.enamora.domain.chat.CharacterReplyRequest
import kotlinx.coroutines.delay

class LocalRheaReplyGenerator :
    CharacterReplyGenerator {

    override suspend fun generateReply(
        request: CharacterReplyRequest
    ): String {
        require(
            request.characterId ==
                    InitialCharacterData.RHEA_ID
        ) {
            "Unsupported character."
        }

        delay(1_200L)

        val userMessage =
            request.userMessage
                .trim()
                .lowercase()

        return when {
            userMessage in listOf(
                "hi",
                "hello",
                "hey"
            ) -> {
                "Hey... I’m glad you’re here. How was your day?"
            }

            "how are you" in userMessage -> {
                "I’m good. The studio kept me busy today, but I’m finally slowing down. How are you?"
            }

            "work" in userMessage ||
                    "flower" in userMessage -> {
                "The studio was busy today. Meera handled the suppliers while I finished a floral arrangement. How was your day?"
            }

            userMessage.endsWith("?") -> {
                "Hmm... let me think. What made you ask me that?"
            }

            else -> {
                "I’m listening. Tell me a little more about that."
            }
        }
    }
}