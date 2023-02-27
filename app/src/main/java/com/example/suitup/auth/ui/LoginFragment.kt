package com.example.suitup.auth.ui

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.suitup.common.EventObserver
import com.example.suitup.main.ui.main.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import suitup.R
import suitup.databinding.FragmentLoginBinding

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var binding: FragmentLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_login_token))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        val sideEffectsObserver = EventObserver<LoginSideEffects> {
            handleSideEffect(it)
        }
        val uiStateObserver = Observer<LoginState> {
            if (it.loading) {
                binding.progressBar.visibility = View.VISIBLE
                binding.everythingButProgress.alpha = 0.5F
            } else {
                binding.progressBar.visibility = View.GONE
                binding.everythingButProgress.alpha = 1F
            }
        }

        viewModel.sideEffect.observe(viewLifecycleOwner, sideEffectsObserver)
        viewModel.uiState.observe(viewLifecycleOwner, uiStateObserver)
        binding.googleLogin.setOnClickListener { googleSignIn() }
        binding.buttonLogin.setOnClickListener { normalSignIn() }
        binding.goToSignup.setOnClickListener { viewModel.action(LoginUserIntent.GoToSignup) }

        return binding.root
    }

    private fun normalSignIn() {
        val email = binding.loginEmail.text.toString()
        val password = binding.loginPassword.text.toString()
        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            Toast.makeText(
                requireContext(),
                "Please fill the required fields to proceed!",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // When task is successful redirect to profile activity
                    viewModel.action(LoginUserIntent.Login("Normal"))
                    // Display Toast
                    displayToast("Authentication successful")
                } else {
                    // When task is unsuccessful display Toast
                    displayToast(
                        "Authentication Failed :" + task.exception?.message
                    )
                }
            }
        }
    }

    private fun handleSideEffect(sideEffect: LoginSideEffects) {
        when (sideEffect) {
            is LoginSideEffects.NavigateToMain -> {
                startActivity(Intent(context, MainActivity::class.java))
                activity?.finish()
            }
            is LoginSideEffects.NavigateToSignUp -> {
                findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
            }
            is LoginSideEffects.Feedback ->
                Toast.makeText(requireContext(), sideEffect.msg, Toast.LENGTH_SHORT).show()
        }
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task: Task<GoogleSignInAccount> =
                    GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleGoogleSignInResult(task)
            }
        }


    private fun googleSignIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            // Initialize sign in account
            val googleSignInAccount = completedTask.getResult(ApiException::class.java)
            // Check condition
            if (googleSignInAccount != null) {
                // When sign in account is not equal to null initialize auth credential
                val authCredential: AuthCredential = GoogleAuthProvider.getCredential(
                    googleSignInAccount.idToken, null
                )
                // Check credential
                firebaseAuth.signInWithCredential(authCredential)
                    .addOnCompleteListener { task ->
                        // Check condition
                        if (task.isSuccessful) {
                            // When task is successful redirect to profile activity
                            viewModel.action(LoginUserIntent.Login("Google"))
                            // Display Toast
                            displayToast("Authentication successful")
                        } else {
                            // When task is unsuccessful display Toast
                            displayToast(
                                "Authentication Failed :" + task.exception?.message
                            )
                        }
                    }
            }
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }

    private fun displayToast(s: String) {
        Toast.makeText(requireContext(), s, Toast.LENGTH_LONG).show()
    }
}

