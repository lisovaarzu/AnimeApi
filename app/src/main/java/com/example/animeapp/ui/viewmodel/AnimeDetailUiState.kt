package com.example.animeapp.ui.viewmodel

import com.example.animeapp.data.model.Anime

sealed class AnimeDetailUiState {
    object Loading : AnimeDetailUiState()
    data class Success(val anime: Anime) : AnimeDetailUiState()
    data class Error(val message: String) : AnimeDetailUiState()
}