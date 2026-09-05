package pt.pulse.core.domain.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import pt.pulse.core.domain.data.model.browse.album.Track

@Entity(tableName = "queue")
data class QueueEntity(
    @PrimaryKey(autoGenerate = false)
    val queueId: Long = 0,
    val listTrack: List<Track>,
)