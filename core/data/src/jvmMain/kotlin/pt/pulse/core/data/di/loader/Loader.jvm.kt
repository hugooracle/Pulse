package pt.pulse.core.data.di.loader

import pt.pulse.media_jvm.di.loadDesktopPlayerModule

actual fun loadMediaService() {
    loadDesktopPlayerModule()
}
