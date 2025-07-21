package de.syntax_institut.androidabschlussprojekt.ui.components.search

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import de.syntax_institut.androidabschlussprojekt.R

/**
 * Tab-Bar für verschiedene Suchkategorien.
 *
 * Features:
 * - Drei Tabs: Alle, Neu veröffentlicht, Top bewertet
 * - Material3 TabRow-Design
 * - Lokalisierte Tab-Titel
 * - Einzeilige Textbegrenzung mit Ellipsis
 * - Callback für Tab-Wechsel
 * - Accessibility-Unterstützung
 *
 * @param selectedTab Index des ausgewählten Tabs (0-2)
 * @param onTabSelected Callback bei Tab-Wechsel
 */
@Composable
fun SearchTabBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    val tabTitles =
        listOf(
            stringResource(R.string.search_tab_all),
            stringResource(R.string.search_tab_new),
            stringResource(R.string.search_tab_top_rated)
        )

    TabRow(selectedTabIndex = selectedTab) {
        tabTitles.forEachIndexed { index, title ->
            Tab(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                text = { Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis) }
            )
        }
    }
}
