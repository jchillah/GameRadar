package de.syntax_institut.androidabschlussprojekt.utils

import org.junit.*
import org.junit.Assert.*

/**
 * Unit-Tests für CommonUtils.
 *
 * Testet alle öffentlichen Funktionen der CommonUtils-Klasse
 * und stellt sicher, dass sie korrekt funktionieren.
 */
class CommonUtilsTest {

    @Test
    fun `getEmptyStateMessage returns correct message for null items`() {
        val message = CommonUtils.getEmptyStateMessage(
            items = null,
            itemName = "Screenshots"
        )
        assertEquals("Für dieses Spiel wurden keine Screenshots gefunden.", message)
    }

    @Test
    fun `getEmptyStateMessage returns correct message for empty items`() {
        val message = CommonUtils.getEmptyStateMessage(
            items = emptyList<String>(),
            itemName = "Trailer"
        )
        assertEquals("Für dieses Spiel wurden keine Trailer gefunden.", message)
    }

    @Test
    fun `getEmptyStateMessage returns correct message for offline state`() {
        val message = CommonUtils.getEmptyStateMessage(
            items = null,
            itemName = "Screenshots",
            isOffline = true
        )
        assertEquals(
            "Keine Screenshots verfügbar. Prüfe deine Internetverbindung und versuche es erneut.",
            message
        )
    }

    @Test
    fun `getEmptyStateMessage returns empty string for non-empty items`() {
        val message = CommonUtils.getEmptyStateMessage(
            items = listOf("item1", "item2"),
            itemName = "Items"
        )
        assertEquals("", message)
    }

    @Test
    fun `formatRating formats positive rating correctly`() {
        val formatted = CommonUtils.formatRating(4.567f)
        assertEquals("4.6", formatted)
    }

    @Test
    fun `formatRating returns no rating message for zero rating`() {
        val formatted = CommonUtils.formatRating(0f)
        assertEquals("Keine Bewertung", formatted)
    }

    @Test
    fun `formatRating returns no rating message for negative rating`() {
        val formatted = CommonUtils.formatRating(-1f)
        assertEquals("Keine Bewertung", formatted)
    }

    @Test
    fun `isValidUrl returns true for valid http URL`() {
        val isValid = CommonUtils.isValidUrl("http://example.com")
        assertTrue(isValid)
    }

    @Test
    fun `isValidUrl returns true for valid https URL`() {
        val isValid = CommonUtils.isValidUrl("https://example.com")
        assertTrue(isValid)
    }

    @Test
    fun `isValidUrl returns false for null URL`() {
        val isValid = CommonUtils.isValidUrl(null)
        assertFalse(isValid)
    }

    @Test
    fun `isValidUrl returns false for empty URL`() {
        val isValid = CommonUtils.isValidUrl("")
        assertFalse(isValid)
    }

    @Test
    fun `isValidUrl returns false for blank URL`() {
        val isValid = CommonUtils.isValidUrl("   ")
        assertFalse(isValid)
    }

    @Test
    fun `isValidUrl returns false for invalid URL`() {
        val isValid = CommonUtils.isValidUrl("not-a-url")
        assertFalse(isValid)
    }

    @Test
    fun `isValidUrl returns false for ftp URL`() {
        val isValid = CommonUtils.isValidUrl("ftp://example.com")
        assertFalse(isValid)
    }
} 