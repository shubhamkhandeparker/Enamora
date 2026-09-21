package com.shubham.enamora.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.shubham.enamora.data.local.entity.RelationshipStateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RelationshipStateDao {

    @Query(
        """
        SELECT *
        FROM relationship_states
        WHERE user_id = :userId
          AND character_id = :characterId
        LIMIT 1
        """
    )
    fun observeRelationship(
        userId: String,
        characterId: String
    ): Flow<RelationshipStateEntity?>

    @Query(
        """
        SELECT *
        FROM relationship_states
        WHERE user_id = :userId
          AND character_id = :characterId
        LIMIT 1
        """
    )
    suspend fun getRelationship(
        userId: String,
        characterId: String
    ): RelationshipStateEntity?

    @Upsert
    suspend fun upsertRelationship(
        relationship: RelationshipStateEntity
    )

    @Query(
        """
        UPDATE relationship_states
        SET trust_score =
                MAX(
                    0,
                    MIN(
                        100,
                        trust_score + :trustChange
                    )
                ),
            closeness_score =
                MAX(
                    0,
                    MIN(
                        100,
                        closeness_score +
                            :closenessChange
                    )
                ),
            romantic_interest_score =
                MAX(
                    0,
                    MIN(
                        100,
                        romantic_interest_score +
                            :romanticChange
                    )
                ),
            updated_at = :updatedAt
        WHERE user_id = :userId
          AND character_id = :characterId
        """
    )
    suspend fun updateScores(
        userId: String,
        characterId: String,
        trustChange: Int,
        closenessChange: Int,
        romanticChange: Int,
        updatedAt: Long
    )

    @Query(
        """
        UPDATE relationship_states
        SET relationship_stage = :newStage,
            stage_changed_at = :changedAt,
            updated_at = :changedAt
        WHERE user_id = :userId
          AND character_id = :characterId
        """
    )
    suspend fun updateStage(
        userId: String,
        characterId: String,
        newStage: String,
        changedAt: Long
    )

    @Query(
        """
        UPDATE relationship_states
        SET total_user_messages =
                total_user_messages + 1,
            last_interaction_at = :interactionAt,
            updated_at = :interactionAt
        WHERE user_id = :userId
          AND character_id = :characterId
        """
    )
    suspend fun recordUserMessage(
        userId: String,
        characterId: String,
        interactionAt: Long
    )

    @Query(
        """
        UPDATE relationship_states
        SET total_character_messages =
                total_character_messages + 1,
            last_interaction_at = :interactionAt,
            updated_at = :interactionAt
        WHERE user_id = :userId
          AND character_id = :characterId
        """
    )
    suspend fun recordCharacterMessage(
        userId: String,
        characterId: String,
        interactionAt: Long
    )

    @Query(
        """
        UPDATE relationship_states
        SET interaction_days = :interactionDays,
            updated_at = :updatedAt
        WHERE user_id = :userId
          AND character_id = :characterId
        """
    )
    suspend fun updateInteractionDays(
        userId: String,
        characterId: String,
        interactionDays: Int,
        updatedAt: Long
    )
}