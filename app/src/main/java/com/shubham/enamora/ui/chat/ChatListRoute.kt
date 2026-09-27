package com.shubham.enamora.ui.screens.chat

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shubham.enamora.EnamoraApplication
import com.shubham.enamora.data.repository.ChatRepository
import com.shubham.enamora.ui.chat.ChatListViewModel
import com.shubham.enamora.ui.chat.ChatListViewModelFactory
import com.shubham.enamora.ui.model.ChatThreadUiModel

@Composable
fun ChatListRoute(
    onChatClick: (ChatThreadUiModel) -> Unit,
    modifier: Modifier = Modifier,
    onSearchClick: () -> Unit = {}
) {
    val application =
        LocalContext.current.applicationContext
                as EnamoraApplication

    val viewModelFactory =
        remember(application) {
            ChatListViewModelFactory(
                chatRepository =
                    ChatRepository(
                        database =
                            application.database
                    )
            )
        }

    val chatListViewModel: ChatListViewModel =
        viewModel(
            factory = viewModelFactory
        )

    val uiState by
    chatListViewModel.uiState
        .collectAsStateWithLifecycle()

    ChatListScreen(
        chatThreads = uiState.chatThreads,
        onChatClick = onChatClick,
        modifier = modifier,
        onSearchClick = onSearchClick
    )
}