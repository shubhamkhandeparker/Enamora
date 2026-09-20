package com.shubham.enamora.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shubham.enamora.data.mock.EnamoraMockData
import com.shubham.enamora.ui.components.EnamoraBottomBar
import com.shubham.enamora.ui.model.ChatMessageContent
import com.shubham.enamora.ui.model.ChatMessageUiModel
import com.shubham.enamora.ui.model.MessageAuthor
import com.shubham.enamora.ui.model.MessageDeliveryState
import com.shubham.enamora.ui.screens.about.AboutScreen
import com.shubham.enamora.ui.screens.chat.ChatListScreen
import com.shubham.enamora.ui.screens.chat.ChatScreen
import com.shubham.enamora.ui.screens.explore.ExploreScreen
import com.shubham.enamora.ui.screens.home.HomeScreen
import com.shubham.enamora.ui.theme.EnamoraObsidian
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EnamoraApp(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    var selectedCharacterId by rememberSaveable {
        mutableStateOf(
            EnamoraMockData.activeCharacter.id
        )
    }

    val localMessagesByCharacter = remember {
        mutableStateMapOf<
                String,
                List<ChatMessageUiModel>
                >()
    }

    val selectedCharacter =
        EnamoraMockData.characters.firstOrNull { character ->
            character.id == selectedCharacterId
        } ?: EnamoraMockData.activeCharacter

    val currentChatThreads =
        EnamoraMockData.chatThreads.map { thread ->
            val messages =
                localMessagesByCharacter[
                    thread.character.id
                ] ?: EnamoraMockData.messagesForCharacter(
                    thread.character.id
                )

            val latestMessage =
                messages.lastOrNull()

            if (latestMessage == null) {
                thread.copy(
                    latestMessage = null,
                    latestMessageTimestamp = null,
                    unreadCount = 0
                )
            } else {
                thread.copy(
                    latestMessage =
                        chatListPreviewText(
                            message = latestMessage
                        ),
                    latestMessageTimestamp =
                        latestMessage.timestamp,
                    unreadCount = 0
                )
            }
        }

    fun openConversation(
        characterId: String
    ) {
        selectedCharacterId = characterId

        navController.navigate(
            EnamoraRoutes.conversation(characterId)
        ) {
            launchSingleTop = true
        }
    }

    fun returnToChatList() {
        val chatListWasFound =
            navController.popBackStack(
                route = EnamoraDestination.CHAT.route,
                inclusive = false
            )

        if (!chatListWasFound) {
            navController.popBackStack()

            navController.navigate(
                EnamoraDestination.CHAT.route
            ) {
                launchSingleTop = true
            }
        }
    }

    fun sendUserMessage(
        characterId: String,
        messageText: String
    ) {
        val existingMessages =
            localMessagesByCharacter[
                characterId
            ] ?: EnamoraMockData.messagesForCharacter(
                characterId
            )

        val newMessage =
            ChatMessageUiModel(
                id =
                    "user_${characterId}_" +
                            System.currentTimeMillis(),
                author = MessageAuthor.USER,
                content = ChatMessageContent.Text(
                    text = messageText
                ),
                timestamp =
                    currentMessageTimestamp(),
                deliveryState =
                    MessageDeliveryState.SENT
            )

        localMessagesByCharacter[
            characterId
        ] = existingMessages + newMessage
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = EnamoraObsidian,
        bottomBar = {
            EnamoraBottomBar(
                navController = navController
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination =
                EnamoraDestination.HOME.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(
                route = EnamoraDestination.HOME.route
            ) {
                HomeScreen(
                    userName =
                        EnamoraMockData.currentUserName,
                    character =
                        EnamoraMockData.activeCharacter,
                    voiceNoteDuration =
                        EnamoraMockData.latestVoiceNoteDuration,
                    sharedMemoryTitle =
                        EnamoraMockData.sharedMemoryTitle,
                    onContinueConversation = {
                        openConversation(
                            EnamoraMockData.activeCharacter.id
                        )
                    }
                )
            }

            composable(
                route = EnamoraDestination.EXPLORE.route
            ) {
                ExploreScreen(
                    characters =
                        EnamoraMockData.characters,
                    onViewProfile = { character ->
                        selectedCharacterId =
                            character.id

                        navController.navigate(
                            EnamoraDestination.ABOUT.route
                        ) {
                            launchSingleTop = true
                        }
                    },
                    onPass = { _ ->
                    },
                    onConnect = { character ->
                        openConversation(
                            character.id
                        )
                    }
                )
            }

            composable(
                route = EnamoraDestination.CHAT.route
            ) {
                ChatListScreen(
                    chatThreads =
                        currentChatThreads,
                    onChatClick = { thread ->
                        openConversation(
                            thread.character.id
                        )
                    }
                )
            }

            composable(
                route = EnamoraRoutes.CONVERSATION
            ) { backStackEntry ->
                val characterId =
                    backStackEntry.arguments
                        ?.getString(
                            EnamoraRoutes.CHARACTER_ID_ARGUMENT
                        )

                val conversationCharacter =
                    EnamoraMockData.characters
                        .firstOrNull { character ->
                            character.id == characterId
                        } ?: selectedCharacter

                val conversationMessages =
                    localMessagesByCharacter[
                        conversationCharacter.id
                    ] ?: EnamoraMockData.messagesForCharacter(
                        conversationCharacter.id
                    )

                ChatScreen(
                    character =
                        conversationCharacter,
                    messages =
                        conversationMessages,
                    isTyping =
                        EnamoraMockData.isCharacterTyping(
                            conversationCharacter.id
                        ),
                    onBackClick = {
                        returnToChatList()
                    },
                    onProfileClick = {
                        selectedCharacterId =
                            conversationCharacter.id

                        navController.navigate(
                            EnamoraDestination.ABOUT.route
                        ) {
                            launchSingleTop = true
                        }
                    },
                    onVoiceCallClick = {
                    },
                    onMoreClick = {
                    },
                    onAttachmentClick = {
                    },
                    onCameraClick = {
                    },
                    onMicrophoneClick = {
                    },
                    onSendMessage = { messageText ->
                        sendUserMessage(
                            characterId =
                                conversationCharacter.id,
                            messageText =
                                messageText
                        )
                    }
                )
            }

            composable(
                route = EnamoraDestination.ABOUT.route
            ) {
                AboutScreen(
                    character = selectedCharacter,
                    onBackClick = {
                        val returnedToPreviousScreen =
                            navController.popBackStack()

                        if (!returnedToPreviousScreen) {
                            navController.navigate(
                                EnamoraDestination.HOME.route
                            ) {
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }
    }
}

private fun chatListPreviewText(
    message: ChatMessageUiModel
): String {
    return when (val content = message.content) {
        is ChatMessageContent.Text -> {
            content.text
        }

        is ChatMessageContent.VoiceNote -> {
            "Voice note"
        }

        is ChatMessageContent.Photo -> {
            if (content.caption.isBlank()) {
                "Photo"
            } else {
                content.caption
            }
        }
    }
}

private fun currentMessageTimestamp(): String {
    return SimpleDateFormat(
        "h:mm a",
        Locale.getDefault()
    ).format(Date())
}