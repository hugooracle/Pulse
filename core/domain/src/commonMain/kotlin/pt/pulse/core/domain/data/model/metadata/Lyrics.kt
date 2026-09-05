package pt.pulse.core.domain.data.model.metadata

import kotlinx.serialization.Serializable

@Serializable
data class Lyrics(
    val error: Boolean = false,
    val lines: List<Line>?,
    val syncType: String?,
    val pulseLyrics: PulseLyrics? = null,
)

@Serializable
data class PulseLyrics(
    val id: String,
    val vote: Int,
)