package pt.pulse.core.domain.data.model.browse.artist

import pt.pulse.core.domain.data.model.searchResult.songs.Thumbnail
import pt.pulse.core.domain.data.type.HomeContentType

data class ResultPlaylist(
    val id: String,
    val author: String,
    val thumbnails: List<Thumbnail>,
    val title: String,
) : HomeContentType