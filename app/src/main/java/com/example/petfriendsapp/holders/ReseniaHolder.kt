package com.example.petfriendsapp.holders

import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.petfriendsapp.R

class ReseniaHolder (v: View): RecyclerView.ViewHolder(v){

    private var view: View

    init {
        this.view = v
    }

    fun setValoracion(valoracion: Int){
        val txtValoracion: TextView = view.findViewById(R.id.valoration)
        txtValoracion.text = valoracion.toString()
    }
    fun setResenia(resenia: String) {
        val txtResenia: TextView = view.findViewById(R.id.resenia_text)
        txtResenia.text = resenia
    }

    fun getCardResenia(): CardView {
        return view.findViewById(R.id.card_resenia)
    }



}