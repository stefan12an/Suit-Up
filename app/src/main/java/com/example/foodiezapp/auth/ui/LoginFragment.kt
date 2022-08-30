package com.example.foodiezapp.auth.ui

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
import com.example.foodiezapp.common.EventObserver
import com.example.foodiezapp.databinding.FragmentLoginBinding
import com.example.foodiezapp.main.ui.main.MainActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var callBackManager: CallbackManager
    private lateinit var binding: FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        callBackManager = CallbackManager.Factory.create()
        binding.fbLogin.setPermissions(listOf("email", "public_profile"))
        binding.fbLogin.setFragment(this)
        binding.fbLogin.registerCallback(callBackManager, object : FacebookCallback<LoginResult> {
            override fun onCancel() { // this method is invoked when the request is cancelled
                Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: FacebookException) {
                Toast.makeText(context, "$error", Toast.LENGTH_SHORT).show()
            }

            override fun onSuccess(result: LoginResult) {
                viewModel.action(LoginUserIntent.Login("Facebook"))
            }
        })
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
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

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callBackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun handleSideEffect(sideEffect: LoginSideEffects) {
        when (sideEffect) {
            is LoginSideEffects.NavigateToMain -> {
                startActivity(Intent(context, MainActivity::class.java))
                activity?.finish()
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
            completedTask.getResult(ApiException::class.java)
            viewModel.action(LoginUserIntent.Login("Google"))
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }
}

