package com.shubham.enamora.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "character_presence",
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
data class CharacterPresenceEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "character_id")
    val characterId: String,

    @ColumnInfo(name = "availability_status")
    val availabilityStatus: String = "OFFLINE",

    @ColumnInfo(name = "activity_context")
    val activityContext: String? = null,

    @ColumnInfo(name = "timezone_id")
    val timezoneId: String,

    @ColumnInfo(name = "last_seen_at")
    val lastSeenAt: Long? = null,

    @ColumnInfo(name = "next_available_at")
    val nextAvailableAt: Long? = null,

    @ColumnInfo(name = "status_updated_at")
    val statusUpdatedAt: Long
)