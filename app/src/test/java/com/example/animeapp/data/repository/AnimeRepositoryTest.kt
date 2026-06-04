package com.example.animeapp.data.repository

import com.example.animeapp.data.local.FavoriteAnimeDao
import com.example.animeapp.data.local.FavoriteAnimeEntity
import com.example.animeapp.data.model.Anime
import com.example.animeapp.data.model.AnimeApiModel
import com.example.animeapp.data.model.AnimeDetailResponse
import com.example.animeapp.data.model.AnimeSearchResponse
import com.example.animeapp.data.model.WatchStatus
import com.example.animeapp.data.remote.AnimeApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AnimeRepositoryTest {

    private lateinit var api: FakeAnimeApi
    private lateinit var dao: FakeFavoriteAnimeDao
    private lateinit var repository: AnimeRepository

    @Before
    fun setUp() {
        api = FakeAnimeApi()
        dao = FakeFavoriteAnimeDao()
        repository = AnimeRepository(api, dao)
    }

    @Test
    fun getAnimeReturnsNetworkAnime() = runTest {
        api.searchResponse = AnimeSearchResponse(
            data = listOf(
                AnimeApiModel(
                    mal_id = 1,
                    title = "Naruto",
                    synopsis = "Ninja story",
                    year = 2002,
                    episodes = 220
                )
            )
        )

        val result = repository.getAnime("naruto")

        assertEquals(1, result.size)
        assertEquals("Naruto", result.first().title)
        assertFalse(result.first().isFavorite)
    }

    @Test
    fun getAnimeMarksFavoriteItems() = runTest {
        dao.insertFavorite(
            FavoriteAnimeEntity(
                malId = 1,
                title = "Naruto",
                synopsis = "Ninja story",
                year = 2002,
                episodes = 220,
                userStatus = WatchStatus.WATCHING
            )
        )

        api.searchResponse = AnimeSearchResponse(
            data = listOf(
                AnimeApiModel(
                    mal_id = 1,
                    title = "Naruto",
                    synopsis = "Ninja story",
                    year = 2002,
                    episodes = 220
                )
            )
        )

        val result = repository.getAnime("naruto")

        assertTrue(result.first().isFavorite)
    }

    @Test
    fun getAnimeByIdUsesFavoriteStatusFromRoom() = runTest {
        dao.insertFavorite(
            FavoriteAnimeEntity(
                malId = 10,
                title = "Bleach",
                synopsis = "Soul reapers",
                year = 2004,
                episodes = 366,
                userStatus = WatchStatus.COMPLETED
            )
        )

        api.detailResponse = AnimeDetailResponse(
            data = AnimeApiModel(
                mal_id = 10,
                title = "Bleach",
                synopsis = "Soul reapers",
                year = 2004,
                episodes = 366
            )
        )

        val result = repository.getAnimeById(10)

        assertTrue(result.isFavorite)
        assertEquals(WatchStatus.COMPLETED, result.userStatus)
    }

    @Test
    fun addFavoriteSavesAnimeToFavorites() = runTest {
        repository.addFavorite(
            Anime(
                mal_id = 2,
                title = "Death Note",
                synopsis = "Notebook",
                year = 2006,
                episodes = 37,
                userStatus = WatchStatus.PLANNED,
                isFavorite = true
            )
        )

        val favorites = repository.getFavorites().first()

        assertEquals(1, favorites.size)
        assertEquals("Death Note", favorites.first().title)
    }

    @Test
    fun removeFavoriteDeletesAnimeFromFavorites() = runTest {
        repository.addFavorite(
            Anime(
                mal_id = 3,
                title = "One Piece",
                synopsis = "Pirates",
                year = 1999,
                episodes = 1000,
                userStatus = WatchStatus.PLANNED,
                isFavorite = true
            )
        )

        repository.removeFavorite(3)

        val favorites = repository.getFavorites().first()

        assertTrue(favorites.isEmpty())
    }

    @Test
    fun updateFavoriteStatusChangesSavedStatus() = runTest {
        val anime = Anime(
            mal_id = 4,
            title = "Attack on Titan",
            synopsis = "Titans",
            year = 2013,
            episodes = 87,
            userStatus = WatchStatus.PLANNED,
            isFavorite = true
        )

        repository.addFavorite(anime)
        repository.updateFavoriteStatus(anime, WatchStatus.WATCHING)

        val favorite = repository.getFavorites().first().first()

        assertEquals(WatchStatus.WATCHING, favorite.userStatus)
    }
}

private class FakeAnimeApi : AnimeApi {

    var searchResponse = AnimeSearchResponse(emptyList())

    var detailResponse = AnimeDetailResponse(
        AnimeApiModel(
            mal_id = 1,
            title = "Test",
            synopsis = null,
            year = null,
            episodes = null
        )
    )

    override suspend fun getAnime(
        query: String?,
        page: Int,
        limit: Int
    ): AnimeSearchResponse {
        return searchResponse
    }

    override suspend fun getAnimeById(id: Int): AnimeDetailResponse {
        return detailResponse
    }
}

private class FakeFavoriteAnimeDao : FavoriteAnimeDao {

    private val data = MutableStateFlow<List<FavoriteAnimeEntity>>(emptyList())

    override fun getFavorites(): Flow<List<FavoriteAnimeEntity>> {
        return data
    }

    override suspend fun getFavoriteIds(): List<Int> {
        return data.value.map { it.malId }
    }

    override suspend fun getFavoriteById(id: Int): FavoriteAnimeEntity? {
        return data.value.firstOrNull { it.malId == id }
    }

    override suspend fun insertFavorite(anime: FavoriteAnimeEntity) {
        data.value = data.value
            .filterNot { it.malId == anime.malId } + anime
    }

    override suspend fun deleteFavoriteById(id: Int) {
        data.value = data.value.filterNot { it.malId == id }
    }
}