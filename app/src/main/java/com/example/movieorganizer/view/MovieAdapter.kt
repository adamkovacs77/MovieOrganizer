package com.example.movieorganizer.view

import android.content.Context
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieorganizer.R
import com.example.movieorganizer.domain.Movie
import com.example.movieorganizer.utils.AppUtils
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.*
import java.time.format.FormatStyle

class MovieAdapter(context: Context, to_delete: Boolean, items: List<Movie>?, recyclerView: RecyclerView) :
    RecyclerView.Adapter<MovieAdapter.ViewHolder?>() {
    private var mValues: MutableList<Movie>?
    private var mContext: Context
    private var mToDelete: Boolean
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var mRecyclerView = recyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_movie, parent, false)
        return ViewHolder(view, mToDelete)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val detail: Movie = mValues!![position]
        val title: String = detail.title
        val imdbId: String = detail.imdbId
        val type: String = detail.type
        val year: String = detail.year
        val poster: String = detail.poster
        val addedOn: String = detail.addedOn

        holder.mTitleView.text = title
        holder.mYearView.text = year
        holder.mAddedOn.text = addedOn

        val imageUrl: String
        imageUrl = if (detail.poster != "N/A") {
            detail.poster
        } else { // default image if there is no poster available
            "http://www.imdb.com/images/nopicture/medium/film.png"
        }

        holder.mThumbImageView.layout(
            0,
            0,
            0,
            0
        ) // invalidate the width so that glide wont use that dimension

        Glide.with(mContext).load(imageUrl).into(holder.mThumbImageView)

        holder.mButtonAddMovie.setOnClickListener {
            db.collection(type)
                .whereEqualTo("imdbID", imdbId)
                .get()
                .addOnSuccessListener { doc ->
                    if (doc.documents.size > 0) {
                        AppUtils.makeSnackBar("The $type is already in your list.", mRecyclerView).show()
                    } else {
                        val item = hashMapOf(
                            "imdbID" to imdbId,
                            "Title" to title,
                            "Poster" to poster,
                            "Year" to year,
                            "AddedOn" to LocalDateTime.now().format(ofPattern("EEEE dd MMM yyyy HH:mm"))
                        )

                        db.collection(type)
                            .add(item)
                            .addOnSuccessListener { documentReference ->
                                Log.d(
                                    "MovieAdapter",
                                    "DocumentSnapshot added with ID: ${documentReference.id}"
                                )
                            }
                            .addOnFailureListener { e ->
                                Log.w("MovieAdapter", "Error adding document", e)
                            }

                        AppUtils.makeSnackBar("The $type was added to your list.", mRecyclerView).show()
                    }
                }
        }

        holder.mDeleteButton.setOnClickListener {
            db.collection(type).document(detail.doc_id).delete()
                .addOnSuccessListener {
                    Log.d(
                        "MovieAdapter",
                        "DocumentSnapshot successfully deleted!"
                    )

                    mValues!!.removeAt(position)
                    notifyDataSetChanged()
                }
                .addOnFailureListener { e -> Log.w("MovieAdapter", "Error deleting document", e) }
        }
    }

    override fun getItemCount(): Int {
        return if (mValues == null) {
            0
        } else mValues!!.size
    }

    inner class ViewHolder(private val mView: View, to_delete: Boolean) : RecyclerView.ViewHolder(mView) {
        val mTitleView: TextView
        val mYearView: TextView
        val mAddedOn: TextView
        val mButtonAddMovie: Button
        val mDeleteButton: Button
        val mThumbImageView: ImageView

        init {
            mTitleView = mView.findViewById(R.id.movie_title)
            mYearView = mView.findViewById(R.id.movie_year)
            mThumbImageView = mView.findViewById(R.id.thumbnail)
            mButtonAddMovie = mView.findViewById(R.id.button_add_movie)
            mDeleteButton = mView.findViewById(R.id.button_delete)
            mAddedOn = mView.findViewById(R.id.added_on)

            mButtonAddMovie.visibility = if (to_delete) {
                Button.GONE
            } else {
                Button.VISIBLE
            }

            mDeleteButton.visibility = if (to_delete) {
                Button.VISIBLE
            } else {
                Button.GONE
            }

            mAddedOn.visibility = if (to_delete) {
                TextView.VISIBLE
            } else {
                TextView.GONE
            }
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        super.onViewRecycled(holder)
        Glide.clear(holder.mThumbImageView)
    }

    init {
        mToDelete = to_delete
        mValues = items!!.toMutableList()
        mContext = context
    }
}