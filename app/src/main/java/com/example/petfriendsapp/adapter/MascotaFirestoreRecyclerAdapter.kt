package com.example.petfriendsapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.petfriendsapp.R
import com.example.petfriendsapp.entities.Mascota
import com.example.petfriendsapp.fragments.FirestoreDataManager
import com.example.petfriendsapp.holders.MascotaHolder
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class MascotaFirestoreRecyclerAdapter(
    private val options: FirestoreRecyclerOptions<Mascota>,
    private val firestoreDataManager: FirestoreDataManager,
    private val mascotaClickListener: (Mascota, String) -> Unit
) : FirestoreRecyclerAdapter<Mascota, MascotaHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MascotaHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.mascota, parent, false)
        return MascotaHolder(view)
    }

    override fun onBindViewHolder(holder: MascotaHolder, position: Int, model: Mascota) {
        val mascotaId = snapshots.getSnapshot(position).id

        // Obtener los IDs de las mascotas favoritas del usuario actual
        firestoreDataManager.getFavoriteIds { favoritos ->
            val isFavorite = favoritos.contains(mascotaId)

            // Llamar al método bind en MascotaHolder para configurar la vista
            holder.bind(model, isFavorite) { newFavoriteState ->
                if (newFavoriteState) {
                    // Agregar a favoritos
                    firestoreDataManager.addToFavorites(mascotaId) { success ->
                        if (success) {
                            // Actualizar la UI si es necesario
                            holder.updateFavoriteButton(true)
                        } else {
                            // Manejar error al agregar a favoritos
                            // Por ejemplo, mostrar un mensaje al usuario
                        }
                    }
                } else {
                    firestoreDataManager.removeFromFavorites(mascotaId) { success ->
                        if (success) {
                            // Actualizar la UI si es necesario
                            holder.updateFavoriteButton(false)
                        }
                    }
                }
            }
        }


        holder.getCardLayout().setOnClickListener {
            mascotaClickListener(model, mascotaId)
        }
    }
}