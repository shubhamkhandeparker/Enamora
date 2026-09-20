package com.shubham.enamora.ui.model

import androidx.annotation.DrawableRes

data class CharacterPromptUiModel(
    val question: String,
    val answer: String
)

data class ImportantPersonUiModel(
    val id: String,
    val name: String,
    val relationship: String,
    @DrawableRes val photoResId: Int? = null
)

data class SharedMemoryUiModel(
    val id: String,
    val title: String,
    val description: String,
    @DrawableRes val imageResId: Int? = null
)

data class CharacterUiModel(
    val id: String,
    val name: String,
    val age: Int,
    @DrawableRes val portraitResId: Int,
    val isAvailable: Boolean,
    val availabilityText: String,
    val recentMessagePreview: String,
    val tagline: String,
    val interests: List<String>,
    val prompts: List<CharacterPromptUiModel> = emptyList(),
    val photoResIds: List<Int> = listOf(portraitResId),
    val personality: String = "",
    val occupation: String = "",
    val dailyLife: String = "",
    val currentActivity: String = "",
    val importantPeople: List<ImportantPersonUiModel> = emptyList(),
    val sharedMemories: List<SharedMemoryUiModel> = emptyList(),
    val conversationPreferences: List<String> = emptyList()
) {
    init {
        require(id.isNotBlank()) {
            "Every Enamora character must have an ID."
        }

        require(name.isNotBlank()) {
            "Every Enamora character must have a name."
        }

        require(age >= 18) {
            "Every Enamora character must be an adult."
        }

        require(photoResIds.isNotEmpty()) {
            "Every Enamora character must have at least one photograph."
        }
    }
}