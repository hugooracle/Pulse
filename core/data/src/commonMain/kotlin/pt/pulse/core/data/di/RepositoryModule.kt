package pt.pulse.core.data.di

import pt.pulse.core.common.Config.SERVICE_SCOPE
import pt.pulse.core.data.io.fileDir
import pt.pulse.core.data.repository.AccountRepositoryImpl
import pt.pulse.core.data.repository.AlbumRepositoryImpl
import pt.pulse.core.data.repository.AnalyticsRepositoryImpl
import pt.pulse.core.data.repository.ArtistRepositoryImpl
import pt.pulse.core.data.repository.AutoEqRepositoryImpl
import pt.pulse.core.data.lyrics.LyricsRomanizerRepositoryImpl
import pt.pulse.core.data.repository.CommonRepositoryImpl
import pt.pulse.core.data.repository.HomeRepositoryImpl
import pt.pulse.core.data.repository.ImportRepositoryImpl
import pt.pulse.core.data.repository.LocalPlaylistRepositoryImpl
import pt.pulse.core.data.repository.LyricsCanvasRepositoryImpl
import pt.pulse.core.data.repository.PlaylistRepositoryImpl
import pt.pulse.core.data.repository.PodcastRepositoryImpl
import pt.pulse.core.data.repository.SearchRepositoryImpl
import pt.pulse.core.data.repository.SongRepositoryImpl
import pt.pulse.core.data.repository.StreamRepositoryImpl
import pt.pulse.core.data.repository.UpdateRepositoryImpl
import pt.pulse.core.domain.repository.AccountRepository
import pt.pulse.core.domain.repository.AlbumRepository
import pt.pulse.core.domain.repository.AnalyticsRepository
import pt.pulse.core.domain.repository.ArtistRepository
import pt.pulse.core.domain.repository.AutoEqRepository
import pt.pulse.core.domain.repository.LyricsRomanizerRepository
import pt.pulse.core.domain.repository.CommonRepository
import pt.pulse.core.domain.repository.HomeRepository
import pt.pulse.core.domain.repository.ImportRepository
import pt.pulse.core.domain.repository.LocalPlaylistRepository
import pt.pulse.core.domain.repository.LyricsCanvasRepository
import pt.pulse.core.domain.repository.PlaylistRepository
import pt.pulse.core.domain.repository.PodcastRepository
import pt.pulse.core.domain.repository.SearchRepository
import pt.pulse.core.domain.repository.SongRepository
import pt.pulse.core.domain.repository.StreamRepository
import pt.pulse.core.domain.repository.UpdateRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule =
    module {
        single<AccountRepository>(createdAtStart = true) {
            AccountRepositoryImpl(get(), get())
        }

        single<AlbumRepository>(createdAtStart = true) {
            AlbumRepositoryImpl(get(), get())
        }

        single<ArtistRepository>(createdAtStart = true) {
            ArtistRepositoryImpl(get(), get(), get())
        }

        single<CommonRepository>(createdAtStart = true) {
            CommonRepositoryImpl(get(named(SERVICE_SCOPE)), get(), get(), get(), get()).apply {
                this.init("${fileDir()}/ytdlp-cookie.txt", get())
            }
        }

        // Lazy for the same reason its client is: the picker is the only thing that wants it.
        single<AutoEqRepository> {
            AutoEqRepositoryImpl(get(), get())
        }

        // Lazy: constructing it costs a few File.length() calls, but the kuromoji dictionary
        // behind it is loaded on first Japanese line and never before — so this must NOT be
        // createdAtStart, or every launch pays for a feature most listeners leave off. The path
        // is where Android keeps the downloaded ipadic pack (the APK no longer bundles it);
        // Desktop and iOS ignore it.
        single<LyricsRomanizerRepository> {
            LyricsRomanizerRepositoryImpl("${fileDir()}/kuromoji-ipadic")
        }

        single<HomeRepository>(createdAtStart = true) {
            HomeRepositoryImpl(get(), get())
        }

        single<ImportRepository>(createdAtStart = true) {
            ImportRepositoryImpl(get())
        }

        single<LocalPlaylistRepository>(createdAtStart = true) {
            LocalPlaylistRepositoryImpl(get(), get())
        }

        single<LyricsCanvasRepository>(createdAtStart = true) {
            LyricsCanvasRepositoryImpl(get(), get(), get(), get())
        }

        single<PlaylistRepository>(createdAtStart = true) {
            PlaylistRepositoryImpl(get(), get(), get())
        }

        single<PodcastRepository>(createdAtStart = true) {
            PodcastRepositoryImpl(get(), get())
        }

        single<SearchRepository>(createdAtStart = true) {
            SearchRepositoryImpl(get(), get())
        }

        single<SongRepository>(createdAtStart = true) {
            SongRepositoryImpl(get(), get(), get())
        }

        single<StreamRepository>(createdAtStart = true) {
            StreamRepositoryImpl(get(), get())
        }

        single<UpdateRepository>(createdAtStart = true) {
            UpdateRepositoryImpl(get())
        }

        single<AnalyticsRepository>(createdAtStart = true) {
            AnalyticsRepositoryImpl(get())
        }
    }
