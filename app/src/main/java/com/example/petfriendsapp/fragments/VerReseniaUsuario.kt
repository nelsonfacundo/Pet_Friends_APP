package com.example.petfriendsapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.petfriendsapp.R
import com.example.petfriendsapp.adapter.ReviewFirestoreRecyclerAdpater
import com.example.petfriendsapp.entities.Review
import com.example.petfriendsapp.viewmodels.ListViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class VerReseniaUsuario : Fragment() {
    private lateinit var viewResenia: View
    private lateinit var buttonBack: ImageView
    private lateinit var txtValoracion : TextView
    private lateinit var name : TextView
    private lateinit var picture : ImageView
    private lateinit var recResenia : RecyclerView
    private lateinit var viewModel : ListViewModel

    private  var resenias: MutableList<Review> = ArrayList()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    companion object {
        val BUTTON_BACK = R.id.ic_back_resenia
    }
    private fun fillRecycler() {

        val rootRef = FirebaseFirestore.getInstance()

        val query = rootRef.collection("ratings")

        val options = FirestoreRecyclerOptions.Builder<Review>()
            .setQuery(query, Review::class.java)
            .build()


    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewResenia=inflater.inflate(R.layout.fragment_ver_resenia_usuario, container, false)
        recResenia=viewResenia.findViewById(R.id.review_list)
        initViews()

        fetchUserProfile()
        fetchUserRatings()


        return viewResenia
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(this)[ListViewModel::class.java]

        //
    }

    private fun checkRecords(userId: String) {
        val userRef = db.collection("users").document(userId).collection("ratings")

        db.collection("ratings").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (review in viewModel.mascotas) {
                        db.collection("mascotas").document().get()

                    }
                }

            }
    }

    override fun onStart() {
        super.onStart()
        initListeners()

    }
    private fun initViews() {
        buttonBack = viewResenia.findViewById(BUTTON_BACK)
        txtValoracion = viewResenia.findViewById(R.id.valoration)
        name = viewResenia.findViewById(R.id.name_review)
        picture = viewResenia.findViewById(R.id.picture_review)

    }

    private fun initListeners() {
        buttonBack.setOnClickListener { navigateToHome() }

    }
    private fun navigateToHome() {
        val action1 = VerReseniaUsuarioDirections.actionReviewFragmentToInicio()
        viewResenia.findNavController().navigate(action1)
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
                        name.text = nombreCompleto

                        val urlImagenPerfil = document.getString("avatarUrl")

                        Glide.with(requireContext())
                            .load(urlImagenPerfil)
                            .transform(CenterCrop(), RoundedCorners(250))
                            .placeholder(R.drawable.avatar)
                            .error(R.drawable.avatar)
                            .into(picture)

                    } else {
                        Log.d("Perfil", "No existe el documento")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Perfil", "La obtención de datos falló con ", exception)
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

                        // Establece el promedio en el RatingBar
                       // binding.ratingBarPerfil.rating = averageRating.toFloat()

                        // Convierte el promedio a cadena de texto antes de mostrarlo
                        val averageRatingText = String.format(Locale.getDefault(), "%.0f", averageRating)
                        val ratingText = getString(R.string.average_rating_text, averageRatingText )
                        txtValoracion.text = ratingText


                    } else {
                       // Log.d("PerfilFragment", "Valoraciones no encontradas")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("PerfilFragment", "Error al obtener las valoraciones: ROMPIO TODO ACA!!! ", exception)
                }
            }
    }

}