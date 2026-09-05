package pt.pulse.core.kotlinytmusicscraper.pages

import pt.pulse.core.kotlinytmusicscraper.models.SongItem

data class PlaylistContinuationPage(
    val songs: List<SongItem>,
    val continuation: String?,
)