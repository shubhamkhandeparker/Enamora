package com.shubham.enamora.ui.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.shubham.enamora.data.mock.EnamoraMockData
import com.shubham.enamora.data.repository.ChatRepository
import com.shubham.enamora.ui.model.ChatThreadUiModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatListUiState(
    val isLoading: Boolean = true,
    val chatThreads: List<ChatThreadUiModel> =
        emptyList(),
    val errorMessage: String? = null
)

class ChatListViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(ChatListUiState())

    val uiState: StateFlow<ChatListUiState> =
        _uiState.asStateFlow()

    init {
        prepareAndObserveThreads()
    }

    private fun prepareAndObserveThreads() {
        viewModelScope.launch {
            try {
                chatRepository
                    .prepareRheaConversation()

                chatRepository
                    .observeThreads()
                    .collect { storedThreads ->
                        val chatThreads =
                            storedThreads.mapNotNull {
                                    storedThread ->

                                val templateThread =
                                    EnamoraMockData
                                        .chatThreads
                                        .firstOrNull {
                                                template ->
                                            template
                                                .character
                                                .id ==
                                                    storedThread
                                                        .characterId
                                        }
                                        ?: return@mapNotNull null

                                templateThread.copy(
                                    latestMessage =
                                        storedThread
                                            .lastMessagePreview,
                                    latestMessageTimestamp =
                                        storedThread
                                            .lastMessageAt
                                            ?.let {
                                                    timestamp ->
                                                formatThreadTime(
                                                    timestamp
                                                )
                                            },
                                    unreadCount =
                                        storedThread
                                            .unreadCount
                                )
                            }

                        _uiState.update {
                                currentState ->
                            currentState.copy(
                                isLoading = false,
                                chatThreads =
                                    chatThreads,
                                errorMessage = null
                            )
                        }
                    }
            } catch (exception: Exception) {
                Log.e(
                    "EnamoraChatList",
                    "Chat list loading failed",
                    exception
                )

                _uiState.update {
                        currentState ->
                    currentState.copy(
                        isLoading = false,
                        errorMessage =
                            exception.message
                                ?: "Unable to load conversations."
                    )
                }
            }
        }
    }

    private fun formatThreadTime(
        timestamp: Long
    ): String {
        val currentLocale =
            Locale.getDefault()

        return SimpleDateFormat(
            "h:mm a",
            currentLocale
        ).format(
            Date(timestamp)
        ).lowercase(currentLocale)
    }
}

class ChatListViewModelFactory(
    private val chatRepository: ChatRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (
            modelClass.isAssignableFrom(
                ChatListViewModel::class.java
            )
        ) {
            return ChatListViewModel(
                chatRepository =
                    chatRepository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: " +
                    modelClass.name
        )
    }
}