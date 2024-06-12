package com.example.petfriendsapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.petfriendsapp.adapter.MascotaUserFirestoreRecycletAdapter
import com.example.petfriendsapp.databinding.FragmentMyPetsBinding
import com.example.petfriendsapp.entities.Mascota
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class MyPetsFragment : Fragment() {
    private var _binding: FragmentMyPetsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MascotaUserFirestoreRecycletAdapter
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var mascotaClickListener: (Mascota, String) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyPetsBinding.inflate(inflater, container, false)
        binding.recMisMascotas.layoutManager = LinearLayoutManager(context)

        mascotaClickListener = { mascota, mascotaId ->
            redirigir(mascota, mascotaId)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadUserPets()
    }

    private fun initListeners() {
        binding.icBackMisMascotas.setOnClickListener { navigateToPerfil() }
    }

    private fun loadUserPets() {
        val userId = auth.currentUser?.uid
        userId?.let { uid ->
            val query: Query = db.collection("mascotas")
                .whereEqualTo("userId", uid)
                .whereEqualTo("estado", "pendiente")

            val options = FirestoreRecyclerOptions.Builder<Mascota>()
                .setQuery(query, Mascota::class.java)
                .build()

            adapter = MascotaUserFirestoreRecycletAdapter(requireContext(), options, mascotaClickListener)
            binding.recMisMascotas.adapter = adapter
            adapter.startListening()

            query.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null && !snapshot.isEmpty) {
                    binding.noPetsText.visibility = View.GONE
                } else {
                    binding.noPetsText.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun redirigir(mascota: Mascota, mascotaId: String) {
        // Aquí defines la acción de redireccionamiento al hacer clic en una mascota
    }

    override fun onStart() {
        super.onStart()
        initListeners()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun navigateToPerfil() {
        val action = MyPetsFragmentDirections.actionMyPetsFragmentToPerfil()
        binding.root.findNavController().navigate(action)
    }
}
