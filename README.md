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

##### Step

Dependencies for API calls:
* Retrofit
* Gson
* RX


Add following dependencies to `build.gradle`

```
implementation 'com.squareup.retrofit2:retrofit:2.5.0'
implementation 'com.squareup.retrofit2:converter-gson:2.5.0'

```



