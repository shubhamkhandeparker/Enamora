package com.shubham.enamora.data.local.seed

import com.shubham.enamora.data.local.entity.CharacterEntity

object InitialCharacterData {

    const val RHEA_ID = "rhea_dsouza"

    fun createRhea(
        createdAt: Long,
        updatedAt: Long = createdAt
    ): CharacterEntity {
        return CharacterEntity(
            id = RHEA_ID,
            displayName = "Rhea D’Souza",
            birthDate = "1999-03-18",
            pronouns = "she/her",
            genderIdentity = "woman",
            orientation = "bisexual",
            city = "Panaji",
            region = "Goa",
            countryCode = "IN",
            occupation =
                "Floral designer and studio co-owner",
            shortBio =
                "Warm, creative and quietly adventurous.",
            profileImageKey = "rhea_profile",
            isActive = true,
            sortOrder = 0,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}