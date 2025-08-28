package io.devexpert.splitbill.navigation

import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
object Receipt

@Serializable
sealed class NavigationRoute {
    @Serializable
    data object Home: NavigationRoute()

    @Serializable
    data object Receipt: NavigationRoute()
}