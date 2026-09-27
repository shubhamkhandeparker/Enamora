package com.shubham.enamora.domain.chat

enum class CharacterMood {
    NEUTRAL,
    WARM,
    PLAYFUL,
    CURIOUS,
    EXCITED,
    THOUGHTFUL,
    TIRED,
    ANNOYED,
    HURT
}

enum class CharacterMoodIntensity {
    LOW,
    MEDIUM,
    HIGH
}

data class CharacterMoodState(
    val mood: CharacterMood =
        CharacterMood.NEUTRAL,
    val intensity: CharacterMoodIntensity =
        CharacterMoodIntensity.LOW,
    val remainingTurns: Int = 0,
    val reason: String = "",
    val updatedAtEpochMillis: Long = 0L
)