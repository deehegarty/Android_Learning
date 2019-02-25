package com.example.recyclerview_api

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), MediaContract.View {

    private var disposable: Disposable? = null
    // declare adapter
    private lateinit var mediaAdapter: MediaAdapter
    // declare & initialize Presenter
    private val mediaPresenter: MediaPresenter = MediaPresenter(this, MediaRepository(MediaClient()), Executors())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // access RecyclerView in activity_main.xml create layout manager and instantiate adapter
        recyclerViewXml.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?

        // initialize adapter
        mediaAdapter = MediaAdapter()
        recyclerViewXml.adapter = mediaAdapter

    }

    override fun onResume() {
        super.onResume()

        mediaPresenter.searchMedia("test")
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }

    override fun showNoMatchesError() {
        Toast.makeText(this, "Error while matching", Toast.LENGTH_LONG).show()
    }

    override fun showGenericError() {
        Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
    }

    override fun showMedia(media: ArrayList<Media>) {
        mediaAdapter.updateData(media)
    }

}

// acts as a contract for Media related View, Presenter and Repository
interface MediaContract {

    interface View {
        fun showNoMatchesError()
        fun showGenericError()
        fun showMedia(media: ArrayList<Media>)
    }

    interface Presenter {
        fun searchMedia(query: String)
    }

    interface Repository {
        fun search(query: String): Flowable<MediaResponse>
    }
}

data class Executors(val worker: Scheduler = Schedulers.newThread(), val ui: Scheduler = AndroidSchedulers.mainThread())

class MediaPresenter(
    private val view: MediaContract.View,
    private val repository: MediaContract.Repository,
    private val executor: Executors
) :
    MediaContract.Presenter {

    override fun searchMedia(query: String) {
        repository.search(query)
            .subscribeOn(executor.worker)
            .observeOn(executor.ui)
            .subscribe(
                { mediaResponse ->
                    // convert to media list first
                    val convertedResponse: ArrayList<Media> = this.handleResponse(mediaResponse)

                    if (convertedResponse.size == 0) {
                        // if no valid Media results
                        view.showNoMatchesError()
                    } else {
                        // then pass to view to display
                        view.showMedia(convertedResponse)
                    }
                },
                { error -> view.showGenericError() }
            )
    }

    // Convert MediaResponse to a ArrayList<Media> and return
    private fun handleResponse(response: MediaResponse): ArrayList<Media> {
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
            } else if (result.type.toLowerCase() == "movie") {
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
        return mediaData
    }
}

class MediaRepository(private val client: MediaClient) : MediaContract.Repository {

    override fun search(query: String): Flowable<MediaResponse> {
        return client.executeSearch(query)
    }
}
