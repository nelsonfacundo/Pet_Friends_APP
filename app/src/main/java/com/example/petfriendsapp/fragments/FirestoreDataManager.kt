package com.example.petfriendsapp.fragments

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth

class FirestoreDataManager {

    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

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

    fun loadEstadoSolicitud(idPeticion : String, onSuccess: (String) -> Unit, onError: (Exception) -> Unit){
        val peticionRef = db.collection("peticiones").document(idPeticion)
        peticionRef.get().addOnSuccessListener { documentSnapshot ->
            val estadoPeticion = documentSnapshot.getString("estado")
            if(estadoPeticion != null){
                onSuccess(estadoPeticion)
            } else{
                Log.e("FirestoreDataManager", "Datos de la peticion no encontrados")
                onError(Exception("Datos de la peticion no encontrados"))
            }
        }
            .addOnFailureListener{ exception ->
                Log.e("FirestoreDataManager", "Error al cargar datos de la peticion", exception)
                onError(exception) }
    }

    fun loadImageMascota(idMascota: String, onSuccess: (String) -> Unit, onError: (Exception) -> Unit) {
        val mascotaRef = db.collection("mascotas").document(idMascota)
        mascotaRef.get()
            .addOnSuccessListener { documentSnapshot ->
                val urlMascotaImage = documentSnapshot.getString("imageUrl")
                if (urlMascotaImage != null) {
                    onSuccess(urlMascotaImage)
                } else {
                    Log.e("FirestoreDataManager", "Foto de la mascota no encontrada.")
                    onError(Exception("Foto de la mascota no encontrada."))
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreDataManager", "Error al cargar datos", exception)
                onError(exception)
            }
    }

    fun loadReview(idPeticion : String, onSuccess: (Boolean) -> Unit, onError: (Exception) -> Unit){
        val peticionRef = db.collection("peticiones").document(idPeticion)
        peticionRef.get().addOnSuccessListener { documentSnapshot ->
            val reviewPeticion = documentSnapshot.getBoolean("Review")
            if(reviewPeticion != null){
                onSuccess(reviewPeticion)
            } else{
                Log.e("FirestoreDataManager", "No se encontro una review")
                onError(Exception("No se encontró una review"))
            }
        }
            .addOnFailureListener{ exception ->
                Log.e("FirestoreDataManager", "Error al cargar datos de la peticion", exception)
                onError(exception) }
    }
    fun addToFavorites(mascotaId: String, callback: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val favRef = db.collection("users").document(userId).collection("favoritos").document(mascotaId)

        favRef.set(hashMapOf("mascotaId" to mascotaId))
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }
    fun getFavoriteMascotaIds(callback: (List<String>) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val favRef = db.collection("users").document(userId).collection("favoritos")

        favRef.get()
            .addOnSuccessListener { documents ->
                val favoritos = documents.mapNotNull { it.getString("mascotaId") }
                callback(favoritos)
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreDataManager", "Error al obtener favoritos", exception)
                callback(emptyList())
            }
    }

    fun removeFromFavorites(mascotaId: String, callback: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val favRef = db.collection("users").document(userId).collection("favoritos").document(mascotaId)

        favRef.delete()
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    fun getFavoriteIds(callback: (List<String>) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val favRef = db.collection("users").document(userId).collection("favoritos")

        favRef.get()
            .addOnSuccessListener { documents ->
                val favoritos = documents.mapNotNull { it.getString("mascotaId") }
                callback(favoritos)
            }
            .addOnFailureListener { callback(emptyList()) }
    }
}