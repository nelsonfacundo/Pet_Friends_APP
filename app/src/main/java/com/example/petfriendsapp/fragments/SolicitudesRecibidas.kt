package com.example.petfriendsapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.petfriendsapp.R
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petfriendsapp.adapter.SolicitudRecibidaFirestoreRecyclerAdapter
import com.example.petfriendsapp.entities.Solicitud
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.example.petfriendsapp.fragments.FirestoreDataManager
import com.google.firebase.auth.FirebaseAuth

class SolicitudesRecibidas : Fragment() {

    private lateinit var recSolicitudesRecibidas: RecyclerView
    private lateinit var solicitudAdapter: SolicitudRecibidaFirestoreRecyclerAdapter
    private val db = FirebaseFirestore.getInstance()
    private val dataManager = FirestoreDataManager()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_solicitudes_recibidas, container, false)
        recSolicitudesRecibidas = view.findViewById(R.id.rec_solicitudesRecibidas)
        setupRecyclerView()
        return view
    }

    private fun setupRecyclerView() {
        val user = auth.currentUser
        val uid = user?.uid

        if (uid != null) {
            val query = db.collection("peticiones")
                .whereEqualTo("idUsuarioDueño", uid)
                .whereEqualTo("estado", "pendiente")

            val options = FirestoreRecyclerOptions.Builder<Solicitud>()
                .setQuery(query, Solicitud::class.java)
                .build()

            solicitudAdapter = SolicitudRecibidaFirestoreRecyclerAdapter(options, dataManager)

            recSolicitudesRecibidas.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = solicitudAdapter
            }
        } else {
            // Manejar el caso donde el usuario no está logueado
            // Podrías mostrar un mensaje o redirigir a la pantalla de login
        }
    }


    override fun onStart() {
        super.onStart()
        solicitudAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        solicitudAdapter.stopListening()
    }
}
