package com.example.animeapp.ui.viewmodel

import com.example.animeapp.data.model.Anime

sealed class AnimeUiState {

    object Loading : AnimeUiState()

    data class Success(val data: List<Anime>) : AnimeUiState()

    object Empty : AnimeUiState()

    data class Error(val message: String) : AnimeUiState()
}
