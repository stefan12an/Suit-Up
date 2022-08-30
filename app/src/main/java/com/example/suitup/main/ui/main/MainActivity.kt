package com.example.suitup.main.ui.main

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.suitup.R
import com.example.suitup.common.EventObserver
import com.example.suitup.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.main_screen_fragment_placeholder) as NavHostFragment
        binding = ActivityMainBinding.inflate(layoutInflater)
        navController = navHostFragment.navController

        val sideEffectsObserver = EventObserver<PermissionSideEffects> {
            handleSideEffect(it, navController)
        }
        viewModel.sideEffect.observe(this, sideEffectsObserver)
        viewModel.action(PermissionIntent.Check)
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                navController.navigate(R.id.action_requestAccesFragment_to_homeFragment)
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