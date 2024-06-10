package com.example.petfriendsapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.petfriendsapp.R
import com.example.petfriendsapp.entities.Review
import com.example.petfriendsapp.holders.ReseniaHolder
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class ReviewFirestoreRecyclerAdpater (private val reviews: FirestoreRecyclerOptions<Review> ) :
    FirestoreRecyclerAdapter<Review, ReseniaHolder>(reviews) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReseniaHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.mascota, parent, false)
        return ReseniaHolder(view)
    }

    override fun onBindViewHolder(holder: ReseniaHolder, position: Int, model: Review) {
        holder.setValoracion(model.valoracion)
        holder.setOpinion(model.opinion)
        holder.setCondicionRating(model.condicionRating)
        holder.setComunicacionRating(model.comunicacionRating)

        val reseniaId = snapshots.getSnapshot(position).id
        }
}