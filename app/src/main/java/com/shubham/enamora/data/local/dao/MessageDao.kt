package com.shubham.enamora.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.shubham.enamora.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Query(
        """
        SELECT *
        FROM messages
        WHERE thread_id = :threadId
          AND deleted_at IS NULL
        ORDER BY created_at ASC, id ASC
        """
    )
    fun observeMessages(
        threadId: String
    ): Flow<List<MessageEntity>>

    @Query(
        """
        SELECT *
        FROM messages
        WHERE id = :messageId
        LIMIT 1
        """
    )
    suspend fun getMessage(
        messageId: String
    ): MessageEntity?

    @Query(
        """
        SELECT *
        FROM (
            SELECT *
            FROM messages
            WHERE thread_id = :threadId
              AND deleted_at IS NULL
            ORDER BY created_at DESC, id DESC
            LIMIT :limit
        )
        ORDER BY created_at ASC, id ASC
        """
    )
    suspend fun getRecentMessages(
        threadId: String,
        limit: Int
    ): List<MessageEntity>

    @Query(
        """
        SELECT *
        FROM messages
        WHERE sync_status IN (
            'LOCAL_ONLY',
            'FAILED'
        )
          AND deleted_at IS NULL
        ORDER BY created_at ASC
        """
    )
    suspend fun getMessagesWaitingForSync():
            List<MessageEntity>

    @Upsert
    suspend fun upsertMessage(
        message: MessageEntity
    )

    @Upsert
    suspend fun upsertMessages(
        messages: List<MessageEntity>
    )

    @Query(
        """
        UPDATE messages
        SET delivery_status = 'PENDING',
            sync_status = 'LOCAL_ONLY',
            server_id = NULL,
            sent_at = NULL,
            delivered_at = NULL,
            read_at = NULL
        WHERE id = :messageId
          AND sender_type = 'USER'
          AND deleted_at IS NULL
        """
    )
    suspend fun markMessagePending(
        messageId: String
    )

    @Query(
        """
        UPDATE messages
        SET delivery_status = 'SENT',
            sync_status = 'SYNCED',
            server_id = :serverId,
            sent_at = :sentAt
        WHERE id = :messageId
        """
    )
    suspend fun markMessageSent(
        messageId: String,
        serverId: String,
        sentAt: Long
    )

    @Query(
        """
        UPDATE messages
        SET delivery_status = 'DELIVERED',
            delivered_at = :deliveredAt
        WHERE id = :messageId
        """
    )
    suspend fun markMessageDelivered(
        messageId: String,
        deliveredAt: Long
    )

    @Query(
        """
        UPDATE messages
        SET delivery_status = 'READ',
            read_at = :readAt
        WHERE id = :messageId
        """
    )
    suspend fun markMessageRead(
        messageId: String,
        readAt: Long
    )

    @Query(
        """
        UPDATE messages
        SET delivery_status = 'READ',
            read_at = :readAt
        WHERE thread_id = :threadId
          AND sender_type = 'CHARACTER'
          AND read_at IS NULL
          AND deleted_at IS NULL
        """
    )
    suspend fun markCharacterMessagesReadByUser(
        threadId: String,
        readAt: Long
    )

    @Query(
        """
        UPDATE messages
        SET delivery_status = 'READ',
            read_at = :readAt
        WHERE thread_id = :threadId
          AND sender_type = 'USER'
          AND read_at IS NULL
          AND deleted_at IS NULL
        """
    )
    suspend fun markUserMessagesReadByCharacter(
        threadId: String,
        readAt: Long
    )

    @Query(
        """
        UPDATE messages
        SET delivery_status = 'FAILED',
            sync_status = 'FAILED'
        WHERE id = :messageId
        """
    )
    suspend fun markMessageFailed(
        messageId: String
    )

    @Query(
        """
        UPDATE messages
        SET sync_status = 'SYNCING'
        WHERE id = :messageId
        """
    )
    suspend fun markMessageSyncing(
        messageId: String
    )

    @Query(
        """
        UPDATE messages
        SET deleted_at = :deletedAt,
            body_text = NULL,
            media_local_uri = NULL,
            media_remote_url = NULL
        WHERE id = :messageId
        """
    )
    suspend fun softDeleteMessage(
        messageId: String,
        deletedAt: Long
    )
}