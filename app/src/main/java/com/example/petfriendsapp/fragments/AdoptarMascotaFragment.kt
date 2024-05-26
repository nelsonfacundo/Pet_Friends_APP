package com.example.petfriendsapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.example.petfriendsapp.R


class AdoptarMascotaFragment : Fragment() {

    private lateinit var backButton: ImageView
    private lateinit var buttonAdoptarMascota: Button

    companion object {
        val BACK_BUTTON_ID = R.id.ic_back_adoptar_mascota
        val BUTTON_ADOPTAR_MASCOTA = R.id.button_adoptar_mascota
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_adoptar_mascota, container, false)
    }


}