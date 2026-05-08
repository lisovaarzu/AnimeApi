package com.example.animeapp.data.repository

import com.example.animeapp.data.model.Anime
import com.example.animeapp.data.model.WatchStatus
import com.example.animeapp.data.remote.AnimeApi

class AnimeRepository(
    private val api: AnimeApi
) {

    suspend fun getAnime(query: String): List<Anime> {
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
                isFavorite = false
            )
        }
    }

    suspend fun getAnimeById(id: Int): Anime {
        val apiAnime = api.getAnimeById(id).data

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