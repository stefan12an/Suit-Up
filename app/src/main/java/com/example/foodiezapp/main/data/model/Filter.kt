package com.example.foodiezapp.main.data.model

data class Filter(
    val type: Int,
    val id: String,
    val name: String,
    var checked: Boolean
)