package com.example.recyclerview_api

import com.google.gson.annotations.SerializedName
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

// Retrofit client
// Pass the API Key & actual query
interface MediaService {

    @GET("search/multi?language=en-US&include_adult=false&page=1")
    fun search(@Query("api_key") apyKey: String,
               @Query("query") query: String): Flowable<MediaResponse>

}


// MediaResponse is specific to the data we want to retrieve from the API:
// Number of results retrieved & list containing multiple results that matched query
// @SerializedName can be used for storing retrieved attributes into a variable
data class MediaResponse(@SerializedName("total_results") val total: Int,
                         val results: List<MediaResult>)


// Retrieve the specific attributes from the results that were retrieved
// This will then propagate each view/row in the RecyclerView
data class MediaResult(@SerializedName("media_type") val type: String,
                       @SerializedName("original_title") val origMovieName: String,
                       @SerializedName("title") val movieName: String,
                       @SerializedName("overview") val description: String,
                       @SerializedName("first_air_date") val aired: String,
                       @SerializedName("name") val tvName: String)


// Client for retrieving Media related queries via the MediaService
class MediaClient {

    // MediaService - interface defined above (Retrofit Client)
    private val service: MediaService
    // environment variable
    val api_key = "RECYCLER_API_KEY"

    init {
        // IMPORTANT:
        // specifies how to generate implementation of MediaService
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