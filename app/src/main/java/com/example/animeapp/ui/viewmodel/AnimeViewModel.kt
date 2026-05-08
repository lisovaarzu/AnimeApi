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

class AnimeViewModel(
    private val repository: AnimeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AnimeUiState>(AnimeUiState.Loading)
    val uiState: StateFlow<AnimeUiState> = _uiState.asStateFlow()

    private val _detailUiState = MutableStateFlow<AnimeDetailUiState>(AnimeDetailUiState.Loading)
    val detailUiState: StateFlow<AnimeDetailUiState> = _detailUiState.asStateFlow()

    private val _favorites = MutableStateFlow<List<Anime>>(emptyList())
    val favorites: StateFlow<List<Anime>> = _favorites.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _selectedStatus = MutableStateFlow<WatchStatus?>(null)
    val selectedStatus: StateFlow<WatchStatus?> = _selectedStatus.asStateFlow()

    private var sourceList: List<Anime> = emptyList()
    private var currentList: List<Anime> = emptyList()
    private val favoriteIds = mutableSetOf<Int>()
    private val statuses = mutableMapOf<Int, WatchStatus>()

    init {
        loadAnime()
    }

    fun loadAnime() {
        viewModelScope.launch {
            _uiState.value = AnimeUiState.Loading
            try {
                val result = repository.getAnime(_query.value)
                sourceList = result.map(::applyUserData)
                updateListState()
            } catch (e: Exception) {
                _uiState.value = AnimeUiState.Error(e.message ?: "Ошибка загрузки")
            }
        }
    }

    fun search(query: String) {
        _query.value = query
        _selectedStatus.value = null
        loadAnime()
    }

    fun retry() {
        loadAnime()
    }

    fun loadAnimeDetails(id: Int) {
        viewModelScope.launch {
            _detailUiState.value = AnimeDetailUiState.Loading
            try {
                val anime = applyUserData(repository.getAnimeById(id))
                _detailUiState.value = AnimeDetailUiState.Success(anime)
            } catch (e: Exception) {
                _detailUiState.value = AnimeDetailUiState.Error(e.message ?: "Ошибка загрузки")
            }
        }
    }

    fun toggleFavorite(id: Int) {
        if (id in favoriteIds) {
            favoriteIds.remove(id)
        } else {
            favoriteIds.add(id)
        }

        sourceList = sourceList.map(::applyUserData)
        currentList = currentList.map(::applyUserData)
        updateCurrentUiState()
        updateFavorites()
        updateDetailState(id)
    }

    fun updateStatus(id: Int, newStatus: WatchStatus) {
        statuses[id] = newStatus

        sourceList = sourceList.map(::applyUserData)
        currentList = currentList.map(::applyUserData)
        updateCurrentUiState()
        updateFavorites()
        updateDetailState(id)
    }

    fun filterByStatus(status: WatchStatus) {
        _selectedStatus.value = status
        updateListState()
    }

    fun clearFilter() {
        _selectedStatus.value = null
        updateListState()
    }

    private fun updateListState() {
        currentList = _selectedStatus.value?.let { status ->
            sourceList.filter { it.userStatus == status }
        } ?: sourceList

        updateCurrentUiState()
        updateFavorites()
    }

    private fun updateCurrentUiState() {
        _uiState.value = if (currentList.isEmpty()) {
            AnimeUiState.Empty
        } else {
            AnimeUiState.Success(currentList)
        }
    }

    private fun updateFavorites() {
        _favorites.value = sourceList.filter { it.mal_id in favoriteIds }
    }

    private fun updateDetailState(id: Int) {
        val state = _detailUiState.value
        if (state is AnimeDetailUiState.Success && state.anime.mal_id == id) {
            _detailUiState.value = AnimeDetailUiState.Success(applyUserData(state.anime))
        }
    }

    private fun applyUserData(anime: Anime): Anime {
        return anime.copy(
            isFavorite = anime.mal_id in favoriteIds,
            userStatus = statuses[anime.mal_id] ?: WatchStatus.PLANNED
        )
    }
}