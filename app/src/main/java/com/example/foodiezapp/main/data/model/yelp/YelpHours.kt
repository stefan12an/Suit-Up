package com.example.foodiezapp.main.data.model.yelp

import java.util.*

data class YelpHours(
    val open: List<YelpSchedule>,
    val hours_type: String,
    val is_open_now: Boolean
) {
    fun getCorrectSchedule(): YelpSchedule? {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_WEEK)
        return open.firstOrNull { it.day == day }
    }
}