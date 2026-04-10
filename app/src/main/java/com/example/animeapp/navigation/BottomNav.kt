package com.example.animeapp.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController

@Composable
fun BottomNav(navController: NavController) {

    val items = listOf("home", "favorites")
    var selected by remember { mutableStateOf("home") }

    NavigationBar {

        NavigationBarItem(
            selected = selected == "home",
            onClick = {
                selected = "home"
                navController.navigate("list")
            },
            label = { Text("Home") },
            icon = {}
        )

        NavigationBarItem(
            selected = selected == "favorites",
            onClick = {
                selected = "favorites"
                navController.navigate("favorites")
            },
            label = { Text("Favorites") },
            icon = {}
        )
    }
}