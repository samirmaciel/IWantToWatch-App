package com.sm.iwanttowatch.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class MovieEntity(
    @PrimaryKey val id: String,
    val apiId: Int,
    val title: String,
    val originalTitle: String = "",
    val posterPath: String? = null,
    val overview: String = "",
    val rating: Double = 0.0,
    val releaseDate: String = "",
    val type: String = "movie",
    val watched: Boolean = false,
    val addedAt: Long = System.currentTimeMillis()
)
