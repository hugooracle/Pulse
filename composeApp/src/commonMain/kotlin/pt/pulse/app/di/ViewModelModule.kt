package pt.pulse.app.di

import pt.pulse.app.viewModel.AlbumViewModel
import pt.pulse.app.utils.VersionManager
import pt.pulse.app.viewModel.AnalyticsViewModel
import pt.pulse.app.viewModel.ListenTogetherSettingsViewModel
import pt.pulse.app.viewModel.ListenTogetherViewModel
import pt.pulse.app.viewModel.ArtistViewModel
import pt.pulse.app.viewModel.HomeViewModel
import pt.pulse.app.viewModel.ImportViewModel
import pt.pulse.app.viewModel.LibraryDynamicPlaylistViewModel
import pt.pulse.app.viewModel.LibraryViewModel
import pt.pulse.app.viewModel.LocalPlaylistViewModel
import pt.pulse.app.viewModel.LogInViewModel
import pt.pulse.app.viewModel.MoodViewModel
import pt.pulse.app.viewModel.MoreAlbumsViewModel
import pt.pulse.app.viewModel.NotificationViewModel
import pt.pulse.app.viewModel.NowPlayingBottomSheetViewModel
import pt.pulse.app.viewModel.PlaylistViewModel
import pt.pulse.app.viewModel.PodcastViewModel
import pt.pulse.app.viewModel.RecentlySongsViewModel
import pt.pulse.app.viewModel.SearchViewModel
import pt.pulse.app.viewModel.AutoEqViewModel
import pt.pulse.app.viewModel.SettingsViewModel
import pt.pulse.app.viewModel.SharedViewModel
import pt.pulse.app.viewModel.SongSelectionViewModel
import pt.pulse.app.viewModel.WrappedViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule =
    module {
        single {
            SharedViewModel(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
        single {
            SearchViewModel(
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            SongSelectionViewModel(
                get(),
                get(),
            )
        }
        viewModel {
            NowPlayingBottomSheetViewModel(
                get(),
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            LibraryViewModel(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            LibraryDynamicPlaylistViewModel(
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            ImportViewModel(
                get(),
            )
        }
        viewModel {
            AlbumViewModel(
                get(),
                get(),
            )
        }
        viewModel {
            HomeViewModel(
                get(),
                get(),
            )
        }
        viewModel {
            AutoEqViewModel(
                get(),
                get(),
            )
        }
        viewModel {
            SettingsViewModel(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            ArtistViewModel(
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            PlaylistViewModel(
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            LogInViewModel(
                get(),
            )
        }
        viewModel {
            PodcastViewModel(
                get(),
            )
        }
        viewModel {
            MoreAlbumsViewModel(
                get(),
            )
        }
        viewModel {
            RecentlySongsViewModel(
                get(),
            )
        }
        viewModel {
            LocalPlaylistViewModel(
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            NotificationViewModel(
                get(),
            )
        }
        viewModel {
            MoodViewModel(
                get(),
                get(),
            )
        }
        viewModel {
            AnalyticsViewModel(
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            WrappedViewModel(
                get(),
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            ListenTogetherSettingsViewModel(get())
        }
        viewModel {
            ListenTogetherViewModel(
                repository = get(),
                dataStore = get(),
                bridge = get(),
            )
        }

    }