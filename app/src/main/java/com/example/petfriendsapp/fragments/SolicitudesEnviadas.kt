package com.example.petfriendsapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petfriendsapp.R
import com.example.petfriendsapp.adapter.SolicitudEnviadaFIrestoreRecyclerAdapter
import com.example.petfriendsapp.adapter.SolicitudRecibidaFirestoreRecyclerAdapter
import com.example.petfriendsapp.entities.Solicitud
import com.example.petfriendsapp.viewmodels.ListViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class SolicitudesEnviadas : Fragment() {

    private lateinit var viewSolicitudes: View
    private lateinit var recyclerSolicitudes: RecyclerView
    private lateinit var adapter: SolicitudEnviadaFIrestoreRecyclerAdapter
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val dataManager = FirestoreDataManager()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewSolicitudes = inflater.inflate(R.layout.fragment_solicitudes_enviadas, container, false)

        initViews()
        setupRecyclerView()
        observeSolicitudes()

        return viewSolicitudes
    }

    private fun initViews() {
        recyclerSolicitudes = viewSolicitudes.findViewById(R.id.rec_solicitudes_enviadas)
    }

    private fun setupRecyclerView() {
        recyclerSolicitudes.setHasFixedSize(true)
        recyclerSolicitudes.layoutManager = LinearLayoutManager(context)
    }

    private fun observeSolicitudes() {
        val user = auth.currentUser
        val uid = user?.uid


        if (uid != null) {

            val query = db.collection("peticiones")
                .whereEqualTo("idUsuarioDueño", uid)
                .whereEqualTo("estado", "pendiente")

            val options = FirestoreRecyclerOptions.Builder<Solicitud>()
                .setQuery(query, Solicitud::class.java)
                .build()

            adapter = SolicitudEnviadaFIrestoreRecyclerAdapter(options, FirestoreDataManager())
            recyclerSolicitudes.adapter = adapter
        }

    }





    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}