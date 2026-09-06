package pt.pulse.app.expect

actual fun getDownloadFolderPath(): String = System.getProperty("user.home") + "/Downloads"