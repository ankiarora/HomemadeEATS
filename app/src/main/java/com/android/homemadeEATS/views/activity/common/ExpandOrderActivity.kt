package com.android.homemadeEATS.views.activity.common

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.homemadeEATS.R
import com.android.homemadeEATS.model.common.MealDetail
import com.android.homemadeEATS.views.fragment.common.ExpandOrderFragment

class ExpandOrderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expand_order)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
            Log.e(Companion.TAG, "exception $e")
        }
        val mealDetail: List<MealDetail> = intent.extras?.get("mealDetails") as List<MealDetail>
        addFragment(ExpandOrderFragment(mealDetail))
    }

    private fun addFragment(fragment: ExpandOrderFragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.address_container, fragment)
        transaction.commit()
    }

    companion object {
        private const val TAG = "ExpandOrderActivity"
    }
}