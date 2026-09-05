package pt.pulse.core.domain.data.model.home.chart

import pt.pulse.core.domain.data.model.browse.artist.ResultPlaylist

data class ChartItemPlaylist(
    val title: String,
    val playlists: List<ResultPlaylist>,
)