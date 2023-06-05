package com.android.homemadeEATS.network

import com.android.homemadeEATS.AppConstants
import com.android.homemadeEATS.services.ApiService

object ApiFactory {

    val apiWithoutToken: ApiService = RetrofitFactory.retrofit(AppConstants.BASE_URL)
        .create(ApiService::class.java)

    val apiWithToken: ApiService = RetrofitFactory.callWithToken(AppConstants.BASE_URL)
        .create(ApiService::class.java)
}