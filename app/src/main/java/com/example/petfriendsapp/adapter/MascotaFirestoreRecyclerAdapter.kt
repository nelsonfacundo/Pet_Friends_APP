package com.example.petfriendsapp.adapter

import android.util.Log
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

        firestoreDataManager.getFavoriteIds { favoritos ->
            val isFavorite = favoritos.contains(mascotaId)

            holder.bind(model, isFavorite) { newFavoriteState ->
                if (newFavoriteState) {
                    firestoreDataManager.addToFavorites(mascotaId) { success ->
                        if (success) {
                            holder.updateFavoriteButton(true)
                        } else {
                            // Manejar error al agregar a favoritos
                        }
                    }
                } else {
                    firestoreDataManager.removeFromFavorites(mascotaId) { success ->
                        if (success) {
                            holder.updateFavoriteButton(false)
                        }
                    }
                }
            }
        }

        holder.getCardLayout().setOnClickListener {
            Log.d("Adapter", "Card clicked for Mascota ID: $mascotaId")
            mascotaClickListener(model, mascotaId)
        }
    }
}
