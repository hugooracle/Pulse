package pt.pulse.core.data.mediaservice

import pt.pulse.core.domain.repository.AnalyticsRepository

actual fun createMediaServiceHandler(
    dataStoreManager: pt.pulse.core.domain.manager.DataStoreManager,
    songRepository: pt.pulse.core.domain.repository.SongRepository,
    streamRepository: pt.pulse.core.domain.repository.StreamRepository,
    localPlaylistRepository: pt.pulse.core.domain.repository.LocalPlaylistRepository,
    analyticsRepository: AnalyticsRepository,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
): pt.pulse.core.domain.mediaservice.handler.MediaPlayerHandler =
    JvmMediaPlayerHandlerImpl(
        dataStoreManager = dataStoreManager,
        songRepository = songRepository,
        streamRepository = streamRepository,
        localPlaylistRepository = localPlaylistRepository,
        analyticsRepository = analyticsRepository,
        coroutineScope = coroutineScope,
    )