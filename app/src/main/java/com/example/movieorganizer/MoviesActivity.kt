package com.example.movieorganizer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MoviesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapter: MovieAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var moviesList: ArrayList<Movie>
    private lateinit var deleteButton: MovieAdapter.ViewHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movies)

        recyclerView = findViewById(R.id.recycler_view_movies)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.visibility = RecyclerView.VISIBLE

        db = FirebaseFirestore.getInstance()
        moviesList = ArrayList()
        db.collection("movie")
            .get()
            .addOnSuccessListener { documents ->

                for (document in documents) {
                    var movie: Movie = Movie(document.get("imdbID").toString(),
                        document.get("Title").toString(),
                        document.get("Poster").toString(),
                        document.get("Year").toString(),
                        "movie",
                        document.id)
                    moviesList.add(movie)
                }

                mAdapter = MovieAdapter(this@MoviesActivity, true, moviesList)
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
