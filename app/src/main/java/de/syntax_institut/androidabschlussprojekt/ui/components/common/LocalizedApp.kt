package de.syntax_institut.androidabschlussprojekt.ui.components.common

import androidx.compose.runtime.*
import androidx.compose.ui.platform.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import org.koin.compose.*

/**
 * LocalizedApp-Komponente, die die Sprache der gesamten App verwaltet.
 * Folgt Clean Code Best Practices: Single Responsibility, DRY, KISS.
 */
@Composable
fun LocalizedApp(
    content: @Composable () -> Unit,
) {
    val settingsRepository: SettingsRepository = koinInject()
    val currentLanguage by settingsRepository.language.collectAsState()
    val context = LocalContext.current

    // Erstelle einen lokalisierten Context basierend auf der ausgewählten Sprache
    val localizedContext = remember(currentLanguage) {
        if (currentLanguage == "system") context else LocaleManager.createLocalizedContext(context, currentLanguage)
    }

    // Verwende den lokalisierten Context für die gesamte App
    CompositionLocalProvider(
        LocalContext provides localizedContext
    ) {
        content()
    }
} 