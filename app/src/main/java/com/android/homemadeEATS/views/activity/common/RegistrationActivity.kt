package com.android.homemadeEATS.views.activity.common

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.android.homemadeEATS.R
import com.android.homemadeEATS.views.activity.MainActivity
import com.android.homemadeEATS.views.fragment.common.RegistrationFragment

class RegistrationActivity : MainActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
            Log.e(TAG, "exception $e")
        }
        val fragment: Fragment = RegistrationFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.registration_container, fragment)
        transaction.commit()
    }

    companion object {
        private const val TAG = "RegistrationActivity"
    }
}