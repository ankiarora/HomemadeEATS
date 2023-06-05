package com.android.homemadeEATS.repository.common

import androidx.lifecycle.MutableLiveData
import com.android.homemadeEATS.AppConstants
import com.android.homemadeEATS.model.common.*
import com.android.homemadeEATS.network.ApiFactory
import com.google.gson.Gson
import kotlinx.coroutines.*

object UserProfileRepository {
    var userProfile: GetUserProfile? = null
    fun getUserProfileInfo(): MutableLiveData<UserProfileResponse> {
        val job: CompletableJob = Job()
        return object : MutableLiveData<UserProfileResponse>() {
            override fun onActive() {
                job.let { job ->
                    CoroutineScope(Dispatchers.IO + job).launch {
                        if (userProfile == null) {
                            val response = ApiFactory.apiWithToken.getUserProfile()
                            withContext(Dispatchers.Main) {
                                value = if (response.code() == AppConstants.SUCCESS) {
                                    userProfile = response.body()
                                    UserProfileResponse(response.body(), null)
                                } else if (response.code() == AppConstants.CONFLICT) {
                                    val errorBody = Gson().fromJson(
                                        response.errorBody()!!.string(),
                                        PostResponse::class.java
                                    )
                                    UserProfileResponse(null, errorBody.message)
                                } else if (response.code() == AppConstants.UNPROCESSABLE_ENTITY && response.errorBody() != null) {
                                    val errorBody = Gson().fromJson(
                                        response.errorBody()!!.string(),
                                        ErrorBody::class.java
                                    )
                                    UserProfileResponse(null, errorBody.errors?.get(0)?.msg)
                                } else {
                                    UserProfileResponse(
                                        null,
                                        "Unexpected error occurred. Please try again later."
                                    )
                                }
                                job.complete()
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                value = UserProfileResponse(
                                    userProfile,
                                    ""
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun saveUserProfileInfo(userProfile: PostUserProfile): MutableLiveData<PostResponse> {
        val job: CompletableJob = Job()
        return object : MutableLiveData<PostResponse>() {
            override fun onActive() {
                job.let { job ->
                    CoroutineScope(Dispatchers.IO + job).launch {
                        val response = ApiFactory.apiWithToken.saveUserProfile(userProfile)
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