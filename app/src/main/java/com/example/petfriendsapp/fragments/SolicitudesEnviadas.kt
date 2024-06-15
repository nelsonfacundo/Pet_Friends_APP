package com.example.petfriendsapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petfriendsapp.R
import com.example.petfriendsapp.adapter.SolicitudEnviadaFirestoreRecyclerAdapter
import com.example.petfriendsapp.entities.Solicitud
import com.example.petfriendsapp.fragments.FiltrosFragment.Companion.TAG
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.DocumentChange

class SolicitudesEnviadas : Fragment() {

    private lateinit var viewSolicitudes: View
    private lateinit var recSolicitudesEnviadas: RecyclerView
    private lateinit var solicitudAdapter: SolicitudEnviadaFirestoreRecyclerAdapter
    private lateinit var reviewClickListener: () -> Unit
    private val db = FirebaseFirestore.getInstance()
    private val dataManager = FirestoreDataManager()
    private val auth = FirebaseAuth.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewSolicitudes = inflater.inflate(R.layout.fragment_solicitudes_enviadas, container, false)

        reviewClickListener = { ->
            navigateToDarReviewFragment()
        }

        initRecSolicitudes()
        setupRecyclerView()



        return viewSolicitudes
    }

    private fun initRecSolicitudes() {
        recSolicitudesEnviadas = viewSolicitudes.findViewById(R.id.rec_solicitudes_enviadas)
    }

    private fun setupRecyclerView() {
        val user = auth.currentUser
        val uid = user?.uid

        if (uid != null) {
            val query = db.collection("peticiones")
                .whereEqualTo("idUsuarioAdopta", uid)
                .whereIn("estado", listOf("pendiente", "aprobado"))
                .whereEqualTo("Review", false)

            val options = FirestoreRecyclerOptions.Builder<Solicitud>()
                .setQuery(query, Solicitud::class.java)
                .build()

            solicitudAdapter = SolicitudEnviadaFirestoreRecyclerAdapter(options, dataManager, reviewClickListener)

            recSolicitudesEnviadas.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = solicitudAdapter
            }
        }
    }

    private fun navigateToDarReviewFragment() {
        findNavController().navigate(R.id.darReviewFragment)
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