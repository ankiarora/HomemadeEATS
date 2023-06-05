package com.android.homemadeEATS.repository.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.homemadeEATS.AppConstants
import com.android.homemadeEATS.model.common.*
import com.android.homemadeEATS.network.ApiFactory
import com.google.gson.Gson
import kotlinx.coroutines.*

object LoginRepository {
    var fcmToken: String? = ""
    var accessToken: String? = null
    var user: User? = null
    fun signUp(request: SignupRequest): MutableLiveData<SignupResponse> {
        val job: CompletableJob = Job()
        return object : MutableLiveData<SignupResponse>() {
            override fun onActive() {
                job.let { job ->
                    CoroutineScope(Dispatchers.IO + job).launch {
                        val response = ApiFactory.apiWithoutToken.signUp(request)
                        withContext(Dispatchers.Main) {
                            value = if (response.code() == AppConstants.SUCCESS) {
                                SignupResponse(response.body()?.message, null)
                            } else if (response.code() == AppConstants.CONFLICT) {
                                SignupResponse(null, response.errorBody()!!.string())
                            } else if (response.code() == AppConstants.UNPROCESSABLE_ENTITY && response.errorBody() != null) {
                                val errorBody = Gson().fromJson(
                                    response.errorBody()!!.string(),
                                    ErrorBody::class.java
                                )
                                SignupResponse(null, errorBody.errors?.get(0)?.msg)
                            } else {
                                SignupResponse(
                                    null,
                                    "Unexpected error occurred. Please try again later."
                                )
                            }
                            job.complete()
                        }
                    }
                }
            }
        }
    }

    fun signIn(request: SigninRequest): MutableLiveData<SigninResponse> {
        val job: CompletableJob = Job()
        return object : MutableLiveData<SigninResponse>() {
            override fun onActive() {
                if (accessToken.isNullOrEmpty()) {
                    job.let { job ->
                        CoroutineScope(Dispatchers.IO + job).launch {
                            val response = ApiFactory.apiWithoutToken.signIn(request)
                            withContext(Dispatchers.Main) {
                                if (response.code() == AppConstants.SUCCESS) {
                                    accessToken = response.body()?.user?.token
                                    user = response.body()?.user
                                    value = SigninResponse(
                                        response.body()!!.message,
                                        response.body()!!.user,
                                        null
                                    )
                                } else if (response.code() == AppConstants.CONFLICT) {
                                    value = SigninResponse(
                                        null,
                                        null,
                                        response.body()!!.message
                                    )
                                } else if (response.code() == AppConstants.UNPROCESSABLE_ENTITY && response.errorBody() != null) {
                                    val errorBody = Gson().fromJson(
                                        response.errorBody()!!.string(),
                                        ErrorBody::class.java
                                    )
                                    value =
                                        SigninResponse(null, null, errorBody.errors?.get(0)?.msg)
                                } else if (response.code() == AppConstants.UNAUTHORIZED) {
                                    value = SigninResponse(
                                        null,
                                        null,
                                        "Please enter correct username and password."
                                    )
                                } else {
                                    value = SigninResponse(
                                        null,
                                        null,
                                        "Unexpected error occurred. Please try again later."
                                    )
                                }
                                job.complete()
                            }
                        }
                    }
                }
            }
        }
    }

    fun setFCMToken(fcmToken: FCMToken?): LiveData<PostResponse> {
        val job: CompletableJob = Job()
        return object : MutableLiveData<PostResponse>() {
            override fun onActive() {
                job.let { job ->
                    CoroutineScope(Dispatchers.IO + job).launch {
                        val response = ApiFactory.apiWithToken.setFcmToken(fcmToken!!)
                        withContext(Dispatchers.Main) {
                            if (response.code() == AppConstants.SUCCESS)
                                value = PostResponse(response.body()!!.message, null)
                            else if (response.code() == AppConstants.CONFLICT) {
                                val errorBody = Gson().fromJson(
                                    response.errorBody()!!.string(),
                                    PostResponse::class.java
                                )
                                value = PostResponse("", errorBody.message)
                            } else if (response.code() == AppConstants.UNPROCESSABLE_ENTITY && response.errorBody() != null) {
                                val errorBody = Gson().fromJson(
                                    response.errorBody()!!.string(),
                                    ErrorBody::class.java
                                )
                                value = PostResponse("", errorBody.errors?.get(0)?.msg)
                            } else {
                                value = PostResponse(
                                    "",
                                    "Unexpected error occurred. Please try again later."
                                )
                            }
                            job.complete()
                        }
                    }
                }
            }
        }
    }
}
