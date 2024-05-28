package com.example.petfriendsapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.petfriendsapp.R
import com.example.petfriendsapp.entities.Mascota
import com.example.petfriendsapp.fragments.DetailsAdoptar
import com.example.petfriendsapp.holders.MascotaHolder

class MascotaAdapter(
    private val mascotas: MutableList<Mascota>,
    private val clickListener: (Mascota) -> Unit
): RecyclerView.Adapter<MascotaHolder>(){
    override fun getItemCount() = mascotas.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MascotaHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mascota ,parent,false)
        return (MascotaHolder(view))
    }
    override fun onBindViewHolder(holder: MascotaHolder, position: Int) {
        val mascota = mascotas[position]
        holder.setRaza(mascota.raza)
        holder.setUbicacion(mascota.ubicacion)
        holder.setEdad(mascota.edad)
        holder.getCardLayout().setOnClickListener { clickListener(mascota) }
    }
}