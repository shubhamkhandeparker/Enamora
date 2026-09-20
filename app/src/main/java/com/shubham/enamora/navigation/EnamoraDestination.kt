package com.shubham.enamora.navigation

enum class EnamoraDestination(
    val route: String,
    val label: String
) {
    HOME(
        route = "home",
        label = "Home"
    ),
    EXPLORE(
        route = "explore",
        label = "Explore"
    ),
    CHAT(
        route = "chat",
        label = "Chat"
    ),
    ABOUT(
        route = "about",
        label = "About"
    );

    companion object {
        val bottomNavigationItems =
            listOf(
                HOME,
                EXPLORE,
                CHAT,
                ABOUT
            )
    }
}

object EnamoraRoutes {

    const val CHARACTER_ID_ARGUMENT =
        "characterId"

    const val CONVERSATION =
        "conversation/{characterId}"

    fun conversation(
        characterId: String
    ): String {
        return "conversation/$characterId"
    }
}