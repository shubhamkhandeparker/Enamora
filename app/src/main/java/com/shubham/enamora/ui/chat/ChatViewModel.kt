package com.shubham.enamora.ui.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.shubham.enamora.data.local.seed.InitialCharacterData
import com.shubham.enamora.data.reply.LocalRheaReplyGenerator
import com.shubham.enamora.data.repository.ChatRepository
import com.shubham.enamora.data.repository.ConversationContextRepository
import com.shubham.enamora.domain.chat.CharacterMood
import com.shubham.enamora.domain.chat.CharacterMoodIntensity
import com.shubham.enamora.domain.chat.CharacterMoodState
import com.shubham.enamora.domain.chat.CharacterReplyGenerator
import com.shubham.enamora.domain.chat.CharacterReplyRequest
import com.shubham.enamora.domain.chat.RheaMoodEngine
import com.shubham.enamora.ui.mapper.toChatMessageUiModel
import com.shubham.enamora.ui.model.ChatMessageUiModel
import java.util.Locale
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random

data class ChatUiState(
    val isLoading: Boolean = true,
    val isSending: Boolean = false,
    val isCharacterOnline: Boolean = false,
    val isCharacterTyping: Boolean = false,
    val characterLastSeenAt: Long? = null,
    val threadId: String? = null,
    val messages: List<ChatMessageUiModel> =
        emptyList(),
    val errorMessage: String? = null
)

