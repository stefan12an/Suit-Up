package com.example.suitup.intro

import androidx.lifecycle.ViewModel
import suitup.R

class IntroViewModel: ViewModel() {
    private val modelList = listOf(
        SlideModel(
            R.drawable.tablet,
            "Quick search",
            "Search for your favourite clothing and brands right within the app!"
        ),
        SlideModel(
            R.drawable.shopping_bag,
            "Variety of clothes",
            "All your favourite clothing shops around you at your finger tips!"
        ),
        SlideModel(
            R.drawable.map,
            "Look where to shop",
            "Set your location to start exploring stores around you!"
        ),
    )

    fun getList(): List<SlideModel>{
        return modelList
    }
}