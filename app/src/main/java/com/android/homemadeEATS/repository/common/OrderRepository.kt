package com.android.homemadeEATS.repository.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.homemadeEATS.AppConstants
import com.android.homemadeEATS.model.common.ErrorBody
import com.android.homemadeEATS.model.common.PostResponse
import com.android.homemadeEATS.model.cook.OrderId
import com.android.homemadeEATS.model.cook.CookNewOrder
import com.android.homemadeEATS.model.cook.CookNewOrdersGet
import com.android.homemadeEATS.network.ApiFactory
import com.android.homemadeEATS.views.fragment.cook.CookPreparedOrdersFragment
import com.google.gson.Gson
import kotlinx.coroutines.*
import retrofit2.Response

object OrderRepository {
    var job: CompletableJob? = null
    private val PENDING = "PENDING"
    private val BOOKED = "BOOKED"
    private val PREPARED = "PREPARED"
    private val PAST = "PAST"

    fun getOrderList(orderListType: String): MutableLiveData<CookNewOrdersGet> {
        val job: CompletableJob = Job()
        return object : MutableLiveData<CookNewOrdersGet>() {
            private lateinit var response: Response<List<CookNewOrder>>

            override fun onActive() {
                job.let { job ->
                    CoroutineScope(Dispatchers.IO + job).launch {
                        when (orderListType) {
                            PENDING -> response = ApiFactory.apiWithToken.getCookPendingOrders()
                            BOOKED -> response = ApiFactory.apiWithToken.getCookBookedOrders()
                            PREPARED -> response = ApiFactory.apiWithToken.getCookPreparedOrders()
                            PAST -> response = ApiFactory.apiWithToken.getCookPastOrders()
                        }
                        withContext(Dispatchers.Main) {
                            if (response.code() == AppConstants.SUCCESS) {
                                value = CookNewOrdersGet(response.body(), "")
                            } else if (response.code() == AppConstants.UNAUTHORIZED) {
                                val errorBody = Gson().fromJson(
                                    response.errorBody()!!.string(),
                                    ErrorBody::class.java
                                )
                                value = CookNewOrdersGet(emptyList(), errorBody.errors?.get(0)?.msg)
                            } else {
                                value = CookNewOrdersGet(
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

    fun acceptRejectOrder(
        orderId: OrderId,
        orderAccepted: Boolean
    ): LiveData<PostResponse> {
        val job: CompletableJob = Job()
        return object : MutableLiveData<PostResponse>() {
            private lateinit var response: Response<PostResponse>

            override fun onActive() {
                job.let { job ->
                    CoroutineScope(Dispatchers.IO + job).launch {
                        if (orderAccepted)
                            response = ApiFactory.apiWithToken.cookAcceptOrder(orderId)
                        else
                            response = ApiFactory.apiWithToken.cookRejectOrder(orderId)

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

    fun orderPrepared(
        orderId: OrderId,
    ): LiveData<PostResponse> {
        val job: CompletableJob = Job()
        return object : MutableLiveData<PostResponse>() {
            private lateinit var response: Response<PostResponse>

            override fun onActive() {
                job.let { job ->
                    CoroutineScope(Dispatchers.IO + job).launch {
                        response = ApiFactory.apiWithToken.orderPrepared(orderId)

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

    fun verifyOtp(otpVerification: CookPreparedOrdersFragment.OtpVerification): LiveData<PostResponse> {
        val job: CompletableJob = Job()
        return object : MutableLiveData<PostResponse>() {
            private lateinit var response: Response<PostResponse>

            override fun onActive() {
                job.let { job ->
                    CoroutineScope(Dispatchers.IO + job).launch {
                        response = ApiFactory.apiWithToken.verifyOtp(otpVerification)

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