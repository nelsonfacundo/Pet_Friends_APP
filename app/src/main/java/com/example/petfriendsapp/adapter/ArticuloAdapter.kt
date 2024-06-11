package com.example.petfriendsapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.petfriendsapp.R
import com.example.petfriendsapp.entities.Articulo
import com.example.petfriendsapp.holders.ArticuloHolder

class ArticuloAdapter(
    private val articles: List<Articulo>
): RecyclerView.Adapter<ArticuloHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticuloHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.articulo_card, parent, false)
        return ArticuloHolder(view)
    }

    override fun getItemCount() = articles.size

    override fun onBindViewHolder(holder: ArticuloHolder, position: Int) {
        val article = articles[position]

        holder.setTitle(article.title)
        holder.setDescription(article.description)
        holder.setImage(article.image)
    }

}