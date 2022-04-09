package com.example.cwv2

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONException
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlin.String as String1

class SearchActivity : AppCompatActivity() {

    lateinit var jsonData: String1
    private lateinit var movie: MovieDetails
    private var saved = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val retrieveMovieButton = findViewById<Button>(R.id.retrieveMovieButton)
        val progressLoader = findViewById<ProgressBar>(R.id.progress_loader)
        val movieDetailsTextView = findViewById<TextView>(R.id.movieDetailsTextView)

        retrieveMovieButton.setOnClickListener {
            progressLoader.visibility = View.VISIBLE
            movieDetailsTextView.text = ""
            try {
                val imm: InputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            } catch (e: java.lang.Exception) {
                Log.d("Keyboard Hiding", "Failed")
            }
            searchMovie(it)
            progressLoader.visibility = View.INVISIBLE
        }
    }

    private fun searchMovie(v: View?) {
        val editTextMovieName = findViewById<EditText>(R.id.editTextMovieName)
        val movieURL =
            "https://www.omdbapi.com/?t=" + editTextMovieName.text + "&apikey=8e43b6ad&plot=full"
        getData(movieURL)
    }

    @SuppressLint("SetTextI18n")
    fun processData() {
        val saveMovieButton = findViewById<Button>(R.id.saveMovieButton)
        val movieDetailsTextView = findViewById<TextView>(R.id.movieDetailsTextView)

        movie = MovieDetails()

        try {
            movie.mapJson(jsonData)
            val title = movie.title
            val year = movie.year
            val rated = movie.rated
            val released = movie.released
            val runtime = movie.runtime
            val genre = movie.genre
            val director = movie.director
            val writer = movie.writer
            val actors = movie.actors
            val plot = movie.plot

            val output = "Title: $title \n" +
                    "Year: $year \n" +
                    "Rated: $rated \n" +
                    "Released: $released \n" +
                    "Runtime: $runtime \n" +
                    "Genre: $genre \n" +
                    "Director: $director \n" +
                    "Writer: $writer \n" +
                    "Actors: $actors \n\n" +
                    "Plot: $plot \n"

            saved = output
            movieDetailsTextView.text = output

            val imageURL = movie.poster
            Log.d("Poster", imageURL)

            saveMovieButton.setOnClickListener {
                val database = Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    "roomDataBase"
                ).build()
                val movieDao = database.movieDao()

                runBlocking {
                    launch {
                        val movie = Movie(
                            0,
                            movie.title,
                            movie.year,
                            movie.rated,
                            movie.released,
                            movie.runtime,
                            movie.genre,
                            movie.director,
                            movie.writer,
                            movie.actors,
                            movie.plot
                        )
                        movieDao.insertMovies(movie)
                    }
                }
                Toast.makeText(applicationContext, "Movie Saved To The Database", Toast.LENGTH_LONG)
                    .show()
            }
        } catch (e: JSONException) {
            Toast.makeText(this@SearchActivity, e.message, Toast.LENGTH_LONG).show()
        }
    }

    private fun getData(api_url: String1) {
        val task: AsyncTask<String1, String1, String1> =
            @SuppressLint("StaticFieldLeak")
            object : AsyncTask<String1, String1, String1>() {
                override fun doInBackground(vararg params: String1): String1 {
                    val response = StringBuilder()
                    try {
                        val url = URL(params[0])
                        val urlConnection = url.openConnection() as HttpURLConnection
                        val reader = BufferedReader(InputStreamReader(urlConnection.inputStream))
                        var line: String1?
                        while (reader.readLine().also { line = it } != null) {
                            response.append(line).append("\n")
                        }
                    } catch (e: Exception) {
                        return "Exception"
                    }
                    return response.toString()
                }

                override fun onPostExecute(result: String1?) {
                    jsonData = result!!
                    processData()
                }
            }
        task.execute(api_url)
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putString("movieDetails", saved)
        super.onSaveInstanceState(savedInstanceState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val saved = savedInstanceState.getString("movieDetails")

        val movieDetailsTextView = findViewById<TextView>(R.id.movieDetailsTextView)
        movieDetailsTextView.text = saved

        val retrieveMovieButton = findViewById<Button>(R.id.retrieveMovieButton)
        retrieveMovieButton.performClick()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            com.google.android.material.R.anim.abc_fade_in,
            com.google.android.material.R.anim.abc_fade_out
        )
    }
}