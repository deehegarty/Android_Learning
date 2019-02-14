package com.example.recyclerview_api

interface Media {
    fun getType(): MediaType
}

enum class MediaType{
    Movie, Tv
}
