package com.android.homemadeEATS.domain

import com.android.homemadeEATS.repository.common.CommonRepository

class MoreDomain {
    fun clearAllData() {
        CommonRepository.clearData()
    }
}