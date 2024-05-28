package com.example.petfriendsapp.holders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.petfriendsapp.R

class MascotaHolder (v: View): RecyclerView.ViewHolder(v) {
    private var view: View

    init {
        this.view = v
    }

    fun setRaza(raza: String) {
        val txtRaza: TextView = view.findViewById(R.id.raza)
        txtRaza.text = raza
    }

    fun setUbicacion(ubicacion: String) {
        val txtUbicacion: TextView = view.findViewById(R.id.ubicacion)
        txtUbicacion.text = ubicacion
    }

    fun setDesc(descripcion: String) {
        //val txtDescripcion: TextView = view.findViewById(R.id.textViewDesc)
        //txtDescripcion.text = descripcion
    }
    fun setEdad(edad: Int) {
        val txtDescripcion: TextView = view.findViewById(R.id.edad)
        txtDescripcion.text = edad.toString()
    }

    fun setSexo(sexo: String) {
//        val txtDescripcion: TextView = view.findViewById(R.id.textViewDesc)
//        txtDescripcion.text = sexo
    }
    fun getCardLayout(): View {
        return view.findViewById(R.id.card_mascota)
    }
}