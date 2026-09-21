package com.shubham.enamora.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shubham.enamora.data.local.dao.CharacterDao
import com.shubham.enamora.data.local.dao.CharacterMemoryDao
import com.shubham.enamora.data.local.dao.CharacterPresenceDao
import com.shubham.enamora.data.local.dao.ChatThreadDao
import com.shubham.enamora.data.local.dao.MessageDao
import com.shubham.enamora.data.local.dao.RelationshipStateDao
import com.shubham.enamora.data.local.entity.CharacterEntity
import com.shubham.enamora.data.local.entity.CharacterMemoryEntity
import com.shubham.enamora.data.local.entity.CharacterPresenceEntity
import com.shubham.enamora.data.local.entity.ChatThreadEntity
import com.shubham.enamora.data.local.entity.MessageEntity
import com.shubham.enamora.data.local.entity.RelationshipStateEntity

@Database(
    entities = [
        CharacterEntity::class,
        ChatThreadEntity::class,
        MessageEntity::class,
        RelationshipStateEntity::class,
        CharacterMemoryEntity::class,
        CharacterPresenceEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class EnamoraDatabase : RoomDatabase() {

    abstract fun characterDao(): CharacterDao

    abstract fun chatThreadDao(): ChatThreadDao

    abstract fun messageDao(): MessageDao

    abstract fun relationshipStateDao():
            RelationshipStateDao

    abstract fun characterMemoryDao():
            CharacterMemoryDao

    abstract fun characterPresenceDao():
            CharacterPresenceDao
}