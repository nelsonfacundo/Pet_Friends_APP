package com.example.petfriendsapp.viewmodels

import androidx.lifecycle.ViewModel
import com.example.petfriendsapp.entities.Mascota

class ListViewModel : ViewModel() {

    var mascotas : MutableList<Mascota> = ArrayList<Mascota>()

}