package com.example.recyclerview

interface Animal {
    fun getType(): AnimalType
}

enum class AnimalType {
    CAT, DOG
}