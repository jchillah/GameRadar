package de.syntax_institut.androidabschlussprojekt.ui.screens

import android.net.*
import androidx.activity.*
import androidx.activity.compose.*
import androidx.activity.result.contract.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import de.syntax_institut.androidabschlussprojekt.*
import de.syntax_institut.androidabschlussprojekt.R
import de.syntax_institut.androidabschlussprojekt.data.local.models.*
import de.syntax_institut.androidabschlussprojekt.data.repositories.*
import de.syntax_institut.androidabschlussprojekt.ui.components.common.*
import de.syntax_institut.androidabschlussprojekt.ui.components.settings.*
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.*
import de.syntax_institut.androidabschlussprojekt.utils.*
import kotlinx.coroutines.*
import org.koin.androidx.compose.*
import org.koin.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
        modifier: Modifier = Modifier,
        viewModel: SettingsViewModel = koinViewModel(),
        favoritesViewModel: FavoritesViewModel = koinViewModel(),
) {
        val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
        val autoRefreshEnabled by viewModel.autoRefreshEnabled.collectAsState()
        val imageQuality by viewModel.imageQuality.collectAsState()
        val language by viewModel.language.collectAsState()
        val gamingModeEnabled by viewModel.gamingModeEnabled.collectAsState()
        val performanceModeEnabled by viewModel.performanceModeEnabled.collectAsState()
        val shareGamesEnabled by viewModel.shareGamesEnabled.collectAsState()
        val darkModeEnabled by viewModel.darkModeEnabled.collectAsState()
        var showAboutDialog by remember { mutableStateOf(false) }
        var showPrivacyDialog by remember { mutableStateOf(false) }
        val exportResult by favoritesViewModel.exportResult.collectAsState()
        val importResult by favoritesViewModel.importResult.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        val context = LocalContext.current
        val canUseLauncher = context is ComponentActivity
        val coroutineScope = rememberCoroutineScope()

        val gameRepository: GameRepository = koinInject()
        var cacheStats by remember { mutableStateOf<CacheStats?>(null) }
        var lastSyncTime by remember { mutableStateOf<Long?>(null) }
        val analyticsEnabled by viewModel.analyticsEnabled.collectAsState()
        val isProUser by viewModel.proStatus.collectAsState()
        val adsEnabled by viewModel.adsEnabled.collectAsState()

        // Sessionbasierte Freischaltung für Favoriten- und Wishlist-Export
        var isFavoritesExportUnlocked by rememberSaveable { mutableStateOf(isProUser) }
        var isWishlistExportUnlocked by rememberSaveable { mutableStateOf(isProUser) }
        val rewardedAdFavoritesRewardText =
                stringResource(R.string.rewarded_ad_favorites_reward_text)
        val rewardedAdWishlistRewardText = stringResource(R.string.rewarded_ad_wishlist_reward_text)

        // SAF-Launcher für Export und Import nur, wenn möglich
        val exportLauncher =
                if (canUseLauncher) {
                        rememberLauncherForActivityResult(
                                ActivityResultContracts.CreateDocument("application/json")
                        ) { uri: Uri? ->
                                uri?.let {
                                        coroutineScope.launch {
                                                favoritesViewModel.exportFavoritesToUri(context, it)
                                        }
                                }
                        }
                } else null

        LaunchedEffect(Unit) {
                coroutineScope.launch {
                        cacheStats = gameRepository.getCacheStats()
                        lastSyncTime = gameRepository.getLastSyncTime()
                }
        }

        // Crashlytics-Einstellung überwachen und anwenden
        LaunchedEffect(Unit) {
                // Setze initiale Crashlytics-Einstellung basierend auf Analytics-Opt-In
                val analyticsEnabled = viewModel.analyticsEnabled.value
                CrashlyticsHelper.setCrashlyticsEnabled(analyticsEnabled)
        }

        Column(
                modifier = modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                // --- Pro-Status & Werbung ---
                Card(
                        modifier =
                                Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors =
                                CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                ) {
                        Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(16.dp)
                        ) {
                                Icon(
                                        Icons.Default.Star,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                        text = stringResource(R.string.pro_status_title),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Tooltip(text = stringResource(R.string.pro_status_tooltip))
                        }
                        ProStatusBanner(
                                isProUser = isProUser,
                                onUpgradeClick = {}, // TODO: Upgrade-Dialog oder Billing-Flow
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                        HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                thickness = DividerDefaults.Thickness,
                                color = DividerDefaults.color
                        )
                        Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(16.dp)
                        ) {
                                Icon(
                                        Icons.Default.EmojiEvents,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                        text = stringResource(R.string.ads_section),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Tooltip(text = stringResource(R.string.ads_section_tooltip))
                        }
                        SettingsSwitchItem(
                                icon = Icons.Default.EmojiEvents,
                                title = stringResource(R.string.ads_enabled),
                                subtitle = stringResource(R.string.ads_enabled_description),
                                checked = adsEnabled,
                                onCheckedChange = { viewModel.setAdsEnabled(it) },
                                // modifier = Modifier.padding(horizontal = 16.dp) // falls benötigt
                        )
                }
                Spacer(modifier = Modifier.height(8.dp))

                // --- Cache Management ---
                CacheManagementCard(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        cacheSize = cacheStats?.count ?: 0,
                        maxCacheSize = remember { CacheUtils.calculateRecommendedMaxCacheSize() },
                        lastSyncTime = lastSyncTime,
                        onClearCache = {
                                coroutineScope.launch {
                                        gameRepository.clearCache()
                                        cacheStats = gameRepository.getCacheStats()
                                        lastSyncTime = gameRepository.getLastSyncTime()
                                }
                        },
                        onOptimizeCache = {
                                coroutineScope.launch {
                                        gameRepository.optimizeCache()
                                        cacheStats = gameRepository.getCacheStats()
                                        lastSyncTime = gameRepository.getLastSyncTime()
                                }
                        }
                )
                Spacer(modifier = Modifier.height(8.dp))

                // --- Sprache, Design, Datenschutz etc. ---
                SettingsSection(title = stringResource(R.string.notifications_section)) {
                        SectionNotifications(
                                notificationsEnabled = notificationsEnabled,
                                onCheckedChange = viewModel::setNotificationsEnabled
                        )
                }

                // Daten & Synchronisation-Sektion ausgelagert
                SettingsSection(title = stringResource(R.string.data_sync_section)) {
                        SectionDataSync(
                                autoRefreshEnabled = autoRefreshEnabled,
                                imageQuality = imageQuality,
                                onAutoRefreshChange = viewModel::setAutoRefreshEnabled,
                                onImageQualityChange = viewModel::setImageQuality
                        )
                }

                // Sprachsektion ausgelagert
                SettingsSection(title = stringResource(R.string.language_section)) {
                        SectionLanguage(
                                language = language,
                                onLanguageChange = viewModel::setLanguage
                        )
                }

                // Gaming-Features-Sektion ausgelagert
                SettingsSection(title = stringResource(R.string.gaming_features_section)) {
                        SectionGamingFeatures(
                                gamingModeEnabled = gamingModeEnabled,
                                performanceModeEnabled = performanceModeEnabled,
                                shareGamesEnabled = shareGamesEnabled,
                                onGamingModeChange = viewModel::setGamingModeEnabled,
                                onPerformanceModeChange = viewModel::setPerformanceModeEnabled,
                                onShareGamesChange = viewModel::setShareGamesEnabled
                        )
                }

                // Design-Sektion ausgelagert
                SettingsSection(title = stringResource(R.string.design_section)) {
                        SectionDesign(
                                darkModeEnabled = darkModeEnabled,
                                onDarkModeChange = viewModel::setDarkModeEnabled
                        )
                }

                // "Über die App"-Sektion ausgelagert
                SettingsSection(title = stringResource(R.string.about_app_section)) {
                        SectionAbout(
                                onShowAboutDialog = { showAboutDialog = true },
                                onShowPrivacyDialog = { showPrivacyDialog = true }
                        )
                }

                // Werbung & Analytics Sektion
                SettingsSection(title = stringResource(R.string.analytics_section)) {
                        Row(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Icon(
                                        imageVector = Icons.Default.BarChart,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                        text = stringResource(R.string.analytics_enabled),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onBackground
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Switch(
                                        checked = analyticsEnabled,
                                        onCheckedChange = { viewModel.setAnalyticsEnabled(it) },
                                        colors =
                                                SwitchDefaults.colors(
                                                        checkedThumbColor =
                                                                MaterialTheme.colorScheme.primary,
                                                        checkedTrackColor =
                                                                MaterialTheme.colorScheme
                                                                        .primaryContainer,
                                                        uncheckedThumbColor =
                                                                MaterialTheme.colorScheme.outline,
                                                        uncheckedTrackColor =
                                                                MaterialTheme.colorScheme
                                                                        .surfaceVariant
                                                )
                                )
                        }
                        Text(
                                text = stringResource(R.string.analytics_enabled_description),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier =
                                        Modifier.padding(start = 48.dp, end = 16.dp, bottom = 8.dp)
                        )
                        if (isProUser || BuildConfig.DEBUG) {
                                Row(
                                        modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                                text =
                                                        stringResource(
                                                                R.string.pro_ads_switch_label
                                                        ),
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.onBackground
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Switch(
                                                checked = adsEnabled,
                                                onCheckedChange = { viewModel.setAdsEnabled(it) },
                                                colors =
                                                        SwitchDefaults.colors(
                                                                checkedThumbColor =
                                                                        MaterialTheme.colorScheme
                                                                                .primary,
                                                                checkedTrackColor =
                                                                        MaterialTheme.colorScheme
                                                                                .primaryContainer,
                                                                uncheckedThumbColor =
                                                                        MaterialTheme.colorScheme
                                                                                .outline,
                                                                uncheckedTrackColor =
                                                                        MaterialTheme.colorScheme
                                                                                .surfaceVariant
                                                        )
                                        )
                                }
                                Text(
                                        text = stringResource(R.string.pro_ads_switch_desc),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier =
                                                Modifier.padding(
                                                        start = 48.dp,
                                                        end = 16.dp,
                                                        bottom = 8.dp
                                                )
                                )
                        }
                        BannerAdView(
                                adUnitId = "ca-app-pub-7269049262039376/9765911397",
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                analyticsEnabled = analyticsEnabled
                        )
                }
                // Datenbank-Management und Dialoge werden immer angezeigt (oder nach Wunsch)
                SettingsSection(title = stringResource(R.string.database_management_section)) {
                        SectionDatabase()
                        Spacer(modifier = Modifier.height(8.dp))
                }
                // TODO: unsicher ob ich diese Cards überhaupt nutzen werde
                /*
                // Favoriten-Export/Import Card
                Card(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors =
                                CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                ) {
                        Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(16.dp)
                        ) {
                                Icon(
                                        Icons.Default.FileUpload,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                        text = stringResource(R.string.export_favorites),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                )
                        }
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                        Button(
                                                onClick = {
                                                        if (isProUser || isFavoritesExportUnlocked
                                                        ) {
                                                                exportLauncher?.launch(
                                                                        "favoritenliste_export.json"
                                                                )
                                                        } else {
                                                                coroutineScope.launch {
                                                                        snackbarHostState
                                                                                .showSnackbar(
                                                                                        rewardedAdFavoritesRewardText
                                                                                )
                                                                }
                                                        }
                                                },
                                                enabled = isProUser || isFavoritesExportUnlocked,
                                                modifier = Modifier.weight(1f)
                                        ) {
                                                Icon(
                                                        Icons.Default.FileUpload,
                                                        contentDescription = null
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(stringResource(R.string.export))
                                        }
                                        if ((!isProUser && adsEnabled) || BuildConfig.DEBUG) {
                                                RewardedAdButton(
                                                        adUnitId =
                                                                "ca-app-pub-3940256099942544/5224354917",
                                                        adsEnabled = adsEnabled,
                                                        isProUser = isProUser,
                                                        rewardText = rewardedAdFavoritesRewardText,
                                                        onReward = {
                                                                isFavoritesExportUnlocked = true
                                                        }
                                                )
                                        }
                                        if (isFavoritesExportUnlocked && !isProUser) {
                                                Spacer(modifier = Modifier.width(8.dp))
                                                AssistChip(
                                                        onClick = {},
                                                        label = {
                                                                Text(
                                                                        stringResource(
                                                                                R.string
                                                                                        .export_unlocked
                                                                        )
                                                                )
                                                        },
                                                        leadingIcon = {
                                                                Icon(
                                                                        Icons.Default.Check,
                                                                        contentDescription = null
                                                                )
                                                        }
                                                )
                                        }
                                }
                        }
                }
                 */

                /*
                // Wishlist-Export/Import Card
                Card(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(4.dp),
                        colors =
                                CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                ) {
                        Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(16.dp)
                        ) {
                                Icon(
                                        Icons.Default.FileUpload,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                        text = stringResource(R.string.wishlist_export),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                )
                        }
                        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                        Button(
                                                onClick = {
                                                        if (isProUser || isWishlistExportUnlocked) {
                                                                exportLauncher?.launch(
                                                                        "wunschliste_export.json"
                                                                )
                                                        } else {
                                                                coroutineScope.launch {
                                                                        snackbarHostState
                                                                                .showSnackbar(
                                                                                        rewardedAdWishlistRewardText
                                                                                )
                                                                }
                                                        }
                                                },
                                                enabled = isProUser || isWishlistExportUnlocked,
                                                modifier = Modifier.weight(1f)
                                        ) {
                                                Icon(
                                                        Icons.Default.FileUpload,
                                                        contentDescription = null
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(stringResource(R.string.export))
                                        }
                                        if ((!isProUser && adsEnabled) || BuildConfig.DEBUG) {
                                                RewardedAdButton(
                                                        adUnitId =
                                                                "ca-app-pub-3940256099942544/5224354917",
                                                        adsEnabled = adsEnabled,
                                                        isProUser = isProUser,
                                                        rewardText = rewardedAdWishlistRewardText,
                                                        onReward = {
                                                                isWishlistExportUnlocked = true
                                                        }
                                                )
                                        }
                                        if (isWishlistExportUnlocked && !isProUser) {
                                                Spacer(modifier = Modifier.width(8.dp))
                                                AssistChip(
                                                        onClick = {},
                                                        label = {
                                                                Text(
                                                                        stringResource(
                                                                                R.string
                                                                                        .export_unlocked
                                                                        )
                                                                )
                                                        },
                                                        leadingIcon = {
                                                                Icon(
                                                                        Icons.Default.Check,
                                                                        contentDescription = null
                                                                )
                                                        }
                                                )
                                        }
                                }
                        }
                }
                 */

                /*
                // Snackbar für Export/Import-Feedback und Dialoge
                LaunchedEffect(exportResult) {
                        exportResult?.let {
                                snackbarHostState.showSnackbar(
                                        if (it.isSuccess) "Favoriten erfolgreich exportiert!"
                                        else
                                                "Fehler beim Export: ${it.exceptionOrNull()?.localizedMessage ?: "Unbekannter Fehler"}"
                                )
                        }
                }

                 */
                LaunchedEffect(importResult) {
                        importResult?.let {
                                snackbarHostState.showSnackbar(
                                        if (it.isSuccess) "Favoriten erfolgreich importiert!"
                                        else
                                                "Fehler beim Import: ${it.exceptionOrNull()?.localizedMessage ?: "Unbekannter Fehler"}"
                                )
                        }
                }
                if (showAboutDialog) {
                        AboutAppDialog(onDismiss = { showAboutDialog = false })
                }
                if (showPrivacyDialog) {
                        PrivacyPolicyDialog(onDismiss = { showPrivacyDialog = false })
                }
        }
        // Entferne die Anzeige von BannerAdView am Dateiende (mit viewModel.adsEnabled)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                SnackbarHost(hostState = snackbarHostState)
        }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsScreenPreview() {
        SettingsScreen()
}
