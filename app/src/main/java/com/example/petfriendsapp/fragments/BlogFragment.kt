package com.example.petfriendsapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petfriendsapp.R
import com.example.petfriendsapp.adapter.ArticuloAdapter
import com.example.petfriendsapp.entities.Articulo
import com.example.petfriendsapp.viewmodels.ArticuloViewModel


class BlogFragment : Fragment() {
    private lateinit var viewBlog: View
    private lateinit var buttonBack: ImageView
    private lateinit var articlesRV: RecyclerView
    private lateinit var articleViewModel: ArticuloViewModel
    private var articlesList: MutableList<Articulo> = ArrayList()

    companion object {
        val BUTTON_BACK = R.id.ic_back_blog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBlog = inflater.inflate(R.layout.fragment_blog, container, false)

        initViews()

        articleViewModel = ViewModelProvider(this).get(ArticuloViewModel::class.java)
        articlesRV = viewBlog.findViewById(R.id.articles_rv)

        return viewBlog
    }
    override fun onStart() {
        super.onStart()
        initListeners()
        loadArticlesRecycler()
    }

    private fun loadArticlesRecycler(){
        articlesRV.layoutManager = LinearLayoutManager(context)
        articlesRV.setHasFixedSize(true)
        listArticles()
        val articleAdapter = ArticuloAdapter(articlesList)
        articlesRV.adapter = articleAdapter


      /*  articleViewModel.articles.observe(viewLifecycleOwner, Observer { articles ->
            articlesRV.adapter = ArticuloAdapter(articles)
        })*/
    }

    private fun listArticles() {
        articlesList.add(Articulo(title = context?.getString(R.string.txt_blog_title_1), description = context?.getString(R.string.txt_blog_desc_1), image = R.drawable.perro_ejercitando))
        articlesList.add(Articulo(title = context?.getString(R.string.txt_blog_title_2), description = context?.getString(R.string.txt_blog_desc_2), image = R.drawable.alimentos_prohibidos_para_perros))
        articlesList.add(Articulo(title = context?.getString(R.string.txt_blog_title_3), description = context?.getString(R.string.txt_blog_desc_3), image = R.drawable.adoptame))
    }

    private fun initViews() {
        buttonBack = viewBlog.findViewById(BUTTON_BACK)
    }

    private fun initListeners() {
        buttonBack.setOnClickListener { navigateToHome() }

    }
    private fun navigateToHome() {
        val action1 = BlogFragmentDirections.actionBlogFragmentToInicio()
        viewBlog.findNavController().navigate(action1)
    }
}