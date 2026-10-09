package com.sm.iwanttowatch.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {
    @Query("SELECT * FROM watchlist ORDER BY addedAt DESC") fun observeAll(): Flow<List<MovieEntity>>
    @Query("SELECT * FROM watchlist WHERE id = :id LIMIT 1") suspend fun find(id: String): MovieEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(movie: MovieEntity)
    @Query("DELETE FROM watchlist WHERE id = :id") suspend fun delete(id: String)
    @Query("UPDATE watchlist SET watched = :watched WHERE id = :id") suspend fun setWatched(id: String, watched: Boolean)
}
