package com.example.animeapp.di

import android.content.Context
import androidx.room.Room
import com.example.animeapp.data.local.AnimeDatabase
import com.example.animeapp.data.local.FavoriteAnimeDao
import com.example.animeapp.data.remote.AnimeApi
import com.example.animeapp.data.remote.RetrofitInstance
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAnimeApi(): AnimeApi {
        return RetrofitInstance.api
    }

    @Provides
    @Singleton
    fun provideAnimeDatabase(
        @ApplicationContext context: Context
    ): AnimeDatabase {
        return Room.databaseBuilder(
            context,
            AnimeDatabase::class.java,
            "anime_database"
        ).build()
    }

    @Provides
    fun provideFavoriteAnimeDao(
        database: AnimeDatabase
    ): FavoriteAnimeDao {
        return database.favoriteAnimeDao()
    }
}