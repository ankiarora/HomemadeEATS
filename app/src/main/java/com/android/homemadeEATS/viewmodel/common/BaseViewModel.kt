package com.android.homemadeEATS.viewmodel.common

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.android.homemadeEATS.domain.MoreDomain

open class BaseViewModel(application: Application) : AndroidViewModel(application) {
    val moreDomain: MoreDomain = MoreDomain()
    fun clearAllData() {
        moreDomain.clearAllData()
    }

    protected var appContext: Application = application
}