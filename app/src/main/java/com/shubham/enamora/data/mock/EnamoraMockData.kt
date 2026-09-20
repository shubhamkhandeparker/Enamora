package com.shubham.enamora.data.mock

import com.shubham.enamora.R
import com.shubham.enamora.ui.model.CharacterUiModel

object EnamoraMockData {

    const val currentUserName = "User"

    val characters = listOf(
        CharacterUiModel(
            id = "character_demo_01",
            name = "Rhea",
            age = 27,
            portraitResId = R.drawable.character_demo_01,
            isAvailable = true,
            availabilityText = "Rhea is around",
            recentMessagePreview = "I saved something to tell you when you got back...",
            tagline = "Warm • witty • quietly adventurous",
            interests = listOf(
                "Photography",
                "Cooking",
                "Long drives"
            )
        )
    )

    val activeCharacter: CharacterUiModel = characters.first()

    const val latestVoiceNoteDuration = "0:18"
    const val sharedMemoryTitle = "That beautiful evening by the sea"
}