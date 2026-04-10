package com.example.animeapp.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.*
import com.example.animeapp.ui.screens.*
import com.example.animeapp.ui.viewmodel.AnimeViewModel

@Composable
fun NavGraph(viewModel: AnimeViewModel) {

    val navController = rememberNavController()

    val state by viewModel.uiState.collectAsState()
    val favorites by viewModel.favorites.collectAsState()

    NavHost(navController = navController, startDestination = "list") {

        composable("list") {
            AnimeListScreen(
                state = state,
                onSearch = { viewModel.search(it) },
                onRetry = { viewModel.search(it) },
                onItemClick = {
                    navController.navigate("detail/$it")
                },
                onToggleFavorite = {
                    viewModel.toggleFavorite(it)
                },
                onFilter = { viewModel.filterByStatus(it) },
                onClearFilter = { viewModel.clearFilter() },
                onOpenFavorites = {
                    navController.navigate("favorites")
                }
            )
        }

        composable("detail/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id")?.toInt() ?: 0
            AnimeDetailScreen(
                onBack = { navController.popBackStack() },
                onToggleFavorite = { viewModel.toggleFavorite(id) },
                onUpdateStatus = { newStatus -> viewModel.updateStatus(id, newStatus) },
                animeId = id,
                viewModel = viewModel
            )
        }

        composable("favorites") {
            FavoritesScreen(
                favorites = favorites,
                onItemClick = {
                    navController.navigate("detail/$it")
                }
            )
        }
    }
}