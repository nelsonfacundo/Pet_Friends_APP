package com.example.petfriendsapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.findNavController
import com.example.petfriendsapp.R


class BlogFragment : Fragment() {
    private lateinit var viewBlog: View
    private lateinit var buttonBack: ImageView

    companion object {
        val BUTTON_BACK = R.id.ic_back_blog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBlog=inflater.inflate(R.layout.fragment_blog, container, false)
        initViews()
        return viewBlog
    }
    override fun onStart() {
        super.onStart()
        initListeners()

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