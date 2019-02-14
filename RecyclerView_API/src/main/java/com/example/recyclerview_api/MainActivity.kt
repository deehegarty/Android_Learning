package com.example.recyclerview_api

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // create dummy data (Movie and Tv - Media Type reference)
        val mediaData: ArrayList<Media> = ArrayList()

        for (i in 1..100) {
            val rnds = (0..10).random()
            if (rnds % 2 == 0) {
                mediaData.add(Tv("Tv Test", "January 2019", "Test Description"))
            }else{
                mediaData.add(Movie("Movie Test", "Testing 1, 2, 3", "Test Description"))
            }
        }

        // Access element in activity_main.xml and create layout manager and adapter
        recyclerViewXml.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        recyclerViewXml.adapter = MediaAdapter(mediaData)
    }

    override fun onResume() {
        super.onResume()

        val mediaClient = MediaClient()

        disposable = mediaClient.executeSearch("test")
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { mediaResponse -> handleResponse(mediaResponse) },
                { error -> handleError(error) }
            )
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        disposable?.dispose()
        super.onDestroy()
    }

    private fun handleResponse(response: MediaResponse) {
        Toast.makeText(this, "Success", Toast.LENGTH_LONG).show()
        Log.d("Test", "Response = $response")
    }

    private fun handleError(error: Throwable) {
        Toast.makeText(this, "Error", Toast.LENGTH_LONG).show()
        Log.e("Test", "Error", error)
    }
}
