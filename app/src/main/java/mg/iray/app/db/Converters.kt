package mg.iray.app.db

import androidx.room.TypeConverter
import com.google.gson.Gson

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromStringList(value: List<String>): String = value.joinToString("|")

    @TypeConverter
    fun toStringList(value: String): List<String> =
        if (value.isBlank()) emptyList() else value.split("|")

    @TypeConverter
    fun fromStringMap(value: Map<String, Any>): String = gson.toJson(value)

    @Suppress("UNCHECKED_CAST")
    @TypeConverter
    fun toStringMap(value: String): Map<String, Any> =
        if (value.isBlank()) emptyMap()
        else gson.fromJson(value, Map::class.java) as Map<String, Any>
}