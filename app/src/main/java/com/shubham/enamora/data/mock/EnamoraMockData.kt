package com.shubham.enamora.data.mock

import com.shubham.enamora.R
import com.shubham.enamora.ui.model.CharacterPromptUiModel
import com.shubham.enamora.ui.model.CharacterUiModel
import com.shubham.enamora.ui.model.ChatMessageUiModel
import com.shubham.enamora.ui.model.ChatThreadUiModel
import com.shubham.enamora.ui.model.ImportantPersonUiModel
import com.shubham.enamora.ui.model.SharedMemoryUiModel

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
            recentMessagePreview =
                "I saved something to tell you when you got back...",
            tagline = "Warm • witty • quietly adventurous",
            interests = listOf(
                "Photography",
                "Cooking",
                "Long drives"
            ),
            prompts = listOf(
                CharacterPromptUiModel(
                    question = "My perfect slow Sunday...",
                    answer =
                        "Coffee, rain, and a playlist worth sharing."
                )
            ),
            photoResIds = listOf(
                R.drawable.character_demo_01
            ),
            personality =
                "Warm, observant, opinionated, and quietly curious.",
            occupation =
                "Works at a neighbourhood flower studio.",
            dailyLife =
                "She enjoys calm mornings, evening walks, and discovering small weekend cafés.",
            currentActivity =
                "Learning to make tiramisu.",
            importantPeople = listOf(
                ImportantPersonUiModel(
                    id = "anaya",
                    name = "Anaya",
                    relationship = "Best friend"
                ),
                ImportantPersonUiModel(
                    id = "karan",
                    name = "Karan",
                    relationship = "College friend"
                ),
                ImportantPersonUiModel(
                    id = "meera",
                    name = "Meera",
                    relationship = "Studio partner"
                )
            ),
            sharedMemories = listOf(
                SharedMemoryUiModel(
                    id = "seaside_evening",
                    title = "Evening by the sea",
                    description =
                        "The sunset that kept us talking longer than planned."
                ),
                SharedMemoryUiModel(
                    id = "rainy_cafe",
                    title = "Rainy café afternoon",
                    description =
                        "Coffee, soft music, and an unexpectedly honest conversation."
                ),
                SharedMemoryUiModel(
                    id = "flower_market",
                    title = "The flower market",
                    description =
                        "The morning Rhea showed you her favourite corner of the city."
                )
            ),
            conversationPreferences = listOf(
                "Thoughtful everyday conversations",
                "Gentle humour and playful teasing",
                "Honest, unhurried replies"
            )
        )
    )

    val activeCharacter: CharacterUiModel =
        characters.first()

    val chatThreads = listOf(
        ChatThreadUiModel(
            character = activeCharacter
        )
    )

    private val conversationsByCharacterId:
            Map<String, List<ChatMessageUiModel>> =
        emptyMap()

    private val typingCharacterIds:
            Set<String> =
        emptySet()

    fun messagesForCharacter(
        characterId: String
    ): List<ChatMessageUiModel> {
        return conversationsByCharacterId[
            characterId
        ].orEmpty()
    }

    fun isCharacterTyping(
        characterId: String
    ): Boolean {
        return characterId in typingCharacterIds
    }

    const val latestVoiceNoteDuration = "0:18"

    const val sharedMemoryTitle =
        "That beautiful evening by the sea"
}