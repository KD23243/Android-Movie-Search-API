package com.example.cwv2

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Suppress("NAME_SHADOWING")
class ActorsActivity : AppCompatActivity() {

    private var saved = ""  //The variable that will be used for restoring the instance state.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_actors)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        //Buttons and text boxes used in the activity.
        val editTextActorNames = findViewById<EditText>(R.id.editTextactorNames)
        val actorSearchButton = findViewById<Button>(R.id.actorSearchButton)
        val titleInActor = findViewById<TextView>(R.id.titleinActor)

        titleInActor.visibility = View.INVISIBLE    //Hiding the tittle textview at the beginning.

        //Loading the room database.
        val database =
            Room.databaseBuilder(applicationContext, AppDatabase::class.java, "roomDataBase")
                .build()
        val movieDao = database.movieDao()

        //actorSearchButton on click listener.
        actorSearchButton.setOnClickListener {

            //Hiding the keyboard after the button is clicked.
            try {
                val imm: InputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            } catch (e: java.lang.Exception) {
                Log.d("Keyboard Hiding", "Failed")
            }

            titleInActor.text = ""
            val userString = editTextActorNames.text
            var output = ""

            //Checking whether the edit text is empty or not.
            if (TextUtils.isEmpty(userString)) {
                titleInActor.visibility = View.INVISIBLE
                Toast.makeText(applicationContext, "Field Cannot Be Empty", Toast.LENGTH_LONG)
                    .show()
            } else {
                //Creating a coroutine
                runBlocking {
                    launch {
                        val movies: List<Movie> = movieDao.getAll()
                        val titleInActor = findViewById<TextView>(R.id.titleinActor)

                        val tempMovieArray = ArrayList<String>()
                        for (item in movies) {
                            var dataString = item.actors
                            dataString = dataString!!.replace(", ", ",")
                            val actorArray: List<String> = dataString.split(",")

                            for (item1 in actorArray) {
                                if (item1.contains(userString, ignoreCase = true)) {
                                    tempMovieArray.add(item.tittle.toString())
                                }
                            }
                        }
                        val duplicateDeleted: Set<String> = HashSet<String>(tempMovieArray)

                        for (item1 in duplicateDeleted) {
                            output = output + "\n" + item1 + "\n"
                        }
                        titleInActor.visibility = View.VISIBLE
                        saved = output
                        titleInActor.text = output
                    }
                }
            }
        }
    }

    /*  When the orientation of the device is changed onSaveInstanceState() will
    *   be called. */
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putString("actorDetails", saved)
        super.onSaveInstanceState(savedInstanceState)
    }

    /*  When the orientation of the device is changed onRestoreInstanceState() will
    *   be called and it will restore all the data. */
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val saved = savedInstanceState.getString("actorDetails")

        val titleInActor = findViewById<TextView>(R.id.titleinActor)
        titleInActor.text = saved

        val actorSearchButton = findViewById<Button>(R.id.actorSearchButton)
        actorSearchButton.performClick()

        titleInActor.visibility = View.VISIBLE

    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            com.google.android.material.R.anim.abc_fade_in,
            com.google.android.material.R.anim.abc_fade_out
        )
    }
}