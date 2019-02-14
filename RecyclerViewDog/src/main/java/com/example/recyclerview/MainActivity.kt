package com.example.recyclerview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // create dummy data (Cats & Dogs - Animal Type reference)
        val animalData: ArrayList<Animal> = ArrayList()
        for (i in 1..100) {
            val rnds = (0..10).random()
            if (rnds % 2 == 0) {
                animalData.add(dog("Dog Type", "Fido #$i", rnds.toInt()))
            } else {
                animalData.add(cat("Cat Type", "Alexa #$rnds"))
            }
        }

        // Access element in content_main.xml and create layout manager and adapter
        recyclerViewXml.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager
        recyclerViewXml.adapter = animalAdapter(animalData)

    }
}
