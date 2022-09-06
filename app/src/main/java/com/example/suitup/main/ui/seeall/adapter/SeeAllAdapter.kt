package com.example.suitup.main.ui.seeall.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.suitup.common.Constants.INTERVAL_GREEN
import com.example.suitup.common.Constants.INTERVAL_RED
import com.example.suitup.common.Constants.INTERVAL_YELLOW
import com.example.suitup.main.data.model.Restaurant
import suitup.R
import suitup.databinding.SeeAllHeaderBinding
import suitup.databinding.SeeAllItemBinding
import java.util.*

private const val ITEM_HEADER = 0
private const val ITEM_RV = 1

class RestaurantItemCallback : DiffUtil.ItemCallback<Restaurant>() {

    override fun areItemsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Restaurant, newItem: Restaurant): Boolean {
        return oldItem.isFavorite == newItem.isFavorite
    }
}

class SeeAllAdapter(
    private val clickListener: SeeAllOnClickListener
) :
    ListAdapter<Restaurant, RecyclerView.ViewHolder>(RestaurantItemCallback()) {

    override fun getItemViewType(position: Int): Int {
        if (isHeaderPosition(position)) {
            return ITEM_HEADER
        }
        return ITEM_RV
    }

    private fun isHeaderPosition(position: Int): Boolean {
        if (position == 0) {
            return true
        }
        return false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == 0) {
            val binding = SeeAllHeaderBinding.inflate(inflater, parent, false)
            return HeaderViewHolder(binding)
        }
        val binding = SeeAllItemBinding.inflate(inflater, parent, false)
        return RvViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            ITEM_HEADER -> {
                val viewHolder = holder as HeaderViewHolder
                viewHolder.binding.seeAllTotal.text =
                    itemCount.toString() + " Restaurants"
                viewHolder.binding.seeAllFilter.setOnClickListener {
                    clickListener.onFilterClick()
                }
            }
            ITEM_RV -> {
                val viewHolder = holder as RvViewHolder
                viewHolder.binding.seeAllName.text = getItem(position).name
                viewHolder.binding.seeAllAddress.text = getItem(position).location?.address1
                viewHolder.binding.seeAllClosed.text =
                    "Closed: " + getItem(position).is_closed.toString()
                        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

                var categories = ""
                for (item in getItem(position).categories) {
                    categories = "$categories$item, "
                }
                categories = categories.substring(0..categories.length - 3)

                viewHolder.binding.seeAllFavorites.setOnClickListener {
                    clickListener.onFavoritesClick(getItem(position))
                }
                viewHolder.binding.seeAllCard.setOnClickListener {
                    clickListener.onClick(getItem(position).id)
                }
                if (getItem(position).isFavorite == true) {
                    viewHolder.binding.seeAllFavorites.setImageResource(R.drawable.ic_blue_favorites)
                }
                viewHolder.binding.seeAllCategories.text = categories
                viewHolder.binding.seeAllRating.text = getItem(position).rating.toString()
                when (getItem(position).rating) {
                    in INTERVAL_RED -> viewHolder.binding.seeAllRatingCard.setCardBackgroundColor(
                        ContextCompat.getColor(
                            viewHolder.binding.root.context,
                            R.color.danger
                        )
                    )
                    in INTERVAL_YELLOW -> viewHolder.binding.seeAllRatingCard.setCardBackgroundColor(
                        ContextCompat.getColor(
                            viewHolder.binding.root.context,
                            R.color.warning
                        )
                    )
                    in INTERVAL_GREEN -> viewHolder.binding.seeAllRatingCard.setCardBackgroundColor(
                        ContextCompat.getColor(
                            viewHolder.binding.root.context,
                            R.color.success
                        )
                    )
                }
            }
        }
    }

    inner class HeaderViewHolder(val binding: SeeAllHeaderBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class RvViewHolder(val binding: SeeAllItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}

class SeeAllOnClickListener(
    val filterClickListener: () -> Unit,
    val clickListener: (String) -> Unit,
    val favoritesClickListener: (Restaurant) -> Unit
) {
    fun onFilterClick() = filterClickListener()
    fun onClick(restaurantId: String) = clickListener(restaurantId)
    fun onFavoritesClick(restaurant: Restaurant) = favoritesClickListener(restaurant)
}