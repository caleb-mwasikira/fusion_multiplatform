package org.example.project.data

import kotlinx.serialization.json.Json
import org.example.project.ContextProvider
import java.io.File

actual object CustomPreferences {
    private var _file: File? = null
    actual val file: File
        get() {
            if (_file != null) {
                return _file!!
            }

            val context = ContextProvider.get()
            val newFile = File(context.filesDir, "preferences.json")
            if (!newFile.exists()) {
                newFile.createNewFile()
            }
            _file = newFile
            return _file!!
        }

    private var _values: Preferences? = null
    actual val values: Preferences
        get() {
            if (_values != null) {
                return _values!!
            }

            val data = file.readText()
            if (data.isEmpty()) {
                _values = Preferences()
                save()
                return _values!!
            }

            _values = Json.decodeFromString<Preferences>(data)
            return _values!!
        }

    actual fun save() {
        file.writeText(Json.encodeToString(values))
    }

    actual fun putString(key: String, value: String) {
        values.data[key] = value
        save()
    }

    actual fun getString(key: String): String? {
        return values.data[key]
    }

}