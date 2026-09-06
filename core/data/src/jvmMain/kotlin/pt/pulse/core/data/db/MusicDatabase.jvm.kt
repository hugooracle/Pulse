package pt.pulse.core.data.db

import androidx.room.Room
import androidx.room.RoomDatabase
import pt.pulse.core.common.DB_NAME
import pt.pulse.core.data.io.getHomeFolderPath
import java.io.File

actual fun getDatabaseBuilder(
    converters: Converters
): RoomDatabase.Builder<MusicDatabase> {
    return Room.databaseBuilder<MusicDatabase>(
        name = getDatabasePath()
    ).addTypeConverter(converters)
}

actual fun getDatabasePath(): String {
    val dbFile = File(getHomeFolderPath(listOf(".pulse", "db")), DB_NAME)
    return dbFile.absolutePath
}