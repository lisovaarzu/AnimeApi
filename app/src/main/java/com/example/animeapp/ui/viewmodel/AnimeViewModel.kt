package com.example.animeapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animeapp.data.model.Anime
import com.example.animeapp.data.model.WatchStatus
import com.example.animeapp.data.repository.AnimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AnimeViewModel : ViewModel() {

    private val repository = AnimeRepository()

    private val _uiState = MutableStateFlow<AnimeUiState>(AnimeUiState.Loading)
    val uiState: StateFlow<AnimeUiState> = _uiState.asStateFlow()

    private val _detailUiState = MutableStateFlow<AnimeDetailUiState>(AnimeDetailUiState.Loading)
    val detailUiState: StateFlow<AnimeDetailUiState> = _detailUiState.asStateFlow()

    private val _favorites = MutableStateFlow<List<Anime>>(emptyList())
    val favorites: StateFlow<List<Anime>> = _favorites.asStateFlow()

    private var currentList: List<Anime> = emptyList()
    private var originalList: List<Anime> = emptyList()

    private val defaultAnime = listOf(
        Anime(1, "Sword Art Online", "VR MMO world", 2012, 25, WatchStatus.PLANNED, false),
        Anime(2, "One Punch Man", "Hero who wins instantly", 2015, 12, WatchStatus.WATCHING, false),
        Anime(3, "Naruto", "Ninja story", 2002, 220, WatchStatus.COMPLETED, false),
        Anime(4, "Attack on Titan", "Humans vs Titans", 2013, 87, WatchStatus.PLANNED, false),
        Anime(5, "Death Note", "Death notebook", 2006, 37, WatchStatus.WATCHING, false),
        Anime(6, "Jujutsu Kaisen", "Curses and sorcerers", 2020, 24, WatchStatus.COMPLETED, false)
    )

    init {
        originalList = defaultAnime
        currentList = defaultAnime
        _uiState.value = AnimeUiState.Success(currentList)
        _favorites.value = currentList.filter { it.isFavorite }
    }

    fun search(query: String) {
        if (query.isBlank()) {
            currentList = originalList
            _uiState.value = AnimeUiState.Success(currentList)
            return
        }
        viewModelScope.launch {
            _uiState.value = AnimeUiState.Loading
            val filtered = originalList.filter { it.title.contains(query, ignoreCase = true) }
            currentList = filtered
            if (filtered.isEmpty()) {
                _uiState.value = AnimeUiState.Empty
            } else {
                _uiState.value = AnimeUiState.Success(filtered)
            }
        }
    }

    fun loadAnimeDetails(id: Int) {
        viewModelScope.launch {
            _detailUiState.value = AnimeDetailUiState.Loading
            try {
                val anime = repository.getAnimeById(id)
                _detailUiState.value = AnimeDetailUiState.Success(anime)
            } catch (e: Exception) {
                _detailUiState.value = AnimeDetailUiState.Error(e.message ?: "Ошибка загрузки")
            }
        }
    }

    fun toggleFavorite(id: Int) {
        val updatedList = currentList.map { anime ->
            if (anime.mal_id == id) {
                anime.copy(isFavorite = !anime.isFavorite)
            } else anime
        }
        currentList = updatedList
        originalList = originalList.map { anime ->
            if (anime.mal_id == id) {
                anime.copy(isFavorite = !anime.isFavorite)
            } else anime
        }
        _uiState.value = AnimeUiState.Success(currentList)
        _favorites.value = updatedList.filter { it.isFavorite }
    }

    fun updateStatus(id: Int, newStatus: WatchStatus) {
        val updatedList = currentList.map { anime ->
            if (anime.mal_id == id) {
                anime.copy(userStatus = newStatus)
            } else anime
        }
        currentList = updatedList
        originalList = originalList.map { anime ->
            if (anime.mal_id == id) {
                anime.copy(userStatus = newStatus)
            } else anime
        }
        _uiState.value = AnimeUiState.Success(currentList)
        _favorites.value = updatedList.filter { it.isFavorite }
    }

    fun filterByStatus(status: WatchStatus) {
        val filtered = originalList.filter { it.userStatus == status }
        currentList = filtered
        if (filtered.isEmpty()) {
            _uiState.value = AnimeUiState.Empty
        } else {
            _uiState.value = AnimeUiState.Success(filtered)
        }
    }

    fun clearFilter() {
        currentList = originalList
        _uiState.value = AnimeUiState.Success(currentList)
    }
}