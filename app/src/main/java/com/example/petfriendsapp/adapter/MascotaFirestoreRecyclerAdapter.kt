package com.example.petfriendsapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.petfriendsapp.R
import com.example.petfriendsapp.entities.Mascota
import com.example.petfriendsapp.holders.MascotaHolder
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class MascotaFirestoreRecyclerAdapter(private val options: FirestoreRecyclerOptions<Mascota>) :
    FirestoreRecyclerAdapter<Mascota, MascotaHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MascotaHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.mascota, parent, false)
        return MascotaHolder(view)
    }

    override fun onBindViewHolder(holder: MascotaHolder, position: Int, model: Mascota) {
        holder.setName(model.nombre)
        holder.setEdad(model.edad)
        holder.setRaza(model.especie)
        holder.setSexo(model.sexo)
        holder.setImageUrl(model.imageUrl)


//        holder.setUbicacion(model.ubicacion)
    }
}