### RecyclerView

**Example response from API used**
```json
    {
      "original_name": "Johnny Test",
      "id": 1769,
      "media_type": "tv",
      "name": "Johnny Test",
      "vote_count": 20,
      "vote_average": 4.55,
      "poster_path": "/wr7i6FxsLxT1PpaETCpGjchgT3C.jpg",
      "first_air_date": "2005-09-17",
      "popularity": 9.831,
      "genre_ids": [
        16,
        35
      ],
      "original_language": "en",
      "backdrop_path": "/bYZtFjLJg1BoIsr2yH48WqdBKqm.jpg",
      "overview": "...",
      "origin_country": [
        "CA",
        "US"
      ]
    }

```

#### Step 1

Create a layout for the main RecyclerView body. This will be populated with views later on.

```xml

    <!--this is the main RecyclerView that will be populated by views (rows)-->
    <android.support.v7.widget.RecyclerView
            android:id="@+id/recyclerViewXml"
            android:layout_width="0dp"
            android:layout_height="0dp"
            app:layout_constraintBottom_toBottomOf="parent"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintRight_toRightOf="parent"
            app:layout_constraintTop_toTopOf="parent"/>

```


#### Step 2

Create an abstract class/interface that specifies a `getType` method. This method will be used by child classes to return their type.

```
interface Media {
    fun getType(): MediaType
}

enum class MediaType{
    Movie, Tv
}

// child classes
data class Tv(val name:String): Media {
    override fun getType(): MediaType {
        return MediaType.Tv
    }
}

data class Movie (val name:String): Media{
    override fun getType(): MediaType {
        return MediaType.Movie
    }
}
```

#### Step 3

Within the MainActivity create a layout manager and adapter. For now create some dummy data to pass to `MediaAdapter`

```
		// ...

		val dummyData : ArrayList<Media> = ArrayList()

		// ...

        // Access element in activity_main.xml and create layout manager and adapter
        recyclerViewXml.layoutManager = LinearLayoutManager(this)
        recyclerViewXml.adapter = MediaAdapter(dummyData)
```


#### Step 4

Create different layouts for `Tv` and `Movie` views. These will be called into the RecyclerView.

#### Step 5

Create `MediaViewHolder` that is referneced in the MainActivity. This class is dependent on `BaseViewHolder` - an abstract ViewHolder
that can be extended by different type of ViewHolders e.g. for Movie and Tv views.

```
class MediaAdapter(mediaData: ArrayList<Media>): RecyclerView.Adapter<MediaAdapter.BaseViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    abstract class BaseViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        // abstract ViewHolder that can be extended by different types of ViewHolders
        // MoviewViewHolder or TvViewHolder
        abstract fun bind(media: Media)
    }
}
```

Next create the subclasses from parent class `BaseViewHolder`. These classes will describe the `Tv` and `Movie` view that will populate the RecyclerView.

```
    class MovieViewHolder(itemView: View) : BaseViewHolder(itemView) {

        // elements from the Movie view layout
        private val movieTitle: TextView = itemView.findViewById(R.id.movieTitle)
        private val originalTitle: TextView = itemView.findViewById(R.id.originalTitle)
        private val movieDescription: TextView = itemView.findViewById(R.id.movieDescription)

        override fun bind(media: Media) {
            val movie = media as Movie // cast Media type to Movie type

            // set the text of the elements listed above
            movieTitle.text = movie.title
            originalTitle.text = movie.originalTitle
            movieDescription.text = movie.movieDescription
        }
    }
```

Change the method for binding every row to the ViewHolder to bind according the data in the list that was passed to the `MediaAdapter`

```
    // return the size of data passed
    override fun getItemCount(): Int = mediaData.size

    // bind every row to ViewHolder
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(mediaData[position])
    }

```

Create a method to determine the type and add private variables to the top the `MediaAdapter`. These private variables will store an int that identifies
each specific object type.

```
class MediaAdapter(val mediaData: ArrayList<Media>) : RecyclerView.Adapter<MediaAdapter.BaseViewHolder>() {

    private val MOVIE_TYPE = 0
    private val TV_TYPE = 1

    // ...

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

	// ...

```

---

### API Calls

Dependencies for API calls:
* Retrofit
* Gson
* RX
* RXJava

*Informtion from - https://medium.com/@biratkirat/8-rxjava-rxandroid-in-kotlin-e599509753c8*

**RxJava**

RxJava is a Java VM implementation of Reactive Extensions: a library for composing asynchronous and event-based programs by using observable sequences

**RxAndroid**

Android specific bindings for RxJava that make writing reactive components in Android applications easy and hassle-free. More specifically, it provides a Scheduler that schedules on the main thread or any given Looper.

Add following dependencies to `build.gradle`

```
  implementation 'com.squareup.retrofit2:retrofit:2.5.0'         // retrofit for REST API consumption
  implementation 'com.squareup.retrofit2:converter-gson:2.5.0'   // retrofit converter for JSON response
  implementation 'com.squareup.retrofit2:adapter-rxjava2:2.5.0'  // retrofit adapter for use with RX
  implementation 'io.reactivex.rxjava2:rxjava:2.2.6'             // RX for asynchronous calls
  implementation 'io.reactivex.rxjava2:rxandroid:2.1.0'          // RX for android (provides Scheduler - schedules in main thread)
```


Allow internet access in `AndroidManifest.xml`

```
uses-permission android:name="android.permission.INTERNET"/>
```

Create an interface that will act as the Retrofir Client. This interface defines a method `search`, that will return a `Flowable` of type `MediaResponse`.
Flowable which is an Observable with backpressure support. Backpressure is when an Observable emits values faster than an Observer is able to handle.  

The `MediaResponse` that is returned by `search()` will the response from the API call. The `MediaResult` reflects the attributes of each object
that has been retrieved via the API call.

```
// Network.kt

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
```

`MediaClient` is used to query the `MediaService`. `executeSearch()` will call the `search()` method inside the `MediaService` with a specific API key and query.
`provideAccessKey()` is a helper function that retrieves the API key.  


```
// Network.kt

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
```


`onCreate()`  inside `MainActivity.kt` is changed to save the `MediaAdapter` into a variable. This means that the data passed to the adapter 
can be updated/changed at a later point.  

```
// MainActivity.kt

class MainActivity : AppCompatActivity() {

    // ...

    private lateinit var mediaAdapter: MediaAdapter // declare adapter

    // ...

  override fun onCreate(savedInstanceState: Bundle?) {

    // ...

    mediaAdapter = MediaAdapter()
    recyclerViewXml.adapter = mediaAdapter
  }

```

`onResume()` is responsible for creating a `MediaClient` to make an API call.   

`handleResponse()` is triggered is the request is successful. It will accept a list of type `Media` and then create objects according to their `media_type`. These objects are then passed to the adapter and displayed according to layout and type.


```
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

    // ...

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
```
