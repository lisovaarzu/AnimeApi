package com.example.animeapp.data.model

data class AnimeSearchResponse(
    val data: List<AnimeApiModel>
)

data class AnimeDetailResponse(
    val data: AnimeApiModel
)