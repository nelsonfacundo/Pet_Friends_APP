package com.example.petfriendsapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petfriendsapp.R
import com.example.petfriendsapp.adapter.HistorialFirestoreRecyclerAdapter
import com.example.petfriendsapp.adapter.SolicitudRecibidaFirestoreRecyclerAdapter
import com.example.petfriendsapp.entities.Solicitud
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class Historial : Fragment() {

private lateinit var recHistorial : RecyclerView
private lateinit var historialAdapter : HistorialFirestoreRecyclerAdapter
    private val db = FirebaseFirestore.getInstance()
    private val dataManager = FirestoreDataManager()
    private val auth = FirebaseAuth.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val historialView = inflater.inflate(R.layout.fragment_historial, container, false)
        recHistorial = historialView.findViewById(R.id.rec_historial)
        setupRecyclerView()
        return historialView
    }

    private fun setupRecyclerView(){
        val user = auth.currentUser
        val uid = user?.uid

        if (uid != null) {
            val query = db.collection("peticiones")
                .whereEqualTo("idUsuarioDueño", uid)
            //   .whereEqualTo("estado","rechazado")
           //       .whereEqualTo("estado","aprobado")
                .whereEqualTo("estado", "pendiente")

            val options = FirestoreRecyclerOptions.Builder<Solicitud>()
                .setQuery(query, Solicitud::class.java)
                .build()

            historialAdapter = HistorialFirestoreRecyclerAdapter(options, dataManager)

            recHistorial.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = historialAdapter
            }
        } else {
            // Manejar el caso donde el usuario no está logueado
            // Podrías mostrar un mensaje o redirigir a la pantalla de login
        }
    }

    override fun onStart() {
        super.onStart()
        historialAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        historialAdapter.stopListening()
    }
}