package com.example.foodiezapp.main.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.foodiezapp.auth.AuthActivity
import com.example.foodiezapp.common.EventObserver
import com.example.foodiezapp.databinding.FragmentProfileBinding
import com.facebook.*
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var binding: FragmentProfileBinding
    private lateinit var origin: String
    private val viewModel: ProfileViewModel by viewModels()
    private val settingsList = listOf("Edit credentials", "Log out")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val sideEffectsObserver = EventObserver<ProfileSideEffects> {
            handleSideEffect(it)
        }
        FacebookSdk.fullyInitialize()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        val arrayAdapter = ArrayAdapter(
            binding.root.context,
            android.R.layout.simple_list_item_1,
            settingsList
        )
        binding.settingsList.adapter = arrayAdapter
        val profileUiStateObserver = Observer<ProfileUiState> {
            if (it.loading) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
                origin = viewModel.profileUiState.value?.origin.toString()
                when (origin) {
                    "Google" -> {
                        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
                        binding.settingsEmail.text = account?.displayName
                        Picasso.get().load(account?.photoUrl).into(binding.settingsImage)
                    }
                    "Facebook" -> {
                        tokenTracker.startTracking()
                    }
                }
            }
        }
        viewModel.profileUiState.observe(viewLifecycleOwner, profileUiStateObserver)
        viewModel.sideEffect.observe(viewLifecycleOwner, sideEffectsObserver)
        binding.settingsList.setOnItemClickListener { _, _, i, _ ->
            when (settingsList[i]) {
                "Edit credentials" -> {}
                "Log out" -> {
                    when (origin) {
                        "Google" -> mGoogleSignInClient.signOut().addOnCompleteListener {
                            viewModel.action(ProfileIntent.LogOut)
                        }
                        "Facebook" -> {
                            LoginManager.getInstance().logOut()
                            viewModel.action(ProfileIntent.LogOut)
                        }
                    }

                }
            }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.action(ProfileIntent.GetOrigin)
    }

    private val tokenTracker = object : AccessTokenTracker() {
        override fun onCurrentAccessTokenChanged(
            oldAccessToken: AccessToken?,
            currentAccessToken: AccessToken?
        ) {
            loadUserProfile(currentAccessToken)
        }
    }

    private fun loadUserProfile(newAccessToken: AccessToken?) {
        val request = GraphRequest.newMeRequest(
            newAccessToken
        ) { obj, _ ->
            val firstName = obj?.getString("first_name")
            val lastName = obj?.getString("last_name")
            val email = obj?.getString("email")
            val id = obj?.getString("id")
            val imageUrl = "https://graph.facebook.com/$id/picture?type=normal"
            binding.settingsEmail.text = "$firstName $lastName"
            Picasso.get().load(imageUrl).into(binding.settingsImage)
        }

        val parameters = Bundle()
        parameters.putString("fields", "first_name,last_name,email,id")
        request.parameters = parameters
        request.executeAsync()
    }

    private fun handleSideEffect(sideEffect: ProfileSideEffects) {
        when (sideEffect) {
            is ProfileSideEffects.NavigateToLogin -> {
                val i = Intent(context, AuthActivity::class.java)
                startActivity(i)
                activity?.finish()
            }
            is ProfileSideEffects.Feedback ->
                Toast.makeText(requireContext(), sideEffect.msg, Toast.LENGTH_SHORT).show()
        }
    }
}