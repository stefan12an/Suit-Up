package com.example.foodiezapp.main.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodiezapp.MainNavGraphDirections
import com.example.foodiezapp.R
import com.example.foodiezapp.common.EventObserver
import com.example.foodiezapp.databinding.FragmentHomeBinding
import com.example.foodiezapp.main.ui.home.adapter.HomeParentOnClickListener
import com.example.foodiezapp.main.ui.home.adapter.ParentAdapter
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val sideEffectsObserver = EventObserver<HomeSideEffects> {
            handleSideEffect(it)
        }
        val homeUiStateObserver = Observer<HomeUiState> {
            if (it.loading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.parentHomeRecyclerView.alpha = 0.3F
            } else {
                binding.progressBar.visibility = View.GONE
                binding.parentHomeRecyclerView.alpha = 1F
                bindRv(requireContext())
            }
        }

        viewModel.sideEffect.observe(viewLifecycleOwner, sideEffectsObserver)
        viewModel.homeUiState.observe(viewLifecycleOwner, homeUiStateObserver)

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.action(HomeIntent.GetSearchResult(binding.searchBar.query))
                return false
            }
        })

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.action(HomeIntent.GetData)
    }

    private fun handleSideEffect(sideEffect: HomeSideEffects) {
        when (sideEffect) {
            is HomeSideEffects.NavigateToSeeAll -> findNavController().navigate(sideEffect.directions)
            is HomeSideEffects.NavigateToDetails -> findNavController().navigate(
                MainNavGraphDirections.moveToDetailsFragment(sideEffect.restaurantId)
            )
            is HomeSideEffects.NavigateToRequest -> findNavController().navigate(R.id.action_homeFragment_to_requestAccesFragment)
            is HomeSideEffects.Feedback ->
                Toast.makeText(requireContext(), sideEffect.msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun bindRv(context: Context) {
        val listOfRestaurants = listOf(
            viewModel.homeUiState.value?.restaurantsNearBy ?: emptyList(),
            viewModel.homeUiState.value?.restaurantsHotNew ?: emptyList(),
            viewModel.homeUiState.value?.restaurantsDeals ?: emptyList()
        )
        val parentAdapter =
            ParentAdapter(listOfRestaurants, HomeParentOnClickListener({ direction ->
                viewModel.action(HomeIntent.SeeAll(direction))
            }, {
                viewModel.action(HomeIntent.OpenDetails(it))
            }))
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.parentHomeRecyclerView.adapter = parentAdapter
        binding.parentHomeRecyclerView.layoutManager = linearLayoutManager
    }
}