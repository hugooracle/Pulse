package pt.pulse.core.domain.data.model.browse.artist

import pt.pulse.core.domain.data.model.searchResult.songs.Thumbnail
import pt.pulse.core.domain.data.type.HomeContentType

data class ResultAlbum(
    val browseId: String,
    val isExplicit: Boolean,
    val thumbnails: List<Thumbnail>,
    val title: String,
    val year: String,
) : HomeContentType