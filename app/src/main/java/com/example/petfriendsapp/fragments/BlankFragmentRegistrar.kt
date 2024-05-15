package com.example.petfriendsapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.petfriendsapp.R

class BlankFragmentRegistrar : Fragment() {
    private lateinit var view3: View
    private lateinit var backButton : ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        view3 = inflater.inflate(R.layout.fragment_registrar, container, false)
        backButton = findViewById(R.id.icon_back)
        return view3
    }

    override fun onStart() {
        super.onStart()
    }

}