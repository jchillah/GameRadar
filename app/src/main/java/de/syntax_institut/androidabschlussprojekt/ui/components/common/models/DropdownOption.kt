package de.syntax_institut.androidabschlussprojekt.ui.components.common.models

import androidx.compose.ui.graphics.vector.*

/**
 * Datenklasse für Dropdown-Optionen mit Icon und Trennlinien-Support.
 * Folgt Clean Code Best Practices: Single Source of Truth, DRY, KISS.
 */
data class DropdownOption(
    val value: String,
    val label: String,
    val icon: ImageVector? = null,
    val showDividerBefore: Boolean = false,
) 