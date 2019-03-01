package com.example.recyclerview_api

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import java.util.ArrayList

open class MediaDataHolder : ViewModel() {

    lateinit var query : String
    var data = MutableLiveData<ArrayList<Media>>()
    var disposables: CompositeDisposable = CompositeDisposable()

    open fun mediaData(newData: ArrayList<Media>){
        data.value = newData
    }

    override fun onCleared(){
        disposables.clear()
    }
}