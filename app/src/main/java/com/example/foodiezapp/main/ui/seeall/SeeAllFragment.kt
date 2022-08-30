package com.example.foodiezapp.main.ui.seeall

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodiezapp.MainNavGraphDirections
import com.example.foodiezapp.R
import com.example.foodiezapp.common.Constants.ATTRIBUTES
import com.example.foodiezapp.common.EventObserver
import com.example.foodiezapp.databinding.FragmentSeeAllBinding
import com.example.foodiezapp.main.data.model.Filter
import com.example.foodiezapp.main.data.model.Restaurant
import com.example.foodiezapp.main.ui.seeall.adapter.SeeAllAdapter
import com.example.foodiezapp.main.ui.seeall.adapter.SeeAllOnClickListener
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SeeAllFragment : Fragment() {
    private val viewModel: SeeAllViewModel by viewModels()
    private lateinit var binding: FragmentSeeAllBinding
    private var attribute: String? = null
    private lateinit var filterResults: FilterResults
    private val priceList = listOf(
        Filter(0, "4", "Price $$$$", false),
        Filter(0, "3", "Price $$$", false),
        Filter(0, "2", "Price $$", false),
        Filter(0, "1", "Price $", false)
    )
    private val orderList = listOf(
        Filter(1, "close", "Close to me", true),
        Filter(1, "desc", "Price high to low", false),
        Filter(1, "asc", "Price low to high", false)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSeeAllBinding.inflate(inflater, container, false)
        val bundle = arguments?.let { SeeAllFragmentArgs.fromBundle(it) }
        attribute = bundle?.attributes
        val sideEffectsObserver = EventObserver<SeeAllSideEffects> {
            handleSideEffect(it)
        }
        filterResults = object : FilterResults {
            override fun filter(priceFilter: String?, orderFilter: String?) {
                viewModel.action(SeeAllIntent.GetData(attribute, priceFilter, orderFilter))
            }
        }
        val seeAllUiStateObserver = Observer<SeeAllUiState> {
            if (it.loading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
                val newCopy = it.restaurantsAll?.map { restaurant -> restaurant.copy() }
                updateFavoriteData(newCopy ?: emptyList())
                binding.myToolbar.title = getKey(ATTRIBUTES, attribute)
                Picasso.get().load(it.restaurantsAll?.get(0)?.image_url).into(binding.toolbarIv)
            }
        }
        viewModel.sideEffect.observe(viewLifecycleOwner, sideEffectsObserver)
        viewModel.seeAllUiState.observe(viewLifecycleOwner, seeAllUiStateObserver)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.action(SeeAllIntent.GetData(attribute))
        bindToolbar()
    }

    private fun handleSideEffect(sideEffect: SeeAllSideEffects) {
        when (sideEffect) {
            is SeeAllSideEffects.NavigateToDetails -> {
                findNavController().navigate(MainNavGraphDirections.moveToDetailsFragment(sideEffect.restaurantId))
            }
            is SeeAllSideEffects.NavigateToFilter -> {
                val modalBottomSheet = FilterFragment(filterResults, priceList, orderList)
                modalBottomSheet.show(parentFragmentManager, "FilterFragment")
            }
            is SeeAllSideEffects.NavigateToRequest ->
                findNavController().navigate(R.id.action_seeAllFragment_to_requestAccesFragment)
            is SeeAllSideEffects.Feedback ->
                Toast.makeText(requireContext(), sideEffect.msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateFavoriteData(restaurantList: List<Restaurant>) {
        bindRv()
        val adapter = binding.seeAllRv.adapter
        if (adapter is SeeAllAdapter) {
            adapter.submitList(restaurantList)
        } else {
            Toast.makeText(
                context,
                "Required adapter type: SeeAllAdapter",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun bindRv() {
        if (binding.seeAllRv.layoutManager == null) {
            val linearLayoutManager = LinearLayoutManager(context)
            linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            binding.seeAllRv.layoutManager = linearLayoutManager
        }
        if (binding.seeAllRv.adapter == null) {
            binding.seeAllRv.adapter = SeeAllAdapter(
                SeeAllOnClickListener({
                    viewModel.action(SeeAllIntent.Filter)
                }, {
                    viewModel.action(SeeAllIntent.OpenDetails(it))
                }, {
                    viewModel.action(SeeAllIntent.AddToFavorites(it))
                })
            )
        }
    }

    private fun <K, V> getKey(map: Map<K, V>, target: V): K? {
        return map.keys.firstOrNull { target == map[it] };
    }

    private fun bindToolbar() {
        binding.myToolbar.setNavigationIcon(R.drawable.ic_arrow_back)
        binding.myToolbar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }
}