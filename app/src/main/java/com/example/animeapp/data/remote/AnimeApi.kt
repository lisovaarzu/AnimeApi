package com.example.animeapp.data.remote

import com.example.animeapp.data.model.AnimeDetailResponse
import com.example.animeapp.data.model.AnimeSearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AnimeApi {

    @GET("anime")
    suspend fun getAnime(
        @Query("q") query: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): AnimeSearchResponse

    @GET("anime/{id}")
    suspend fun getAnimeById(
        @Path("id") id: Int
    ): AnimeDetailResponse
}