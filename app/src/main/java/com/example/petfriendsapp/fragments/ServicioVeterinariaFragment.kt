package com.example.petfriendsapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.findNavController
import com.example.petfriendsapp.R


class ServicioVeterinariaFragment : Fragment() {
    private lateinit var viewVeterinaria: View
    private lateinit var buttonBack: ImageView


    companion object {
        val BUTTON_BACK = R.id.id_back_fragment_veterinaria
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewVeterinaria = inflater.inflate(R.layout.fragment_servicio_veterinaria, container, false)

        initViews()


        return  viewVeterinaria
    }

    override fun onStart() {
        super.onStart()
        initListeners()

    }
    private fun initViews() {
        buttonBack = viewVeterinaria.findViewById(BUTTON_BACK)
    }

    private fun initListeners() {
        buttonBack.setOnClickListener { navigateToHome() }
    }

    private fun navigateToHome() {
        val action1 = ServicioVeterinariaFragmentDirections.actionServicioToInicio()
        viewVeterinaria.findNavController().navigate(action1)
    }


}