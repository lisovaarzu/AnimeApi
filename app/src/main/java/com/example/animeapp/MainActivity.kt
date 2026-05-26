package com.example.animeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.animeapp.data.remote.RetrofitInstance
import com.example.animeapp.data.repository.AnimeRepository
import com.example.animeapp.navigation.NavGraph
import com.example.animeapp.ui.theme.AnimeAppTheme
import com.example.animeapp.ui.viewmodel.AnimeViewModel
import com.example.animeapp.ui.viewmodel.AnimeViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = AnimeRepository(RetrofitInstance.api)
        val factory = AnimeViewModelFactory(repository)

        setContent {
            AnimeAppTheme {
                val viewModel: AnimeViewModel = viewModel(factory = factory)
                NavGraph(viewModel = viewModel)
            }
        }
    }
}