package com.example.suitup.intro

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.suitup.auth.AuthActivity
import com.google.android.material.tabs.TabLayoutMediator
import suitup.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {
    private val introViewModel: IntroViewModel by viewModels()
    private lateinit var binding: ActivityIntroBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val adapter = SlideAdapter(introViewModel.getList(), this)
        binding.viewPager2.adapter = adapter
        binding.viewPager2.isUserInputEnabled = false
        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == introViewModel.getList().size - 1) {
                    binding.slideButton.text = "Login"
                }
            }
        })
        binding.slideButton.setOnClickListener {
            if (binding.tabLayout.selectedTabPosition != introViewModel.getList().size - 1) {
                binding.viewPager2.currentItem = getItem(+1)
            }else{
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, _ ->
            tab.text = ""
        }.attach()
    }

    private fun getItem(i: Int): Int {
        return binding.viewPager2.currentItem + i
    }
}