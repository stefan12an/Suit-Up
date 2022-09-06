package com.example.suitup.intro

import androidx.lifecycle.ViewModel
import suitup.R

class IntroViewModel: ViewModel() {
    private val modelList = listOf(
        SlideModel(
            R.drawable.tablet,
            "Quick search",
            "Set your loctaion to start exploring restaurants around you"
        ),
        SlideModel(
            R.drawable.shopping_bag,
            "Variety of food",
            "Set your loctaion to start exploring restaurants around you"
        ),
        SlideModel(
            R.drawable.map,
            "Search for a place",
            "Set your loctaion to start exploring restaurants around you"
        ),
    )

    fun getList(): List<SlideModel>{
        return modelList
    }
}