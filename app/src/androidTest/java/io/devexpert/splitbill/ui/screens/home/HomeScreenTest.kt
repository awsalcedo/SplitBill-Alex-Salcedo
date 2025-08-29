package io.devexpert.splitbill.ui.screens.home

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.devexpert.splitbill.R
import org.junit.Rule
import org.junit.Test

class HomeScreenUiTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun button_enabled_and_shows_scan_label_when_scans_available_and_not_processing() {
        val ctx = composeRule.activity
        val scansText = ctx.getString(R.string.scans_remaining, 3)
        val scanLabel = ctx.getString(R.string.scan_ticket)

        composeRule.setContent {
            HomeScreen(
                uiState = HomeUiState(scansLeft = 3, isProcessing = false),
                onScanClicked = {}
            )
        }

        // Muestra "quedan X escaneos"
        composeRule.onNodeWithText(scansText).assertIsDisplayed()

        // Botón habilitado + etiqueta "Escanear ticket"
        composeRule.onNodeWithText(scanLabel)
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun button_disabled_when_no_scans_remaining() {
        val ctx = composeRule.activity
        val noScansText = ctx.getString(R.string.no_scans_remaining)
        val scanLabel = ctx.getString(R.string.scan_ticket)

        composeRule.setContent {
            HomeScreen(
                uiState = HomeUiState(scansLeft = 0, isProcessing = false),
                onScanClicked = {}
            )
        }

        composeRule.onNodeWithText(noScansText).assertIsDisplayed()
        composeRule.onNodeWithText(scanLabel).assertIsNotEnabled()
    }

    @Test
    fun shows_processing_state_and_label_when_isProcessing_true() {
        val ctx = composeRule.activity
        val scansText = ctx.getString(R.string.scans_remaining, 2)
        val processingLabel = ctx.getString(R.string.processing)
        val processingMsg = ctx.getString(R.string.photo_captured_processing)

        composeRule.setContent {
            HomeScreen(
                uiState = HomeUiState(scansLeft = 2, isProcessing = true),
                onScanClicked = {}
            )
        }

        composeRule.onNodeWithText(scansText).assertIsDisplayed()
        composeRule.onNodeWithText(processingLabel).assertIsDisplayed()
        composeRule.onNodeWithText(processingMsg).assertIsDisplayed()

        // En processing el botón debe estar deshabilitado
        composeRule.onNodeWithText(processingLabel).assertIsNotEnabled()
    }

    @Test
    fun shows_error_message_when_present_and_not_processing() {
        val error = "Error processing image"

        composeRule.setContent {
            HomeScreen(
                uiState = HomeUiState(
                    scansLeft = 1,
                    isProcessing = false,
                    errorMessage = error
                ),
                onScanClicked = {}
            )
        }

        composeRule.onNode(hasText(error)).assertIsDisplayed()
    }

    @Test
    fun clicking_enabled_button_triggers_onScanClicked() {
        var clicked = false
        val ctx = composeRule.activity
        val scanLabel = ctx.getString(R.string.scan_ticket)

        composeRule.setContent {
            HomeScreen(
                uiState = HomeUiState(scansLeft = 5, isProcessing = false),
                onScanClicked = { clicked = true }
            )
        }

        composeRule.onNodeWithText(scanLabel).assertIsEnabled().performClick()

        assert(clicked) { "onScanClicked no fue invocado" }
    }
}