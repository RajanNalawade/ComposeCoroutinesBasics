package com.example.coroutinesbasics.db

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

class MovieTypeConverter {

    @TypeConverter
    fun fromIntList(value: List<Int>?): String? {
        return value?.let { Json.encodeToString(value) }
    }

    @TypeConverter
    fun toIntList(value: String?): List<Int>? {
        return value?.let { Json.decodeFromString<List<Int>>(it) }
    }

    @TypeConverter
    fun fromMovieList(value: List<Movie>) = Json.encodeToString(value)

    @TypeConverter
    fun toMovieList(value: String) = Json.decodeFromString<List<Movie>>(value)
}