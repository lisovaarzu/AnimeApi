package com.example.animeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.animeapp.navigation.NavGraph
import com.example.animeapp.ui.viewmodel.AnimeViewModel

class MainActivity : ComponentActivity() {

    private val viewModel = AnimeViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavGraph(viewModel)
        }
    }
}