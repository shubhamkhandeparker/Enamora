package com.shubham.enamora.data.repository

import com.shubham.enamora.data.local.database.EnamoraDatabase
import com.shubham.enamora.data.local.seed.InitialCharacterData
import com.shubham.enamora.domain.chat.CharacterContextMessage
import com.shubham.enamora.domain.chat.CharacterMessageRole
import com.shubham.enamora.domain.chat.RheaPromptBuilder
import com.shubham.enamora.domain.chat.RheaPromptContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class PreparedConversationContext(
    val systemPrompt: String,
    val recentMessages:
    List<CharacterContextMessage>
)

class ConversationContextRepository(
    database: EnamoraDatabase
) {
    companion object {
        private const val RECENT_MESSAGE_LIMIT = 16
        private const val RHEA_TIMEZONE =
            "Asia/Kolkata"
    }

    private val messageDao =
        database.messageDao()

    private val relationshipStateDao =
        database.relationshipStateDao()

    private val memoryRepository =
        CharacterMemoryRepository(database)

    suspend fun prepareRheaContext(
        threadId: String,
        userName: String = "User",
        userId: String =
            ChatRepository.LOCAL_USER_ID
    ): PreparedConversationContext {
        val relationship =
            relationshipStateDao
                .getRelationship(
                    userId = userId,
                    characterId =
                        InitialCharacterData.RHEA_ID
                )

        val memories =
            memoryRepository
                .getMemoriesForContext(
                    userId = userId,
                    characterId =
                        InitialCharacterData.RHEA_ID,
                    includeSensitive = false
                )

        val summaryMemory =
            memories.firstOrNull { memory ->
                memory.memoryType ==
                        "CONVERSATION_SUMMARY"
            }

        val reliableMemoryText =
            memories
                .filterNot { memory ->
                    memory.id ==
                            summaryMemory?.id
                }
                .map { memory ->
                    memory.content
                }

        val recentMessages =
            messageDao
                .getRecentMessages(
                    threadId = threadId,
                    limit = RECENT_MESSAGE_LIMIT
                )
                .mapNotNull { message ->
                    val messageText =
                        message.bodyText
                            ?.trim()
                            .orEmpty()

                    if (
                        message.contentType != "TEXT" ||
                        messageText.isEmpty()
                    ) {
                        return@mapNotNull null
                    }

                    val role =
                        when (message.senderType) {
                            "USER" ->
                                CharacterMessageRole.USER

                            "CHARACTER" ->
                                CharacterMessageRole.CHARACTER

                            else ->
                                return@mapNotNull null
                        }

                    CharacterContextMessage(
                        role = role,
                        text = messageText
                    )
                }

        val systemPrompt =
            RheaPromptBuilder.build(
                RheaPromptContext(
                    userName = userName,
                    relationshipStage =
                        relationship
                            ?.relationshipStage
                            ?: "NEW_CONNECTION",
                    localDateTimeText =
                        currentRheaDateTime(),
                    conversationSummary =
                        summaryMemory?.content,
                    memories =
                        reliableMemoryText
                )
            )

        memoryRepository.markMemoriesUsed(
            memories = memories
        )

        return PreparedConversationContext(
            systemPrompt = systemPrompt,
            recentMessages = recentMessages
        )
    }

    private fun currentRheaDateTime(): String {
        return SimpleDateFormat(
            "EEEE, d MMMM yyyy, h:mm a",
            Locale.ENGLISH
        ).apply {
            timeZone =
                TimeZone.getTimeZone(
                    RHEA_TIMEZONE
                )
        }.format(Date())
    }
}