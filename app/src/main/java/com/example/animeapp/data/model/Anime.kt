package com.example.animeapp.data.model

data class Anime(
    val mal_id: Int,
    val title: String,
    val synopsis: String?,
    val year: Int?,
    val episodes: Int?,
    val userStatus: WatchStatus = WatchStatus.PLANNED,
    val isFavorite: Boolean = false
)

enum class WatchStatus {
    PLANNED,
    WATCHING,
    COMPLETED
}