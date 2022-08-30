package com.example.suitup.auth.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.suitup.R
import com.example.suitup.auth.data.model.UserCredentials
import com.example.suitup.common.EventObserver
import com.example.suitup.databinding.FragmentLoginBinding
import com.example.suitup.main.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment: Fragment() {

    private val model: LoginViewModel by viewModels()

    private lateinit var binding: FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        val sideEffectsObserver = EventObserver<LoginSideEffects> {
            handleSideEffect(it)
        }
        val uiStateObserver = Observer<LoginState>{
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

        binding.loginLogBtn.setOnClickListener {
            model.action(
                LoginUserIntent.Login(
                    UserCredentials(
                        binding.loginUsername.text.toString(),
                        binding.loginPassword.text.toString()
                    )
                )
            )
        }
        binding.loginRegBtn.setOnClickListener {
            model.action(LoginUserIntent.Register)
        }
        binding.loginForBtn.setOnClickListener {
            model.action(LoginUserIntent.Forgot)
        }
        return binding.root
    }

    private fun handleSideEffect(sideEffect: LoginSideEffects) {
        when (sideEffect) {
            is LoginSideEffects.NavigateToMain -> {
                startActivity(Intent(context, MainActivity::class.java))
                activity?.finish()
            }
            is LoginSideEffects.NavigateToResetPass ->
                findNavController().navigate(R.id.action_loginFragment_to_forgotFragment)
            is LoginSideEffects.NavigateToRegister -> {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
            is LoginSideEffects.Feedback ->
                Toast.makeText(requireContext(), sideEffect.msg, Toast.LENGTH_SHORT).show()

        }
    }

}

