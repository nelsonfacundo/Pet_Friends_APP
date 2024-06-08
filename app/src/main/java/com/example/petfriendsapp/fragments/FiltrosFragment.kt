package com.example.petfriendsapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.petfriendsapp.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FiltrosFragment : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_filtros, container, false)

        val aplicarFiltros: Button = view.findViewById(R.id.btn_aplicar_filtros)

        aplicarFiltros.setOnClickListener{

        }

        return view
    }

    companion object {
        const val TAG = "FiltrosFragment"
    }

}