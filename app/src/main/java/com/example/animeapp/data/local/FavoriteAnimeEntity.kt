package com.example.animeapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.animeapp.data.model.Anime
import com.example.animeapp.data.model.WatchStatus

@Entity(tableName = "favorite_anime")
data class FavoriteAnimeEntity(
    @PrimaryKey val malId: Int,
    val title: String,
    val synopsis: String?,
    val year: Int?,
    val episodes: Int?,
    val userStatus: WatchStatus
)

fun FavoriteAnimeEntity.toAnime(): Anime {
    return Anime(
        mal_id = malId,
        title = title,
        synopsis = synopsis,
        year = year,
        episodes = episodes,
        userStatus = userStatus,
        isFavorite = true
    )
}

fun Anime.toFavoriteEntity(): FavoriteAnimeEntity {
    return FavoriteAnimeEntity(
        malId = mal_id,
        title = title,
        synopsis = synopsis,
        year = year,
        episodes = episodes,
        userStatus = userStatus
    )
}