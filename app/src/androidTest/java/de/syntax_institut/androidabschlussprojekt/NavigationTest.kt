package de.syntax_institut.androidabschlussprojekt

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class NavigationTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testNavigationToSettings() {
        // HomeScreen mit NavController stub testen
        composeTestRule.onNodeWithText("Go to Settings").performClick()
        // Nun sollte Settings Screen sichtbar sein
        composeTestRule.onNodeWithText("Settings Content Here").assertExists()
    }
}
