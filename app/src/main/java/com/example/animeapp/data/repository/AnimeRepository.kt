package com.example.animeapp.data.repository

import com.example.animeapp.data.local.FavoriteAnimeDao
import com.example.animeapp.data.local.toAnime
import com.example.animeapp.data.local.toFavoriteEntity
import com.example.animeapp.data.model.Anime
import com.example.animeapp.data.model.WatchStatus
import com.example.animeapp.data.remote.AnimeApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AnimeRepository @Inject constructor(
    private val api: AnimeApi,
    private val favoriteAnimeDao: FavoriteAnimeDao
) {

    fun getFavorites(): Flow<List<Anime>> {
        return favoriteAnimeDao.getFavorites().map { favorites ->
            favorites.map { it.toAnime() }
        }
    }

    suspend fun getFavoriteIds(): Set<Int> {
        return favoriteAnimeDao.getFavoriteIds().toSet()
    }

    suspend fun getAnime(query: String): List<Anime> {
        val favoriteIds = getFavoriteIds()
        val response = api.getAnime(
            query = query.ifBlank { null }
        )

        return response.data.map { apiAnime ->
            Anime(
                mal_id = apiAnime.mal_id,
                title = apiAnime.title,
                synopsis = apiAnime.synopsis,
                year = apiAnime.year,
                episodes = apiAnime.episodes,
                userStatus = WatchStatus.PLANNED,
                isFavorite = apiAnime.mal_id in favoriteIds
            )
        }
    }

    suspend fun getAnimeById(id: Int): Anime {
        val favorite = favoriteAnimeDao.getFavoriteById(id)
        val apiAnime = api.getAnimeById(id).data

        return Anime(
            mal_id = apiAnime.mal_id,
            title = apiAnime.title,
            synopsis = apiAnime.synopsis,
            year = apiAnime.year,
            episodes = apiAnime.episodes,
            userStatus = favorite?.userStatus ?: WatchStatus.PLANNED,
            isFavorite = favorite != null
        )
    }

    suspend fun addFavorite(anime: Anime) {
        favoriteAnimeDao.insertFavorite(anime.toFavoriteEntity())
    }

    suspend fun removeFavorite(id: Int) {
        favoriteAnimeDao.deleteFavoriteById(id)
    }

    suspend fun updateFavoriteStatus(anime: Anime, status: WatchStatus) {
        favoriteAnimeDao.insertFavorite(
            anime.copy(
                userStatus = status,
                isFavorite = true
            ).toFavoriteEntity()
        )
    }
}