package pt.pulse.core.kotlinytmusicscraper.pages

import pt.pulse.core.kotlinytmusicscraper.models.AlbumItem
import pt.pulse.core.kotlinytmusicscraper.models.VideoItem

data class ExplorePage(
    val released: List<AlbumItem>,
    val musicVideo: List<VideoItem>,
)