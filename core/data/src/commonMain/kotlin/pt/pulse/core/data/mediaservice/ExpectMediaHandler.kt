package pt.pulse.core.data.mediaservice

import pt.pulse.core.domain.manager.DataStoreManager
import pt.pulse.core.domain.mediaservice.handler.MediaPlayerHandler
import pt.pulse.core.domain.repository.AnalyticsRepository
import pt.pulse.core.domain.repository.LocalPlaylistRepository
import pt.pulse.core.domain.repository.SongRepository
import pt.pulse.core.domain.repository.StreamRepository
import kotlinx.coroutines.CoroutineScope

expect fun createMediaServiceHandler(
    dataStoreManager: DataStoreManager,
    songRepository: SongRepository,
    streamRepository: StreamRepository,
    localPlaylistRepository: LocalPlaylistRepository,
    analyticsRepository: AnalyticsRepository,
    coroutineScope: CoroutineScope,
): MediaPlayerHandler