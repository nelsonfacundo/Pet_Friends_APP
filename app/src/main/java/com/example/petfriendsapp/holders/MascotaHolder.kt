package com.example.petfriendsapp.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
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

    fun setName(name: String) {
        val txt: TextView = view.findViewById(R.id.nombre_card_mascota)
        txt.text = name
    }
//    fun setUbicacion(ubicacion: String) {
//        val txtUbicacion: TextView = view.findViewById(R.id.ubicacion)
//        txtUbicacion.text = ubicacion
//    }

    fun setDesc(descripcion: String) {
        //val txtDescripcion: TextView = view.findViewById(R.id.textViewDesc)
        //txtDescripcion.text = descripcion
    }

    fun setEdad(edad: Int) {
        val txtDescripcion: TextView = view.findViewById(R.id.edad)
        txtDescripcion.text = edad.toString()
    }

    fun setImageUrl(imageUrl: String) {
        val image: ImageView = view.findViewById(R.id.pet_image)
        Glide.with(view.context)
            .load(imageUrl)
            .placeholder(R.drawable.avatar)
            .error(R.drawable.avatar)
            .into(image)
    }

        fun setSexo(sexo: String) {
        val txtDescripcion: TextView = view.findViewById(R.id.sexo)
        txtDescripcion.text = sexo
    }
    fun getCardLayout(): CardView {
        return view.findViewById(R.id.card_mascota)
    }
}