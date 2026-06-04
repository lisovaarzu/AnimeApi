package com.example.animeapp.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.animeapp.data.model.Anime
import com.example.animeapp.data.model.WatchStatus
import com.example.animeapp.ui.viewmodel.AnimeUiState

private val AppBackground = Color(0xFF090404)
private val CardWine = Color(0xFF260909)
private val DarkWine = Color(0xFF3A0A0A)
private val AccentRed = Color(0xFFC21818)
private val TextWhite = Color(0xFFFFF7F7)
private val TextSoft = Color(0xFFE6BDBD)

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
            .background(AppBackground)
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
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )

                Text(
                    text = "Search titles from Jikan API",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSoft
                )
            }

            Button(
                onClick = onOpenFavorites,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentRed,
                    contentColor = TextWhite
                )
            ) {
                Text("Favorites")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            label = { Text("Search anime") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedContainerColor = DarkWine,
                unfocusedContainerColor = DarkWine,
                focusedBorderColor = AccentRed,
                unfocusedBorderColor = Color(0xFF6D1A1A),
                focusedLabelColor = TextSoft,
                unfocusedLabelColor = TextSoft,
                cursorColor = AccentRed
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            FilterChip(
                selected = selectedStatus == null,
                onClick = onClearFilter,
                label = { Text("All") },
                colors = filterColors()
            )

            WatchStatus.values().forEach { status ->

                FilterChip(
                    selected = selectedStatus == status,
                    onClick = { onFilter(status) },
                    label = { Text(status.title()) },
                    colors = filterColors()
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (state) {

            is AnimeUiState.Loading -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AppBackground),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AccentRed)
                }
            }

            is AnimeUiState.Error -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AppBackground),
                    contentAlignment = Alignment.Center
                ) {

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {

                        Text(
                            text = "Failed to load anime",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextWhite
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSoft
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onRetry,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AccentRed,
                                contentColor = TextWhite
                            )
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }

            is AnimeUiState.Empty -> {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AppBackground),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        "Nothing found",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite
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
            .clickable { onClick() },
        colors = CardDefaults.elevatedCardColors(
            containerColor = CardWine,
            contentColor = TextWhite
        )
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
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text("Year: ${anime.year ?: "—"}", color = TextSoft)
                    Text("Episodes: ${anime.episodes ?: "—"}", color = TextSoft)
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
                    label = { Text(anime.userStatus.title()) },
                    colors = assistColors()
                )

                AssistChip(
                    onClick = onClick,
                    label = { Text("Details") },
                    colors = assistColors()
                )
            }
        }
    }
}

@Composable
private fun filterColors() = FilterChipDefaults.filterChipColors(
    containerColor = DarkWine,
    labelColor = TextSoft,
    selectedContainerColor = AccentRed,
    selectedLabelColor = TextWhite
)

@Composable
private fun assistColors() = AssistChipDefaults.assistChipColors(
    containerColor = DarkWine,
    labelColor = TextWhite
)

fun WatchStatus.title(): String {

    return when (this) {
        WatchStatus.PLANNED -> "Planned"
        WatchStatus.WATCHING -> "Watching"
        WatchStatus.COMPLETED -> "Completed"
    }
}