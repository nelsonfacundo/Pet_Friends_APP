package com.example.petfriendsapp.fragments

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

import android.util.Log
import com.example.petfriendsapp.entities.Mascota
import com.example.petfriendsapp.entities.Solicitud

class FirestoreDataManager {

    private val db = Firebase.firestore

    fun cargarNombreMascota(idMascota: String, onSuccess: (String) -> Unit, onError: (Exception) -> Unit) {
        val mascotaRef = db.collection("mascotas").document(idMascota)
        mascotaRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val nombreMascota = documentSnapshot.getString("nombre")
                if (nombreMascota != null) {
                    onSuccess(nombreMascota)
                } else {
                    Log.e("FirestoreDataManager", "Nombre de mascota no encontrado")
                    onError(Exception("Nombre de mascota no encontrado"))
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreDataManager", "Error al cargar nombre de mascota", exception)
                onError(exception)
            }
    }

    fun cargarDatosSolicitante(idSolicitante: String, onSuccess: (String, String) -> Unit, onError: (Exception) -> Unit) {
        val solicitanteRef = db.collection("users").document(idSolicitante)
        solicitanteRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val nombreSolicitante = documentSnapshot.getString("nombre")
                val urlAvatarSolicitante = documentSnapshot.getString("avatarUrl")
                if (nombreSolicitante != null && urlAvatarSolicitante != null) {
                    onSuccess(nombreSolicitante, urlAvatarSolicitante)
                } else {
                    Log.e("FirestoreDataManager", "Datos del solicitante no encontrados")
                    onError(Exception("Datos del solicitante no encontrados"))
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreDataManager", "Error al cargar datos del solicitante", exception)
                onError(exception)
            }
    }

}

