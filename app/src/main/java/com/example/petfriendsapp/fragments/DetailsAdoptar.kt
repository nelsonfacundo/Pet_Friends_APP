package com.example.petfriendsapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.petfriendsapp.R
import com.example.petfriendsapp.entities.Mascota

class DetailsAdoptar : Fragment() {

    private lateinit var buttonBackDetails: ImageView
   private val args: DetailsAdoptarArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_details_adoptar, container, false)

        buttonBackDetails = view.findViewById(R.id.ic_back_fragment_detail)

        buttonBackDetails.setOnClickListener {
            findNavController().navigateUp()
        }

        // Bind the details to the views
        val txtRaza: TextView = view.findViewById(R.id.razaMascota)
        val txtEdad: TextView = view.findViewById(R.id.edadMascota)
        val txtNombre: TextView = view.findViewById(R.id.nombreMascota)
        val txtSexo: TextView = view.findViewById(R.id.sexoMascota)
        val txtUbicacion: TextView = view.findViewById(R.id.ubicacionMascotaDetalle)
        val imagen : ImageView = view.findViewById(R.id.imagenPerro)

        val mascota: Mascota = args.Mascota


        txtRaza.text = mascota.raza
        txtEdad.text = mascota.edad.toString()
        txtNombre.text = mascota.nombre
        txtSexo.text = mascota.sexo
        txtUbicacion.text = mascota.ubicacion

        return view
    }

}
