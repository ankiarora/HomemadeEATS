package com.android.homemadeEATS.repository.customer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.homemadeEATS.AppConstants
import com.android.homemadeEATS.model.common.ErrorBody
import com.android.homemadeEATS.model.common.PostResponse
import com.android.homemadeEATS.model.customer.*
import com.android.homemadeEATS.network.ApiFactory
import com.google.gson.Gson
import kotlinx.coroutines.*
import retrofit2.Response

object CustomerMealsRepository {
    var cartItems: ArrayList<CustomerMeal> = ArrayList()
    var currentItemList: List<CustomerMeal> = ArrayList()

    fun getAllMeals(skip: Int, search: String): MutableLiveData<CustomerMealGet> {
        val job: CompletableJob = Job()
        return object : MutableLiveData<CustomerMealGet>() {
            override fun onActive() {
                job.let { job ->
                    CoroutineScope(Dispatchers.IO + job).launch {
                        val response =
                            ApiFactory.apiWithToken.getCustomerAllMeals(search, skip, 5)
                        withContext(Dispatchers.Main) {
                            value = when {
                                response.code() == AppConstants.SUCCESS -> {
                                    currentItemList = response.body()!!
                                    CustomerMealGet(response.body(), "")
                                }
                                response.code() == AppConstants.UNAUTHORIZED -> {
                                    val errorBody = Gson().fromJson(
                                        response.errorBody()!!.string(),
                                        ErrorBody::class.java
                                    )
                                    CustomerMealGet(emptyList(), errorBody.errors?.get(0)?.msg)
                                }
                                else -> {
                                    CustomerMealGet(
                                        emptyList(),
                                        "Unexpected error occurred. Please try again later."
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun customerOrderItems(customerOrderMeal: CustomerOrderMeal): LiveData<PostResponse> {
        val job: CompletableJob = Job()
        return object : MutableLiveData<PostResponse>() {
            override fun onActive() {
                job.let { job ->
                    CoroutineScope(Dispatchers.IO + job).launch {
                        val response = ApiFactory.apiWithToken.customerOrderItems(customerOrderMeal)
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

    fun getBookedOrders(): MutableLiveData<CustomerBookedMealGet> {
        val job: CompletableJob = Job()
        return object : MutableLiveData<CustomerBookedMealGet>() {
            private lateinit var response: Response<List<CustomerBookedMeal>>

            override fun onActive() {
                job.let { job ->
                    CoroutineScope(Dispatchers.IO + job).launch {
                        response = ApiFactory.apiWithToken.getCustomerBookedOrders()
                        withContext(Dispatchers.Main) {
                            if (response.code() == AppConstants.SUCCESS) {
                                value = CustomerBookedMealGet(response.body(), "")
                            } else if (response.code() == AppConstants.UNAUTHORIZED) {
                                val errorBody = Gson().fromJson(
                                    response.errorBody()!!.string(),
                                    ErrorBody::class.java
                                )
                                value = CustomerBookedMealGet(
                                    emptyList(),
                                    errorBody.errors?.get(0)?.msg
                                )
                            } else {
                                value = CustomerBookedMealGet(
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