package com.example.recyclerview

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class animalAdapter(val animals: ArrayList<Animal>) : RecyclerView.Adapter<animalAdapter.BaseViewHolder>(){

    // to later identify animal type
    private val TYPE_DOG = 0
    private val TYPE_CAT = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        // inflate different layouts depending on the viewType
        // TYPE_DOG or TYPE_CAT
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == TYPE_DOG) {
            return DogViewHolder(inflater.inflate(R.layout.dog_row, parent, false))
        } else {
            return CatViewHolder(inflater.inflate(R.layout.cat_row, parent, false))
        }
    }

    abstract class BaseViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        // abstract ViewHolder that can be extended by different types of ViewHolders
        // DogViewHolder and CatViewHolder
        abstract fun bind(animal: Animal)
    }

    class DogViewHolder(itemView : View) : BaseViewHolder(itemView) {
        // initialised inside the class BUT outside of the bind method
        // to avoid creating and destroying the same things every time bind()
        // is called
        private val breed: TextView = itemView.findViewById(R.id.breedName)
        private val name: TextView = itemView.findViewById(R.id.dogName)
        private val age: TextView = itemView.findViewById(R.id.dogAge)

        // bind the value inside the dog object to the element in the xml
        override fun bind(animal: Animal) {
            //cast to dog
            val dog = animal as dog

            // using Kotlin synthax instead of Java synthax
            // breed.setText() = dog.breed
            breed.text = dog.breed
            name.text = dog.name
            age.text = dog.age.toString()
        }
    }

    class CatViewHolder(itemView : View) : BaseViewHolder(itemView) {
        // initialised inside the class BUT outisde of the bind method
        // to avoid creating and destroying the same things every time bind()
        // is called
        private val name: TextView = itemView.findViewById(R.id.catName)
        private val breed: TextView = itemView.findViewById(R.id.breedName)

        // bind the value inside the cat object to the element in the xml
        override fun bind(animal: Animal) {
            val cat = animal as cat
            breed.text = cat.breed
            name.text = cat.name
        }
    }

    // bind every row to ViewHolder
    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(animals[position])
    }

    override fun getItemViewType(position: Int): Int {
        // AnimalType - enum
        // return different animal type
        // TYPE_CAT or TYPE_DOG
        // depending on if the enum specifies
        // CAT or DOG
        val animal = animals[position]
        return when (animal.getType()) {
            AnimalType.CAT -> TYPE_CAT
            AnimalType.DOG -> TYPE_DOG
        }
    }

    // return the size of the Arraylist (the list of animals that was passed from MainActivity)
    override fun getItemCount(): Int = animals.size

    // methods for updating data/list
    fun updateAnimals(animals: ArrayList<Animal>) {
        this.animals.clear()
        this.animals.addAll(animals)
        notifyDataSetChanged()
    }

    fun addAnimal(animal: Animal) {
        animals.add(animal)
        notifyItemInserted(animals.size - 1)
    }
}