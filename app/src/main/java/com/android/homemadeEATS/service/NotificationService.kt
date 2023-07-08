package com.android.homemadeEATS.service

import android.R.id
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.homemadeEATS.R
import com.android.homemadeEATS.views.activity.cook.CookNavActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class NotificationService : FirebaseMessagingService() {
    // create a notification service for the functionality
    private val TAG = "NotificationService"
    private var sharedPref: SharedPreferences? = null
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        notify(remoteMessage.notification?.title, remoteMessage.notification?.body)
    }

    private fun notify(title: String?, message: String?) {
        val intent = Intent(this, CookNavActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("data", id.message)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val largeIcon = BitmapFactory.decodeResource(resources, R.drawable.accept)

        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, "notification_channel")
                .setSmallIcon(R.drawable.cart_icon)
                .setLargeIcon(largeIcon)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

        val managerCompat = NotificationManagerCompat.from(this)
        managerCompat.notify(123, builder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Log.d(TAG, "FCM Token: $token")
        })
    }
}