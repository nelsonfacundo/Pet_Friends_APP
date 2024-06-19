package com.example.petfriendsapp.holders

import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.petfriendsapp.R
import com.example.petfriendsapp.entities.Mascota
import org.checkerframework.common.subtyping.qual.Bottom

class MascotaHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    fun setName(name: String) {
        val txt: TextView = view.findViewById(R.id.nombre_card_mascota)
        txt.text = name
    }

    fun setRaza(raza: String) {
        val txtRaza: TextView = view.findViewById(R.id.raza)
        txtRaza.text = raza
    }

    fun setEdad(edad: Int) {
        val txtEdad: TextView = view.findViewById(R.id.edad)
        txtEdad.text = edad.toString()
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
        val txtSexo: TextView = view.findViewById(R.id.sexo)
        txtSexo.text = sexo
    }

    fun getCardLayout(): CardView {
        return view.findViewById(R.id.card_mascota)
    }

    fun getFavoriteButton(): ImageButton {
        return view.findViewById(R.id.favorite_button)
    }

    fun bind(mascota: Mascota, isFavorite: Boolean, favoriteClickListener: (Boolean) -> Unit) {
        setName(mascota.nombre)
        setEdad(mascota.edad)
        setSexo(mascota.sexo)
        setRaza(mascota.especie)
        setImageUrl(mascota.imageUrl)

        updateFavoriteButton(isFavorite)

        getFavoriteButton().setOnClickListener {
            favoriteClickListener(!isFavorite)
        }

    }

    fun updateFavoriteButton(isFavorite: Boolean) {
        if (isFavorite) {
            getFavoriteButton().setImageResource(R.drawable.favorite_active)
        } else {
            getFavoriteButton().setImageResource(R.drawable.favorito)
        }
    }
}
