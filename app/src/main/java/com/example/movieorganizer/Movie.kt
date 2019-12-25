package com.example.movieorganizer

import com.google.gson.annotations.SerializedName

data class Movie(
        @SerializedName("imdbID") val imdbId: String,
        @SerializedName("Title") val title: String,
        @SerializedName("Poster") val poster: String,
        @SerializedName("Year") val year: String,
        @SerializedName("Type") val type: String)
