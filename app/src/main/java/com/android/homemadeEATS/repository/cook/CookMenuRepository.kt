package com.android.homemadeEATS.repository.cook

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.homemadeEATS.AppConstants
import com.android.homemadeEATS.model.common.ErrorBody
import com.android.homemadeEATS.model.common.PostResponse
import com.android.homemadeEATS.network.ApiFactory
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.MultipartBody

object CookMenuRepository {
    fun setFoodMenu(
        mealType: MultipartBody.Part,
        meals: MultipartBody.Part,
        price: MultipartBody.Part,
        images: ArrayList<MultipartBody.Part?>
    ): MutableLiveData<PostResponse> {
        val job: CompletableJob = Job()
        return object : MutableLiveData<PostResponse>() {
            override fun onActive() {
                super.onActive()
                job.let { job ->
                    CoroutineScope(Dispatchers.IO + job).launch {
                        val response =
                            ApiFactory.apiWithToken.setFoodMenu(mealType, meals, price, images)
                        withContext(Dispatchers.Main) {
                            Log.d("TAG", "response: $response")
                            if (response.code() == AppConstants.SUCCESS) {
                                value = Gson().fromJson(
                                    response.body()!!.string(),
                                    PostResponse::class.java
                                )
                            } else if (response.code() == AppConstants.CONFLICT) {
                                val errorBody = Gson().fromJson(
                                    response.errorBody()!!.string(),
                                    PostResponse::class.java
                                )
                                value = PostResponse(null, errorBody.message)
                            } else if (response.code() == AppConstants.UNPROCESSABLE_ENTITY && response.errorBody() != null) {
                                val errorBody = Gson().fromJson(
                                    response.errorBody()!!.string(),
                                    ErrorBody::class.java
                                )
                                value = PostResponse(null, errorBody.errors?.get(0)?.msg)
                            } else {
                                value = PostResponse(
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