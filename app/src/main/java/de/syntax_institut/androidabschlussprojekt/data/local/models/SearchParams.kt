package de.syntax_institut.androidabschlussprojekt.data.local.models

data class SearchParams(
        val query: String = "",
        val platforms: String? = null,
        val genres: String? = null,
        val ordering: String? = null
    )