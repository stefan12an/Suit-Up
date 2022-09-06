package com.example.suitup.main.ui.details.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.suitup.main.data.model.Restaurant
import com.example.suitup.main.data.model.yelp.YelpReview
import suitup.R
import suitup.databinding.DetailsHeaderBinding
import suitup.databinding.DetailsPhotosParentBinding
import suitup.databinding.DetailsReviewsParentBinding


private const val ITEM_HEADER = 0
private const val ITEM_PHOTOS_RV = 1
private const val ITEM_REVIEWS_RV = 2
private const val ITEM_NUMBER = 3
private const val CATEGORIES_LIMIT = 2

class DetailsAdapter(
    private val restaurant: Restaurant?,
    private val reviews: List<YelpReview>?,
    private val clickListener: DetailsOnClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return if (isHeaderPosition(position)) {
            ITEM_HEADER
        } else if (position == ITEM_PHOTOS_RV) {
            ITEM_PHOTOS_RV
        } else {
            ITEM_REVIEWS_RV
        }
    }

    private fun isHeaderPosition(position: Int): Boolean {
        if (position == 0) {
            return true
        }
        return false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM_HEADER -> {
                val binding = DetailsHeaderBinding.inflate(inflater, parent, false)
                HeaderViewHolder(binding)
            }
            ITEM_PHOTOS_RV -> {
                val binding = DetailsPhotosParentBinding.inflate(inflater, parent, false)
                PhotosViewHolder(binding)
            }
            ITEM_REVIEWS_RV -> {
                val binding = DetailsReviewsParentBinding.inflate(inflater, parent, false)
                ReviewsViewHolder(binding)
            }
            else -> {
                throw IllegalStateException()
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            ITEM_HEADER -> {
                val viewHolder = holder as HeaderViewHolder
                with(viewHolder.binding) {
                    detailsAddress.text = restaurant?.location?.address1
                    detailsCategories.text =
                        restaurant?.categories?.take(CATEGORIES_LIMIT).toString()
                    detailsHours.text =
                        restaurant?.hours?.get(0)?.getCorrectSchedule()?.toSchedule()
                            ?: "Closed today"
                    detailsPhone.text = restaurant?.phone ?: "Not available"
                    detailsCuisines.text = restaurant?.categories?.take(CATEGORIES_LIMIT).toString()
                    detailsPrice.text = restaurant?.price ?: "Not available"
                    if (restaurant?.isFavorite == true) {
                        detailsFavorites.setImageResource(R.drawable.ic_blue_favorites)
                    }
                    detailsFavorites.setOnClickListener {
                        clickListener.onFavoritesClick()
                    }
                    detailsPhone.setOnClickListener {
                        val intent = Intent(
                            Intent.ACTION_DIAL,
                            Uri.fromParts("tel", restaurant?.phone, null)
                        )
                        startActivity(root.context, intent, null)
                    }
                    detailsShare.setOnClickListener {
                        val i = Intent(Intent.ACTION_SEND)
                        i.type = "text/plain"
                        i.putExtra(Intent.EXTRA_SUBJECT, "Share this restaurant with everyone!")
                        i.putExtra(Intent.EXTRA_TEXT, restaurant?.url)
                        startActivity(root.context, Intent.createChooser(i, "Share via"), null)
                    }
                }
            }
            ITEM_PHOTOS_RV -> {
                val viewHolder = holder as PhotosViewHolder
                viewHolder.setItems(restaurant?.photos)
            }
            ITEM_REVIEWS_RV -> {
                val viewHolder = holder as ReviewsViewHolder
                viewHolder.setItems(reviews)
            }
        }
    }

    override fun getItemCount(): Int {
        return ITEM_NUMBER
    }


    inner class HeaderViewHolder(val binding: DetailsHeaderBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class PhotosViewHolder(val binding: DetailsPhotosParentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val adapter = PhotosAdapter(PhotosOnClickListener { clickListener.onClick(it) })

        init {
            binding.detailsPhotosRv.layoutManager = LinearLayoutManager(
                binding.root.context,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            binding.detailsPhotosRv.adapter = adapter
        }

        fun setItems(restaurantPhotos: List<String>?) {
            adapter.setItems(restaurantPhotos)
        }
    }

    inner class ReviewsViewHolder(val binding: DetailsReviewsParentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        private val adapter = ReviewsAdapter()

        init {
            binding.detailsReviewsRv.layoutManager = LinearLayoutManager(
                binding.root.context,
                LinearLayoutManager.VERTICAL,
                false
            )
            binding.detailsReviewsRv.adapter = adapter
        }

        fun setItems(restaurantReviews: List<YelpReview>?) {
            adapter.setItems(restaurantReviews)
        }
    }
}

class DetailsOnClickListener(
    val favoritesClickListener: () -> Unit,
    val clickListener: (String) -> Unit
) {
    fun onFavoritesClick() = favoritesClickListener()
    fun onClick(restaurantPhoto: String) = clickListener(restaurantPhoto)
}