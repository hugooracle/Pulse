package pt.pulse.core.kotlinytmusicscraper.models.response

import pt.pulse.core.kotlinytmusicscraper.models.Button
import pt.pulse.core.kotlinytmusicscraper.models.Continuation
import pt.pulse.core.kotlinytmusicscraper.models.Menu
import pt.pulse.core.kotlinytmusicscraper.models.MusicResponsiveListItemRenderer
import pt.pulse.core.kotlinytmusicscraper.models.MusicShelfRenderer
import pt.pulse.core.kotlinytmusicscraper.models.MusicTwoRowItemRenderer
import pt.pulse.core.kotlinytmusicscraper.models.ResponseContext
import pt.pulse.core.kotlinytmusicscraper.models.Runs
import pt.pulse.core.kotlinytmusicscraper.models.SectionListRenderer
import pt.pulse.core.kotlinytmusicscraper.models.SubscriptionButton
import pt.pulse.core.kotlinytmusicscraper.models.Tabs
import pt.pulse.core.kotlinytmusicscraper.models.ThumbnailRenderer
import pt.pulse.core.kotlinytmusicscraper.models.youtube.data.YouTubeDataPage
import kotlinx.serialization.Serializable

@Serializable
data class BrowseResponse(
    val contents: Contents?,
    val continuationContents: ContinuationContents?,
    val header: Header?,
    val microformat: Microformat?,
    val responseContext: ResponseContext,
    val background: Background?,
    val onResponseReceivedActions: List<OnResponseReceivedActions>?,
) {
    @Serializable
    data class OnResponseReceivedActions(
        val appendContinuationItemsAction: AppendContinuationItemsAction?,
    ) {
        @Serializable
        data class AppendContinuationItemsAction(
            val continuationItems: List<ContinuationItem>,
            val targetId: String,
        ) {
            @Serializable
            data class ContinuationItem(
                val musicResponsiveListItemRenderer: MusicResponsiveListItemRenderer?,
                val continuationItemRenderer:
                    YouTubeDataPage.Contents.TwoColumnWatchNextResults.Results.Results.Content.ItemSectionRenderer.Content.ContinuationItemRenderer?,
            )
        }
    }

    @Serializable
    data class Background(
        val musicThumbnailRenderer: ThumbnailRenderer.MusicThumbnailRenderer?,
    )

    @Serializable
    data class Contents(
        val singleColumnBrowseResultsRenderer: Tabs?,
        val twoColumnBrowseResultsRenderer: TwoColumnBrowseResultsRenderer?,
        val sectionListRenderer: SectionListRenderer?,
    ) {
        @Serializable
        data class TwoColumnBrowseResultsRenderer(
            val secondaryContents: SecondaryContents?,
            val tabs: List<Tabs.Tab>?,
        ) {
            @Serializable
            data class SecondaryContents(
                val sectionListRenderer: SectionListRenderer?,
            )
        }
    }

    @Serializable
    data class ContinuationContents(
        val sectionListContinuation: SectionListContinuation?,
        val musicPlaylistShelfContinuation: MusicPlaylistShelfContinuation?,
        val musicShelfContinuation: SearchResponse.ContinuationContents.MusicShelfContinuation?,
        val gridContinuation: GridContinuation?,
    ) {
        @Serializable
        data class GridContinuation(
            val itemSize: String?,
            val items: List<GridContinuation.Item>,
            val continuations: List<Continuation>?,
        ) {
            @Serializable
            data class Item(
                val musicTwoRowItemRenderer: MusicTwoRowItemRenderer?,
            )
        }

        @Serializable
        data class SectionListContinuation(
            val contents: List<SectionListRenderer.Content>,
            val continuations: List<Continuation>?,
        )

        @Serializable
        data class MusicPlaylistShelfContinuation(
            val contents: List<MusicShelfRenderer.Content>,
            val continuations: List<Continuation>?,
        )
    }

    @Serializable
    data class Header(
        val musicImmersiveHeaderRenderer: MusicImmersiveHeaderRenderer?,
        val musicDetailHeaderRenderer: MusicDetailHeaderRenderer?,
        val musicEditablePlaylistDetailHeaderRenderer: MusicEditablePlaylistDetailHeaderRenderer?,
        val musicVisualHeaderRenderer: MusicVisualHeaderRenderer?,
        val musicHeaderRenderer: MusicHeaderRenderer?,
    ) {
        @Serializable
        data class MusicImmersiveHeaderRenderer(
            val title: Runs,
            val description: Runs?,
            val thumbnail: ThumbnailRenderer?,
            val playButton: Button?,
            val startRadioButton: Button?,
            val subscriptionButton: SubscriptionButton?,
            val menu: Menu,
        )

        @Serializable
        data class MusicDetailHeaderRenderer(
            val title: Runs,
            val subtitle: Runs,
            val secondSubtitle: Runs,
            val description: Runs?,
            val thumbnail: ThumbnailRenderer,
            val menu: Menu,
        )

        @Serializable
        data class MusicEditablePlaylistDetailHeaderRenderer(
            val header: Header,
        ) {
            @Serializable
            data class Header(
                val musicDetailHeaderRenderer: MusicDetailHeaderRenderer?,
                val musicResponsiveHeaderRenderer: SectionListRenderer.Content.MusicResponsiveHeaderRenderer?,
            )
        }

        @Serializable
        data class MusicVisualHeaderRenderer(
            val title: Runs,
            val foregroundThumbnail: ThumbnailRenderer,
            val thumbnail: ThumbnailRenderer?,
        )

        @Serializable
        data class MusicHeaderRenderer(
            val title: Runs,
        )
    }

    @Serializable
    data class Microformat(
        val microformatDataRenderer: MicroformatDataRenderer?,
    ) {
        @Serializable
        data class MicroformatDataRenderer(
            val urlCanonical: String?,
        )
    }
}