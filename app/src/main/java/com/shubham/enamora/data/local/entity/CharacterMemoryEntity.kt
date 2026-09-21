package com.shubham.enamora.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "character_memories",
    foreignKeys = [
        ForeignKey(
            entity = CharacterEntity::class,
            parentColumns = ["id"],
            childColumns = ["character_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = MessageEntity::class,
            parentColumns = ["id"],
            childColumns = ["source_message_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["character_id"]),
        Index(value = ["source_message_id"]),
        Index(
            value = [
                "user_id",
                "character_id",
                "memory_key"
            ],
            unique = true
        ),
        Index(
            value = [
                "user_id",
                "character_id",
                "is_active",
                "importance"
            ]
        )
    ]
)
data class CharacterMemoryEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "character_id")
    val characterId: String,

    @ColumnInfo(name = "source_message_id")
    val sourceMessageId: String? = null,

    @ColumnInfo(name = "memory_key")
    val memoryKey: String? = null,

    @ColumnInfo(name = "memory_type")
    val memoryType: String,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "importance")
    val importance: Int = 1,

    @ColumnInfo(name = "confidence")
    val confidence: Float = 1f,

    @ColumnInfo(name = "is_sensitive")
    val isSensitive: Boolean = false,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @ColumnInfo(name = "first_learned_at")
    val firstLearnedAt: Long,

    @ColumnInfo(name = "last_confirmed_at")
    val lastConfirmedAt: Long? = null,

    @ColumnInfo(name = "last_used_at")
    val lastUsedAt: Long? = null,

    @ColumnInfo(name = "expires_at")
    val expiresAt: Long? = null,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)