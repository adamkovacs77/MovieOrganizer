package com.example.movieorganizer.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieorganizer.R
import com.example.movieorganizer.domain.Movie
import com.example.movieorganizer.utils.AppUtils
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.*
import kotlin.collections.ArrayList

class MoviesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: MovieAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var moviesList: LinkedList<Movie>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies)

        recyclerView = findViewById(R.id.recycler_view_movies)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.visibility = RecyclerView.VISIBLE

        db = FirebaseFirestore.getInstance()
        moviesList = LinkedList()
        db.collection("movie")
            .orderBy("AddedOn", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->

                for (document in documents) {
                    var movie: Movie =
                        Movie(
                            document.get("imdbID").toString(),
                            document.get("Title").toString(),
                            document.get("Poster").toString(),
                            document.get("Year").toString(),
                            "movie",
                            document.id,
                            document.get("AddedOn").toString()
                        )
                    moviesList.add(movie)
                }

                mAdapter = MovieAdapter(
                    this@MoviesActivity,
                    true,
                    moviesList,
                    recyclerView
                )
                recyclerView.adapter = mAdapter
            }
            .addOnFailureListener { exception ->
                Log.w("MovieDB", "Error getting documents: ", exception)
            }

        val goBackButton: Button = findViewById(R.id.moviesGoBackButton)
        goBackButton.setOnClickListener{
            finish()
        }
    }
}
