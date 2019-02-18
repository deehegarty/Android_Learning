package com.example.recyclerview_api

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class MediaAdapter : RecyclerView.Adapter<MediaAdapter.BaseViewHolder>() {

    private val MOVIE_TYPE = 0
    private val TV_TYPE = 1

    private val mediaData: ArrayList<Media> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        // inflate different layouts depending on the viewType
        val inflater = LayoutInflater.from(parent.context)

        if(viewType == MOVIE_TYPE){
            return MovieViewHolder(inflater.inflate(R.layout.movie, parent, false))
        }else{
            return TvViewHolder(inflater.inflate(R.layout.tv, parent, false))
        }
    }

    // return the size of data passed
    override fun getItemCount(): Int = mediaData.size

    // bind every row to ViewHolder
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(mediaData[position])
    }

    abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // abstract ViewHolder that can be extended by different types of ViewHolders
        // MovieViewHolder or TvViewHolder
        abstract fun bind(media: Media)
    }

    class MovieViewHolder(itemView: View) : BaseViewHolder(itemView) {

        // elements from the Movie view layout
        private val movieTitle: TextView = itemView.findViewById(R.id.movieTitle)
        private val originalTitle: TextView = itemView.findViewById(R.id.originalTitle)
        private val movieDescription: TextView = itemView.findViewById(R.id.movieDescription)

        override fun bind(media: Media) {
            // cast Media type to Movie
            val movie = media as Movie

            // set the text of the elements listed above
            movieTitle.text = movie.title
            originalTitle.text = movie.originalTitle
            movieDescription.text = movie.movieDescription
        }
    }

    class TvViewHolder(itemView: View) : BaseViewHolder(itemView) {

        // elements from the tv view layout
        private val tvTitle: TextView = itemView.findViewById(R.id.episodeTitle)
        private val firstAir: TextView = itemView.findViewById(R.id.firstAir)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)

        override fun bind(media: Media) {
            // cast Media type to Tv
            val tv = media as Tv

            tvTitle.text = tv.episodeTitle
            firstAir.text = tv.firstAir
            tvDescription.text = tv.tvDescription
        }

    }

    override fun getItemViewType(position: Int): Int {
        // MediaType is enum class
        // the media variable stores the current object to be displayed
        // if the object type is Tv then is is a TV_TYPE and will = 1
        // if the object type is Movie then it is MOVIE_TYPE and will = 0
        val media = mediaData[position]
        return when (media.getType()) {
            MediaType.Tv -> TV_TYPE
            MediaType.Movie -> MOVIE_TYPE
        }
    }

    // update with new data
    fun updateData(newData: ArrayList<Media>) {
        mediaData.clear()
        mediaData.addAll(newData)
        notifyDataSetChanged()
    }

}