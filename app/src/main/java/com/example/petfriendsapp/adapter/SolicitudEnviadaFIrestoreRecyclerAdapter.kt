package com.example.petfriendsapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.petfriendsapp.R
import com.example.petfriendsapp.entities.Solicitud
import com.example.petfriendsapp.fragments.FirestoreDataManager
import com.example.petfriendsapp.holders.SolicitudEnviadaHolder
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class SolicitudEnviadaFIrestoreRecyclerAdapter(
    private val options: FirestoreRecyclerOptions<Solicitud>,
    private val dataManager: FirestoreDataManager
    ) : FirestoreRecyclerAdapter<Solicitud, SolicitudEnviadaHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SolicitudEnviadaHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.solicitud_enviada_card, parent, false)
        return SolicitudEnviadaHolder(view)
    }

    override fun onBindViewHolder(holder: SolicitudEnviadaHolder, position: Int, model: Solicitud) {
}
    }
