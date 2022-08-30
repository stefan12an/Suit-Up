package com.example.foodiezapp.main.ui.details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodiezapp.databinding.DetailsReviewsItemBinding
import com.example.foodiezapp.main.data.model.yelp.YelpReview
import com.squareup.picasso.Picasso

class ReviewsAdapter : RecyclerView.Adapter<ReviewsAdapter.ViewHolder>() {
    private var reviews: List<YelpReview>? = emptyList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DetailsReviewsItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder.binding){
            reviewName.text = reviews?.get(position)?.user?.name
            reviewDate.text = reviews?.get(position)?.time_created
            reviewRating.text = reviews?.get(position)?.rating?.toString()
            reviewRatingBar.rating = reviews?.get(position)?.rating!!
            reviewText.text = reviews?.get(position)?.text
            Picasso.get().load(reviews?.get(position)?.user?.image_url).into(reviewProfile)
        }
    }

    override fun getItemCount(): Int {
        return reviews?.size ?: 0
    }

    fun setItems(reviews: List<YelpReview>?) {
        this.reviews = reviews
    }

    inner class ViewHolder(val binding: DetailsReviewsItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}