package com.example.foodiezapp.main.ui.main

import android.Manifest
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.foodiezapp.MainNavGraphDirections
import com.example.foodiezapp.R
import com.example.foodiezapp.common.EventObserver
import com.example.foodiezapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_screen_fragment_placeholder) as NavHostFragment
        navController = navHostFragment.navController

        val sideEffectsObserver = EventObserver<PermissionSideEffects> {
            handleSideEffect(it, navController)
        }
        viewModel.sideEffect.observe(this, sideEffectsObserver)
        viewModel.action(PermissionIntent.Check)
        binding.chipNavigationBar.setOnItemSelectedListener {
            when (it) {
                R.id.home -> navController.navigate(MainNavGraphDirections.moveToHomeFragment())
                R.id.map -> navController.navigate(MainNavGraphDirections.moveToMapsFragment())
                R.id.favorites -> navController.navigate(MainNavGraphDirections.moveToFavoritesFragment())
                R.id.profile -> navController.navigate(MainNavGraphDirections.moveToProfileFragment())
            }
        }
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                navController.navigate(R.id.action_requestAccesFragment_to_homeFragment)
                binding.chipNavigationBar.setItemSelected(R.id.home, true)
                Toast.makeText(this, "E din fragment permis", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "NU E permis", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleSideEffect(sideEffect: PermissionSideEffects, navController: NavController) {
        when (sideEffect) {
            is PermissionSideEffects.NavigateToRequest -> {
                navController.navigate(R.id.action_homeFragment_to_requestAccesFragment)
                Toast.makeText(this, "Este nevoie de permisiuni", Toast.LENGTH_SHORT).show()
            }
            is PermissionSideEffects.NavigateToHome -> {
                binding.chipNavigationBar.setItemSelected(R.id.home, true)
                Toast.makeText(this, "E deja permis", Toast.LENGTH_SHORT).show()
            }
            is PermissionSideEffects.ActivateRequest -> {
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
            is PermissionSideEffects.Feedback ->
                Toast.makeText(this, sideEffect.msg, Toast.LENGTH_SHORT).show()
        }
    }
}