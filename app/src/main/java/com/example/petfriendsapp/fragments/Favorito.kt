package com.example.petfriendsapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petfriendsapp.R
import com.example.petfriendsapp.adapter.MascotaFirestoreRecyclerAdapter
import com.example.petfriendsapp.entities.Mascota
import com.example.petfriendsapp.viewmodels.ListViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore


class Favorito : Fragment() {

    private lateinit var viewFavorito: View
    private lateinit var recMascotas: RecyclerView
    private lateinit var viewModel: ListViewModel
    private lateinit var mascotaClickListener: (Mascota, String) -> Unit

    private val db = FirebaseFirestore.getInstance()
    private val firestoreDataManager = FirestoreDataManager()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewFavorito = inflater.inflate(R.layout.fragment_favorito, container, false)
        initViews()
        setupRecyclerView()

        mascotaClickListener = { mascota, mascotaId ->
            redirigir(mascota, mascotaId)
        }

        return viewFavorito
    }

    override fun onStart() {
        super.onStart()
        fillRecycler()
    }

    private fun initViews() {
        recMascotas = viewFavorito.findViewById(R.id.recyclerViewFavorite)
    }

    private fun setupRecyclerView() {
        recMascotas.setHasFixedSize(true)
        recMascotas.layoutManager = LinearLayoutManager(context)
    }

    private fun redirigir(mascota: Mascota, mascotaId: String) {
        val action = FavoritoDirections.actionFavoritoToDetailsAdoptar(mascota, mascotaId)
        findNavController().navigate(action)
    }

    private fun fillRecycler() {
        firestoreDataManager.getFavoriteMascotaIds { favoriteIds ->
            if (favoriteIds.isNotEmpty()) {
                val query = db.collection("mascotas").whereIn(FieldPath.documentId(), favoriteIds)

                val options = FirestoreRecyclerOptions.Builder<Mascota>()
                    .setQuery(query, Mascota::class.java)
                    .build()

                val adapter = MascotaFirestoreRecyclerAdapter(options, firestoreDataManager, mascotaClickListener)
                adapter.startListening()
                recMascotas.adapter = adapter
            } else {
                // Mostrar un Toast indicando que no hay favoritos
                Toast.makeText(requireContext(), "No tienes mascotas marcadas como favoritas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[ListViewModel::class.java]
        checkRecords()
    }

    private fun checkRecords() {
        db.collection("mascotas").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Hacer algo si es necesario
                }
            }
    }
}