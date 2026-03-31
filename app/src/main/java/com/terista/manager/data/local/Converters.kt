package com.terista.manager.data.local

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun fromString(value: String?): String? = value

    @TypeConverter
    fun toString(value: String?): String? = value
}
