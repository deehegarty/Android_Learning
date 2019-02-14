package com.example.recyclerview_api

import com.google.gson.annotations.SerializedName
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


// https://api.themoviedb.org/3/search/multi?api_key=9b3978a022a1ac181a73ce6688f0ab93&language=en-US&query=test&page=1&include_adult=false

interface MediaService {

    @GET("search/multi?language=en-US&include_adult=false&page=1")
    fun search(@Query("api_key") apyKey: String,
               @Query("query") query: String): Flowable<MediaResponse>
}

data class MediaResponse(@SerializedName("total_results") val total: Int,
                         val results: List<Result>)

data class Result(@SerializedName("media_type") val type: String,
                  @SerializedName("original_title") val title: String)

class MediaClient {

    private val service: MediaService

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        service = retrofit.create(MediaService::class.java)
    }

    fun executeSearch(query: String): Flowable<MediaResponse> {
        val apiKey = provideAccessKey()
        return service.search(apiKey, query)
    }

    fun provideAccessKey() : String {
        return "API_KEY_HERE"
    }

}