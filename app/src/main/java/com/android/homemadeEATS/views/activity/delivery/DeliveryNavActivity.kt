package com.android.homemadeEATS.views.activity.delivery

import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.homemadeEATS.R
import com.android.homemadeEATS.model.common.FCMToken
import com.android.homemadeEATS.repository.common.LoginRepository
import com.android.homemadeEATS.views.fragment.common.MoreFragment
import com.android.homemadeEATS.views.fragment.cook.CookBookedOrdersFragment
import com.android.homemadeEATS.views.fragment.cook.CookFoodMenuFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.messaging.FirebaseMessaging

class DeliveryNavActivity : AppCompatActivity() {
    lateinit var bottomNavigationView: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_nav)

        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
            Log.e(Companion.TAG, "exception $e")
        }

        bottomNavigationView = findViewById(R.id.bottomNavBar)
        val fragment = CookFoodMenuFragment()
        setCurrentFragment(fragment)
        handleBottomNavigation()
        fetchFCMToken()
    }

    private fun setFcmToken(fcmToken: String) {
        if (!TextUtils.isEmpty(fcmToken)) {
            LoginRepository.setFCMToken(FCMToken(fcmToken)).observe(
                this,
                {
                    if (!it?.error.isNullOrEmpty()) {
                        Toast.makeText(this, it?.error, Toast.LENGTH_LONG).show()
                    } else {
                        it?.message?.let { it1 ->
                            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                        }
                    }
                })
        }
    }

    private fun fetchFCMToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                // Get new Instance ID token
                val token = task.result
                setFcmToken(token!!)
            })
    }

    private fun handleBottomNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.menu_item_1 -> setCurrentFragment(CookFoodMenuFragment())
                R.id.menu_item_2 -> setCurrentFragment(CookBookedOrdersFragment())
                else -> setCurrentFragment(MoreFragment())
            }
        }
    }

    override fun onBackPressed() {
        logoutAlert()
    }

    private fun logoutAlert() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.leaving_alert_title))
        builder.setMessage(getString(R.string.leaving_alert_message))
            .setPositiveButton("Ok") { _: DialogInterface, _: Int ->
                finish()
            }
        builder.setCancelable(false);
        builder.show()
    }

    fun setCurrentFragment(fragment: Fragment): Boolean {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
        return true
    }


    companion object {
        private const val TAG = "DeliveryNavActivity"
    }
}