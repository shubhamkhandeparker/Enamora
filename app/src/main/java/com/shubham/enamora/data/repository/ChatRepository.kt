package com.shubham.enamora.data.repository

import androidx.room.withTransaction
import com.shubham.enamora.data.local.database.EnamoraDatabase
import com.shubham.enamora.data.local.entity.CharacterPresenceEntity
import com.shubham.enamora.data.local.entity.ChatThreadEntity
import com.shubham.enamora.data.local.entity.MessageEntity
import com.shubham.enamora.data.local.entity.RelationshipStateEntity
import com.shubham.enamora.data.local.seed.InitialCharacterData
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class ChatRepository(
    private val database: EnamoraDatabase
) {
    companion object {
        const val LOCAL_USER_ID = "local_user"
    }

    private val characterDao =
        database.characterDao()

    private val chatThreadDao =
        database.chatThreadDao()

    private val messageDao =
        database.messageDao()

    private val relationshipStateDao =
        database.relationshipStateDao()

    private val characterPresenceDao =
        database.characterPresenceDao()

    fun observeThreads(
        userId: String = LOCAL_USER_ID
    ): Flow<List<ChatThreadEntity>> {
        return chatThreadDao.observeThreads(userId)
    }

    fun observeMessages(
        threadId: String
    ): Flow<List<MessageEntity>> {
        return messageDao.observeMessages(threadId)
    }

    suspend fun prepareRheaConversation(
        userId: String = LOCAL_USER_ID
    ): String {
        return database.withTransaction {
            val currentTime =
                System.currentTimeMillis()

            val existingCharacter =
                characterDao.getCharacter(
                    InitialCharacterData.RHEA_ID
                )

            characterDao.upsertCharacter(
                InitialCharacterData.createRhea(
                    createdAt =
                        existingCharacter?.createdAt
                            ?: currentTime,
                    updatedAt = currentTime
                )
            )

            val existingThread =
                chatThreadDao.getThreadForCharacter(
                    userId = userId,
                    characterId =
                        InitialCharacterData.RHEA_ID
                )

            val thread =
                existingThread
                    ?: ChatThreadEntity(
                        id = UUID
                            .randomUUID()
                            .toString(),
                        userId = userId,
                        characterId =
                            InitialCharacterData.RHEA_ID,
                        createdAt = currentTime,
                        updatedAt = currentTime
                    ).also { newThread ->
                        chatThreadDao.upsertThread(
                            newThread
                        )
                    }

            val existingRelationship =
                relationshipStateDao
                    .getRelationship(
                        userId = userId,
                        characterId =
                            InitialCharacterData.RHEA_ID
                    )

            if (existingRelationship == null) {
                relationshipStateDao
                    .upsertRelationship(
                        RelationshipStateEntity(
                            id = UUID
                                .randomUUID()
                                .toString(),
                            userId = userId,
                            characterId =
                                InitialCharacterData.RHEA_ID,
                            firstConnectedAt =
                                currentTime,
                            stageChangedAt =
                                currentTime,
                            updatedAt =
                                currentTime
                        )
                    )
            }

            val existingPresence =
                characterPresenceDao.getPresence(
                    userId = userId,
                    characterId =
                        InitialCharacterData.RHEA_ID
                )

            if (existingPresence == null) {
                characterPresenceDao.upsertPresence(
                    CharacterPresenceEntity(
                        id = UUID
                            .randomUUID()
                            .toString(),
                        userId = userId,
                        characterId =
                            InitialCharacterData.RHEA_ID,
                        timezoneId =
                            "Asia/Kolkata",
                        statusUpdatedAt =
                            currentTime
                    )
                )
            }

            thread.id
        }
    }

    suspend fun sendUserTextMessage(
        threadId: String,
        rawText: String,
        userId: String = LOCAL_USER_ID,
        characterId: String =
            InitialCharacterData.RHEA_ID
    ): String {
        val text = rawText.trim()

        require(text.isNotEmpty()) {
            "Message cannot be empty."
        }

        val currentTime =
            System.currentTimeMillis()

        val messageId =
            UUID.randomUUID().toString()

        database.withTransaction {
            messageDao.upsertMessage(
                MessageEntity(
                    id = messageId,
                    threadId = threadId,
                    senderType = "USER",
                    contentType = "TEXT",
                    bodyText = text,
                    deliveryStatus = "PENDING",
                    syncStatus = "LOCAL_ONLY",
                    createdAt = currentTime
                )
            )

            chatThreadDao.updateLastMessage(
                threadId = threadId,
                preview = text,
                messageTime = currentTime,
                updatedAt = currentTime
            )

            relationshipStateDao.recordUserMessage(
                userId = userId,
                characterId = characterId,
                interactionAt = currentTime
            )
        }

        return messageId
    }

    suspend fun saveCharacterTextMessage(
        threadId: String,
        rawText: String,
        userId: String = LOCAL_USER_ID,
        characterId: String =
            InitialCharacterData.RHEA_ID
    ): String {
        val text = rawText.trim()

        require(text.isNotEmpty()) {
            "Message cannot be empty."
        }

        val currentTime =
            System.currentTimeMillis()

        val messageId =
            UUID.randomUUID().toString()

        database.withTransaction {
            messageDao.upsertMessage(
                MessageEntity(
                    id = messageId,
                    threadId = threadId,
                    senderType = "CHARACTER",
                    contentType = "TEXT",
                    bodyText = text,
                    deliveryStatus = "DELIVERED",
                    syncStatus = "SYNCED",
                    createdAt = currentTime,
                    sentAt = currentTime,
                    deliveredAt = currentTime
                )
            )

            chatThreadDao.updateLastMessage(
                threadId = threadId,
                preview = text,
                messageTime = currentTime,
                updatedAt = currentTime
            )

            chatThreadDao.incrementUnreadCount(
                threadId = threadId,
                updatedAt = currentTime
            )

            relationshipStateDao
                .recordCharacterMessage(
                    userId = userId,
                    characterId = characterId,
                    interactionAt = currentTime
                )
        }

        return messageId
    }

    suspend fun markThreadRead(
        threadId: String
    ) {
        val currentTime =
            System.currentTimeMillis()

        database.withTransaction {
            messageDao
                .markCharacterMessagesReadByUser(
                    threadId = threadId,
                    readAt = currentTime
                )

            chatThreadDao.clearUnreadCount(
                threadId = threadId,
                updatedAt = currentTime
            )
        }
    }
}