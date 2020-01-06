package com.example.movieorganizer.model

import com.example.movieorganizer.model.Movie
import com.google.gson.annotations.SerializedName

data class SearchResult(
    @SerializedName("Search") val search: List<Movie>,
    @SerializedName("totalResults") val totalResults: String,
    @SerializedName("Response") val response: String)
