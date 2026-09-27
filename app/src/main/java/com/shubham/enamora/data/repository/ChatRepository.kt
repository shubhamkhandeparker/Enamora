package com.shubham.enamora.data.repository

import androidx.room.withTransaction
import com.shubham.enamora.data.local.database.EnamoraDatabase
import com.shubham.enamora.data.local.entity.CharacterPresenceEntity
import com.shubham.enamora.data.local.entity.ChatThreadEntity
import com.shubham.enamora.data.local.entity.MessageEntity
import com.shubham.enamora.data.local.entity.RelationshipStateEntity
import com.shubham.enamora.data.local.seed.InitialCharacterData
import java.util.UUID
import kotlinx.coroutines.flow.Flow

class ChatRepository(
    private val database: EnamoraDatabase
) {
    companion object {
        const val LOCAL_USER_ID =
            "local_user"

        const val PRESENCE_STATUS_OFFLINE =
            "OFFLINE"

        const val PRESENCE_STATUS_ONLINE =
            "ONLINE"

        const val PRESENCE_STATUS_TYPING =
            "TYPING"

        private const val
                STALE_ACTIVE_PRESENCE_DURATION_MS =
            2L * 60L * 1000L
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
        return chatThreadDao.observeThreads(
            userId
        )
    }

    fun observeMessages(
        threadId: String
    ): Flow<List<MessageEntity>> {
        return messageDao.observeMessages(
            threadId
        )
    }

    fun observeCharacterPresence(
        userId: String = LOCAL_USER_ID,
        characterId: String =
            InitialCharacterData.RHEA_ID
    ): Flow<CharacterPresenceEntity?> {
        return characterPresenceDao
            .observePresence(
                userId = userId,
                characterId = characterId
            )
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
                        existingCharacter
                            ?.createdAt
                            ?: currentTime,
                    updatedAt = currentTime
                )
            )

            val existingThread =
                chatThreadDao
                    .getThreadForCharacter(
                        userId = userId,
                        characterId =
                            InitialCharacterData
                                .RHEA_ID
                    )

            val thread =
                existingThread
                    ?: ChatThreadEntity(
                        id = UUID
                            .randomUUID()
                            .toString(),
                        userId = userId,
                        characterId =
                            InitialCharacterData
                                .RHEA_ID,
                        createdAt = currentTime,
                        updatedAt = currentTime
                    ).also { newThread ->
                        chatThreadDao
                            .upsertThread(
                                newThread
                            )
                    }

            val existingRelationship =
                relationshipStateDao
                    .getRelationship(
                        userId = userId,
                        characterId =
                            InitialCharacterData
                                .RHEA_ID
                    )

            if (
                existingRelationship == null
            ) {
                relationshipStateDao
                    .upsertRelationship(
                        RelationshipStateEntity(
                            id = UUID
                                .randomUUID()
                                .toString(),
                            userId = userId,
                            characterId =
                                InitialCharacterData
                                    .RHEA_ID,
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
                characterPresenceDao
                    .getPresence(
                        userId = userId,
                        characterId =
                            InitialCharacterData
                                .RHEA_ID
                    )

            if (existingPresence == null) {
                characterPresenceDao
                    .upsertPresence(
                        CharacterPresenceEntity(
                            id = UUID
                                .randomUUID()
                                .toString(),
                            userId = userId,
                            characterId =
                                InitialCharacterData
                                    .RHEA_ID,
                            availabilityStatus =
                                PRESENCE_STATUS_OFFLINE,
                            timezoneId =
                                "Asia/Kolkata",
                            statusUpdatedAt =
                                currentTime
                        )
                    )
            } else {
                val hasActiveStatus =
                    existingPresence
                        .availabilityStatus
                        .equals(
                            other =
                                PRESENCE_STATUS_ONLINE,
                            ignoreCase = true
                        ) ||
                            existingPresence
                                .availabilityStatus
                                .equals(
                                    other =
                                        PRESENCE_STATUS_TYPING,
                                    ignoreCase = true
                                )

                val activeStatusAge =
                    currentTime -
                            existingPresence
                                .statusUpdatedAt

                val isStaleActiveStatus =
                    hasActiveStatus &&
                            activeStatusAge >=
                            STALE_ACTIVE_PRESENCE_DURATION_MS

                if (isStaleActiveStatus) {
                    characterPresenceDao
                        .markOffline(
                            userId = userId,
                            characterId =
                                InitialCharacterData
                                    .RHEA_ID,
                            lastSeenAt =
                                existingPresence
                                    .statusUpdatedAt,
                            nextAvailableAt = null
                        )
                }
            }

            thread.id
        }
    }

    suspend fun markCharacterOnline(
        userId: String = LOCAL_USER_ID,
        characterId: String =
            InitialCharacterData.RHEA_ID,
        activityContext: String? =
            "ACTIVE_CHAT"
    ) {
        val currentTime =
            System.currentTimeMillis()

        characterPresenceDao.updatePresence(
            userId = userId,
            characterId = characterId,
            status =
                PRESENCE_STATUS_ONLINE,
            activityContext =
                activityContext,
            lastSeenAt = null,
            nextAvailableAt = null,
            updatedAt = currentTime
        )
    }

    suspend fun markCharacterTyping(
        userId: String = LOCAL_USER_ID,
        characterId: String =
            InitialCharacterData.RHEA_ID
    ) {
        val currentTime =
            System.currentTimeMillis()

        characterPresenceDao.updatePresence(
            userId = userId,
            characterId = characterId,
            status =
                PRESENCE_STATUS_TYPING,
            activityContext =
                "REPLYING",
            lastSeenAt = null,
            nextAvailableAt = null,
            updatedAt = currentTime
        )
    }

    suspend fun markCharacterOffline(
        userId: String = LOCAL_USER_ID,
        characterId: String =
            InitialCharacterData.RHEA_ID,
        nextAvailableAt: Long? = null
    ) {
        val currentTime =
            System.currentTimeMillis()

        characterPresenceDao.markOffline(
            userId = userId,
            characterId = characterId,
            lastSeenAt = currentTime,
            nextAvailableAt =
                nextAvailableAt
        )
    }

    suspend fun sendUserTextMessage(
        threadId: String,
        rawText: String,
        userId: String = LOCAL_USER_ID,
        characterId: String =
            InitialCharacterData.RHEA_ID
    ): String {
        val text =
            rawText.trim()

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
                    deliveryStatus =
                        "PENDING",
                    syncStatus =
                        "LOCAL_ONLY",
                    createdAt =
                        currentTime
                )
            )

            chatThreadDao
                .updateLastMessage(
                    threadId = threadId,
                    preview = text,
                    messageTime =
                        currentTime,
                    updatedAt =
                        currentTime
                )

            relationshipStateDao
                .recordUserMessage(
                    userId = userId,
                    characterId =
                        characterId,
                    interactionAt =
                        currentTime
                )
        }

        return messageId
    }

    suspend fun prepareOutgoingMessageRetry(
        messageId: String
    ): String {
        return database.withTransaction {
            val message =
                requireNotNull(
                    messageDao.getMessage(
                        messageId =
                            messageId
                    )
                ) {
                    "The failed message could not be found."
                }

            require(
                message.senderType.equals(
                    other = "USER",
                    ignoreCase = true
                )
            ) {
                "Only user messages can be retried."
            }

            require(
                message.contentType.equals(
                    other = "TEXT",
                    ignoreCase = true
                )
            ) {
                "Only text messages can currently be retried."
            }

            require(
                message.deliveryStatus.equals(
                    other = "FAILED",
                    ignoreCase = true
                )
            ) {
                "Only failed messages can be retried."
            }

            require(
                message.deletedAt == null
            ) {
                "A deleted message cannot be retried."
            }

            val messageText =
                message.bodyText
                    ?.trim()
                    ?.takeIf {
                        it.isNotEmpty()
                    }
                    ?: throw
                    IllegalStateException(
                        "The failed message is empty."
                    )

            messageDao.markMessagePending(
                messageId = messageId
            )

            messageText
        }
    }

    suspend fun markOutgoingMessageSent(
        messageId: String
    ) {
        val currentTime =
            System.currentTimeMillis()

        messageDao.markMessageSent(
            messageId = messageId,
            serverId = messageId,
            sentAt = currentTime
        )
    }

    suspend fun markOutgoingMessageDelivered(
        messageId: String
    ) {
        messageDao.markMessageDelivered(
            messageId = messageId,
            deliveredAt =
                System.currentTimeMillis()
        )
    }

    suspend fun markUserMessagesReadByCharacter(
        threadId: String
    ) {
        messageDao
            .markUserMessagesReadByCharacter(
                threadId = threadId,
                readAt =
                    System.currentTimeMillis()
            )
    }

    suspend fun markOutgoingMessageFailed(
        messageId: String
    ) {
        messageDao.markMessageFailed(
            messageId = messageId
        )
    }

    suspend fun saveCharacterTextMessage(
        threadId: String,
        rawText: String,
        userId: String = LOCAL_USER_ID,
        characterId: String =
            InitialCharacterData.RHEA_ID
    ): String {
        val text =
            rawText.trim()

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
                    senderType =
                        "CHARACTER",
                    contentType = "TEXT",
                    bodyText = text,
                    deliveryStatus =
                        "DELIVERED",
                    syncStatus =
                        "SYNCED",
                    createdAt =
                        currentTime,
                    sentAt = currentTime,
                    deliveredAt =
                        currentTime
                )
            )

            chatThreadDao
                .updateLastMessage(
                    threadId = threadId,
                    preview = text,
                    messageTime =
                        currentTime,
                    updatedAt =
                        currentTime
                )

            chatThreadDao
                .incrementUnreadCount(
                    threadId = threadId,
                    updatedAt =
                        currentTime
                )

            relationshipStateDao
                .recordCharacterMessage(
                    userId = userId,
                    characterId =
                        characterId,
                    interactionAt =
                        currentTime
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

            chatThreadDao
                .clearUnreadCount(
                    threadId = threadId,
                    updatedAt =
                        currentTime
                )
        }
    }
}