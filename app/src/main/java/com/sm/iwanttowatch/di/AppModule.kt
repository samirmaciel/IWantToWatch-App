package com.sm.iwanttowatch.di

import androidx.room.Room
import com.sm.iwanttowatch.BuildConfig
import com.sm.iwanttowatch.data.MovieRepository
import com.sm.iwanttowatch.data.local.WantToWatchDatabase
import com.sm.iwanttowatch.data.remote.TmdbApi
import com.sm.iwanttowatch.presentation.AppViewModel
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { Room.databaseBuilder(androidContext(), WantToWatchDatabase::class.java, "want-to-watch.db").build() }
    single { get<WantToWatchDatabase>().watchlistDao() }
    single { Retrofit.Builder().baseUrl("https://api.themoviedb.org/3/").client(OkHttpClient.Builder().build()).addConverterFactory(MoshiConverterFactory.create(Moshi.Builder().add(KotlinJsonAdapterFactory()).build())).build().create(TmdbApi::class.java) }
    single { MovieRepository(get(), get(), BuildConfig.TMDB_API_KEY) }
    viewModel { AppViewModel(get()) }
}
