package com.example.cwv2

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["Tittle"], unique = true)])
data class Movie(
    @PrimaryKey(autoGenerate = true) var mid: Int = 0,
    @ColumnInfo(name = "Tittle") val tittle: String?,
    @ColumnInfo(name = "Year") val year: String?,
    @ColumnInfo(name = "Rating") val rating: String?,
    @ColumnInfo(name = "Release Date") val releaseDate: String?,
    @ColumnInfo(name = "Runtime") val runtime: String?,
    @ColumnInfo(name = "Genre") val genre: String?,
    @ColumnInfo(name = "Director") val director: String?,
    @ColumnInfo(name = "Writer") val writer: String?,
    @ColumnInfo(name = "Actors") val actors: String?,
    @ColumnInfo(name = "Plot") val plot: String?
)