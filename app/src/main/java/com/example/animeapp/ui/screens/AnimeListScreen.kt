package com.example.animeapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.animeapp.data.model.Anime
import com.example.animeapp.data.model.WatchStatus
import com.example.animeapp.ui.viewmodel.AnimeUiState

@Composable
fun AnimeListScreen(
    state: AnimeUiState,
    query: String,
    selectedStatus: WatchStatus?,
    onQueryChange: (String) -> Unit,
    onRetry: () -> Unit,
    onItemClick: (Int) -> Unit,
    onToggleFavorite: (Int) -> Unit,
    onOpenFavorites: () -> Unit,
    onFilter: (WatchStatus) -> Unit,
    onClearFilter: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {
                Text(
                    text = "Anime Library",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Search titles from Jikan API",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Button(onClick = onOpenFavorites) {
                Text("Favorites")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text("Search anime") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            FilterChip(
                selected = selectedStatus == null,
                onClick = onClearFilter,
                label = { Text("All") }
            )

            WatchStatus.values().forEach { status ->

                FilterChip(
                    selected = selectedStatus == status,
                    onClick = { onFilter(status) },
                    label = { Text(status.title()) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (state) {

            is AnimeUiState.Loading -> {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is AnimeUiState.Error -> {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        Text(
                            text = "Failed to load anime",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(onClick = onRetry) {
                            Text("Retry")
                        }
                    }
                }
            }

            is AnimeUiState.Empty -> {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        "Nothing found",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            is AnimeUiState.Success -> {

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    items(
                        items = state.data,
                        key = { it.mal_id }
                    ) { anime ->

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

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {

                Column(modifier = Modifier.weight(1f)) {

                    Text(
                        text = anime.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text("Year: ${anime.year ?: "—"}")
                    Text("Episodes: ${anime.episodes ?: "—"}")
                }

                Text(
                    text = if (anime.isFavorite) "❤️" else "🤍",
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .clickable { onFavoriteClick() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                AssistChip(
                    onClick = {},
                    label = { Text(anime.userStatus.title()) }
                )

                AssistChip(
                    onClick = onClick,
                    label = { Text("Details") }
                )
            }
        }
    }
}

fun WatchStatus.title(): String {

    return when (this) {
        WatchStatus.PLANNED -> "Planned"
        WatchStatus.WATCHING -> "Watching"
        WatchStatus.COMPLETED -> "Completed"
    }
}