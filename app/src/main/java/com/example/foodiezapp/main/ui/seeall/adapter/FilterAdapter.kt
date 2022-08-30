package com.example.foodiezapp.main.ui.seeall.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodiezapp.databinding.BottomSheetCheckItemBinding
import com.example.foodiezapp.databinding.BottomSheetRadioItemBinding
import com.example.foodiezapp.main.data.model.Filter

private const val FILTER_PRICE = 0
private const val FILTER_ORDER = 1

class FilterAdapter(
    private val filterList: List<Filter>,
    private val itemClickListener: ItemClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var priceFilters: MutableList<Filter> = ArrayList()
    private var order: Filter? = null


    override fun getItemViewType(position: Int): Int {
        if (filterList.first().type == FILTER_PRICE) {
            return FILTER_PRICE
        }
        return FILTER_ORDER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == 0) {
            val binding = BottomSheetCheckItemBinding.inflate(inflater, parent, false)
            return PriceViewHolder(binding)
        }
        val binding = BottomSheetRadioItemBinding.inflate(inflater, parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            FILTER_PRICE -> {
                val viewHolder = holder as PriceViewHolder
                with(viewHolder) {
                    binding.filterName.text = filterList[position].name
                    binding.filterChecked.setOnCheckedChangeListener(null)
                    binding.filterChecked.isChecked = filterList[position].checked
                    binding.filterChecked.setOnCheckedChangeListener { _, b ->
                        if (b) {
                            filterList[position].checked = true
                            priceFilters.add(filterList[position])
                        } else {
                            priceFilters.remove(filterList[position])
                            filterList[position].checked = false
                        }
                    }
                }
            }
            FILTER_ORDER -> {
                val viewHolder = holder as OrderViewHolder
                with(viewHolder) {
                    binding.filterName.text = filterList[position].name
                    binding.filterRadio.setOnCheckedChangeListener(null)
                    binding.filterRadio.isChecked = filterList[position] == order
                    binding.filterRadio.setOnCheckedChangeListener { _, b ->
                        if (b) {
                            filterList.forEach {
                                if (it != filterList[adapterPosition]) {
                                    it.checked = false
                                } else {
                                    filterList[adapterPosition].checked = true
                                }
                            }
                            order = filterList[adapterPosition]
                            itemClickListener.onClick(order!!)
                        }
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return filterList.size
    }

    fun getFilters(): Pair<Filter?, List<Filter>> {
        return order to priceFilters.toList()
    }

    inner class PriceViewHolder(val binding: BottomSheetCheckItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            priceFilters = filterList.filter { it.checked } as MutableList<Filter>
        }
    }

    inner class OrderViewHolder(val binding: BottomSheetRadioItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            order = filterList.firstOrNull { it.checked }
        }
    }
}

interface ItemClickListener {
    fun onClick(filter: Filter)
}