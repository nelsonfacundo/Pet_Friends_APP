package com.example.petfriendsapp.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.petfriendsapp.entities.Articulo


class ArticuloViewModel : ViewModel() {
    var articles = MutableLiveData<List<Articulo>>()

    init {
        /*addArticles()*/
    }

   /* private fun addArticles(){
       val articlesList = listOf(
           *//* Articulo(R.string.txt_blog_title_1, R.string.txt_blog_desc_1.toString(), R.drawable.perro_ejercitando.toString()),
            Articulo(R.string.txt_blog_title_2.toString(), R.string.txt_blog_desc_2.toString(), R.drawable.alimentos_prohibidos_para_perros.toString()),
            Articulo(R.string.txt_blog_title_3.toString(), R.string.txt_blog_desc_3.toString(), R.drawable.adoptame.toString())*//*
       )
        articles.value = articlesList
    }*/


}
