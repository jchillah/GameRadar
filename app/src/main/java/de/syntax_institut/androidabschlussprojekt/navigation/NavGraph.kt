package de.syntax_institut.androidabschlussprojekt.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.navigation.*
import androidx.navigation.compose.*
import de.syntax_institut.androidabschlussprojekt.ui.screens.*
import de.syntax_institut.androidabschlussprojekt.ui.viewmodels.*
import org.koin.androidx.compose.*

@Composable
fun NavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    isDarkTheme: Boolean,
    setDarkTheme: (Boolean) -> Unit,
    isOffline: Boolean,
    lastSyncTime: Long?,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.SEARCH,
        modifier = modifier
    ) {
        composable(
            route = Routes.SEARCH
        ) {
            SearchScreen(navController = navController)
        }

        composable(
            route = Routes.FAVORITES
        ) {
            FavoritesScreen(navController = navController)
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("gameId") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("gameId") ?: return@composable
            DetailScreen(id, navController)
        }

        composable(
            route = Routes.SETTINGS,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(700)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(700)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(700)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(700)
                )
            }
        ) {
            val settingsViewModel: SettingsViewModel = koinViewModel()
            val context = LocalContext.current

            // Initialisiere SettingsViewModel mit Context
            LaunchedEffect(Unit) {
                settingsViewModel.initialize(context.applicationContext)
            }

            val notificationsEnabled by settingsViewModel.notificationsEnabled.collectAsState()
            val autoRefreshEnabled by settingsViewModel.autoRefreshEnabled.collectAsState()
            val imageQuality by settingsViewModel.imageQuality.collectAsState()
            val language by settingsViewModel.language.collectAsState()
            val gamingModeEnabled by settingsViewModel.gamingModeEnabled.collectAsState()
            val performanceModeEnabled by settingsViewModel.performanceModeEnabled.collectAsState()
            val shareGamesEnabled by settingsViewModel.shareGamesEnabled.collectAsState()
            val cacheSize by settingsViewModel.cacheSize.collectAsState()
            
            SettingsScreen(
                isDarkTheme = isDarkTheme,
                setDarkTheme = setDarkTheme,
                cacheSize = cacheSize,
                isOffline = isOffline,
                lastSyncTime = lastSyncTime,
                notificationsEnabled = notificationsEnabled,
                setNotificationsEnabled = settingsViewModel::setNotificationsEnabled,
                autoRefreshEnabled = autoRefreshEnabled,
                setAutoRefreshEnabled = settingsViewModel::setAutoRefreshEnabled,
                imageQuality = imageQuality,
                setImageQuality = settingsViewModel::setImageQuality,
                language = language,
                setLanguage = settingsViewModel::setLanguage,
                clearCache = settingsViewModel::clearCache,
                aboutApp = settingsViewModel::aboutApp,
                privacyPolicy = settingsViewModel::privacyPolicy,
                gamingModeEnabled = gamingModeEnabled,
                setGamingModeEnabled = settingsViewModel::setGamingModeEnabled,
                performanceModeEnabled = performanceModeEnabled,
                setPerformanceModeEnabled = settingsViewModel::setPerformanceModeEnabled,
                shareGamesEnabled = shareGamesEnabled,
                setShareGamesEnabled = settingsViewModel::setShareGamesEnabled,
                optimizeCache = settingsViewModel::optimizeCache
            )
        }
    }
}
