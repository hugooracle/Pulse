package pt.pulse.core.domain.data.model.searchResult

import pt.pulse.core.domain.data.type.SearchResultType

data class SearchSuggestions(
    val queries: List<String>,
    val recommendedItems: List<SearchResultType>,
)