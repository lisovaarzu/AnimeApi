package com.example.animeapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animeapp.data.model.Anime
import com.example.animeapp.data.model.WatchStatus
import com.example.animeapp.data.repository.AnimeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimeViewModel @Inject constructor(
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

    init {
        observeFavorites()
        loadAnime()
    }

    fun loadAnime() {
        viewModelScope.launch {
            _uiState.value = AnimeUiState.Loading
            try {
                sourceList = repository.getAnime(_query.value)
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
                val anime = repository.getAnimeById(id)
                _detailUiState.value = AnimeDetailUiState.Success(anime)
            } catch (e: Exception) {
                _detailUiState.value = AnimeDetailUiState.Error(e.message ?: "Ошибка загрузки")
            }
        }
    }

    fun toggleFavorite(id: Int) {
        viewModelScope.launch {
            val anime = findAnime(id) ?: return@launch

            if (anime.isFavorite) {
                repository.removeFavorite(id)
            } else {
                repository.addFavorite(anime.copy(isFavorite = true))
            }

            refreshAfterRoomChange(id)
        }
    }

    fun updateStatus(id: Int, newStatus: WatchStatus) {
        viewModelScope.launch {
            val anime = findAnime(id)?.copy(userStatus = newStatus) ?: return@launch

            if (anime.isFavorite) {
                repository.updateFavoriteStatus(anime, newStatus)
            }

            sourceList = sourceList.map {
                if (it.mal_id == id) it.copy(userStatus = newStatus) else it
            }
            currentList = currentList.map {
                if (it.mal_id == id) it.copy(userStatus = newStatus) else it
            }

            updateCurrentUiState()
            updateDetailState(id, anime.copy(userStatus = newStatus))
        }
    }

    fun filterByStatus(status: WatchStatus) {
        _selectedStatus.value = status
        updateListState()
    }

    fun clearFilter() {
        _selectedStatus.value = null
        updateListState()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            repository.getFavorites().collect { favorites ->
                _favorites.value = favorites

                val favoriteIds = favorites.map { it.mal_id }.toSet()

                sourceList = sourceList.map { anime ->
                    anime.copy(isFavorite = anime.mal_id in favoriteIds)
                }

                currentList = currentList.map { anime ->
                    anime.copy(isFavorite = anime.mal_id in favoriteIds)
                }

                updateCurrentUiState()

                val detailState = _detailUiState.value
                if (detailState is AnimeDetailUiState.Success) {
                    val updatedAnime = detailState.anime.copy(
                        isFavorite = detailState.anime.mal_id in favoriteIds
                    )
                    _detailUiState.value = AnimeDetailUiState.Success(updatedAnime)
                }
            }
        }
    }

    private fun refreshAfterRoomChange(id: Int) {
        viewModelScope.launch {
            val favoriteIds = repository.getFavoriteIds()

            sourceList = sourceList.map { anime ->
                anime.copy(isFavorite = anime.mal_id in favoriteIds)
            }

            currentList = currentList.map { anime ->
                anime.copy(isFavorite = anime.mal_id in favoriteIds)
            }

            updateCurrentUiState()

            val detailState = _detailUiState.value
            if (detailState is AnimeDetailUiState.Success && detailState.anime.mal_id == id) {
                _detailUiState.value = AnimeDetailUiState.Success(
                    detailState.anime.copy(isFavorite = id in favoriteIds)
                )
            }
        }
    }

    private fun updateListState() {
        currentList = _selectedStatus.value?.let { status ->
            sourceList.filter { it.userStatus == status }
        } ?: sourceList

        updateCurrentUiState()
    }

    private fun updateCurrentUiState() {
        _uiState.value = if (currentList.isEmpty()) {
            AnimeUiState.Empty
        } else {
            AnimeUiState.Success(currentList)
        }
    }

    private fun updateDetailState(id: Int, anime: Anime) {
        val state = _detailUiState.value
        if (state is AnimeDetailUiState.Success && state.anime.mal_id == id) {
            _detailUiState.value = AnimeDetailUiState.Success(anime)
        }
    }

    private fun findAnime(id: Int): Anime? {
        val detailState = _detailUiState.value
        if (detailState is AnimeDetailUiState.Success && detailState.anime.mal_id == id) {
            return detailState.anime
        }

        return sourceList.firstOrNull { it.mal_id == id }
            ?: currentList.firstOrNull { it.mal_id == id }
            ?: _favorites.value.firstOrNull { it.mal_id == id }
    }
}