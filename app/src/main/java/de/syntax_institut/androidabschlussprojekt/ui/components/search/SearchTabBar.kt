package de.syntax_institut.androidabschlussprojekt.ui.components.search

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import de.syntax_institut.androidabschlussprojekt.R

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
