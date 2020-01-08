package com.example.movieorganizer.view

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieorganizer.api.OmdbAPI
import com.example.movieorganizer.R
import com.example.movieorganizer.domain.Movie
import com.example.movieorganizer.utils.AppUtils
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.time.LocalDate

class MainActivity : AppCompatActivity(){

    private lateinit var search: EditText
    private lateinit var searchButton: Button
    private lateinit var movieButton: Button
    private lateinit var seriesButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var client: OmdbAPI
    private lateinit var mAdapter: MovieAdapter

    companion object {
        const val OMDB_API_LINK = "http://www.omdbapi.com"
        const val OMDB_API_KEY = "c68c4396"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        search = findViewById(R.id.search_movie)
        searchButton = findViewById(R.id.search_button)
        movieButton = findViewById(R.id.moviesList)
        seriesButton = findViewById(R.id.seriesList)
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        client = OmdbAPI(OMDB_API_LINK, OMDB_API_KEY)

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
            val intent = Intent(this@MainActivity, MoviesActivity::class.java)
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
                    .subscribe { movies ->
                            if (movies.isEmpty()) {
                                AppUtils.makeSnackBar(getString(R.string.no_movie_found), recyclerView).show()
                            } else {
                                mAdapter =
                                    MovieAdapter(
                                        this@MainActivity,
                                        false,
                                        movies,
                                        recyclerView
                                    )
                                recyclerView.adapter = mAdapter
                            }
                        }

            }
            else {
                recyclerView.visibility = RecyclerView.GONE
                movieButton.visibility = Button.VISIBLE
                seriesButton.visibility = Button.VISIBLE

                AppUtils.makeSnackBar(getString(R.string.enter_movie), recyclerView).show()
            }
        } else {
            AppUtils.makeSnackBar(getString(R.string.no_internet), recyclerView).show()
        }
    }

    override fun onBackPressed() {
        if(search.text.isNotEmpty()) {
            recyclerView.visibility = RecyclerView.GONE
            movieButton.visibility = Button.VISIBLE
            seriesButton.visibility = Button.VISIBLE

            search.text.clear()
            return
        }

        super.onBackPressed()
    }

    private fun getMoviesListBySearch(searchKey: String): Observable<List<Movie>> =
        client.searchMovies(searchKey).
            doOnError {
                println(it.stackTrace)
            }
            .map { searchResult ->
                // return an empty list if there was no response from the API
                if (searchResult.response == "False") {
                    emptyList()
                } else {
                    searchResult.search
                }
            }
            .map { searches ->
                if (searches.isNotEmpty()) {
                    val movies = mutableListOf<Movie>()
                    for (search in searches) {
                        movies.add(
                            Movie(
                                search.imdbId,
                                search.title,
                                search.poster,
                                search.year,
                                search.type,
                                "",
                                LocalDate.now().toString()
                            )
                        )
                    }
                    movies
                } else {
                    searches
                }
            }
}
