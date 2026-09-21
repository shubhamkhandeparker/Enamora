package com.shubham.enamora.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.shubham.enamora.data.local.entity.CharacterPresenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterPresenceDao {

    @Query(
        """
        SELECT *
        FROM character_presence
        WHERE user_id = :userId
          AND character_id = :characterId
        LIMIT 1
        """
    )
    fun observePresence(
        userId: String,
        characterId: String
    ): Flow<CharacterPresenceEntity?>

    @Query(
        """
        SELECT *
        FROM character_presence
        WHERE user_id = :userId
          AND character_id = :characterId
        LIMIT 1
        """
    )
    suspend fun getPresence(
        userId: String,
        characterId: String
    ): CharacterPresenceEntity?

    @Upsert
    suspend fun upsertPresence(
        presence: CharacterPresenceEntity
    )

    @Query(
        """
        UPDATE character_presence
        SET availability_status = :status,
            activity_context = :activityContext,
            last_seen_at = :lastSeenAt,
            next_available_at = :nextAvailableAt,
            status_updated_at = :updatedAt
        WHERE user_id = :userId
          AND character_id = :characterId
        """
    )
    suspend fun updatePresence(
        userId: String,
        characterId: String,
        status: String,
        activityContext: String?,
        lastSeenAt: Long?,
        nextAvailableAt: Long?,
        updatedAt: Long
    )

    @Query(
        """
        UPDATE character_presence
        SET availability_status = 'OFFLINE',
            activity_context = NULL,
            last_seen_at = :lastSeenAt,
            next_available_at = :nextAvailableAt,
            status_updated_at = :lastSeenAt
        WHERE user_id = :userId
          AND character_id = :characterId
        """
    )
    suspend fun markOffline(
        userId: String,
        characterId: String,
        lastSeenAt: Long,
        nextAvailableAt: Long?
    )
}