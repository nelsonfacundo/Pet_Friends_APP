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
        val txtValoracion: TextView = view.findViewById(R.id.val_exp_gral)
        txtValoracion.text = valoracion.toString()
    }
    fun setComunicacionRating(valoracion: Int){
        val txtValoracion: TextView = view.findViewById(R.id.val_com_duenio)
        txtValoracion.text = valoracion.toString()
    }
    fun setCondicionRating(valoracion: Int){
        val txtValoracion: TextView = view.findViewById(R.id.val_cond_pets)
        txtValoracion.text = valoracion.toString()
    }
    fun setOpinion(resenia: String) {
        val txtResenia: TextView = view.findViewById(R.id.resenia_text)
        txtResenia.text = resenia
    }


    fun getCardResenia(): CardView {
        return view.findViewById(R.id.card_resenia)
    }



}