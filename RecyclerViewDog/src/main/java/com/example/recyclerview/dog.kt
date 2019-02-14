package com.example.recyclerview

data class dog(val breed: String, val name: String, val age: Int) : Animal {

    override fun getType(): AnimalType {
        return AnimalType.DOG
    }
}