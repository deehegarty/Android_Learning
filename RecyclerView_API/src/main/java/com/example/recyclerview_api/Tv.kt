package com.example.recyclerview_api

data class Tv(val episodeTitle: String, val firstAir: String, val tvDescription: String) : Media {
    override fun getType(): MediaType {
        return MediaType.Tv
    }
}