package com.example.animeapp.data.repository

import com.example.animeapp.data.model.Anime
import com.example.animeapp.data.model.WatchStatus
import com.example.animeapp.data.remote.RetrofitInstance

class AnimeRepository {

    suspend fun searchAnime(query: String): List<Anime> {
        val response = RetrofitInstance.api.searchAnime(query)
        return response.data.map { apiAnime ->
            Anime(
                mal_id = apiAnime.mal_id,
                title = apiAnime.title,
                synopsis = apiAnime.synopsis,
                year = apiAnime.year,
                episodes = apiAnime.episodes,
                userStatus = WatchStatus.PLANNED,
                isFavorite = false
            )
        }
    }

    suspend fun getAnimeById(id: Int): Anime {
        val response = RetrofitInstance.api.getAnimeById(id)
        val apiAnime = response.data
        return Anime(
            mal_id = apiAnime.mal_id,
            title = apiAnime.title,
            synopsis = apiAnime.synopsis,
            year = apiAnime.year,
            episodes = apiAnime.episodes,
            userStatus = WatchStatus.PLANNED,
            isFavorite = false
        )
    }
}