package com.example.cwv2

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface MovieDao {
    @Query("Select * from movie")
    suspend fun getAll(): List<Movie>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovies(vararg user: Movie)
    @Insert
    suspend fun insertAll(vararg users: Movie)

}