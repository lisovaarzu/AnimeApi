package com.example.animeapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.animeapp.data.model.Anime

@Composable
fun FavoritesScreen(
    favorites: List<Anime>,
    onItemClick: (Int) -> Unit
) {
    if (favorites.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No favorites", style = MaterialTheme.typography.headlineSmall)
        }
    } else {
        LazyColumn {
            items(favorites) { anime ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { onItemClick(anime.mal_id) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(anime.title, style = MaterialTheme.typography.titleMedium)
                        Text("Year: ${anime.year ?: "—"}", style = MaterialTheme.typography.bodySmall)
                        Text("Episodes: ${anime.episodes ?: "—"}", style = MaterialTheme.typography.bodySmall)
                        Text("Status: ${anime.userStatus.name}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}