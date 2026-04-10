package com.example.animeapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.animeapp.data.model.Anime
import com.example.animeapp.data.model.WatchStatus
import com.example.animeapp.ui.viewmodel.AnimeUiState

@Composable
fun AnimeListScreen(
    state: AnimeUiState,
    onSearch: (String) -> Unit,
    onRetry: (String) -> Unit,
    onItemClick: (Int) -> Unit,
    onToggleFavorite: (Int) -> Unit,
    onOpenFavorites: () -> Unit,
    onFilter: (WatchStatus) -> Unit,
    onClearFilter: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf<WatchStatus?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        Button(
            onClick = { onOpenFavorites() },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Favorites")
        }

        TextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search anime") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { onSearch(query) },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Search")
        }

        LazyRow(
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            val filters = listOf(
                "PLANNED",
                "WATCHING",
                "COMPLETED"
            )

            items(filters) { filter ->
                Button(
                    onClick = {
                        val status = WatchStatus.valueOf(filter)
                        selectedFilter = status
                        onFilter(status)
                    },
                    modifier = Modifier.padding(4.dp)
                ) {
                    Text(filter)
                }
            }
        }

        when (state) {
            is AnimeUiState.Loading -> {
                Text("Loading...", modifier = Modifier.padding(16.dp))
            }

            is AnimeUiState.Error -> {
                Column {
                    Text("Error")
                    Button(onClick = { onRetry(query) }) {
                        Text("Retry")
                    }
                }
            }

            is AnimeUiState.Empty -> {
                Text("Nothing found", modifier = Modifier.padding(16.dp))
            }

            is AnimeUiState.Success -> {
                val filteredData = if (selectedFilter == null) {
                    state.data
                } else {
                    state.data.filter { it.userStatus == selectedFilter }
                }

                LazyColumn {
                    items(filteredData) { anime ->
                        AnimeItem(
                            anime = anime,
                            onClick = { onItemClick(anime.mal_id) },
                            onFavoriteClick = { onToggleFavorite(anime.mal_id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnimeItem(
    anime: Anime,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(anime.title, style = MaterialTheme.typography.titleMedium)
            Text("Year: ${anime.year ?: "—"}", style = MaterialTheme.typography.bodySmall)
            Text("Episodes: ${anime.episodes ?: "—"}", style = MaterialTheme.typography.bodySmall)
            Text("Status: ${anime.userStatus.name}", style = MaterialTheme.typography.bodySmall)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Details →", modifier = Modifier.clickable { onClick() })
                Text(
                    text = if (anime.isFavorite) "❤️" else "🤍",
                    modifier = Modifier.clickable { onFavoriteClick() }
                )
            }
        }
    }
}