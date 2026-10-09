package com.sm.iwanttowatch.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class TmdbPage(val results: List<TmdbTitle> = emptyList())
data class TmdbTitle(
    val id: Int,
    val title: String? = null,
    val name: String? = null,
    val original_title: String? = null,
    val original_name: String? = null,
    val poster_path: String? = null,
    val overview: String? = null,
    val vote_average: Double? = null,
    val release_date: String? = null,
    val first_air_date: String? = null,
    val media_type: String? = null
)
interface TmdbApi {
    @GET("trending/all/week") suspend fun trending(@Query("api_key") key: String, @Query("language") language: String = "pt-BR"): TmdbPage
    @GET("movie/top_rated") suspend fun topMovies(@Query("api_key") key: String, @Query("language") language: String = "pt-BR"): TmdbPage
    @GET("tv/top_rated") suspend fun topShows(@Query("api_key") key: String, @Query("language") language: String = "pt-BR"): TmdbPage
    @GET("search/multi") suspend fun search(@Query("api_key") key: String, @Query("query") query: String, @Query("language") language: String = "pt-BR"): TmdbPage
    @GET("{type}/{id}") suspend fun details(@Path("type") type: String, @Path("id") id: Int, @Query("api_key") key: String, @Query("language") language: String = "pt-BR"): TmdbDetails
}
data class TmdbDetails(
    val id: Int,
    val title: String? = null,
    val name: String? = null,
    val original_title: String? = null,
    val original_name: String? = null,
    val poster_path: String? = null,
    val overview: String? = null,
    val vote_average: Double? = null,
    val release_date: String? = null,
    val first_air_date: String? = null,
    val runtime: Int? = null,
    val number_of_seasons: Int? = null,
    val genres: List<TmdbGenre> = emptyList()
)
data class TmdbGenre(val name: String)
