package com.example.petfriendsapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.petfriendsapp.R
import com.example.petfriendsapp.entities.Mascota
import com.example.petfriendsapp.viewmodels.ListViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore


class Favorito : Fragment() {

    private lateinit var viewFavorito: View
    private lateinit var buttonPerfilMascota: Button
    lateinit var recMascotas: RecyclerView
    private lateinit var viewModel: ListViewModel
    val db = Firebase.firestore

    private lateinit var mascotaClickListener: (Mascota, String) -> Unit
    companion object {
        val BUTTON_BACK = R.id.ic_back_favorite
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewFavorito=inflater.inflate(R.layout.fragment_favorito, container, false)


       initViews()
        return viewFavorito



    }
    override fun onStart() {
        super.onStart()
        initListeners()

    }
    private fun initViews() {
        buttonPerfilMascota = viewFavorito.findViewById(R.id.card_mascota)
    }


    private fun initListeners() {
        BUTTON_BACK.setOnClickListener { navigateToHome() }

    }
    private fun navigateToHome() {
      //  val action1 = ReviewFragmentDirections.actionReviewFragmentToInicio()
      //  viewFavorito.findNavController().navigate(action1)
    }


}
