package com.example.petfriendsapp.viewmodels

import androidx.lifecycle.ViewModel
import com.example.petfriendsapp.entities.Mascota
import com.example.petfriendsapp.entities.Review

class ListViewModel : ViewModel() {

    var mascotas : MutableList<Mascota> = ArrayList<Mascota>()
    var reviews : MutableList<Review> = ArrayList<Review>()

}

