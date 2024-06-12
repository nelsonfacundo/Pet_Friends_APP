package com.example.petfriendsapp.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.petfriendsapp.R
import com.example.petfriendsapp.entities.Solicitud
import com.google.firebase.firestore.FirebaseFirestore

class HistorialHolder (view: View) : RecyclerView.ViewHolder(view) {


    private val estadoSolicitudTextView : TextView = view.findViewById(R.id.estado_solicitud)
    private val nombreSolicitanteTextView: TextView = view.findViewById(R.id.person_name)
    private val nombreMascotaTextView: TextView = view.findViewById(R.id.pet_name)
    private val avatarImageView: ImageView = view.findViewById(R.id.id_avatar)
    private val db = FirebaseFirestore.getInstance()

    fun bind(solicitudId: String, solicitud: Solicitud, nombreMascota: String) {
        nombreMascotaTextView.text = nombreMascota

    }

    fun bindEstadoPeticion(estadoPeticion : String){
        estadoSolicitudTextView.text = estadoPeticion
    }
    fun bindSolicitanteData(nombreSolicitante: String, urlAvatarSolicitante: String) {
        nombreSolicitanteTextView.text = nombreSolicitante
        Glide.with(itemView.context).load(urlAvatarSolicitante).into(avatarImageView)
    }

    fun clear() {
        nombreSolicitanteTextView.text = ""
        nombreMascotaTextView.text = ""
        avatarImageView.setImageDrawable(null)
        estadoSolicitudTextView.text= ""
    }

}