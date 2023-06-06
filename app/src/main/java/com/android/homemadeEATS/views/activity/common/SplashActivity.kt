package com.android.homemadeEATS.views.activity.common

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.android.homemadeEATS.R

class SplashActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash2)

        val handler = Handler()
        val runnable = Runnable() {
            kotlin.run {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        handler.postDelayed(runnable, 3000)
    }
}