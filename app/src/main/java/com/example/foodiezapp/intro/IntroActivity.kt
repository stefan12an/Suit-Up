package com.example.foodiezapp.intro

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.example.foodiezapp.auth.AuthActivity
import com.example.foodiezapp.databinding.ActivityIntroBinding
import com.google.android.material.tabs.TabLayoutMediator

class IntroActivity : AppCompatActivity() {
    private val introViewModel: IntroViewModel by viewModels()
    private lateinit var binding: ActivityIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.slideButton.setOnClickListener{
            val intent = Intent(this, AuthActivity::class.java)
            startActivity(intent)
            finish()
        }



        val adapter = SlideAdapter(introViewModel.getList(), this)
        binding.viewPager2.adapter = adapter

//        binding.tabLayout.setupWithViewPager(binding.viewPager2)
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, _ ->
            tab.text = ""
        }.attach()
    }
}