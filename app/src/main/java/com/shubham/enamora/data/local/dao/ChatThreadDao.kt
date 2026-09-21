package com.shubham.enamora.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.shubham.enamora.data.local.entity.ChatThreadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatThreadDao {

    @Query(
        """
        SELECT *
        FROM chat_threads
        WHERE user_id = :userId
          AND is_archived = 0
        ORDER BY is_pinned DESC, updated_at DESC
        """
    )
    fun observeThreads(
        userId: String
    ): Flow<List<ChatThreadEntity>>

    @Query(
        """
        SELECT *
        FROM chat_threads
        WHERE id = :threadId
        LIMIT 1
        """
    )
    fun observeThread(
        threadId: String
    ): Flow<ChatThreadEntity?>

    @Query(
        """
        SELECT *
        FROM chat_threads
        WHERE user_id = :userId
          AND character_id = :characterId
        LIMIT 1
        """
    )
    suspend fun getThreadForCharacter(
        userId: String,
        characterId: String
    ): ChatThreadEntity?

    @Upsert
    suspend fun upsertThread(
        thread: ChatThreadEntity
    )

    @Query(
        """
        UPDATE chat_threads
        SET last_message_preview = :preview,
            last_message_at = :messageTime,
            updated_at = :updatedAt
        WHERE id = :threadId
        """
    )
    suspend fun updateLastMessage(
        threadId: String,
        preview: String?,
        messageTime: Long?,
        updatedAt: Long
    )

    @Query(
        """
        UPDATE chat_threads
        SET unread_count = unread_count + 1,
            updated_at = :updatedAt
        WHERE id = :threadId
        """
    )
    suspend fun incrementUnreadCount(
        threadId: String,
        updatedAt: Long
    )

    @Query(
        """
        UPDATE chat_threads
        SET unread_count = 0,
            updated_at = :updatedAt
        WHERE id = :threadId
        """
    )
    suspend fun clearUnreadCount(
        threadId: String,
        updatedAt: Long
    )

    @Query(
        """
        UPDATE chat_threads
        SET is_pinned = :isPinned,
            updated_at = :updatedAt
        WHERE id = :threadId
        """
    )
    suspend fun updatePinnedState(
        threadId: String,
        isPinned: Boolean,
        updatedAt: Long
    )

    @Query(
        """
        DELETE FROM chat_threads
        WHERE id = :threadId
        """
    )
    suspend fun deleteThread(
        threadId: String
    )
}