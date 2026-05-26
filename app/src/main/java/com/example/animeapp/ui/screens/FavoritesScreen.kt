package com.example.animeapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.animeapp.data.model.Anime

private val AppBackground = Color(0xFF090404)
private val DarkWine = Color(0xFF3A0A0A)
private val TextWhite = Color(0xFFFFF7F7)
private val TextSoft = Color(0xFFE6BDBD)

@Composable
fun FavoritesScreen(
    favorites: List<Anime>,
    onBack: () -> Unit,
    onItemClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
    ) {
        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkWine,
                contentColor = TextWhite
            )
        ) {
            Text("Back")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Favorites",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = TextWhite
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppBackground),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No favorites",
                    style = MaterialTheme.typography.titleMedium,
                    color = TextSoft
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = favorites,
                    key = { it.mal_id }
                ) { anime ->
                    AnimeItem(
                        anime = anime,
                        onClick = { onItemClick(anime.mal_id) },
                        onFavoriteClick = {}
                    )
                }
            }
        }
    }
}