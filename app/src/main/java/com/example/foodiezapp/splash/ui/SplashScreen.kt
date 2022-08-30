package com.example.foodiezapp.splash.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.example.foodiezapp.intro.IntroActivity
import com.example.foodiezapp.R
import com.example.foodiezapp.common.EventObserver
import com.example.foodiezapp.main.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.concurrent.timerTask

@AndroidEntryPoint
class SplashScreen : AppCompatActivity() {
    private val model: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

//        val intent = Intent(this, IntroActivity::class.java)
//        Timer().schedule(timerTask {
//            startActivity(intent)
//            finish()
//        }, 1000)
        val sideEffectsObserver = EventObserver<SplashSideEffects> {
            handleSideEffect(it)
        }
        model.sideEffect.observe(this, sideEffectsObserver)
        model.isLoggedIn()
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
}



