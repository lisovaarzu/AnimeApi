package com.example.animeapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteAnimeDao {

    @Query("SELECT * FROM favorite_anime")
    fun getFavorites(): Flow<List<FavoriteAnimeEntity>>

    @Query("SELECT malId FROM favorite_anime")
    suspend fun getFavoriteIds(): List<Int>

    @Query("SELECT * FROM favorite_anime WHERE malId = :id LIMIT 1")
    suspend fun getFavoriteById(id: Int): FavoriteAnimeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(anime: FavoriteAnimeEntity)

    @Query("DELETE FROM favorite_anime WHERE malId = :id")
    suspend fun deleteFavoriteById(id: Int)
}