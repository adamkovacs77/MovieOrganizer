package com.example.movieorganizer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SeriesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_series)

        val goBackButton: Button = findViewById(R.id.seriesGoBackButton)
        goBackButton.setOnClickListener{
            finish()
        }
    }
}
