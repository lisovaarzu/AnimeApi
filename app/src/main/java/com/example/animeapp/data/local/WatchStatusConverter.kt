package com.example.animeapp.data.local

import androidx.room.TypeConverter
import com.example.animeapp.data.model.WatchStatus

class WatchStatusConverter {

    @TypeConverter
    fun fromStatus(status: WatchStatus): String {
        return status.name
    }

    @TypeConverter
    fun toStatus(value: String): WatchStatus {
        return WatchStatus.valueOf(value)
    }
}