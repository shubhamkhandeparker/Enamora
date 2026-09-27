package com.shubham.enamora.domain.chat

import java.util.Locale

object RheaMoodEngine {

    fun nextState(
        messageText: String,
        currentState: CharacterMoodState,
        nowEpochMillis: Long =
            System.currentTimeMillis()
    ): CharacterMoodState {
        val normalizedMessage =
            messageText
                .lowercase(Locale.ROOT)
                .replace(
                    Regex("[^\\p{L}\\p{N}\\s']"),
                    " "
                )
                .replace(
                    Regex("\\s+"),
                    " "
                )
                .trim()

        if (normalizedMessage.isBlank()) {
            return decayState(
                currentState = currentState,
                nowEpochMillis = nowEpochMillis
            )
        }

        if (
            containsAny(
                normalizedMessage,
                insultSignals
            )
        ) {
            return createState(
                mood = CharacterMood.ANNOYED,
                intensity =
                    CharacterMoodIntensity.HIGH,
                remainingTurns = 2,
                reason = "USER_WAS_DISRESPECTFUL",
                nowEpochMillis = nowEpochMillis
            )
        }

        if (
            currentState.mood in
            setOf(
                CharacterMood.ANNOYED,
                CharacterMood.HURT
            ) &&
            containsAny(
                normalizedMessage,
                apologySignals
            )
        ) {
            return createState(
                mood = CharacterMood.THOUGHTFUL,
                intensity =
                    CharacterMoodIntensity.LOW,
                remainingTurns = 1,
                reason = "USER_APOLOGISED",
                nowEpochMillis = nowEpochMillis
            )
        }

        if (
            containsAny(
                normalizedMessage,
                excitementSignals
            ) ||
            containsExcitedEmoji(
                messageText
            )
        ) {
            return createState(
                mood = CharacterMood.EXCITED,
                intensity =
                    CharacterMoodIntensity.HIGH,
                remainingTurns = 2,
                reason = "USER_SHARED_EXCITING_NEWS",
                nowEpochMillis = nowEpochMillis
            )
        }

        if (
            containsAny(
                normalizedMessage,
                emotionalSupportSignals
            )
        ) {
            return createState(
                mood = CharacterMood.WARM,
                intensity =
                    CharacterMoodIntensity.MEDIUM,
                remainingTurns = 2,
                reason = "USER_NEEDS_WARMTH",
                nowEpochMillis = nowEpochMillis
            )
        }

        if (
            containsAny(
                normalizedMessage,
                playfulSignals
            ) ||
            containsPlayfulEmoji(
                messageText
            )
        ) {
            return createState(
                mood = CharacterMood.PLAYFUL,
                intensity =
                    CharacterMoodIntensity.MEDIUM,
                remainingTurns = 2,
                reason = "PLAYFUL_CONVERSATION",
                nowEpochMillis = nowEpochMillis
            )
        }

        if (
            containsAny(
                normalizedMessage,
                thoughtfulSignals
            )
        ) {
            return createState(
                mood = CharacterMood.THOUGHTFUL,
                intensity =
                    CharacterMoodIntensity.MEDIUM,
                remainingTurns = 2,
                reason = "USER_REQUESTED_AN_OPINION",
                nowEpochMillis = nowEpochMillis
            )
        }

        if (
            messageText.trim()
                .endsWith("?")
        ) {
            return createState(
                mood = CharacterMood.CURIOUS,
                intensity =
                    CharacterMoodIntensity.LOW,
                remainingTurns = 1,
                reason = "USER_ASKED_A_QUESTION",
                nowEpochMillis = nowEpochMillis
            )
        }

        return decayState(
            currentState = currentState,
            nowEpochMillis = nowEpochMillis
        )
    }

    private fun decayState(
        currentState: CharacterMoodState,
        nowEpochMillis: Long
    ): CharacterMoodState {
        if (
            currentState.remainingTurns > 1
        ) {
            return currentState.copy(
                remainingTurns =
                    currentState.remainingTurns - 1,
                updatedAtEpochMillis =
                    nowEpochMillis
            )
        }

        return CharacterMoodState(
            mood = CharacterMood.NEUTRAL,
            intensity =
                CharacterMoodIntensity.LOW,
            remainingTurns = 0,
            reason = "MOOD_SETTLED",
            updatedAtEpochMillis =
                nowEpochMillis
        )
    }

    private fun createState(
        mood: CharacterMood,
        intensity:
        CharacterMoodIntensity,
        remainingTurns: Int,
        reason: String,
        nowEpochMillis: Long
    ): CharacterMoodState {
        return CharacterMoodState(
            mood = mood,
            intensity = intensity,
            remainingTurns =
                remainingTurns,
            reason = reason,
            updatedAtEpochMillis =
                nowEpochMillis
        )
    }

    private fun containsAny(
        message: String,
        signals: List<String>
    ): Boolean {
        return signals.any { signal ->
            message.contains(signal)
        }
    }

    private fun containsExcitedEmoji(
        message: String
    ): Boolean {
        return excitedEmojis.any {
                emoji ->
            message.contains(emoji)
        }
    }

    private fun containsPlayfulEmoji(
        message: String
    ): Boolean {
        return playfulEmojis.any {
                emoji ->
            message.contains(emoji)
        }
    }

    private val insultSignals =
        listOf(
            "fuck off",
            "shut up",
            "get lost",
            "idiot",
            "stupid",
            "useless",
            "bakwas",
            "chup",
            "pagal hai kya"
        )

    private val apologySignals =
        listOf(
            "sorry",
            "my bad",
            "i apologise",
            "i apologize",
            "maaf",
            "galti ho gayi",
            "gussa mat ho"
        )

    private val excitementSignals =
        listOf(
            "good news",
            "great news",
            "i did it",
            "finally did it",
            "i won",
            "selected",
            "success",
            "promotion",
            "ho gaya",
            "ho gayi",
            "mil gaya",
            "mil gayi",
            "kar liya",
            "kar li"
        )

    private val emotionalSupportSignals =
        listOf(
            "tired",
            "exhausted",
            "sad",
            "upset",
            "annoyed",
            "stressed",
            "stress",
            "bad day",
            "mistake",
            "lonely",
            "crying",
            "hurt",
            "galti",
            "gussa",
            "thak gaya",
            "thak gayi",
            "dukhi",
            "udaas",
            "akela",
            "akeli"
        )

    private val playfulSignals =
        listOf(
            "haha",
            "hahaha",
            "lol",
            "lmao",
            "just kidding",
            "i am joking",
            "i'm joking",
            "mazaak",
            "mazak",
            "masti"
        )

    private val thoughtfulSignals =
        listOf(
            "what do you think",
            "your opinion",
            "tumhe kya lagta",
            "kya lagta hai",
            "love",
            "relationship",
            "future",
            "life",
            "trust",
            "pyaar",
            "rishta",
            "zindagi"
        )

    private val excitedEmojis =
        listOf(
            "🎉",
            "🥳",
            "🔥",
            "😍",
            "🤩"
        )

    private val playfulEmojis =
        listOf(
            "😂",
            "🤣",
            "😅",
            "😜",
            "👀"
        )
}