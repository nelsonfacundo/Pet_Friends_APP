package com.example.petfriendsapp.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.petfriendsapp.R

class ArticuloHolder(v: View): RecyclerView.ViewHolder(v) {
    private var view: View

    init {
        this.view = v
    }

    fun setTitle(title: String){
        val txtTitle: TextView = view.findViewById(R.id.txt_title_article)
        txtTitle.text = title
    }

    fun setDescription(description: String){
        val txtDescription: TextView = view.findViewById(R.id.txt_description_article)
        txtDescription.text = description
    }

    fun setImage(image: Int){
      val imageView: ImageView = view.findViewById(R.id.img_article)
      imageView.setImageResource(image)
    }
}