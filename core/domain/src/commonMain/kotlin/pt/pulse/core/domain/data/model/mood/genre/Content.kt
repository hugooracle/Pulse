package pt.pulse.core.domain.data.model.mood.genre

import pt.pulse.core.domain.data.model.searchResult.songs.Thumbnail
import pt.pulse.core.domain.data.type.HomeContentType

data class Content(
    val playlistBrowseId: String,
    val thumbnail: List<Thumbnail>?,
    val title: Title,
) : HomeContentType