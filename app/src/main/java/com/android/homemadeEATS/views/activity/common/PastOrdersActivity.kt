package com.android.homemadeEATS.views.activity.common

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.android.homemadeEATS.R
import com.android.homemadeEATS.views.activity.MainActivity
import com.android.homemadeEATS.views.fragment.common.PastOrdersFragment

class PastOrdersActivity : MainActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_past_orders)
        addFragment(PastOrdersFragment())
    }

    private fun addFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.past_orders_container, fragment)
        transaction.commit()
    }
}