package com.example.suitup.main.ui.profile

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
import com.example.suitup.auth.AuthActivity
import com.example.suitup.common.EventObserver
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import suitup.R
import suitup.databinding.FragmentProfileBinding

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
                        Picasso.get().load(account?.photoUrl).placeholder(R.drawable.logo)
                            .into(binding.settingsImage)
                    }
                    "Normal" -> {
                        val account = FirebaseAuth.getInstance().currentUser
                        binding.settingsEmail.text = account?.email
                        Picasso.get().load(account?.photoUrl).placeholder(R.drawable.logo)
                            .into(binding.settingsImage)
                    }
                }
            }
        }
        viewModel.profileUiState.observe(viewLifecycleOwner, profileUiStateObserver)
        viewModel.sideEffect.observe(viewLifecycleOwner, sideEffectsObserver)
        binding.settingsList.setOnItemClickListener { _, _, i, _ ->
            val firebaseAuth = FirebaseAuth.getInstance()
            when (settingsList[i]) {
                "Edit credentials" -> {
                    when (origin) {
                        "Google" -> Toast.makeText(
                            requireContext(),
                            "You are logged in with Google and can't change the credentials from the app!",
                            Toast.LENGTH_LONG
                        ).show()
                        "Normal" -> EditCredentialsDialog(requireContext()).show()
                    }
                }
                "Log out" -> {
                    when (origin) {
                        "Google" -> mGoogleSignInClient.signOut().addOnCompleteListener {
                            firebaseAuth.signOut()
                            viewModel.action(ProfileIntent.LogOut)
                        }
                        "Normal" -> {
                            firebaseAuth.signOut()
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