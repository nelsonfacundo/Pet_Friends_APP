package com.example.petfriendsapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.petfriendsapp.R
import com.example.petfriendsapp.adapter.ReviewFirestoreRecyclerAdpater
import com.example.petfriendsapp.databinding.FragmentProfilePetOwnerBinding
import com.example.petfriendsapp.entities.Mascota
import com.example.petfriendsapp.entities.Review
import com.example.petfriendsapp.viewmodels.ListViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class ProfilePetOwner : Fragment() {

    private lateinit var binding: FragmentProfilePetOwnerBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var viewModel: ListViewModel
    private lateinit var userId: String
    private lateinit var idMascota: String
    private lateinit var mascota: Mascota

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfilePetOwnerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: ProfilePetOwnerArgs by navArgs()
        userId = args.ownerId
        mascota = args.mascot
        idMascota = args.idMascot

        setupRecyclerView()
        fetchOwnerDetails(userId)
        fetchReviews()
        promRating()
    }

    private fun setupRecyclerView() {
        binding.reviewListProfileOwnerPet.setHasFixedSize(true)
        binding.reviewListProfileOwnerPet.layoutManager = LinearLayoutManager(context)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[ListViewModel::class.java]
        checkRecords()
    }

    override fun onStart() {
        super.onStart()
        initListeners()
        fillRecycler()
        promRating()
    }

    private fun fillRecycler() {
        val query = db.collection("users").document(userId).collection("ratings")

        val options = FirestoreRecyclerOptions.Builder<Review>()
            .setQuery(query, Review::class.java)
            .build()

        val adapter = ReviewFirestoreRecyclerAdpater(options)
        adapter.startListening()
        binding.reviewListProfileOwnerPet.adapter = adapter

        query.get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    binding.reviewListProfileOwnerPet.visibility = View.GONE
                    binding.emptyMessageProfileOwnerPet.visibility = View.VISIBLE
                } else {
                    binding.reviewListProfileOwnerPet.visibility = View.VISIBLE
                    binding.emptyMessageProfileOwnerPet.visibility = View.GONE
                }
            }
            .addOnFailureListener { exception ->
                Log.w("VerReseniaUsuario", "Error al obtener las reseñas: ", exception)
            }
    }

    private fun checkRecords() {
        db.collection("users").document(userId).collection("ratings").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result.isEmpty) {
                        binding.emptyMessageProfileOwnerPet.visibility = View.VISIBLE
                        binding.reviewListProfileOwnerPet.visibility = View.GONE
                    } else {
                        binding.emptyMessageProfileOwnerPet.visibility = View.GONE
                        binding.reviewListProfileOwnerPet.visibility = View.VISIBLE
                    }
                }
            }
    }

    private fun initListeners() {
        binding.icBackProfilePetOwner.setOnClickListener { navigateToDetailsPet() }
    }

    private fun navigateToDetailsPet() {
        val action =
            ProfilePetOwnerDirections.actionPerfilPetOwnerToDetailsAdoptar(mascota, idMascota)
        view?.findNavController()?.navigate(action)
    }

    private fun fetchOwnerDetails(userId: String) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { userDocument ->
                val name = userDocument.getString("nombre")
                val lastName = userDocument.getString("apellido")
                val tel = userDocument.getString("telefono")
                val avatarUrl = userDocument.getString("avatarUrl")

                val fullName = "$name $lastName"

                binding.txtNameProfilePetOwner.text = fullName
                binding.txtTelProfilePetOwner.text = tel

                if (avatarUrl != null) {
                    Glide.with(requireContext())
                        .load(avatarUrl)
                        .transform(MultiTransformation(CenterCrop()))
                        .placeholder(R.drawable.avatar)
                        .into(binding.imageProfilePetOwner)
                }
            }
            .addOnFailureListener {
                Log.w("PerfilPetOwner", "Error al obtener los datos del dueño")
            }
    }

    private fun fetchReviews() {
        val query = db.collection("users").document(userId).collection("ratings")

        val options = FirestoreRecyclerOptions.Builder<Review>()
            .setQuery(query, Review::class.java)
            .build()

        val adapter = ReviewFirestoreRecyclerAdpater(options)
        binding.reviewListProfileOwnerPet.adapter = adapter
        adapter.startListening()

        query.get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    binding.reviewListProfileOwnerPet.visibility = View.GONE
                    binding.emptyMessageProfileOwnerPet.visibility = View.VISIBLE
                } else {
                    binding.reviewListProfileOwnerPet.visibility = View.VISIBLE
                    binding.emptyMessageProfileOwnerPet.visibility = View.GONE
                }
            }
            .addOnFailureListener { exception ->
                Log.w("VerReseniaUsuario", "Error al obtener las reseñas: ", exception)
            }
    }

    private fun promRating() {
        val ratingsRef = db.collection("users").document(userId).collection("ratings")

        ratingsRef.get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    var sum = 0.0
                    var count = 0

                    for (document in documents) {
                        val ratingVal = document.getLong("valoracion")?.toInt()
                        val ratingComu = document.getLong("comunicacionRating")?.toInt()
                        val ratingCond = document.getLong("condicionRating")?.toInt()
                        if (ratingVal != null && ratingComu != null && ratingCond != null) {
                            val averageRatingForReview =
                                (ratingVal + ratingComu + ratingCond) / 3 //se calcula el promedio de las tres valoraciones
                            sum += averageRatingForReview
                            count++
                        }
                    }

                    val averageRating = if (count > 0) sum / count else 0

                    val averageRatingText = String.format(Locale.getDefault(), "%.0f", averageRating)
                    binding.valorationProfilePetOwner.text = averageRatingText


                } else {
                    Log.d("VerReseniaUsuario", "Valoraciones no encontradas")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("VerReseniaUsuario", "Error al obtener las valoraciones: ", exception)
            }
    }
}