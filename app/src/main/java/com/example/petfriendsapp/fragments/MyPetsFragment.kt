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
    private lateinit var _binding: FragmentMyPetsBinding
    private val binding get() = _binding!!
    private lateinit var adapter: MascotaUserFirestoreRecycletAdapter
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyPetsBinding.inflate(inflater, container, false)
        binding.recMisMascotas.layoutManager = LinearLayoutManager(context)


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

            adapter = MascotaUserFirestoreRecycletAdapter(requireContext(), options)
            binding.recMisMascotas.adapter = adapter
            adapter.startListening()
        }
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



    private fun navigateToPerfil() {
        val action = MyPetsFragmentDirections.actionMyPetsFragmentToPerfil()
        binding.root.findNavController().navigate(action)
    }
}


