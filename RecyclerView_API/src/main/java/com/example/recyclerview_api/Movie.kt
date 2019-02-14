package com.example.recyclerview_api

data class Movie (val title:String, val originalTitle: String, val movieDescription:String): Media{
    override fun getType(): MediaType {
        return MediaType.Movie
    }
}