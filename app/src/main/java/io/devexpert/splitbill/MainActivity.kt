package io.devexpert.splitbill

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.devexpert.splitbill.navigation.NavigationHost
import io.devexpert.splitbill.navigation.NavigationRoute
import io.devexpert.splitbill.ui.theme.SplitBillTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplitBillTheme {
                val navController = rememberNavController()

                NavigationHost(
                    navController = navController,
                    startDestination = NavigationRoute.Home
                )
            }
        }
    }
}