package com.example.cwv2

import org.json.JSONException
import org.json.JSONObject

class MovieDetails {
    lateinit var title: String
    lateinit var year: String
    lateinit var rated: String
    lateinit var released: String
    lateinit var runtime: String
    lateinit var genre: String
    lateinit var director: String
    lateinit var writer: String
    lateinit var actors: String
    lateinit var plot: String
    lateinit var poster: String

    @Throws(JSONException::class)
    fun mapJson(jsonData: String) {
        val movieData = JSONObject(jsonData)
        title = movieData.getString("Title")
        year = movieData.getString("Year")
        rated = movieData.getString("Rated")
        released = movieData.getString("Released")
        runtime = movieData.getString("Runtime")
        genre = movieData.getString("Genre")
        director = movieData.getString("Director")
        writer = movieData.getString("Writer")
        actors = movieData.getString("Actors")
        plot = movieData.getString("Plot")

        poster = movieData.getString("Poster")

    }
}