package pt.pulse.core.data.di.loader

import pt.pulse.core.common.AppIdentity
import pt.pulse.core.data.di.databaseModule
import pt.pulse.core.data.di.mediaHandlerModule
import pt.pulse.core.data.di.repositoryModule
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

fun loadAllModules(appIdentity: AppIdentity) {
    loadKoinModules(
        listOf(
            module { single { appIdentity } },
            databaseModule,
            repositoryModule,
        ),
    )
    loadKoinModules(mediaHandlerModule)
    loadMediaService()
}

expect fun loadMediaService()
