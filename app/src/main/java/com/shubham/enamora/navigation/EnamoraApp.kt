package com.shubham.enamora.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shubham.enamora.data.mock.EnamoraMockData
import com.shubham.enamora.ui.components.EnamoraBottomBar
import com.shubham.enamora.ui.screens.home.HomeScreen
import com.shubham.enamora.ui.theme.EnamoraObsidian
import com.shubham.enamora.ui.theme.EnamoraWarmIvory

@Composable
fun EnamoraApp(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = EnamoraObsidian,
        bottomBar = {
            EnamoraBottomBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = EnamoraDestination.HOME.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(EnamoraDestination.HOME.route) {
                HomeScreen(
                    userName = EnamoraMockData.currentUserName,
                    character = EnamoraMockData.activeCharacter,
                    voiceNoteDuration = EnamoraMockData.latestVoiceNoteDuration,
                    sharedMemoryTitle = EnamoraMockData.sharedMemoryTitle,
                    onContinueConversation = {
                        navController.navigate(
                            EnamoraDestination.CHAT.route
                        ) {
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(EnamoraDestination.EXPLORE.route) {
                DestinationPlaceholder(title = "Explore")
            }

            composable(EnamoraDestination.CHAT.route) {
                DestinationPlaceholder(title = "Chat")
            }

            composable(EnamoraDestination.ABOUT.route) {
                DestinationPlaceholder(title = "About")
            }
        }
    }
}

@Composable
private fun DestinationPlaceholder(
    title: String
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = EnamoraWarmIvory,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}