class ChatViewModel(
    private val chatRepository: ChatRepository,
    private val characterReplyGenerator:
    CharacterReplyGenerator,
    private val conversationContextRepository:
    ConversationContextRepository? = null
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(ChatUiState())

    val uiState: StateFlow<ChatUiState> =
        _uiState.asStateFlow()

    private var characterMoodState =
        CharacterMoodState()

    private val retryingMessageIds =
        mutableSetOf<String>()

    private val outgoingMessageMutex =
        Mutex()

    private var replyGeneration = 0L

    private var characterOfflineJob: Job? =
        null

    init {
        prepareConversation()
    }

    private fun prepareConversation() {
        viewModelScope.launch {
            try {
                val threadId =
                    chatRepository
                        .prepareRheaConversation()

                _uiState.update { currentState ->
                    currentState.copy(
                        threadId = threadId,
                        errorMessage = null
                    )
                }

                chatRepository.markThreadRead(
                    threadId = threadId
                )

                combine(
                    chatRepository
                        .observeMessages(threadId),
                    chatRepository
                        .observeCharacterPresence()
                ) { storedMessages, presence ->
                    Pair(
                        storedMessages,
                        presence
                    )
                }.collect {
                        (storedMessages, presence) ->

                    val uiMessages =
                        storedMessages.mapNotNull {
                                storedMessage ->
                            storedMessage
                                .toChatMessageUiModel()
                        }

                    val presenceStatus =
                        presence
                            ?.availabilityStatus
                            ?.uppercase(
                                Locale.ROOT
                            )
                            ?: ChatRepository
                                .PRESENCE_STATUS_OFFLINE

                    val isCharacterTyping =
                        presenceStatus ==
                                ChatRepository
                                    .PRESENCE_STATUS_TYPING

                    val isCharacterOnline =
                        presenceStatus ==
                                ChatRepository
                                    .PRESENCE_STATUS_ONLINE ||
                                isCharacterTyping

                    val latestCharacterMessageAt =
                        storedMessages
                            .lastOrNull {
                                    storedMessage ->
                                storedMessage
                                    .senderType
                                    .equals(
                                        other =
                                            "CHARACTER",
                                        ignoreCase =
                                            true
                                    )
                            }
                            ?.createdAt

                    _uiState.update {
                            currentState ->
                        currentState.copy(
                            isLoading = false,
                            isCharacterOnline =
                                isCharacterOnline,
                            isCharacterTyping =
                                isCharacterTyping,
                            characterLastSeenAt =
                                presence
                                    ?.lastSeenAt
                                    ?: latestCharacterMessageAt,
                            messages = uiMessages,
                            threadId = threadId
                        )
                    }
                }
            } catch (exception: Exception) {
                Log.e(
                    "EnamoraAI",
                    "Conversation loading failed",
                    exception
                )

                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        errorMessage =
                            exception.message
                                ?: "Unable to load chat."
                    )
                }
            }
        }
    }

    fun sendMessage(
        messageText: String
    ) {
        val cleanMessage =
            messageText.trim()

        if (cleanMessage.isEmpty()) {
            return
        }

        val threadId =
            _uiState.value.threadId
                ?: return

        viewModelScope.launch {
            var savedUserMessageId: String? = null
            var savedMoodState: CharacterMoodState? = null
            var savedReplyGeneration: Long? = null

            outgoingMessageMutex.withLock {
                _uiState.update { currentState ->
                    currentState.copy(
                        isSending = true,
                        errorMessage = null
                    )
                }

                val userMessageId =
                    try {
                        chatRepository
                            .sendUserTextMessage(
                                threadId = threadId,
                                rawText = cleanMessage
                            )
                    } catch (exception: Exception) {
                        Log.e(
                            "EnamoraAI",
                            "User message saving failed",
                            exception
                        )

                        _uiState.update {
                                currentState ->
                            currentState.copy(
                                isSending = false,
                                errorMessage =
                                    exception.message
                                        ?: "Message could not be sent."
                            )
                        }

                        return@withLock
                    }

                characterMoodState =
                    RheaMoodEngine.nextState(
                        messageText = cleanMessage,
                        currentState =
                            characterMoodState
                    )

                val activeMoodState =
                    characterMoodState

                val activeReplyGeneration =
                    nextReplyGeneration()

                savedUserMessageId = userMessageId
                savedMoodState = activeMoodState
                savedReplyGeneration =
                    activeReplyGeneration

                _uiState.update { currentState ->
                    currentState.copy(
                        isSending = false
                    )
                }
            }

            val userMessageId =
                savedUserMessageId
                    ?: return@launch

            val activeMoodState =
                savedMoodState
                    ?: return@launch

            val activeReplyGeneration =
                savedReplyGeneration
                    ?: return@launch

            processOutgoingMessage(
                threadId = threadId,
                userMessageId = userMessageId,
                messageText = cleanMessage,
                moodState = activeMoodState,
                activeReplyGeneration =
                    activeReplyGeneration
            )
        }
    }

    fun retryMessage(
        messageId: String
    ) {
        val cleanMessageId =
            messageId.trim()

        if (cleanMessageId.isEmpty()) {
            return
        }

        val threadId =
            _uiState.value.threadId
                ?: return

        if (
            !retryingMessageIds.add(
                cleanMessageId
            )
        ) {
            return
        }

        viewModelScope.launch {
            try {
                var retryMessageText: String? = null
                var retryReplyGeneration: Long? = null

                outgoingMessageMutex.withLock {
                    _uiState.update {
                            currentState ->
                        currentState.copy(
                            isSending = true,
                            errorMessage = null
                        )
                    }

                    val messageText =
                        chatRepository
                            .prepareOutgoingMessageRetry(
                                messageId =
                                    cleanMessageId
                            )

                    val activeReplyGeneration =
                        nextReplyGeneration()

                    retryMessageText = messageText
                    retryReplyGeneration =
                        activeReplyGeneration

                    _uiState.update {
                            currentState ->
                        currentState.copy(
                            isSending = false
                        )
                    }
                }

                val messageText =
                    retryMessageText
                        ?: return@launch

                val activeReplyGeneration =
                    retryReplyGeneration
                        ?: return@launch

                processOutgoingMessage(
                    threadId = threadId,
                    userMessageId =
                        cleanMessageId,
                    messageText = messageText,
                    moodState =
                        characterMoodState,
                    activeReplyGeneration =
                        activeReplyGeneration
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Log.e(
                    "EnamoraAI",
                    "Message retry failed",
                    exception
                )

                _uiState.update { currentState ->
                    currentState.copy(
                        isSending = false,
                        isCharacterTyping = false,
                        errorMessage =
                            exception.message
                                ?: "Message could not be retried."
                    )
                }
            } finally {
                retryingMessageIds.remove(
                    cleanMessageId
                )
            }
        }
    }

    private suspend fun processOutgoingMessage(
        threadId: String,
        userMessageId: String,
        messageText: String,
        moodState: CharacterMoodState,
        activeReplyGeneration: Long
    ) {
        if (
            isLatestReplyGeneration(
                activeReplyGeneration
            )
        ) {
            characterOfflineJob?.cancel()
        }

        delay(
            Random.nextLong(
                from = 180L,
                until = 351L
            )
        )

        safelyUpdateMessageStatus(
            operationName =
                "Mark outgoing message sent"
        ) {
            chatRepository
                .markOutgoingMessageSent(
                    messageId = userMessageId
                )
        }

        val readingDuration =
            calculateReadingDelay(
                messageText = messageText,
                moodState = moodState
            )

        val deliveryDelay =
            (readingDuration / 3L)
                .coerceIn(
                    minimumValue = 300L,
                    maximumValue = 700L
                )

        delay(deliveryDelay)

        safelyUpdateMessageStatus(
            operationName =
                "Mark outgoing message delivered"
        ) {
            chatRepository
                .markOutgoingMessageDelivered(
                    messageId = userMessageId
                )
        }

        val remainingReadingDelay =
            (readingDuration - deliveryDelay)
                .coerceAtLeast(0L)

        if (remainingReadingDelay > 0L) {
            delay(remainingReadingDelay)
        }

        if (
            !isLatestReplyGeneration(
                activeReplyGeneration
            )
        ) {
            Log.d(
                "EnamoraAI",
                "Skipped stale reply before generation."
            )
            return
        }

        try {
            val preparedContext =
                conversationContextRepository
                    ?.prepareRheaContext(
                        threadId = threadId
                    )

            val moodAwareSystemPrompt =
                buildMoodAwareSystemPrompt(
                    baseSystemPrompt =
                        preparedContext
                            ?.systemPrompt
                            .orEmpty(),
                    moodState = moodState
                )

            if (
                !isLatestReplyGeneration(
                    activeReplyGeneration
                )
            ) {
                return
            }

            val reply =
                characterReplyGenerator
                    .generateReply(
                        CharacterReplyRequest(
                            threadId = threadId,
                            characterId =
                                InitialCharacterData
                                    .RHEA_ID,
                            userMessage =
                                messageText,
                            systemPrompt =
                                moodAwareSystemPrompt,
                            recentMessages =
                                preparedContext
                                    ?.recentMessages
                                    .orEmpty()
                        )
                    )

            if (
                !isLatestReplyGeneration(
                    activeReplyGeneration
                )
            ) {
                Log.d(
                    "EnamoraAI",
                    "Discarded stale generated reply."
                )
                return
            }

            require(reply.isNotBlank()) {
                "Character reply cannot be empty."
            }

            safelyUpdateMessageStatus(
                operationName =
                    "Mark Rhea online"
            ) {
                chatRepository
                    .markCharacterOnline(
                        activityContext =
                            "READING_MESSAGE"
                    )
            }

            safelyUpdateMessageStatus(
                operationName =
                    "Mark user messages read by Rhea"
            ) {
                chatRepository
                    .markUserMessagesReadByCharacter(
                        threadId = threadId
                    )
            }

            safelyUpdateMessageStatus(
                operationName =
                    "Mark Rhea typing"
            ) {
                chatRepository
                    .markCharacterTyping()
            }

            _uiState.update { currentState ->
                currentState.copy(
                    isCharacterOnline = true,
                    isCharacterTyping = true
                )
            }

            val requiredTypingDuration =
                calculateTypingDuration(
                    replyText = reply,
                    moodState = moodState
                )

            delay(requiredTypingDuration)

            if (
                !isLatestReplyGeneration(
                    activeReplyGeneration
                )
            ) {
                Log.d(
                    "EnamoraAI",
                    "Discarded stale reply during typing."
                )
                return
            }

            safelyUpdateMessageStatus(
                operationName =
                    "Keep Rhea online after replying"
            ) {
                chatRepository
                    .markCharacterOnline(
                        activityContext =
                            "JUST_REPLIED"
                    )
            }

            _uiState.update { currentState ->
                currentState.copy(
                    isCharacterOnline = true,
                    isCharacterTyping = false
                )
            }

            val replyWasSaved =
                outgoingMessageMutex.withLock {
                    if (
                        !isLatestReplyGeneration(
                            activeReplyGeneration
                        )
                    ) {
                        false
                    } else {
                        chatRepository
                            .saveCharacterTextMessage(
                                threadId =
                                    threadId,
                                rawText = reply
                            )

                        true
                    }
                }

            if (!replyWasSaved) {
                Log.d(
                    "EnamoraAI",
                    "Discarded stale reply before saving."
                )
                return
            }

            safelyUpdateMessageStatus(
                operationName =
                    "Mark thread read"
            ) {
                chatRepository.markThreadRead(
                    threadId = threadId
                )
            }

            outgoingMessageMutex.withLock {
                if (
                    isLatestReplyGeneration(
                        activeReplyGeneration
                    )
                ) {
                    scheduleCharacterOffline(
                        delayMilliseconds =
                            calculatePostReplyOnlineDuration(
                                userMessage =
                                    messageText,
                                replyText = reply
                            )
                    )
                }
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            if (
                !isLatestReplyGeneration(
                    activeReplyGeneration
                )
            ) {
                Log.d(
                    "EnamoraAI",
                    "Ignored failure from stale reply.",
                    exception
                )
                return
            }

            Log.e(
                "EnamoraAI",
                "AI reply failed",
                exception
            )

            safelyUpdateMessageStatus(
                operationName =
                    "Mark outgoing message failed"
            ) {
                chatRepository
                    .markOutgoingMessageFailed(
                        messageId =
                            userMessageId
                    )
            }

            safelyUpdateMessageStatus(
                operationName =
                    "Mark Rhea offline after failure"
            ) {
                chatRepository
                    .markCharacterOffline()
            }

            _uiState.update { currentState ->
                currentState.copy(
                    isCharacterOnline = false,
                    isCharacterTyping = false,
                    errorMessage =
                        exception.message
                            ?: "Rhea could not reply right now."
                )
            }
        } finally {
            if (
                isLatestReplyGeneration(
                    activeReplyGeneration
                )
            ) {
                _uiState.update {
                        currentState ->
                    currentState.copy(
                        isSending = false,
                        isCharacterTyping = false
                    )
                }
            }
        }
    }

    private fun nextReplyGeneration(): Long {
        replyGeneration += 1L
        return replyGeneration
    }

    private fun isLatestReplyGeneration(
        generation: Long
    ): Boolean {
        return generation == replyGeneration
    }

    private fun scheduleCharacterOffline(
        delayMilliseconds: Long
    ) {
        characterOfflineJob?.cancel()

        characterOfflineJob =
            viewModelScope.launch {
                delay(delayMilliseconds)

                safelyUpdateMessageStatus(
                    operationName =
                        "Mark Rhea offline after inactivity"
                ) {
                    chatRepository
                        .markCharacterOffline()
                }
            }
    }

    private fun calculatePostReplyOnlineDuration(
        userMessage: String,
        replyText: String
    ): Long {
        val conversationIsClosing =
            isConversationClosing(
                text = userMessage
            ) ||
                    isConversationClosing(
                        text = replyText
                    )

        return if (conversationIsClosing) {
            Random.nextLong(
                from = 4_000L,
                until = 9_001L
            )
        } else {
            Random.nextLong(
                from = 20_000L,
                until = 46_001L
            )
        }
    }

    private fun isConversationClosing(
        text: String
    ): Boolean {
        val normalizedText =
            text
                .lowercase(Locale.ROOT)
                .replace("’", "'")
                .trim()

        val closingPhrases =
            listOf(
                "good night",
                "goodnight",
                "okay bye",
                "ok bye",
                "bye for now",
                "see you tomorrow",
                "talk tomorrow",
                "talk to you tomorrow",
                "night, see you",
                "night. see you",
                "gn"
            )

        return closingPhrases.any {
                phrase ->
            normalizedText == phrase ||
                    normalizedText.contains(
                        "$phrase."
                    ) ||
                    normalizedText.contains(
                        "$phrase!"
                    )
        }
    }

    private fun buildMoodAwareSystemPrompt(
        baseSystemPrompt: String,
        moodState: CharacterMoodState
    ): String {
        val safeBasePrompt =
            baseSystemPrompt.ifBlank {
                """
                    You are portraying Rhea D’Souza,
                    a fictional adult character inside Enamora.
                """.trimIndent()
            }

        val moodDirection =
            when (moodState.mood) {
                CharacterMood.NEUTRAL ->
                    """
                        Sound relaxed and natural.
                        Give a personal reaction instead of
                        a generic helpful response.
                    """.trimIndent()

                CharacterMood.WARM ->
                    """
                        Respond with genuine warmth.
                        Notice the user's emotion without
                        sounding like a therapist.
                        Gentle teasing is allowed when the
                        situation is mild.
                    """.trimIndent()

                CharacterMood.PLAYFUL ->
                    """
                        Be playfully teasing and expressive.
                        Use light humour or a mischievous
                        observation without becoming childish.
                    """.trimIndent()

                CharacterMood.CURIOUS ->
                    """
                        Show specific, genuine curiosity.
                        You may ask one focused question,
                        but do not interrogate the user.
                    """.trimIndent()

                CharacterMood.EXCITED ->
                    """
                        Sound energetic and genuinely excited.
                        Short exclamations and an enthusiastic
                        follow-up are appropriate.
                    """.trimIndent()

                CharacterMood.THOUGHTFUL ->
                    """
                        Sound reflective and honest.
                        Give one clear personal opinion rather
                        than a polished life lesson.
                    """.trimIndent()

                CharacterMood.TIRED ->
                    """
                        Sound slightly low-energy and concise.
                        Remain attentive without pretending
                        to be endlessly enthusiastic.
                    """.trimIndent()

                CharacterMood.ANNOYED ->
                    """
                        Sound briefly and clearly annoyed.
                        Maintain self-respect and set a calm
                        boundary without insulting the user.
                        Do not apologise without a reason.
                    """.trimIndent()

                CharacterMood.HURT ->
                    """
                        Sound quieter and slightly guarded.
                        Be honest without guilt-tripping,
                        manipulating or punishing the user.
                    """.trimIndent()
            }

        val intensityDirection =
            when (moodState.intensity) {
                CharacterMoodIntensity.LOW ->
                    """
                        Keep this emotion subtle.
                        It should be felt, not announced.
                    """.trimIndent()

                CharacterMoodIntensity.MEDIUM ->
                    """
                        Make the emotion noticeable through
                        wording, rhythm and punctuation.
                    """.trimIndent()

                CharacterMoodIntensity.HIGH ->
                    """
                        Make the emotion clear and expressive,
                        but do not become theatrical.
                    """.trimIndent()
            }

        val emojiDirection =
            when (moodState.mood) {
                CharacterMood.NEUTRAL ->
                    """
                        Usually use no emoji.
                        At most one is allowed when it
                        genuinely improves the reaction.
                    """.trimIndent()

                CharacterMood.WARM ->
                    """
                        For a mild everyday problem, one
                        natural emoji such as 😅 or 🙂 is
                        encouraged.
                        For serious sadness or grief,
                        use no emoji.
                    """.trimIndent()

                CharacterMood.PLAYFUL ->
                    """
                        Usually use one fitting emoji such as
                        😅, 👀, 🙄 or 😂.
                        Do not repeat the same emoji frequently.
                    """.trimIndent()

                CharacterMood.CURIOUS ->
                    """
                        Usually use no emoji.
                        👀 may be used when the curiosity
                        is playful.
                    """.trimIndent()

                CharacterMood.EXCITED ->
                    """
                        Use one expressive emoji.
                        Two are allowed only for genuinely
                        exciting news.
                    """.trimIndent()

                CharacterMood.THOUGHTFUL,
                CharacterMood.TIRED ->
                    """
                        Prefer no emoji.
                        Use one only if it feels completely
                        natural in the sentence.
                    """.trimIndent()

                CharacterMood.ANNOYED,
                CharacterMood.HURT ->
                    """
                        Do not use cheerful, romantic or
                        playful emojis.
                    """.trimIndent()
            }

        return """
            $safeBasePrompt

            CURRENT TEMPORARY EMOTIONAL DIRECTION
            This is private acting direction.
            Never name or explain the mood to the user.

            Mood: ${moodState.mood.name}
            Intensity: ${moodState.intensity.name}
            Internal reason: ${moodState.reason}

            Mood behaviour:
            $moodDirection

            Intensity behaviour:
            $intensityDirection

            Emoji behaviour:
            $emojiDirection

            Express the emotion indirectly through word choice,
            sentence length, punctuation and conversational rhythm.
            The character's relationship rules override emoji use.
            Reply only with Rhea's message.
        """.trimIndent()
    }

    private fun calculateReadingDelay(
        messageText: String,
        moodState: CharacterMoodState
    ): Long {
        val characterCount =
            messageText.length
                .coerceAtMost(180)

        val randomVariation =
            Random.nextLong(
                from = 150L,
                until = 651L
            )

        val moodAdjustment =
            when (moodState.mood) {
                CharacterMood.EXCITED ->
                    -300L

                CharacterMood.PLAYFUL ->
                    -150L

                CharacterMood.THOUGHTFUL ->
                    500L

                CharacterMood.TIRED ->
                    650L

                CharacterMood.ANNOYED ->
                    250L

                CharacterMood.HURT ->
                    600L

                CharacterMood.WARM ->
                    150L

                CharacterMood.CURIOUS,
                CharacterMood.NEUTRAL ->
                    0L
            }

        return (
                600L +
                        characterCount * 8L +
                        randomVariation +
                        moodAdjustment
                ).coerceIn(
                minimumValue = 800L,
                maximumValue = 3_000L
            )
    }

    private fun calculateTypingDuration(
        replyText: String,
        moodState: CharacterMoodState
    ): Long {
        val characterCount =
            replyText.length
                .coerceIn(
                    minimumValue = 1,
                    maximumValue = 220
                )

        val randomVariation =
            Random.nextLong(
                from = -350L,
                until = 651L
            )

        val moodAdjustment =
            when (moodState.mood) {
                CharacterMood.EXCITED ->
                    -450L

                CharacterMood.PLAYFUL ->
                    -250L

                CharacterMood.WARM ->
                    200L

                CharacterMood.THOUGHTFUL ->
                    700L

                CharacterMood.TIRED ->
                    900L

                CharacterMood.ANNOYED ->
                    -300L

                CharacterMood.HURT ->
                    500L

                CharacterMood.CURIOUS ->
                    100L

                CharacterMood.NEUTRAL ->
                    0L
            }

        return (
                1_600L +
                        characterCount * 38L +
                        randomVariation +
                        moodAdjustment
                ).coerceIn(
                minimumValue = 2_500L,
                maximumValue = 10_000L
            )
    }

    private suspend fun safelyUpdateMessageStatus(
        operationName: String,
        action: suspend () -> Unit
    ) {
        try {
            action()
        } catch (exception: Exception) {
            Log.w(
                "EnamoraAI",
                operationName,
                exception
            )
        }
    }

    fun clearError() {
        _uiState.update { currentState ->
            currentState.copy(
                errorMessage = null
            )
        }
    }
}

class ChatViewModelFactory(
    private val chatRepository: ChatRepository,
    private val characterReplyGenerator:
    CharacterReplyGenerator =
        LocalRheaReplyGenerator(),
    private val conversationContextRepository:
    ConversationContextRepository? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                ChatViewModel::class.java
            )
        ) {
            return ChatViewModel(
                chatRepository = chatRepository,
                characterReplyGenerator =
                    characterReplyGenerator,
                conversationContextRepository =
                    conversationContextRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: " +
                    modelClass.name
        )
    }
}