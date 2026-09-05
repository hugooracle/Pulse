package pt.pulse.app.ui.component

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import pt.pulse.app.expect.ui.PlatformBackdrop
import pt.pulse.app.ui.icon.AutoGraph
import pt.pulse.app.ui.icon.Home
import pt.pulse.app.ui.icon.LibraryMusic
import pt.pulse.app.ui.icon.Search
import pt.pulse.app.ui.icon.Sensors
import pt.pulse.app.ui.icon.PulseIcons
import pt.pulse.app.ui.navigation.destination.home.AnalyticsDestination
import pt.pulse.app.ui.navigation.destination.home.HomeDestination
import pt.pulse.app.ui.navigation.destination.library.LibraryDestination
import pt.pulse.app.ui.navigation.destination.library.MixForYouDestination
import pt.pulse.app.ui.navigation.destination.search.SearchDestination
import pt.pulse.app.viewModel.SharedViewModel
import org.jetbrains.compose.resources.StringResource
import pulse.composeapp.generated.resources.Res
import pulse.composeapp.generated.resources.analytics
import pulse.composeapp.generated.resources.home
import pulse.composeapp.generated.resources.library
import pulse.composeapp.generated.resources.mix
import pulse.composeapp.generated.resources.search
import kotlin.reflect.KClass

@Composable
expect fun LiquidGlassAppBottomNavigationBar(
    startDestination: Any = HomeDestination,
    navController: NavController,
    backdrop: PlatformBackdrop,
    viewModel: SharedViewModel,
    isScrolledToTop: Boolean = false,
    showAnalyticsTab: Boolean = false,
    showMixForYouTab: Boolean = false,
    onOpenNowPlaying: () -> Unit = {},
    reloadDestinationIfNeeded: (KClass<*>) -> Unit = { _ -> },
)

sealed class BottomNavScreen(
    val ordinal: Int,
    val destination: Any,
    val title: StringResource,
    val icon: @Composable () -> Unit,
) {
    data object Home : BottomNavScreen(
        ordinal = 0,
        destination = HomeDestination,
        title = Res.string.home,
        icon = {
            Icon(
                PulseIcons.Home,
                contentDescription = null,
            )
        },
    )

    data object Search : BottomNavScreen(
        ordinal = 1,
        destination = SearchDestination,
        title = Res.string.search,
        icon = {
            Icon(
                PulseIcons.Search,
                contentDescription = null,
            )
        },
    )

    data object Library : BottomNavScreen(
        ordinal = 2,
        destination = LibraryDestination,
        title = Res.string.library,
        icon = {
            Icon(
                imageVector = PulseIcons.LibraryMusic,
                contentDescription = null,
            )
        },
    )

    // Only shown when local tracking is enabled.
    data object Analytics : BottomNavScreen(
        ordinal = 3,
        destination = AnalyticsDestination,
        title = Res.string.analytics,
        icon = {
            Icon(
                imageVector = PulseIcons.AutoGraph,
                contentDescription = null,
            )
        },
    )

    // Only shown while signed in to YouTube — an anonymous session gets no mixes.
    // Labelled "Mix", not "Mix for you": the full title is the widest label in the bar and forces
    // every tab to be that wide. The screen itself still uses the full title.
    data object MixForYou : BottomNavScreen(
        ordinal = 4,
        destination = MixForYouDestination,
        title = Res.string.mix,
        icon = {
            Icon(
                imageVector = PulseIcons.Sensors,
                contentDescription = null,
            )
        },
    )
}