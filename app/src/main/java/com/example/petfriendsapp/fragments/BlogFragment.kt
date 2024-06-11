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
import com.example.petfriendsapp.viewmodels.ArticuloViewModel


class BlogFragment : Fragment() {
    private lateinit var viewBlog: View
    private lateinit var buttonBack: ImageView
    private lateinit var articlesRV: RecyclerView
    private lateinit var articleViewModel: ArticuloViewModel

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
        loadArticles()

        return viewBlog
    }
    override fun onStart() {
        super.onStart()

        initListeners()
    }

    private fun loadArticles(){
        articlesRV.layoutManager = LinearLayoutManager(context)
        articlesRV.setHasFixedSize(true)
        articleViewModel.articles.observe(viewLifecycleOwner, Observer{articles ->
            articlesRV.adapter = ArticuloAdapter(articles)
        })
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