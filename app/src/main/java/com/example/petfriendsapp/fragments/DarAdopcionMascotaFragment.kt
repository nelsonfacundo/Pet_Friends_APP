package com.example.petfriendsapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.example.petfriendsapp.R


class DarAdopcionMascotaFragment : Fragment() {

    private lateinit var backButton: ImageView
    private lateinit var buttonDarAdopcionMascota: Button
    private lateinit var viewDarAdopcionMascota: View

    companion object {
        val BACK_BUTTON_ID = R.id.ic_back_dar_adopcion_mascota
        val BUTTON_DAR_ADOPCION_MASCOTA = R.id.button_dar_adopcion_mascota
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewDarAdopcionMascota = inflater.inflate(R.layout.fragment_dar_adopcion_mascota, container, false)

        initViews()
        return viewDarAdopcionMascota
    }
private fun initViews(){
    backButton = viewDarAdopcionMascota.findViewById(BACK_BUTTON_ID)
    buttonDarAdopcionMascota= viewDarAdopcionMascota.findViewById(BUTTON_DAR_ADOPCION_MASCOTA)
}

}