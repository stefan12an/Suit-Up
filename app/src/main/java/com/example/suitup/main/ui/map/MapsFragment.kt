package com.example.suitup.main.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.suitup.common.ClusterManagerRenderer
import com.example.suitup.common.Constants
import com.example.suitup.common.EventObserver
import com.example.suitup.main.data.model.ClusterMarker
import com.example.suitup.main.data.model.CurrentLocation
import com.example.suitup.main.data.model.Store
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.algo.GridBasedAlgorithm
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import suitup.MainNavGraphDirections
import suitup.R
import suitup.databinding.FragmentMapsBinding


@AndroidEntryPoint
class MapsFragment : Fragment() {
    private lateinit var binding: FragmentMapsBinding
    private var clusterManager: ClusterManager<ClusterMarker>? = null
    private var nearByStores: List<Store>? = emptyList()
    private var location: CurrentLocation? = null
    private val viewModel: MapsViewModel by viewModels()
    private val callback = OnMapReadyCallback { googleMap ->
        googleMap.clear()
        val standardBottomSheetBehavior = BottomSheetBehavior.from(binding.mapBottomSheet)
        standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        if (clusterManager == null) {
            clusterManager = ClusterManager<ClusterMarker>(context, googleMap)
        }
        googleMap.setMinZoomPreference(15f)
        clusterManager?.algorithm = GridBasedAlgorithm()
        clusterManager?.renderer = ClusterManagerRenderer(context, googleMap, clusterManager)
        val mapsUiStateObserver = Observer<MapsUiState> {
            if (it.loading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
                nearByStores = viewModel.mapsUiState.value?.storesAll
                location = viewModel.mapsUiState.value?.location
                val userLocation = LatLng(location!!.latitude, location!!.longitude)
                googleMap.addMarker(
                    MarkerOptions().position(userLocation).title("Current location!")
                )
                nearByStores?.forEach { store ->
                    val markerItem = ClusterMarker(store)
                    clusterManager?.addItem(markerItem)
                }
                clusterManager?.cluster()
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation))
                googleMap.setMinZoomPreference(10f)
            }
        }
        viewModel.mapsUiState.observe(viewLifecycleOwner, mapsUiStateObserver)


        googleMap.setOnMarkerClickListener(clusterManager)
        googleMap.setOnInfoWindowClickListener(clusterManager?.markerManager)
        googleMap.isBuildingsEnabled = true
        googleMap.setOnCameraIdleListener(clusterManager)
        clusterManager?.setOnClusterItemClickListener { marker ->
            val storeLocation =
                LatLng(
                    marker.store.coordinates.latitude,
                    marker.store.coordinates.longitude
                )
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(storeLocation))
            with(binding) {
                mapBottomSheet.setOnClickListener { viewModel.action(MapsIntent.OpenDetails(marker.store.id)) }
                mapName.text = marker.store.name
                mapAddress.text = marker.store.location?.address1
                mapCategories.text = marker.store.categories.toString()
                mapClosed.text = "Closed: ${marker.store.is_closed.toString()}"
                mapRating.text = marker.store.rating.toString()
                setRatingColor(marker.store.rating)
                Picasso.get().load(marker.store.image_url).into(mapIcon)
            }
            standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            return@setOnClusterItemClickListener true
        }
        googleMap.setOnMapClickListener {
            if (standardBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
                standardBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        val sideEffectsObserver = EventObserver<MapsSideEffects> {
            handleSideEffect(it)
        }
        viewModel.sideEffect.observe(viewLifecycleOwner, sideEffectsObserver)
        return binding.root
    }

    private fun handleSideEffect(sideEffect: MapsSideEffects) {
        when (sideEffect) {
            is MapsSideEffects.NavigateToRequest ->
                findNavController().navigate(R.id.action_mapsFragment_to_requestAccesFragment)
            is MapsSideEffects.NavigateToDetails -> findNavController().navigate(
                MainNavGraphDirections.moveToDetailsFragment(sideEffect.storeId)
            )
            is MapsSideEffects.Feedback ->
                Toast.makeText(requireContext(), sideEffect.msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onResume() {
        super.onResume()
        viewModel.action(MapsIntent.GetData)
    }

    private fun setRatingColor(rating: Float) {
        when (rating) {
            in Constants.INTERVAL_RED -> binding.mapRatingCard.setCardBackgroundColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.danger
                )
            )
            in Constants.INTERVAL_YELLOW -> binding.mapRatingCard.setCardBackgroundColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.warning
                )
            )
            in Constants.INTERVAL_GREEN -> binding.mapRatingCard.setCardBackgroundColor(
                ContextCompat.getColor(
                    binding.root.context,
                    R.color.success
                )
            )
        }
    }
}