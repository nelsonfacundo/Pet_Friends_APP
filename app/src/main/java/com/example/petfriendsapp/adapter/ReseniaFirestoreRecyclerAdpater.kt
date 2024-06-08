package com.example.petfriendsapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.petfriendsapp.R
import com.example.petfriendsapp.entities.Resenia
import com.example.petfriendsapp.holders.ReseniaHolder
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class ReseniaFirestoreRecyclerAdpater (private val options: FirestoreRecyclerOptions<Resenia> ) :
    FirestoreRecyclerAdapter<Resenia, ReseniaHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReseniaHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.mascota, parent, false)
        return ReseniaHolder(view)
    }

    override fun onBindViewHolder(holder: ReseniaHolder, position: Int, model: Resenia) {
        holder.setValoracion(model.valoracion)
        holder.setResenia(model.resenia)

        val reseniaId = snapshots.getSnapshot(position).id
        }
}