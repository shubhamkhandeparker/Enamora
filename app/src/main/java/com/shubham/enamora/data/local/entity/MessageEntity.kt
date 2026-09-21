package com.shubham.enamora.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ChatThreadEntity::class,
            parentColumns = ["id"],
            childColumns = ["thread_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(
            value = [
                "thread_id",
                "created_at"
            ]
        ),
        Index(
            value = ["server_id"],
            unique = true
        ),
        Index(value = ["reply_to_message_id"])
    ]
)
data class MessageEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "server_id")
    val serverId: String? = null,

    @ColumnInfo(name = "thread_id")
    val threadId: String,

    @ColumnInfo(name = "sender_type")
    val senderType: String,

    @ColumnInfo(name = "content_type")
    val contentType: String = "TEXT",

    @ColumnInfo(name = "body_text")
    val bodyText: String? = null,

    @ColumnInfo(name = "media_local_uri")
    val mediaLocalUri: String? = null,

    @ColumnInfo(name = "media_remote_url")
    val mediaRemoteUrl: String? = null,

    @ColumnInfo(name = "media_duration_ms")
    val mediaDurationMs: Long? = null,

    @ColumnInfo(name = "reply_to_message_id")
    val replyToMessageId: String? = null,

    @ColumnInfo(name = "delivery_status")
    val deliveryStatus: String = "PENDING",

    @ColumnInfo(name = "sync_status")
    val syncStatus: String = "LOCAL_ONLY",

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "sent_at")
    val sentAt: Long? = null,

    @ColumnInfo(name = "delivered_at")
    val deliveredAt: Long? = null,

    @ColumnInfo(name = "read_at")
    val readAt: Long? = null,

    @ColumnInfo(name = "edited_at")
    val editedAt: Long? = null,

    @ColumnInfo(name = "deleted_at")
    val deletedAt: Long? = null
)