package com.example.petfriendsapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.findNavController
import com.example.petfriendsapp.R

class ReviewFragment : Fragment() {
    private lateinit var viewResenia: View
    private lateinit var buttonBack: ImageView

    companion object {
        val BUTTON_BACK = R.id.ic_back_resenia
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewResenia=inflater.inflate(R.layout.fragment_review, container, false)

        initViews()

        return viewResenia
    }
    override fun onStart() {
        super.onStart()
        initListeners()

    }
    private fun initViews() {
        buttonBack = viewResenia.findViewById(BUTTON_BACK)
    }

    private fun initListeners() {
        buttonBack.setOnClickListener { navigateToHome() }

    }
    private fun navigateToHome() {
        val action1 = ReviewFragmentDirections.actionReviewFragmentToInicio()
        viewResenia.findNavController().navigate(action1)
    }

}