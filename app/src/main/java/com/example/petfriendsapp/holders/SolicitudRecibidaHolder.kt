package com.example.petfriendsapp.holders

import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.petfriendsapp.R
import com.example.petfriendsapp.entities.Solicitud
import com.google.firebase.firestore.FirebaseFirestore

class SolicitudRecibidaHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val approveButton: ImageButton = view.findViewById(R.id.approve_button)
    private val rejectButton: ImageButton = view.findViewById(R.id.reject_button)
    private val nombreSolicitanteTextView: TextView = view.findViewById(R.id.person_name)
    private val nombreMascotaTextView: TextView = view.findViewById(R.id.pet_name)
    private val avatarImageView: ImageView = view.findViewById(R.id.id_avatar)
    private val db = FirebaseFirestore.getInstance()

    fun bind(solicitudId: String, solicitud: Solicitud, nombreMascota: String) {
        nombreMascotaTextView.text = nombreMascota

        approveButton.setOnClickListener {
            updateSolicitudStatus(solicitudId, "aprobado")

            val mascotaId = solicitud.idMascota

            // Cambiar el estado de la mascota a "aprobada"
            updateMascotaStatus(mascotaId, "aprobada")

            // Rechazar todas las demás solicitudes para la misma mascota
            rejectOtherSolicitudes(mascotaId, solicitudId)
        }

        rejectButton.setOnClickListener {
            updateSolicitudStatus(solicitudId, "rechazado")
        }
    }

    fun bindSolicitanteData(nombreSolicitante: String, urlAvatarSolicitante: String) {
        nombreSolicitanteTextView.text = nombreSolicitante
        Glide.with(itemView.context)
            .load(urlAvatarSolicitante)
            .circleCrop()
            .into(avatarImageView)
    }

    fun clear() {
        nombreSolicitanteTextView.text = ""
        nombreMascotaTextView.text = ""
        avatarImageView.setImageDrawable(null)
    }

    private fun updateSolicitudStatus(solicitudId: String, status: String) {
        db.collection("peticiones").document(solicitudId)
            .update("estado", status)
            .addOnSuccessListener {
                // Podrías mostrar un mensaje de éxito o manejar la actualización de otra manera
            }
            .addOnFailureListener { exception ->
                Log.e("SolicitudRecibidaHolder", "Error al actualizar estado de la solicitud", exception)
                // Manejar el error
            }
    }

    private fun updateMascotaStatus(mascotaId: String, status: String) {
        db.collection("mascotas").document(mascotaId)
            .update("estado", status)
            .addOnSuccessListener {
                // Podrías mostrar un mensaje de éxito o manejar la actualización de otra manera
            }
            .addOnFailureListener { exception ->
                Log.e("SolicitudRecibidaHolder", "Error al actualizar estado de la mascota", exception)
                // Manejar el error
            }
    }

    private fun rejectOtherSolicitudes(mascotaId: String, approvedSolicitudId: String) {
        db.collection("peticiones")
            .whereEqualTo("idMascota", mascotaId)
            .whereEqualTo("estado", "pendiente")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    if (document.id != approvedSolicitudId) {
                        document.reference.update("estado", "rechazado")
                            .addOnSuccessListener {
                                // Podrías manejar la actualización de otra manera
                            }
                            .addOnFailureListener { exception ->
                                Log.e("SolicitudRecibidaHolder", "Error al rechazar solicitud", exception)
                                // Manejar el error
                            }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("SolicitudRecibidaHolder", "Error al obtener otras solicitudes", exception)
                // Manejar el error
            }
    }
}
