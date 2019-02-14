package com.example.recyclerview

// cat extends animal
// this means that it must return type AnimalType (enum class)
// CAT or DOG
data class cat(val breed:String, val name:String): Animal {

    override fun getType(): AnimalType {
        return AnimalType.CAT
    }
}
