package com.example.foodiezapp.auth.ui.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.foodiezapp.auth.data.model.UserCredentials
import com.example.foodiezapp.R
import com.example.foodiezapp.common.EventObserver
import com.example.foodiezapp.databinding.FragmentRegisterBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private val model: RegisterViewModel by viewModels()
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val sideEffectsObserver = EventObserver<RegisterSideEffects> {
            handleSideEffect(it)
        }
        val uiStateObserver = Observer<RegisterState>{
            if (it.loading){
                binding.progressBar.visibility = View.VISIBLE
                binding.everythingButProgress.alpha = 0.5F
            }else{
                binding.progressBar.visibility = View.GONE
                binding.everythingButProgress.alpha = 1F
            }
        }

        model.sideEffect.observe(viewLifecycleOwner, sideEffectsObserver)
        model.uiState.observe(viewLifecycleOwner,uiStateObserver)

        binding.registerRegBtn.setOnClickListener {
            model.action(
                RegisterUserIntent.Register(
                    UserCredentials(
                        binding.registerUsername.text.toString(),
                        binding.registerPassword.text.toString()
                    )
                )
            )
        }
        binding.registerLogBtn.setOnClickListener {
            model.action(RegisterUserIntent.Login)
            handleSideEffect(RegisterSideEffects.NavigateToLogin)
        }
        return binding.root
    }


    private fun handleSideEffect(sideEffect: RegisterSideEffects) {
        when (sideEffect) {
            is RegisterSideEffects.NavigateToRegister ->
                findNavController().navigate(R.id.action_registerFragment_to_main_nav_graph)
            is RegisterSideEffects.NavigateToLogin -> {
                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
            }
            is RegisterSideEffects.Feedback ->
                Toast.makeText(requireContext(), sideEffect.msg, Toast.LENGTH_SHORT).show()
        }
    }


}