package com.example.animeapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [FavoriteAnimeEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(WatchStatusConverter::class)
abstract class AnimeDatabase : RoomDatabase() {

    abstract fun favoriteAnimeDao(): FavoriteAnimeDao
}