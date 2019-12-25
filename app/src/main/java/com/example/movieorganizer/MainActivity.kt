package com.example.movieorganizer

import android.content.Intent
import android.os.Bundle
import android.os.Debug
import android.util.LayoutDirection
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Adapter
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity(){

    private lateinit var search: EditText
    private lateinit var searchButton: Button
    private lateinit var movieButton: Button
    private lateinit var seriesButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var client: OmdbAPI
    private lateinit var mAdapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        search = findViewById(R.id.search_movie)
        searchButton = findViewById(R.id.search_button)
        movieButton = findViewById(R.id.moviesList)
        seriesButton = findViewById(R.id.seriesList)
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        client = OmdbAPI("http://www.omdbapi.com", "c68c4396")

        search.setOnEditorActionListener { _, actionId, event ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_SEARCH
                || event != null && event.keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                startSearch()
                handled = true
            }
            handled
        }
        searchButton.setOnClickListener{ startSearch() }

        movieButton.setOnClickListener {
            val intent= Intent(this@MainActivity, MoviesActivity::class.java)
            startActivity(intent)
        }

        seriesButton.setOnClickListener{
            val intent = Intent(this@MainActivity, SeriesActivity::class.java)
            startActivity(intent)
        }
    }

    private fun startSearch() {
        if (AppUtils.checkNetworkState(this)) {
            AppUtils.hideSoftKeyboard(this@MainActivity)
            val movieTitle: String = search.text.toString().trim{ it <= ' ' }

            if (movieTitle.isNotEmpty()) {

                recyclerView.visibility = RecyclerView.VISIBLE
                movieButton.visibility = Button.GONE
                seriesButton.visibility = Button.GONE

                val disposable = getMoviesListBySearch(movieTitle)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe{movies ->
                            mAdapter = MovieAdapter(this@MainActivity, movies)
                            recyclerView.adapter = mAdapter
                        }

            }
            else {
                recyclerView.visibility = RecyclerView.GONE
                movieButton.visibility = Button.VISIBLE
                seriesButton.visibility = Button.VISIBLE

                Snackbar.make(
                    recyclerView,
                    "Please enter a movie title",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        } else {
            Snackbar.make(
                recyclerView,
                "Network not available! Please connect to the Internet.",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }

    private fun getMoviesListBySearch(searchKey: String): Observable<List<Movie>> =
        client.searchMovies(searchKey)
            .map { searchResult ->
                searchResult.search
            }
            .map { searches ->
                val movies = mutableListOf<Movie>()
                for (search in searches) {
                    movies.add(Movie(search.imdbId, search.title, search.poster, search.year, search.type))
                }
                movies
            }

    /**
     * Retrieves complete information of a movie online.
     */
    private fun getMovieById(imdbId: String): Observable<Movie> =
        client.getMovie(imdbId)
}
