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
        val bottomNavigationItems: List<EnamoraDestination> = entries
    }
}