package com.example.petfriendsapp.viewmodels

import androidx.lifecycle.ViewModel
import com.example.petfriendsapp.entities.Review

class ListViewModelReview : ViewModel() {

    var reviews : MutableList<Review> = ArrayList<Review>()
}