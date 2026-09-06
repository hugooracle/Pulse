package pt.pulse.core.data.mediaservice

actual fun createMediaServiceHandler(
    dataStoreManager: pt.pulse.core.domain.manager.DataStoreManager,
    songRepository: pt.pulse.core.domain.repository.SongRepository,
    streamRepository: pt.pulse.core.domain.repository.StreamRepository,
    localPlaylistRepository: pt.pulse.core.domain.repository.LocalPlaylistRepository,
    analyticsRepository: pt.pulse.core.domain.repository.AnalyticsRepository,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
): pt.pulse.core.domain.mediaservice.handler.MediaPlayerHandler {
    TODO("Not yet implemented")
}