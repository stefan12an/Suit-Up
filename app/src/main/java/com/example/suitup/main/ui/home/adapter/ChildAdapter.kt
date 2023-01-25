package com.example.suitup.main.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.suitup.main.data.model.Store
import com.squareup.picasso.Picasso
import suitup.databinding.DealsItemBinding
import suitup.databinding.HomeSlideBinding
import suitup.databinding.HotNewItemBinding
import kotlin.properties.Delegates

private const val ITEM_PAGER = 0
private const val ITEM_HOT_NEW = 1
private const val ITEM_DEALS = 2

open class ChildAdapter(private val clickListener: HomeChildOnClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var storeList: List<Store> = ArrayList()
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
                viewHolder.binding.root.setOnClickListener { clickListener.onClick(storeList[position].id) }
                viewHolder.binding.pagerName.text = storeList[position].name
                viewHolder.binding.pagerAddress.text = storeList[position].location?.address1
                Picasso.get().load(storeList[position].image_url)
                    .into(viewHolder.binding.pagerImage)
            }
            ITEM_HOT_NEW -> {
                val viewHolder = holder as ViewHolderTwo
                viewHolder.binding.root.setOnClickListener { clickListener.onClick(storeList[position].id) }
                viewHolder.binding.hotNewName.text = storeList[position].name
                viewHolder.binding.hotNewAdress.text = storeList[position].location?.address1
                var categories = ""
                for (item in storeList[position].categories) {
                    categories = "$categories$item, "
                }
                categories = categories.substring(0..categories.length - 3)
                viewHolder.binding.hotNewCategories.text = categories

                Picasso.get().load(storeList[position].image_url)
                    .into(viewHolder.binding.hotNewImage)
            }
            ITEM_DEALS -> {
                val viewHolder = holder as ViewHolderThree
                viewHolder.binding.root.setOnClickListener { clickListener.onClick(storeList[position].id) }
                viewHolder.binding.dealsName.text = storeList[position].name
                viewHolder.binding.dealsAdress.text = storeList[position].location?.address1
                Picasso.get().load(storeList[position].image_url)
                    .into(viewHolder.binding.dealsImage)
            }
        }
    }

    override fun getItemCount(): Int {
        return storeList.size
    }

    fun setItems(storeData: List<Store>, position: Int) {
        storeList = storeData
        parentPosition = position
    }

    inner class ViewHolderOne(val binding: HomeSlideBinding) : RecyclerView.ViewHolder(binding.root)

    inner class ViewHolderTwo(val binding: HotNewItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class ViewHolderThree(val binding: DealsItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}

class HomeChildOnClickListener(val clickListener: (String) -> Unit) {
    fun onClick(storeId: String) = clickListener(storeId)
}