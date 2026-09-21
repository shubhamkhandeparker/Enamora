package com.shubham.enamora.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.shubham.enamora.data.local.entity.CharacterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {

    @Query(
        """
        SELECT *
        FROM characters
        WHERE is_active = 1
        ORDER BY sort_order ASC, display_name ASC
        """
    )
    fun observeActiveCharacters():
            Flow<List<CharacterEntity>>

    @Query(
        """
        SELECT *
        FROM characters
        WHERE id = :characterId
        LIMIT 1
        """
    )
    fun observeCharacter(
        characterId: String
    ): Flow<CharacterEntity?>

    @Query(
        """
        SELECT *
        FROM characters
        WHERE id = :characterId
        LIMIT 1
        """
    )
    suspend fun getCharacter(
        characterId: String
    ): CharacterEntity?

    @Upsert
    suspend fun upsertCharacter(
        character: CharacterEntity
    )

    @Upsert
    suspend fun upsertCharacters(
        characters: List<CharacterEntity>
    )

    @Query(
        """
        UPDATE characters
        SET is_active = :isActive,
            updated_at = :updatedAt
        WHERE id = :characterId
        """
    )
    suspend fun updateActiveState(
        characterId: String,
        isActive: Boolean,
        updatedAt: Long
    )

    @Query("SELECT COUNT(*) FROM characters")
    suspend fun getCharacterCount(): Int
}