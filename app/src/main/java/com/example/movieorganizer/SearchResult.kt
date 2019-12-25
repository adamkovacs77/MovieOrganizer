package com.example.movieorganizer

import com.google.gson.annotations.SerializedName

data class SearchResult(
    @SerializedName("Search") val search: List<Movie>,
    @SerializedName("totalResults") val totalResults: String,
    @SerializedName("Response") val response: String)
