package ru.iandreyshev.payfriends.data.settings

import android.content.Context
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.iandreyshev.payfriends.domain.settings.Settings
import java.io.File
import javax.inject.Inject

class AppSettings
@Inject constructor(context: Context) {

    private val memoryFile = File(context.filesDir, SETTINGS_FILE_NAME)

    init {
        if (!memoryFile.exists()) {
            memoryFile.setWritable(true)
            memoryFile.setReadable(true)
            memoryFile.createNewFile()
        }
    }

    fun get(): Settings {
        if (!memoryFile.exists()) {
            return Settings.default()
        }

        val text = memoryFile.reader().readText()

        if (text.isBlank()) {
            return Settings.default()
        }

        return Json.decodeFromString<SettingsJson>(string = text)
            .asDomainModel()
    }

    fun save(settings: Settings) {
        memoryFile.writer().use {
            it.write("")
            it.write(Json.encodeToString(settings.asJson()))
        }
    }

    private fun Settings.asJson() =
        SettingsJson(isSmartAlgorithm)

    private fun SettingsJson.asDomainModel() =
        Settings(isSmartAlgorithm)

    companion object {
        private const val SETTINGS_FILE_NAME = "settings.json"
    }

}