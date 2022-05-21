package com.c22_pc383.wacayang

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.postDelayed
import com.c22_pc383.wacayang.databinding.ActivitySplashBinding
import com.c22_pc383.wacayang.service.TokenUpdaterService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        startService(Intent(this, TokenUpdaterService::class.java))

        handler.postDelayed(WAIT_DELAY) {
            if (Firebase.auth.currentUser != null)
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            else
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))

            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        private const val WAIT_DELAY = 1000L
    }
}