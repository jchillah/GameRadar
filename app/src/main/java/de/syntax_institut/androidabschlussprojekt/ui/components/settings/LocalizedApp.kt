package de.syntax_institut.androidabschlussprojekt.ui.components.settings

import androidx.compose.runtime.*
import androidx.compose.ui.platform.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import org.koin.compose.*

/**
 * LocalizedApp-Komponente für die zentrale Sprachverwaltung der gesamten App.
 *
 * Features:
 * - Dynamische Sprachänderung zur Laufzeit
 * - System-Sprache-Unterstützung
 * - LocaleManager-Integration für Context-Lokalisierung
 * - CompositionLocalProvider für App-weite Lokalisierung
 * - SettingsRepository-Integration
 * - Clean Code Best Practices: Single Responsibility, DRY, KISS
 *
 * Funktionalität:
 * - Überwacht Sprachänderungen aus den Einstellungen
 * - Erstellt lokalisierten Context basierend auf ausgewählter Sprache
 * - Stellt lokalisierten Context für alle Child-Composables bereit
 * - Unterstützt "system" als Sprachauswahl für System-Locale
 *
 * @param content Composable-Content der App, der lokalisiert werden soll
 */
@Composable
fun LocalizedApp(
    content: @Composable () -> Unit,
) {
    val settingsRepository: SettingsRepository = koinInject()
    val currentLanguage by settingsRepository.language.collectAsState()
    val context = LocalContext.current

    // Prüfe, ob die ausgewählte Sprache unterstützt wird
    val isLanguageSupported =
        remember(currentLanguage) { LocaleManager.isLanguageSupported(currentLanguage) }

    // Erstelle einen lokalisierten Context basierend auf der ausgewählten Sprache
    val localizedContext =
        remember(currentLanguage) {
            when {
                !isLanguageSupported -> {
                    // Fallback auf Systemsprache wenn nicht unterstützt
                    AppLogger.w(
                        "LocalizedApp",
                        "Sprache '$currentLanguage' nicht unterstützt, verwende Systemsprache"
                    )
                    LocaleManager.createLocalizedContext(context, "system")
                }

                currentLanguage == "system" -> {
                    // Verwende Systemsprache
                    AppLogger.d("LocalizedApp", "Verwende Systemsprache")
                    context
                }

                else -> {
                    // Verwende ausgewählte Sprache
                    AppLogger.d("LocalizedApp", "Verwende Sprache: $currentLanguage")
                    LocaleManager.createLocalizedContext(context, currentLanguage)
                }
            }
        }

    // Verwende den lokalisierten Context für die gesamte App
    CompositionLocalProvider(LocalContext provides localizedContext) {
        // Erzwinge Recomposition bei Sprachänderung
        key(currentLanguage) { content() }
    }
}
