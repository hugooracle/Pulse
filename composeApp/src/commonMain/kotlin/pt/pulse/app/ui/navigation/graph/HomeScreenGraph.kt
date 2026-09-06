package pt.pulse.app.ui.navigation.graph

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import pt.pulse.app.ui.navigation.destination.home.CreditDestination
import pt.pulse.app.ui.navigation.destination.home.MoodDestination
import pt.pulse.app.ui.navigation.destination.home.NotificationDestination
import pt.pulse.app.ui.navigation.destination.home.RecentlySongsDestination
import pt.pulse.app.ui.navigation.destination.home.SettingsDestination
import pt.pulse.app.ui.screen.home.MoodScreen
import pt.pulse.app.ui.screen.home.NotificationScreen
import pt.pulse.app.ui.screen.home.RecentlySongsScreen
import pt.pulse.app.ui.screen.home.SettingScreen
import pt.pulse.app.ui.screen.other.CreditScreen

fun NavGraphBuilder.homeScreenGraph(
    innerPadding: PaddingValues,
    navController: NavController,
) {
    composable<CreditDestination> {
        CreditScreen(
            paddingValues = innerPadding,
            navController = navController,
        )
    }
    composable<MoodDestination> { entry ->
        val params = entry.toRoute<MoodDestination>().params
        MoodScreen(
            navController = navController,
            params = params,
        )
    }
    composable<NotificationDestination> {
        NotificationScreen(
            navController = navController,
        )
    }
    composable<RecentlySongsDestination> {
        RecentlySongsScreen(
            navController = navController,
            innerPadding = innerPadding,
        )
    }
    composable<SettingsDestination> {
        SettingScreen(
            navController = navController,
            innerPadding = innerPadding,
        )
    }
}
