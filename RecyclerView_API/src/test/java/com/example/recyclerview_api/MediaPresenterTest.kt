package com.example.recyclerview_api

import android.util.Log
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.*
import org.junit.Test

class MediaPresenterTest {

    private val scheduler = Schedulers.trampoline()
    private val view = mock<MediaContract.View>()
    private val repository: MediaContract.Repository = mock()
    private val presenter = MediaPresenter(view, repository, Executors(scheduler, scheduler))


    @Test
    fun errorRetrievingFromAPI() {
        val query = "test"
        whenever(repository.search(query)).thenReturn(Flowable.error(Throwable()))
        presenter.searchMedia(query)
        verify(view, times(1)).showGenericError()

    }

    @Test
    fun errorEmptyMediaResultListReturnedFromAPI() {
        val query = ""
        val resArray = listOf(MediaResult())
        val response = MediaResponse(resArray.size, resArray)

        whenever(repository.search(query)).thenReturn(Flowable.just(response))
        presenter.searchMedia(query)

        argumentCaptor<ArrayList<Media>>().apply {
            verify(view).showNoMatchesError()
        }
    }

    @Test
    fun validMediaObjectTypeRetrievedViaAPI() {
        val query = "test"

        val result1 =
            MediaResult(
                "tv",
                tvName = "Original TV Test",
                aired = "2019-02-19",
                description = "Tv description"
            )

        val result2 =
            MediaResult(
                "movie",
                movieName = "New Movie Name",
                origMovieName = "Original Movie Test",
                description = "Movie description"
            )

        val results = listOf(result1, result2)
        val response = MediaResponse(results.size, results)
        whenever(repository.search(query)).thenReturn(Flowable.just(response))

        presenter.searchMedia(query)
        verify(view, never()).showNoMatchesError()

        argumentCaptor<ArrayList<Media>>().apply {
            verify(view, times(1)).showMedia(capture())

            val mediaList = firstValue
            assertEquals(2, mediaList.size)
            assertEquals("Original TV Test", (mediaList[0] as Tv).episodeTitle)
        }
    }

    @Test
    fun invalidMediaObjectTypeRetrievedViaAPI() {

        val query = "test"

        val result = listOf(MediaResult("person"))
        val response = MediaResponse(result.size, result)
        whenever(repository.search(query)).thenReturn(Flowable.just(response))

        presenter.searchMedia(query)
        verify(view).showNoMatchesError()

    }
}