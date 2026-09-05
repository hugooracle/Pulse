#!/usr/bin/env python3
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]


def edit(rel: str, fn):
    path = ROOT / rel
    if not path.exists():
        return
    current = path.read_text(encoding="utf-8")
    changed = fn(current)
    if changed != current:
        path.write_text(changed, encoding="utf-8")


# Desktop custom links remain pulse:// and no longer depend on an inherited web domain.
edit(
    "composeApp/src/jvmMain/kotlin/pt/pulse/app/DesktopDeepLinkHandler.kt",
    lambda s: s.replace("https://pulse.org/app/", "pulse://").replace("pulse.org", "Pulse"),
)

# Discord presence links directly to the track source. Remove inherited product/site artwork and sponsor links.
def fix_discord(s: str) -> str:
    s = s.replace(
        '"Listen on Pulse" to "https://pulse.org/app/watch?v=${song.videoId}",',
        '"Ouvir no YouTube Music" to "https://music.youtube.com/watch?v=${song.videoId}",',
    )
    s = re.sub(r'^\s*"Visit Pulse" to "https://github\.com/[^"\n]+",\s*$', '', s, flags=re.M)
    s = re.sub(r'^\s*smallImage = RpcImage\.ExternalImage\(APP_ICON\),\s*$', '', s, flags=re.M)
    s = re.sub(r'\n\s*private const val APP_ICON: String =\s*\n\s*"https://[^"\n]+"', '', s)
    return s

edit("core/service/kizzy/src/commonMain/kotlin/com/my/kizzy/DiscordRPC.kt", fix_discord)

# Share links use canonical YouTube Music URLs for songs, videos, playlists, albums and artists.
def fix_share_links(s: str) -> str:
    return (
        s.replace("https://pulse.org/app/watch?v=", "https://music.youtube.com/watch?v=")
        .replace("https://pulse.org/app/playlist?list=", "https://music.youtube.com/playlist?list=")
        .replace("https://pulse.org/app/channel/", "https://music.youtube.com/channel/")
    )

edit(
    "core/service/kotlinYtmusicScraper/src/commonMain/kotlin/pt/pulse/core/kotlinytmusicscraper/models/YTItem.kt",
    fix_share_links,
)

# Disable the inherited chart backend. The repository continues to return a valid empty result
# until a Pulse-owned chart source is implemented.
def remove_chart_transport(s: str) -> str:
    return re.sub(
        r'\n\s*suspend fun getPulseChart\(\)\s*=\s*\n?\s*httpClient\.get\("https://chart\.pulse\.org/api/playlists"\)\s*\{.*?\n\s*\}',
        '',
        s,
        flags=re.S,
    )

edit(
    "core/service/kotlinYtmusicScraper/src/commonMain/kotlin/pt/pulse/core/kotlinytmusicscraper/Ytmusic.kt",
    remove_chart_transport,
)


def neutral_chart(s: str) -> str:
    return re.sub(
        r'suspend fun getPulseChart\(\)\s*=\s*\n\s*runCatching\s*\{\s*\n\s*ytMusic\.getPulseChart\(\)\.body<PulseChartResponse>\(\)\s*\n\s*\}',
        'suspend fun getPulseChart() =\n        runCatching {\n            PulseChartResponse(data = emptyList(), meta = null, success = true)\n        }',
        s,
        flags=re.S,
    )

edit(
    "core/service/kotlinYtmusicScraper/src/commonMain/kotlin/pt/pulse/core/kotlinytmusicscraper/YouTube.kt",
    neutral_chart,
)

# The inherited private lyrics host is removed. LRCLIB is already a provider in this module;
# requests that still target the legacy-compatible path will fail safely and fall back to the
# existing alternative providers until the Pulse lyrics layer is simplified.
def fix_lyrics(s: str) -> str:
    return s.replace("https://api-lyrics.pulse.org/v1/", "https://lrclib.net/api/").replace(
        "https://api-lyrics.pulse.org/v1", "https://lrclib.net/api"
    )

edit(
    "core/service/lyricsService/src/commonMain/kotlin/pt/pulse/service/lyrics/PulseLyrics.kt",
    fix_lyrics,
)

print("Endpoints herdados removidos.")
