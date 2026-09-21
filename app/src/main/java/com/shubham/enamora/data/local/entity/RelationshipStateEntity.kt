package com.shubham.enamora.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "relationship_states",
    foreignKeys = [
        ForeignKey(
            entity = CharacterEntity::class,
            parentColumns = ["id"],
            childColumns = ["character_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["character_id"]),
        Index(
            value = [
                "user_id",
                "character_id"
            ],
            unique = true
        )
    ]
)
data class RelationshipStateEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "character_id")
    val characterId: String,

    @ColumnInfo(name = "relationship_stage")
    val relationshipStage: String = "NEW_CONNECTION",

    @ColumnInfo(name = "trust_score")
    val trustScore: Int = 0,

    @ColumnInfo(name = "closeness_score")
    val closenessScore: Int = 0,

    @ColumnInfo(name = "romantic_interest_score")
    val romanticInterestScore: Int = 0,

    @ColumnInfo(name = "total_user_messages")
    val totalUserMessages: Int = 0,

    @ColumnInfo(name = "total_character_messages")
    val totalCharacterMessages: Int = 0,

    @ColumnInfo(name = "interaction_days")
    val interactionDays: Int = 0,

    @ColumnInfo(name = "first_connected_at")
    val firstConnectedAt: Long,

    @ColumnInfo(name = "last_interaction_at")
    val lastInteractionAt: Long? = null,

    @ColumnInfo(name = "stage_changed_at")
    val stageChangedAt: Long,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)