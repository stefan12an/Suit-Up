package com.example.suitup.main.ui.favourite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.suitup.common.EventObserver
import com.example.suitup.main.data.model.Restaurant
import com.example.suitup.main.ui.favourite.adapter.FavoritesAdapter
import com.example.suitup.main.ui.favourite.adapter.FavoritesOnClickListener
import dagger.hilt.android.AndroidEntryPoint
import suitup.MainNavGraphDirections
import suitup.R
import suitup.databinding.FragmentFavoritesBinding

@AndroidEntryPoint
class FavoritesFragment : Fragment() {
    private val viewModel: FavoritesViewModel by viewModels()
    private lateinit var binding: FragmentFavoritesBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        val sideEffectsObserver = EventObserver<FavoritesSideEffects> {
            handleSideEffect(it)
        }
        val favoritesUiStateObserver = Observer<FavoritesUiState> {
            if (it.loading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
                val newFavoritesCopy = it.restaurantsAll?.map { restaurant -> restaurant.copy() }
                updateFavoriteData(newFavoritesCopy ?: emptyList())
            }
        }
        viewModel.sideEffect.observe(viewLifecycleOwner, sideEffectsObserver)
        viewModel.favoritesUiState.observe(viewLifecycleOwner, favoritesUiStateObserver)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.action(FavoritesIntent.GetData)
        bindToolbar()
    }

    private fun handleSideEffect(sideEffect: FavoritesSideEffects) {
        when (sideEffect) {
            is FavoritesSideEffects.NavigateToDetails -> findNavController().navigate(
                MainNavGraphDirections.moveToDetailsFragment(sideEffect.restaurantId)
            )
            is FavoritesSideEffects.Feedback ->
                Toast.makeText(requireContext(), sideEffect.msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateFavoriteData(restaurantList: List<Restaurant>) {
        bindRv()
        val adapter = binding.favoritesRv.adapter
        if (adapter is FavoritesAdapter) {
            adapter.submitList(restaurantList)
        } else {
            Toast.makeText(
                context,
                "Required adapter type: FavoritesAdapter",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun bindRv() {
        if (binding.favoritesRv.layoutManager == null) {
            val linearLayoutManager = LinearLayoutManager(context)
            linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
            binding.favoritesRv.layoutManager = linearLayoutManager
        }
        if (binding.favoritesRv.adapter == null) {
            binding.favoritesRv.adapter = FavoritesAdapter(FavoritesOnClickListener({
                viewModel.action(
                    FavoritesIntent.AddToFavorites(it)
                )
            }, {
                viewModel.action(FavoritesIntent.OpenDetails(it))
            }))
        }
    }

    private fun bindToolbar() {
        binding.myToolbar.setupWithNavController(
            findNavController(),
            AppBarConfiguration(findNavController().graph)
        )
        binding.myToolbar.navigationIcon = null
        binding.myToolbar.title = "Favorites"
        binding.myToolbar.setTitleTextColor(requireContext().getColor(R.color.white))
        binding.myToolbar.isTitleCentered = true
    }
}