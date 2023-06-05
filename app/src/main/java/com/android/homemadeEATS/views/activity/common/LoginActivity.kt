package com.android.homemadeEATS.views.activity.common

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.android.homemadeEATS.R
import com.android.homemadeEATS.views.activity.MainActivity
import com.android.homemadeEATS.views.fragment.common.LoginFragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class LoginActivity : MainActivity() {
    private lateinit var analytics: FirebaseAnalytics
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        analytics = Firebase.analytics

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT) {
            param(FirebaseAnalytics.Param.ITEM_ID, "ID");
            param(FirebaseAnalytics.Param.ITEM_NAME, "Login Activity");
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "notification_channel",
                "notification_channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        FirebaseMessaging.getInstance().subscribeToTopic("general")
            .addOnCompleteListener { task ->
                var msg = "Notification Subscribed Successfully"
                if (!task.isSuccessful) {
                    msg = "Notification Subscription failed"
                }
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }


        val fragment: Fragment = LoginFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.login_container, fragment)
        transaction.commit()
    }
}