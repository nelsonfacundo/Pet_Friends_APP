package com.example.petfriendsapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.petfriendsapp.R
import com.example.petfriendsapp.entities.Solicitud
import com.example.petfriendsapp.fragments.FirestoreDataManager
import com.example.petfriendsapp.holders.HistorialHolder
import com.example.petfriendsapp.holders.SolicitudRecibidaHolder
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class HistorialFirestoreRecyclerAdapter (
    private val options: FirestoreRecyclerOptions<Solicitud>,
    private val dataManager: FirestoreDataManager
) : FirestoreRecyclerAdapter<Solicitud, HistorialHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.historial, parent, false)
        return HistorialHolder(view)
    }

    override fun onBindViewHolder(holder: HistorialHolder, position: Int, model: Solicitud) {
        holder.clear()

        // Obtenemos el ID del documento
        val solicitudId = snapshots.getSnapshot(position).id

        // Load and set nombreMascota
        dataManager.cargarNombreMascota(model.idMascota,
            onSuccess = { nombreMascota ->
                holder.bind(solicitudId, model, nombreMascota)
            },
            onError = { error ->
                // Handle error
            }
        )

        // Load and set solicitante data
        dataManager.cargarDatosSolicitante(model.idUsuarioAdopta,
            onSuccess = { nombreSolicitante, urlAvatarSolicitante ->
                holder.bindSolicitanteData(nombreSolicitante, urlAvatarSolicitante)
            },
            onError = { error ->
                // Handle error
            }
        )
    }


}