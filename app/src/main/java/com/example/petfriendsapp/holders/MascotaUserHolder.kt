package com.example.petfriendsapp.holders

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.petfriendsapp.R

class MascotaUserHolder(v: View) : RecyclerView.ViewHolder(v) {

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
        return view.findViewById(R.id.card_mascota_delete)
    }

    fun setOnDeleteClickListener(listener: () -> Unit) {
        val deleteButton = view.findViewById<ImageButton>(R.id.delete_button)
        deleteButton.setOnClickListener {
            listener.invoke()
        }
    }
}
