package com.example.animeapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.animeapp.ui.screens.AnimeDetailScreen
import com.example.animeapp.ui.screens.AnimeListScreen
import com.example.animeapp.ui.screens.FavoritesScreen
import com.example.animeapp.ui.viewmodel.AnimeViewModel

@Composable
fun NavGraph(viewModel: AnimeViewModel) {
    val navController = rememberNavController()

    val state by viewModel.uiState.collectAsState()
    val detailState by viewModel.detailUiState.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val query by viewModel.query.collectAsState()
    val selectedStatus by viewModel.selectedStatus.collectAsState()

    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            AnimeListScreen(
                state = state,
                query = query,
                selectedStatus = selectedStatus,
                onQueryChange = viewModel::search,
                onRetry = viewModel::retry,
                onItemClick = { id -> navController.navigate("detail/$id") },
                onToggleFavorite = viewModel::toggleFavorite,
                onFilter = viewModel::filterByStatus,
                onClearFilter = viewModel::clearFilter,
                onOpenFavorites = { navController.navigate("favorites") }
            )
        }

        composable(
            route = "detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: return@composable

            LaunchedEffect(id) {
                viewModel.loadAnimeDetails(id)
            }

            AnimeDetailScreen(
                state = detailState,
                onBack = { navController.popBackStack() },
                onRetry = { viewModel.loadAnimeDetails(id) },
                onToggleFavorite = { viewModel.toggleFavorite(id) },
                onUpdateStatus = { status -> viewModel.updateStatus(id, status) }
            )
        }

        composable("favorites") {
            FavoritesScreen(
                favorites = favorites,
                onBack = { navController.popBackStack() },
                onItemClick = { id -> navController.navigate("detail/$id") }
            )
        }
    }
}