package com.android.homemadeEATS.repository.common

import androidx.lifecycle.MutableLiveData
import com.android.homemadeEATS.AppConstants
import com.android.homemadeEATS.model.common.Address
import com.android.homemadeEATS.model.common.AddressResponseGet
import com.android.homemadeEATS.model.common.ErrorBody
import com.android.homemadeEATS.model.common.PostResponse
import com.android.homemadeEATS.network.ApiFactory
import com.google.gson.Gson
import kotlinx.coroutines.*

object AddressRepository {
    var addressList: List<Address>? = ArrayList()
    fun setAddress(request: Address): MutableLiveData<PostResponse> {
        val job: CompletableJob = Job()
        return object : MutableLiveData<PostResponse>() {
            override fun onActive() {
                job.let { job ->
                    CoroutineScope(Dispatchers.IO + job).launch {
                        val response = ApiFactory.apiWithToken.setAddress(request)
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

    fun getAddresses(): MutableLiveData<AddressResponseGet> {
        val job: CompletableJob = Job()
        return object : MutableLiveData<AddressResponseGet>() {
            override fun onActive() {
                job.let { job ->
                    CoroutineScope(Dispatchers.IO + job).launch {
                        val response = ApiFactory.apiWithToken.getAddress()
                        withContext(Dispatchers.Main) {
                            if (response.code() == AppConstants.SUCCESS) {
                                addressList = response.body()
                                value = AddressResponseGet(addressList, "")
                            } else if (response.code() == AppConstants.UNAUTHORIZED) {
                                val errorBody = Gson().fromJson(
                                    response.errorBody()!!.string(),
                                    ErrorBody::class.java
                                )
                                value = AddressResponseGet(emptyList(), errorBody.errors?.get(0)?.msg)
                            } else {
                                value = AddressResponseGet(
                                    emptyList(),
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