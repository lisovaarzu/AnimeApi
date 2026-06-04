package com.example.animeapp.data.model

data class AnimeApiModel(
    val mal_id: Int,
    val title: String,
    val synopsis: String?,
    val year: Int?,
    val episodes: Int?
)