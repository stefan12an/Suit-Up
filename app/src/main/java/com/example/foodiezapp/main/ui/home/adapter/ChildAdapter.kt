package com.example.foodiezapp.main.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodiezapp.databinding.DealsItemBinding
import com.example.foodiezapp.databinding.HomeSlideBinding
import com.example.foodiezapp.databinding.HotNewItemBinding
import com.example.foodiezapp.main.data.model.Restaurant
import com.squareup.picasso.Picasso
import kotlin.properties.Delegates

private const val ITEM_PAGER = 0
private const val ITEM_HOT_NEW = 1
private const val ITEM_DEALS = 2

open class ChildAdapter(private val clickListener: HomeChildOnClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var restaurantList: List<Restaurant> = ArrayList()
    private var parentPosition by Delegates.notNull<Int>()
    override fun getItemViewType(position: Int): Int {
        return parentPosition
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM_PAGER -> {
                val binding = HomeSlideBinding.inflate(inflater, parent, false)
                ViewHolderOne(binding)
            }
            ITEM_HOT_NEW -> {
                val binding = HotNewItemBinding.inflate(inflater, parent, false)
                ViewHolderTwo(binding)
            }
            ITEM_DEALS -> {
                val binding = DealsItemBinding.inflate(inflater, parent, false)
                ViewHolderThree(binding)
            }
            else -> {
                throw IllegalStateException()
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            ITEM_PAGER -> {
                val viewHolder = holder as ViewHolderOne
                viewHolder.binding.root.setOnClickListener { clickListener.onClick(restaurantList[position].id) }
                viewHolder.binding.pagerName.text = restaurantList[position].name
                viewHolder.binding.pagerAddress.text = restaurantList[position].location?.address1
                Picasso.get().load(restaurantList[position].image_url)
                    .into(viewHolder.binding.pagerImage)
            }
            ITEM_HOT_NEW -> {
                val viewHolder = holder as ViewHolderTwo
                viewHolder.binding.root.setOnClickListener { clickListener.onClick(restaurantList[position].id) }
                viewHolder.binding.hotNewName.text = restaurantList[position].name
                viewHolder.binding.hotNewAdress.text = restaurantList[position].location?.address1
                var categories = ""
                for (item in restaurantList[position].categories) {
                    categories = "$categories$item, "
                }
                categories = categories.substring(0..categories.length - 3)
                viewHolder.binding.hotNewCategories.text = categories

                Picasso.get().load(restaurantList[position].image_url)
                    .into(viewHolder.binding.hotNewImage)
            }
            ITEM_DEALS -> {
                val viewHolder = holder as ViewHolderThree
                viewHolder.binding.root.setOnClickListener { clickListener.onClick(restaurantList[position].id) }
                viewHolder.binding.dealsName.text = restaurantList[position].name
                viewHolder.binding.dealsAdress.text = restaurantList[position].location?.address1
                Picasso.get().load(restaurantList[position].image_url)
                    .into(viewHolder.binding.dealsImage)
            }
        }
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    fun setItems(restaurantData: List<Restaurant>, position: Int) {
        restaurantList = restaurantData
        parentPosition = position
    }

    inner class ViewHolderOne(val binding: HomeSlideBinding) : RecyclerView.ViewHolder(binding.root)

    inner class ViewHolderTwo(val binding: HotNewItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class ViewHolderThree(val binding: DealsItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}

class HomeChildOnClickListener(val clickListener: (String) -> Unit) {
    fun onClick(restaurantId: String) = clickListener(restaurantId)
}