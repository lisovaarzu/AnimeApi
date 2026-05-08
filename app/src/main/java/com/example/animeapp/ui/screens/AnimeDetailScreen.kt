package com.example.animeapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.animeapp.data.model.WatchStatus
import com.example.animeapp.ui.viewmodel.AnimeDetailUiState

@Composable
fun AnimeDetailScreen(
    state: AnimeDetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onToggleFavorite: () -> Unit,
    onUpdateStatus: (WatchStatus) -> Unit
) {

    when (state) {

        is AnimeDetailUiState.Loading -> {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator()
            }
        }

        is AnimeDetailUiState.Error -> {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Text(
                        text = "Failed to load details",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(state.message)

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {

                        Button(onClick = onBack) {
                            Text("Back")
                        }

                        Button(onClick = onRetry) {
                            Text("Retry")
                        }
                    }
                }
            }
        }

        is AnimeDetailUiState.Success -> {

            val anime = state.anime

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                Button(onClick = onBack) {
                    Text("Back")
                }

                Spacer(modifier = Modifier.height(16.dp))

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth()
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
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text("Year: ${anime.year ?: "—"}")
                                Text("Episodes: ${anime.episodes ?: "—"}")
                            }

                            Button(onClick = onToggleFavorite) {
                                Text(if (anime.isFavorite) "❤️" else "🤍")
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        AssistChip(
                            onClick = {},
                            label = {
                                Text("Status: ${anime.userStatus.title()}")
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = anime.synopsis ?: "No description",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "My status",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            WatchStatus.values().forEach { status ->

                                FilterChip(
                                    selected = anime.userStatus == status,
                                    onClick = { onUpdateStatus(status) },
                                    label = { Text(status.title()) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}