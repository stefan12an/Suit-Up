package com.example.suitup.main.ui.favourite.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.suitup.common.Constants
import com.example.suitup.main.data.model.Restaurant
import suitup.R
import suitup.databinding.FavoritesItemBinding
import java.util.*

class RestaurantItemCallback : DiffUtil.ItemCallback<Restaurant>() {

    override fun areItemsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
        return oldItem.isFavorite == newItem.isFavorite
    }
}

class FavoritesAdapter(
    private val clickListener: FavoritesOnClickListener
) :
    ListAdapter<Restaurant, FavoritesAdapter.ViewHolder>(RestaurantItemCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = FavoritesItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.root.setOnClickListener { clickListener.onClick(getItem(position).id) }
        holder.binding.favoritesName.text = getItem(position).name
        holder.binding.favoritesAddress.text = getItem(position).location?.address1
        holder.binding.favoritesClosed.text =
            "Closed: " + getItem(position).is_closed.toString()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

        var categories = ""
        for (item in getItem(position).categories) {
            categories = "$categories$item, "
        }
        categories =
            categories.substring(0..categories.length - 3)
                .replace("[", "")
                .replace("]", "")

        holder.binding.favoritesFavorites.setOnClickListener {
            clickListener.onFavoritesClick(getItem(position))
        }
        if (getItem(position).isFavorite == true) {
            holder.binding.favoritesFavorites.setImageResource(R.drawable.ic_blue_favorites)
        }
        holder.binding.favoritesCategories.text = categories
        holder.binding.favoritesRating.text = getItem(position).rating.toString()
        when (getItem(position).rating) {
            in Constants.INTERVAL_RED -> holder.binding.favoritesRatingCard.setCardBackgroundColor(
                ContextCompat.getColor(
                    holder.binding.root.context,
                    R.color.danger
                )
            )
            in Constants.INTERVAL_YELLOW -> holder.binding.favoritesRatingCard.setCardBackgroundColor(
                ContextCompat.getColor(
                    holder.binding.root.context,
                    R.color.warning
                )
            )
            in Constants.INTERVAL_GREEN -> holder.binding.favoritesRatingCard.setCardBackgroundColor(
                ContextCompat.getColor(
                    holder.binding.root.context,
                    R.color.success
                )
            )
        }
    }

    inner class ViewHolder(val binding: FavoritesItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}

class FavoritesOnClickListener(
    val favoritesClickListener: (Restaurant) -> Unit,
    val clickListener: (String) -> Unit
) {
    fun onFavoritesClick(restaurant: Restaurant) = favoritesClickListener(restaurant)
    fun onClick(restaurantId: String) = clickListener(restaurantId)
}