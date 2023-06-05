package com.android.homemadeEATS.views.activity.common

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.android.homemadeEATS.R
import com.android.homemadeEATS.views.activity.MainActivity
import com.android.homemadeEATS.views.fragment.common.AddressFragment
import com.android.homemadeEATS.views.fragment.common.SetLocationFragment

class AddressActivity : MainActivity() {
    private var isSelected: Boolean = false
    private var userType: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
            Log.e(TAG, "exception $e")
        }
        isSelected = intent.getBooleanExtra("isSelected", false)
        userType = intent.getIntExtra("userType", -1)
        addFragment(SetLocationFragment())
    }

    fun addFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.address_container, fragment)
        if (fragment.javaClass.simpleName.equals(AddressFragment::class.java.simpleName)) {
            transaction.addToBackStack(fragment.javaClass.simpleName)
            val bundle = Bundle()
            bundle.putBoolean("isSelected", isSelected)
            bundle.putInt("userType", userType)
            fragment.arguments = bundle
        }
        transaction.commit()
    }

    companion object {
        private const val TAG = "AddressActivity"
    }
}