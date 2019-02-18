package com.example.recyclerview_api

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var disposable: Disposable? = null
    private lateinit var mediaAdapter: MediaAdapter // declare adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // create dummy data (Movie and Tv - Media Type reference)
        val mediaData: ArrayList<Media> = ArrayList()

        // Access RecyclerView in activity_main.xml
        // create layout manager
        // and instantiate adapter
        recyclerViewXml.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        mediaAdapter = MediaAdapter()
        recyclerViewXml.adapter = mediaAdapter
    }

    override fun onResume() {
        super.onResume()

        // create a MediaClient to make API call
        val mediaClient = MediaClient()

        // for now the query sent to API will be "test"
        // this query will be on a new thread that is
        // managed by a scheduler. The lines specified below
        // the observer will take place in the main thread.
        disposable = mediaClient.executeSearch("test")
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { mediaResponse -> handleResponse(mediaResponse) },
                { error -> handleError(error) }
            )
    }

    // Save instance
    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
    }

    // One destroyed finish any current API calls/queries
    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }

    // Define how response is handled
    // if movie or tv
    private fun handleResponse(response: MediaResponse) {
        val mediaData: ArrayList<Media> = ArrayList()
        val returnedData: List<MediaResult> = response.results

        // cycle through results and determine of TV or MOVIE
        for (result in returnedData) {
            if (result.type.toLowerCase() == "tv") {
                // TV OBJECT
                mediaData.add(
                    Tv(
                        result.tvName,
                        result.aired,
                        result.description
                    )
                )
            } else {
                // MOVIE OBJECT
                mediaData.add(
                    Movie(
                        result.origMovieName,
                        result.movieName,
                        result.description
                    )
                )
            }
        }
        // update the adapter with retrieved data
        mediaAdapter.updateData(mediaData)
    }

    // Define how an error is handled
    private fun handleError(error: Throwable) {
        Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
    }
}
