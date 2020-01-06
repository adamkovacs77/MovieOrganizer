package com.example.movieorganizer.api

import com.example.movieorganizer.domain.Movie
import com.example.movieorganizer.domain.SearchResult
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class OmdbAPI(baseUrl: String, apiKey: String) {

    private val service: MoviesService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(createHttpClient(apiKey))
            .build()

        service = retrofit.create(MoviesService::class.java)
    }

    private fun createHttpClient(apiKey: String): OkHttpClient {
        if (apiKey.isEmpty()) {
            throw IllegalArgumentException("The API key is missing. Please get one from http://www.omdbapi.com")
        }

        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .addInterceptor(
                ApiKeyInterceptor(
                    apiKey
                )
            )
            .build()
    }

    fun searchMovies(searchKey: String): Observable<SearchResult> {
        var res: Observable<SearchResult> = service.searchMovies(searchKey)

        return if(res == null) {
           Observable.empty()
        }
        else
            return res
    }
    fun getMovie(imdbId: String): Observable<Movie> =
        service.getMovie(imdbId)
}