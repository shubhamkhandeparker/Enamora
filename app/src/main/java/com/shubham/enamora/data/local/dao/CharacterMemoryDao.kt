package com.shubham.enamora.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.shubham.enamora.data.local.entity.CharacterMemoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterMemoryDao {

    @Query(
        """
        SELECT *
        FROM character_memories
        WHERE user_id = :userId
          AND character_id = :characterId
          AND is_active = 1
          AND (
              expires_at IS NULL
              OR expires_at > :currentTime
          )
        ORDER BY importance DESC,
                 COALESCE(
                     last_confirmed_at,
                     first_learned_at
                 ) DESC
        """
    )
    fun observeActiveMemories(
        userId: String,
        characterId: String,
        currentTime: Long
    ): Flow<List<CharacterMemoryEntity>>

    @Query(
        """
        SELECT *
        FROM character_memories
        WHERE user_id = :userId
          AND character_id = :characterId
          AND is_active = 1
          AND (
              expires_at IS NULL
              OR expires_at > :currentTime
          )
        ORDER BY importance DESC,
                 COALESCE(
                     last_used_at,
                     last_confirmed_at,
                     first_learned_at
                 ) DESC
        LIMIT :limit
        """
    )
    suspend fun getRelevantMemories(
        userId: String,
        characterId: String,
        currentTime: Long,
        limit: Int
    ): List<CharacterMemoryEntity>

    @Query(
        """
        SELECT *
        FROM character_memories
        WHERE user_id = :userId
          AND character_id = :characterId
          AND memory_key = :memoryKey
        LIMIT 1
        """
    )
    suspend fun getMemoryByKey(
        userId: String,
        characterId: String,
        memoryKey: String
    ): CharacterMemoryEntity?

    @Upsert
    suspend fun upsertMemory(
        memory: CharacterMemoryEntity
    )

    @Upsert
    suspend fun upsertMemories(
        memories: List<CharacterMemoryEntity>
    )

    @Query(
        """
        UPDATE character_memories
        SET last_used_at = :usedAt,
            updated_at = :usedAt
        WHERE id = :memoryId
        """
    )
    suspend fun markMemoryUsed(
        memoryId: String,
        usedAt: Long
    )

    @Query(
        """
        UPDATE character_memories
        SET is_active = 0,
            updated_at = :updatedAt
        WHERE id = :memoryId
        """
    )
    suspend fun deactivateMemory(
        memoryId: String,
        updatedAt: Long
    )

    @Query(
        """
        DELETE FROM character_memories
        WHERE expires_at IS NOT NULL
          AND expires_at <= :currentTime
        """
    )
    suspend fun deleteExpiredMemories(
        currentTime: Long
    )

    @Query(
        """
        DELETE FROM character_memories
        WHERE user_id = :userId
          AND character_id = :characterId
        """
    )
    suspend fun deleteCharacterMemoriesForUser(
        userId: String,
        characterId: String
    )
}