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

        //Buttons and text views used in the activity.
        val retrieveMovieButton = findViewById<Button>(R.id.retrieveMovieButton)
        val movieDetailsTextView = findViewById<TextView>(R.id.movieDetailsTextView)

        retrieveMovieButton.setOnClickListener {
            movieDetailsTextView.text = ""
            //Hiding the keyboard after the button is clicked.
            try {
                val imm: InputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            } catch (e: java.lang.Exception) {
                Log.d("Keyboard Hiding", "Failed")
            }

            //Calling the Search Movie function.
            searchMovie(it)
        }
    }

    //Getting the movie name from the text box and inserting it into the URL.
    private fun searchMovie(v: View?) {
        val editTextMovieName = findViewById<EditText>(R.id.editTextMovieName)
        val movieURL =
            "https://www.omdbapi.com/?t=" + editTextMovieName.text + "&apikey=8e43b6ad&plot=full"
        getData(movieURL)   //Calling the getData function and passing the movie URL as a string.
    }

    /*  This function opens an connection to the URL and get
    *   the data that the URL returns.   */
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
                            response.append(line).append("\n")   //Reading the returned data line by line and append it to the response.
                        }
                    } catch (e: Exception) {
                        return "Exception"
                    }
                    return response.toString()   //Returning the movie data.
                }

                override fun onPostExecute(result: String1?) {
                    jsonData = result!!
                    processData()   //After getting the data it will be sent for processing.
                }
            }
        task.execute(api_url)
    }

    @SuppressLint("SetTextI18n")
    fun processData() {

        //Buttons and text views used in the activity.
        val saveMovieButton = findViewById<Button>(R.id.saveMovieButton)
        val movieDetailsTextView = findViewById<TextView>(R.id.movieDetailsTextView)

        movie = MovieDetails()      //Custom MovieDetails object.

        try {
            movie.mapJson(jsonData)      //Separating the data from the JSON object.

            //Getting individual data and assigning them to variables.
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

            //Contacting the variables and constructing an output.
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

            saved = output      //Assigning the output into the saved variable in order to save it.
            movieDetailsTextView.text = output

            val imageURL = movie.poster
            Log.d("Poster URL", imageURL)

            //Saving data into the room database.
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

    /*  When the orientation of the device is changed onSaveInstanceState() will
    *   be called. */
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putString("movieDetails", saved)
        super.onSaveInstanceState(savedInstanceState)
    }

    /*  When the orientation of the device is changed onRestoreInstanceState() will
    *   be called and it will restore all the data. */
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