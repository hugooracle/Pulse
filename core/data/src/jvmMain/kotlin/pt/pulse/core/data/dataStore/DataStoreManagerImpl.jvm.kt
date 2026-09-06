package pt.pulse.core.data.dataStore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import pt.pulse.core.common.SETTINGS_FILENAME
import pt.pulse.core.data.io.getHomeFolderPath
import createDataStore
import java.io.File

actual fun createDataStoreInstance(): DataStore<Preferences> = createDataStore(
    producePath = {
        val file = File(getHomeFolderPath(listOf(".pulse")), "$SETTINGS_FILENAME.preferences_pb")
        file.absolutePath
    }
)