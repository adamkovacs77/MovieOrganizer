package com.example.movieorganizer.api

import com.example.movieorganizer.domain.Movie
import com.example.movieorganizer.domain.SearchResult
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesService {

    @GET("/")
    fun searchMovies(
            @Query("s") searchKey: String): Observable<SearchResult>

    @GET("/")
    fun getMovie(
            @Query("i") id: String): Observable<Movie>
}
