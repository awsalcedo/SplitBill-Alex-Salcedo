package io.devexpert.splitbill.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import io.devexpert.splitbill.ui.screens.home.HomeScreen
import io.devexpert.splitbill.ui.screens.receipt.ReceiptScreen

@Composable
fun NavigationHost(
    navController: NavHostController,
    startDestination: NavigationRoute
) {
    NavHost(navController = navController, startDestination = startDestination) {
        homeScreen(navController = navController)
        receiptScreen(navController = navController)
    }
}

fun NavGraphBuilder.homeScreen(navController: NavHostController) {
    composable<NavigationRoute.Home> {
        HomeScreen(
            onTicketProcessed = {
                navController.navigate(NavigationRoute.Receipt)
            }
        )
    }
}

fun NavGraphBuilder.receiptScreen(navController: NavHostController) {
    composable<NavigationRoute.Receipt> {
        ReceiptScreen(
            onBackPressed = {
                navController.popBackStack()
            }
        )
    }
}