package com.example.suitup.splash.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.suitup.common.EventObserver
import com.example.suitup.intro.IntroActivity
import com.example.suitup.main.ui.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import suitup.R
import java.util.*
import kotlin.concurrent.timerTask


@AndroidEntryPoint
class SplashScreen : AppCompatActivity() {
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        val sideEffectsObserver = EventObserver<SplashSideEffects> {
            handleSideEffect(it)
        }
        isLoggedIn()
        viewModel.sideEffect.observe(this, sideEffectsObserver)
    }

    override fun onStop() {
        super.onStop()
        Timer().cancel()
    }

    private fun handleSideEffect(sideEffect: SplashSideEffects) {
        var intent = Intent(this, IntroActivity::class.java)
        when (sideEffect) {
            is SplashSideEffects.NavigateToMain ->
                intent = Intent(this, MainActivity::class.java)
            is SplashSideEffects.NavigateToIntro ->
                intent = Intent(this, IntroActivity::class.java)
            is SplashSideEffects.Feedback ->
                Toast.makeText(applicationContext, sideEffect.msg, Toast.LENGTH_SHORT).show()
        }
        Timer().schedule(timerTask {
            startActivity(intent)
            finish()
        }, 1000)
    }

    private fun isLoggedIn() {
        // Initialize firebase auth
        val firebaseAuth = FirebaseAuth.getInstance()
        // Initialize firebase user
        val firebaseUser = firebaseAuth.currentUser
        // Check condition
        if (firebaseUser != null) {
            // When user already sign in redirect to profile activity
            viewModel.logInCheck(true)
        }else{
            viewModel.logInCheck(false)
        }
    }
}



