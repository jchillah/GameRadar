package de.syntax_institut.androidabschlussprojekt.data.local.models

enum class ImageQuality(val displayName: String) {
    LOW("Niedrig"),
    MEDIUM("Mittel"),
    HIGH("Hoch");

    companion object {
        fun fromDisplayName(displayName: String): ImageQuality {
            return entries.find { it.displayName == displayName } ?: HIGH
        }
    }
} 