package com.example.animeapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.animeapp.data.model.WatchStatus
import com.example.animeapp.ui.viewmodel.AnimeDetailUiState
import com.example.animeapp.ui.viewmodel.AnimeViewModel

@Composable
fun AnimeDetailScreen(
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onUpdateStatus: (WatchStatus) -> Unit,
    animeId: Int,
    viewModel: AnimeViewModel
) {
    val detailState by viewModel.detailUiState.collectAsState()

    LaunchedEffect(animeId) {
        viewModel.loadAnimeDetails(animeId)
    }

    when (detailState) {
        is AnimeDetailUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Loading...")
            }
        }

        is AnimeDetailUiState.Success -> {
            val anime = (detailState as AnimeDetailUiState.Success).anime
            Column(modifier = Modifier.padding(16.dp)) {
                Text(anime.title, style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Year: ${anime.year ?: "—"}")
                Text("Episodes: ${anime.episodes ?: "—"}")
                Text("Status: ${anime.userStatus.name}")
                Spacer(modifier = Modifier.height(16.dp))
                Text(anime.synopsis ?: "No description")
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = onBack) {
                        Text("Back")
                    }
                    Button(onClick = onToggleFavorite) {
                        Text(if (anime.isFavorite) "Remove from Favorites" else "Add to Favorites")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = { onUpdateStatus(WatchStatus.PLANNED) }) {
                        Text("Planned")
                    }
                    Button(onClick = { onUpdateStatus(WatchStatus.WATCHING) }) {
                        Text("Watching")
                    }
                    Button(onClick = { onUpdateStatus(WatchStatus.COMPLETED) }) {
                        Text("Completed")
                    }
                }
            }
        }

        is AnimeDetailUiState.Error -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Error: ${(detailState as AnimeDetailUiState.Error).message}")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { viewModel.loadAnimeDetails(animeId) }) {
                    Text("Retry")
                }
            }
        }
    }
}