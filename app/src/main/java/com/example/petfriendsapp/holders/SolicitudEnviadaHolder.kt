package com.example.petfriendsapp.holders

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.petfriendsapp.R

class SolicitudEnviadaHolder(v: View) : RecyclerView.ViewHolder(v) {

    private var view: View

    init { this.view = v }

    private val nombreMascotaTextView: TextView = view.findViewById(R.id.txt_pet_name)
    private val estadoTextView: TextView = view.findViewById(R.id.txt_adopt_status_static)
    private val mascotaImageView: ImageView = view.findViewById(R.id.pet_img_request)
    private val reviewButton: Button = view.findViewById(R.id.btn_leave_review)
    private val statusStaticText: TextView = view.findViewById(R.id.txt_adopt_status_static)


    fun setNombreMascota(nombreMascota: String) {
        nombreMascotaTextView.text = nombreMascota
    }

    fun setMascotaImage(urlMascotaImage: String){
        Glide.with(itemView.context).load(urlMascotaImage).into(mascotaImageView)
    }

    fun setEstado(estado: String){
        estadoTextView.text = estado
    }

    fun clear() {
        nombreMascotaTextView.text = ""
        estadoTextView.text = ""
        mascotaImageView.setImageDrawable(null)
    }

    fun switchTextToButton(){
        estadoTextView.visibility = View.GONE
        statusStaticText.visibility = View.GONE
        reviewButton.visibility = View.VISIBLE
    }
}

