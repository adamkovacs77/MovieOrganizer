package com.example.movieorganizer.domain

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class Movie(
        @SerializedName("imdbID") val imdbId: String,
        @SerializedName("Title") val title: String,
        @SerializedName("Poster") val poster: String,
        @SerializedName("Year") val year: String,
        @SerializedName("Type") val type: String,
        @SerializedName("doc_id") val doc_id: String,
        @SerializedName("AddedOn") val addedOn: String
)
