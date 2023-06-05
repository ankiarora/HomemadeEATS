package com.android.homemadeEATS.network

import com.android.homemadeEATS.repository.common.LoginRepository
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitFactory {

    private val authInterceptor = Interceptor { chain ->
        val newRequest = chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer " + LoginRepository.accessToken)
            .build()

        chain.proceed(newRequest)
    }

    //Needed for access token passing in future
    private val client =
        OkHttpClient().newBuilder()
            .addInterceptor(authInterceptor)
            .build()


    fun retrofit(baseUrl: String): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    fun callWithToken(baseUrl: String): Retrofit = Retrofit.Builder()
        .client(client)
        .baseUrl(baseUrl)
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
}