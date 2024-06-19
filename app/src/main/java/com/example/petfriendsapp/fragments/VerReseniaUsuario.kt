package com.example.petfriendsapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.petfriendsapp.R
import com.example.petfriendsapp.adapter.ReviewFirestoreRecyclerAdpater
import com.example.petfriendsapp.databinding.FragmentVerReseniaUsuarioBinding
import com.example.petfriendsapp.entities.Review
import com.example.petfriendsapp.viewmodels.ListViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class VerReseniaUsuario : Fragment() {


    private lateinit var bindingFragment: FragmentVerReseniaUsuarioBinding
    private val binding get() = bindingFragment

    private lateinit var viewModel : ListViewModel
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bindingFragment = FragmentVerReseniaUsuarioBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        fetchUserProfile()
        fetchUserRatings()
    }
    private fun setupRecyclerView() {
        binding.reviewList.setHasFixedSize(true)
        binding.reviewList.layoutManager = LinearLayoutManager(context)
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
    }

    private fun fillRecycler() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val query = db.collection("users").document(userId).collection("ratings")

            val options = FirestoreRecyclerOptions.Builder<Review>()
                .setQuery(query, Review::class.java)
                .build()

            val adapter = ReviewFirestoreRecyclerAdpater(options)
            adapter.startListening()
            binding.reviewList.adapter = adapter

            query.get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        binding.reviewList.visibility = View.GONE
                        binding.emptyMessage.visibility = View.VISIBLE
                    } else {
                        binding.reviewList.visibility = View.VISIBLE
                        binding.emptyMessage.visibility = View.GONE
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("VerReseniaUsuario", "Error al obtener las reseñas: ", exception)
                }
        }
    }

    private fun checkRecords() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId).collection("ratings").get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        if (task.result.isEmpty) {
                            binding.emptyMessage.visibility = View.VISIBLE
                            binding.reviewList.visibility = View.GONE
                        } else {
                            binding.emptyMessage.visibility = View.GONE
                            binding.reviewList.visibility = View.VISIBLE
                        }
                    }
                }
        }
    }

    private fun initListeners() {
        binding.icBackResenia.setOnClickListener { navigateToHome() }
    }

    private fun navigateToHome() {
        val action1 = VerReseniaUsuarioDirections.actionReviewFragmentToInicio()
        view?.findNavController()?.navigate(action1)
    }


    private fun fetchUserProfile() {
        val user = auth.currentUser
        val uid = user?.uid

        if (uid != null) {
            val userDocRef = db.collection("users").document(uid)

            userDocRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val nombre = document.getString("nombre")
                        val apellido = document.getString("apellido")
                        val nombreCompleto = "$nombre $apellido"
                        binding.nameReview.text = nombreCompleto

                        val urlImagenPerfil = document.getString("avatarUrl")

                        Glide.with(requireContext())
                            .load(urlImagenPerfil)
                            .transform(CenterCrop(), RoundedCorners(250))
                            .placeholder(R.drawable.avatar)
                            .error(R.drawable.avatar)
                            .into(binding.pictureReview)

                    } else {
                        Log.d("Perfil", "No existe el documento")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("VerReseniaUsuario", "La obtención de datos falló con ", exception)
                }
        }
    }

    private fun fetchUserRatings() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
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
                                val averageRatingForReview = (ratingVal + ratingComu + ratingCond) / 3 //se calcula el promedio de las tres valoraciones
                                sum += averageRatingForReview
                                count++
                            }
                        }

                        val averageRating = if (count > 0) sum / count else 0

                        val averageRatingText = String.format(Locale.getDefault(), "%.0f", averageRating)
                        binding.valoration.text = averageRatingText

                    } else {
                         Log.d("VerReseniaUsuario", "Valoraciones no encontradas")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("VerReseniaUsuario", "Error al obtener las valoraciones: ", exception)
                }
        }
    }

}