package com.example.petfriendsapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.findNavController
import com.example.petfriendsapp.R


class Favorito : Fragment() {

    private lateinit var viewFavorito: View
    private lateinit var buttonBack: ImageView


    companion object {
        val BUTTON_BACK = R.id.ic_back_favorito
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
        buttonBack = viewFavorito.findViewById(BUTTON_BACK)
    }


    private fun initListeners() {
        buttonBack.setOnClickListener { navigateToHome() }

    }
    private fun navigateToHome() {
        val action1 = FavoritoDirections.actionFavoritoToInicio()
        viewFavorito.findNavController().navigate(action1)
    }


}
