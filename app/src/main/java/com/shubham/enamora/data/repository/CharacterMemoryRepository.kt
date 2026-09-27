package com.shubham.enamora.data.repository

import androidx.room.withTransaction
import com.shubham.enamora.data.local.database.EnamoraDatabase
import com.shubham.enamora.data.local.entity.CharacterMemoryEntity
import com.shubham.enamora.data.local.seed.InitialCharacterData
import java.util.Locale
import java.util.UUID

class CharacterMemoryRepository(
    private val database: EnamoraDatabase
) {
    companion object {
        const val DEFAULT_CONTEXT_LIMIT = 8
        const val MAX_CONTEXT_LIMIT = 20
        const val DEFAULT_MINIMUM_CONFIDENCE = 0.6f
    }

    private val characterMemoryDao =
        database.characterMemoryDao()

    suspend fun getMemoriesForContext(
        userId: String =
            ChatRepository.LOCAL_USER_ID,
        characterId: String =
            InitialCharacterData.RHEA_ID,
        includeSensitive: Boolean = false,
        minimumConfidence: Float =
            DEFAULT_MINIMUM_CONFIDENCE,
        limit: Int =
            DEFAULT_CONTEXT_LIMIT
    ): List<CharacterMemoryEntity> {
        require(
            limit in 1..MAX_CONTEXT_LIMIT
        ) {
            "Memory limit must be between 1 and $MAX_CONTEXT_LIMIT."
        }

        require(
            minimumConfidence in 0f..1f
        ) {
            "Memory confidence must be between 0 and 1."
        }

        return characterMemoryDao
            .getMemoriesForContext(
                userId = userId,
                characterId = characterId,
                currentTime =
                    System.currentTimeMillis(),
                minimumConfidence =
                    minimumConfidence,
                includeSensitive =
                    includeSensitive,
                limit = limit
            )
    }

    suspend fun saveOrUpdateMemory(
        memoryKey: String,
        memoryType: String,
        content: String,
        importance: Int = 1,
        confidence: Float = 1f,
        isSensitive: Boolean = false,
        sourceMessageId: String? = null,
        expiresAt: Long? = null,
        userId: String =
            ChatRepository.LOCAL_USER_ID,
        characterId: String =
            InitialCharacterData.RHEA_ID
    ): String {
        val cleanMemoryKey =
            memoryKey.trim()

        val cleanMemoryType =
            memoryType
                .trim()
                .uppercase(Locale.ROOT)

        val cleanContent =
            content.trim()

        require(cleanMemoryKey.isNotEmpty()) {
            "Memory key cannot be empty."
        }

        require(cleanMemoryType.isNotEmpty()) {
            "Memory type cannot be empty."
        }

        require(cleanContent.isNotEmpty()) {
            "Memory content cannot be empty."
        }

        require(importance in 1..5) {
            "Memory importance must be between 1 and 5."
        }

        require(confidence in 0f..1f) {
            "Memory confidence must be between 0 and 1."
        }

        val currentTime =
            System.currentTimeMillis()

        return database.withTransaction {
            val existingMemory =
                characterMemoryDao
                    .getMemoryByKey(
                        userId = userId,
                        characterId = characterId,
                        memoryKey = cleanMemoryKey
                    )

            val memoryId =
                existingMemory?.id
                    ?: UUID.randomUUID().toString()

            characterMemoryDao.upsertMemory(
                CharacterMemoryEntity(
                    id = memoryId,
                    userId = userId,
                    characterId = characterId,
                    sourceMessageId =
                        sourceMessageId
                            ?: existingMemory
                                ?.sourceMessageId,
                    memoryKey = cleanMemoryKey,
                    memoryType = cleanMemoryType,
                    content = cleanContent,
                    importance =
                        maxOf(
                            importance,
                            existingMemory
                                ?.importance
                                ?: 1
                        ),
                    confidence =
                        maxOf(
                            confidence,
                            existingMemory
                                ?.confidence
                                ?: 0f
                        ),
                    isSensitive =
                        isSensitive ||
                                existingMemory
                                    ?.isSensitive ==
                                true,
                    isActive = true,
                    firstLearnedAt =
                        existingMemory
                            ?.firstLearnedAt
                            ?: currentTime,
                    lastConfirmedAt =
                        currentTime,
                    lastUsedAt =
                        existingMemory
                            ?.lastUsedAt,
                    expiresAt =
                        expiresAt
                            ?: existingMemory
                                ?.expiresAt,
                    updatedAt = currentTime
                )
            )

            memoryId
        }
    }

    suspend fun markMemoriesUsed(
        memories: List<CharacterMemoryEntity>
    ) {
        if (memories.isEmpty()) {
            return
        }

        val currentTime =
            System.currentTimeMillis()

        database.withTransaction {
            memories.forEach { memory ->
                characterMemoryDao.markMemoryUsed(
                    memoryId = memory.id,
                    usedAt = currentTime
                )
            }
        }
    }
}