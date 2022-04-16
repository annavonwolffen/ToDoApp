package com.annevonwolffen.data.database

import androidx.room.TypeConverter
import com.annevonwolffen.domain.Priority
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun fromPriority(value: Priority) = value.value

    @TypeConverter
    fun toPriority(value: Int) = Priority.values().first { it.value == value }
}