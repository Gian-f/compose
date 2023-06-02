package com.example.irpfchecker.util.converter

import androidx.room.TypeConverter
import java.util.Date

class DateConverter {
    @TypeConverter
    fun fromTimestamp(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}