package com.example.foodiezapp.main.data.model.yelp

data class YelpSchedule(
    val is_overnight: Boolean?,
    val start: String?,
    val end: String?,
    val day: Int?
){
    fun toSchedule():String{
        val formatStart = start?.substring(0..1) + ":" + start?.substring(2..3)
        val formatEnd = end?.substring(0..1) + ":" + end?.substring(2..3)
        return "$formatStart to $formatEnd"
    }
}