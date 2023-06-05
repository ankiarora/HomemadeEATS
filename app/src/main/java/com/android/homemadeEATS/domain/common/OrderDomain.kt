package com.android.homemadeEATS.domain.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.homemadeEATS.model.common.PostResponse
import com.android.homemadeEATS.model.cook.OrderId
import com.android.homemadeEATS.model.cook.CookNewOrdersGet
import com.android.homemadeEATS.repository.common.OrderRepository
import com.android.homemadeEATS.views.fragment.cook.CookPreparedOrdersFragment

open class OrderDomain {
    private val PENDING = "PENDING"
    private val BOOKED = "BOOKED"
    private val PREPARED = "PREPARED"
    private val PAST = "PAST"

    lateinit var result: MutableLiveData<CookNewOrdersGet>

    fun getPendingOrders(): MutableLiveData<CookNewOrdersGet> {
        result = OrderRepository.getOrderList(PENDING)
        return result
    }

    fun getBookedOrders(): MutableLiveData<CookNewOrdersGet> {
        result = OrderRepository.getOrderList(BOOKED)
        return result
    }

    fun getPreparedOrders(): MutableLiveData<CookNewOrdersGet> {
        result = OrderRepository.getOrderList(PREPARED)
        return result
    }

    fun acceptRejectOrder(
        orderId: OrderId,
        orderAccepted: Boolean
    ): LiveData<PostResponse> {
        return OrderRepository.acceptRejectOrder(orderId, orderAccepted)
    }

    fun orderPrepared(orderId: OrderId): LiveData<PostResponse> {
        return OrderRepository.orderPrepared(orderId)
    }

    fun verifyOtp(otpVerification: CookPreparedOrdersFragment.OtpVerification): LiveData<PostResponse> {
        return OrderRepository.verifyOtp(otpVerification)
    }

    fun getPastOrders(): MutableLiveData<CookNewOrdersGet> {
        return OrderRepository.getOrderList(PAST)
    }

}