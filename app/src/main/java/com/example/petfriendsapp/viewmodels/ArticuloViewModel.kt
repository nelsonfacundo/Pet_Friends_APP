package com.example.petfriendsapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.petfriendsapp.R
import com.example.petfriendsapp.entities.Articulo

class ArticuloViewModel : ViewModel() {
    var articles = MutableLiveData<List<Articulo>>()

    init {
        addArticles()
    }

    private fun addArticles(){
       val articlesList = listOf(
            Articulo("Articulo 1", "Descripcion 1", R.drawable.sample_perro.toString()),
            Articulo("Articulo 2", "Descripcion 2", R.drawable.sample_perro.toString()),
            Articulo("Articulo 3", "Descripcion 3", R.drawable.sample_perro.toString())
            )
        articles.value = articlesList
    }
}