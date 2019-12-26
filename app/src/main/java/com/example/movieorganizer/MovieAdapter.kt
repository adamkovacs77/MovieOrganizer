package com.example.movieorganizer

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class MovieAdapter(context: Context, to_delete: Boolean, items: List<Movie>?) :
    RecyclerView.Adapter<MovieAdapter.ViewHolder?>() {
    private var mValues: MutableList<Movie>?
    private var mContext: Context
    private var mToDelete: Boolean
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()

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
        holder.mTitleView.text = title
        holder.mYearView.text = year
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
            val item = hashMapOf(
                "imdbID" to imdbId,
                "Title" to title,
                "Poster" to poster,
                "Year" to year
            )

            db.collection(type)
                .add(item)
                .addOnSuccessListener { documentReference ->
                    Log.d("MovieAdapter", "DocumentSnapshot added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    Log.w("MovieAdapter", "Error adding document", e)
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
        val mButtonAddMovie: Button
        val mDeleteButton: Button
        val mThumbImageView: ImageView

        init {
            mTitleView = mView.findViewById(R.id.movie_title)
            mYearView = mView.findViewById(R.id.movie_year)
            mThumbImageView = mView.findViewById(R.id.thumbnail)
            mButtonAddMovie = mView.findViewById(R.id.button_add_movie)
            mDeleteButton = mView.findViewById(R.id.button_delete)

            mButtonAddMovie.visibility = if (to_delete) {
                Button.GONE
            }
            else
                Button.VISIBLE

            mDeleteButton.visibility = if (to_delete) {
                Button.VISIBLE
            }
            else
                Button.GONE
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