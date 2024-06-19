package com.example.petfriendsapp.fragments

import android.os.Bundle
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
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SolicitudesEnviadas.newInstance] factory method to
 * create an instance of this fragment.
 */
class SolicitudesEnviadas : Fragment() {

    private lateinit var viewSolicitudes: View
    private lateinit var recSolicitudesEnviadas: RecyclerView
    private lateinit var solicitudAdapter: SolicitudEnviadaFirestoreRecyclerAdapter
    private val db = FirebaseFirestore.getInstance()
    private val dataManager = FirestoreDataManager()
    private val auth = FirebaseAuth.getInstance()

    private val reviewClickListener: (String, String) -> Unit = { idUsuarioDueño, solicitudId ->
        navigateToDarReviewFragment(idUsuarioDueño, solicitudId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewSolicitudes = inflater.inflate(R.layout.fragment_solicitudes_enviadas, container, false)
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

    private fun navigateToDarReviewFragment(idUsuarioDueño: String, solicitudId: String) {
        val bundle = Bundle().apply {
            putString("idUsuarioDueño", idUsuarioDueño)
            putString("solicitudId", solicitudId)
        }
        findNavController().navigate(R.id.darReviewFragment, bundle)
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