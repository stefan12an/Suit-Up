package com.example.suitup.main.ui.seeall

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.suitup.main.data.model.Filter
import com.example.suitup.main.ui.seeall.adapter.FilterAdapter
import com.example.suitup.main.ui.seeall.adapter.ItemClickListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import suitup.databinding.FragmentFilterBinding

@AndroidEntryPoint
class FilterFragment(
    private val filterResults: FilterResults,
    private val priceList: List<Filter>,
    private val orderList: List<Filter>
) : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentFilterBinding
    private lateinit var itemClickListener: ItemClickListener
    private lateinit var priceAdapter: FilterAdapter
    private lateinit var orderAdapter: FilterAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterBinding.inflate(inflater, container, false)

        itemClickListener = object : ItemClickListener {
            override fun onClick(filter: Filter) {
                binding.filterOrder.adapter?.notifyDataSetChanged()
            }
        }
        bindPriceRv(requireContext())
        bindOrderRv(requireContext())
        binding.sheetApply.setOnClickListener {
            val (_, priceFilter) = priceAdapter.getFilters()
            val (orderFilter, _) = orderAdapter.getFilters()
            val stringFilter = StringBuilder()
            if (priceFilter.isNotEmpty()) {
                priceFilter.forEach { stringFilter.append(it.id + ",") }
                stringFilter.deleteCharAt(stringFilter.length - 1)
                filterResults.filter(stringFilter.toString(), orderFilter?.id)
            } else {
                filterResults.filter(null, orderFilter?.id)
            }
            dismiss()
        }
        binding.sheetCancel.setOnClickListener {
            dismiss()
        }
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        return dialog
    }

    private fun bindPriceRv(context: Context) {
        priceAdapter = FilterAdapter(priceList, itemClickListener)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.filterPrice.adapter = priceAdapter
        binding.filterPrice.layoutManager = linearLayoutManager
        binding.filterPrice.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
    }

    private fun bindOrderRv(context: Context) {
        orderAdapter = FilterAdapter(orderList, itemClickListener)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.filterOrder.adapter = orderAdapter
        binding.filterOrder.layoutManager = linearLayoutManager
        binding.filterOrder.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )
    }
}

interface FilterResults {
    fun filter(priceFilter: String?, orderFilter: String?)
}



