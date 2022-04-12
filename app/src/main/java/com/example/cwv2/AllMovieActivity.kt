package com.example.cwv2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class AllMovieActivity : AppCompatActivity() {

    private var saved = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_movie)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        val retrieveMovieButton = findViewById<Button>(R.id.retrieveMovieButton)
        val editTextMovieName = findViewById<EditText>(R.id.editTextMovieName)

        retrieveMovieButton.setOnClickListener() {

            try {
                val imm: InputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            } catch (e: java.lang.Exception) {
                Log.d("Keyboard Hiding", "Failed")
            }

            val searchPhrase = editTextMovieName.text.toString()

            val stb = StringBuilder()
            val url_string =
                "https://www.omdbapi.com/?s=*" + searchPhrase + "*&apikey=8e43b6ad&page=1"
            val url = URL(url_string)
            val con: HttpURLConnection = url.openConnection() as HttpURLConnection

            runBlocking {
                launch {

                    withContext(Dispatchers.IO) {
                        val bf = BufferedReader(InputStreamReader(con.inputStream))
                        var line: String? = bf.readLine()
                        while (line != null) {
                            stb.append(line + "\n")
                            line = bf.readLine()
                        }
                        parseJSON(stb)
                    }
                }
            }
        }
    }

    suspend fun parseJSON(stb: java.lang.StringBuilder) {

        val textView = findViewById<TextView>(R.id.textView)

        try {
            val json = JSONObject(stb.toString())
            val allMovieNames = java.lang.StringBuilder()
            val jsonArray: JSONArray = json.getJSONArray("Search")

            for (i in 0..jsonArray.length() - 1) {
                val cocktail: JSONObject = jsonArray[i] as JSONObject
                val title = cocktail["Title"] as String
                allMovieNames.append(title + "\n")
            }
            Log.d("Output Tester", allMovieNames.toString())
            saved = allMovieNames.toString()
            textView.text = allMovieNames.toString()

        } catch (e: Exception) {
            Log.d("Parsing The Json Object", "Failed")
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putString("allMovieDetails", saved)
        super.onSaveInstanceState(savedInstanceState)

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val saved = savedInstanceState.getString("allMovieDetails")

        val textView = findViewById<TextView>(R.id.textView)
        textView.text = saved

        val retrieveMovieButton = findViewById<Button>(R.id.retrieveMovieButton)
        retrieveMovieButton.performClick()

    }
}