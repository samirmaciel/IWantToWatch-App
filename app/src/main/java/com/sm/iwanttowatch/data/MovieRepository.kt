package com.sm.iwanttowatch.data

import com.sm.iwanttowatch.data.local.MovieEntity
import com.sm.iwanttowatch.data.local.WatchlistDao
import com.sm.iwanttowatch.data.remote.TmdbApi
import com.sm.iwanttowatch.data.remote.TmdbTitle
import kotlinx.coroutines.flow.Flow

class MovieRepository(private val dao: WatchlistDao, private val api: TmdbApi, private val apiKey: String) {
    val watchlist: Flow<List<MovieEntity>> = dao.observeAll()
    suspend fun toggle(movie: MovieEntity) { if (dao.find(movie.id) == null) dao.insert(movie) else dao.delete(movie.id) }
    suspend fun setWatched(id: String, watched: Boolean) = dao.setWatched(id, watched)
    suspend fun trending() = if (apiKey.isBlank()) emptyList() else api.trending(apiKey).results.map { it.toEntity() }
    suspend fun topRated() = if (apiKey.isBlank()) emptyList() else (api.topMovies(apiKey).results + api.topShows(apiKey).results).map { it.toEntity() }.sortedByDescending { it.rating }
    suspend fun search(query: String) = if (apiKey.isBlank() || query.isBlank()) emptyList() else api.search(apiKey, query).results.filter { it.media_type == "movie" || it.media_type == "tv" }.map { it.toEntity() }
}

private fun TmdbTitle.toEntity() = MovieEntity(
    id = "${media_type ?: if (name != null) "tv" else "movie"}-$id", apiId = id,
    title = title ?: name ?: "Sem título", originalTitle = original_title ?: original_name ?: "",
    posterPath = poster_path, overview = overview.orEmpty(), rating = vote_average ?: 0.0,
    releaseDate = release_date ?: first_air_date.orEmpty(), type = media_type ?: if (name != null) "tv" else "movie"
)
