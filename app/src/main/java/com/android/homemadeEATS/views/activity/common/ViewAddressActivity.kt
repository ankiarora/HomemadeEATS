package com.android.homemadeEATS.views.activity.common

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.android.homemadeEATS.R
import com.android.homemadeEATS.model.common.Address
import com.android.homemadeEATS.views.activity.MainActivity
import com.android.homemadeEATS.views.fragment.common.ViewAddressFragment

class ViewAddressActivity : MainActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_address)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
            Log.e(TAG, "exception $e")
        }
        val selectedAddress = intent.extras?.getParcelable<Address>("Selected Address")

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.view_address_container, ViewAddressFragment(true, selectedAddress))
        transaction.commit()
    }

    fun addFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.view_address_container, fragment)
        transaction.addToBackStack(fragment.javaClass.simpleName)
        transaction.commit()
    }

    companion object {
        private const val TAG = "ViewAddressActivity"
    }
}