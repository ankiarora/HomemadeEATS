package com.android.homemadeEATS.viewmodel.common

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.homemadeEATS.domain.common.OrderDomain
import com.android.homemadeEATS.model.common.PostResponse
import com.android.homemadeEATS.model.cook.OrderId
import com.android.homemadeEATS.model.cook.CookNewOrdersGet
import com.android.homemadeEATS.views.fragment.cook.CookPreparedOrdersFragment

open class OrdersViewModel(application: Application) : BaseViewModel(application) {

    private var ordersDomain: OrderDomain = OrderDomain()

    fun pendingOrders(): MutableLiveData<CookNewOrdersGet> {
        return ordersDomain.getPendingOrders()
    }

    fun pastOrders(): MutableLiveData<CookNewOrdersGet> {
        return ordersDomain.getPastOrders()
    }

    fun bookedOrders(): MutableLiveData<CookNewOrdersGet> {
        return ordersDomain.getBookedOrders()
    }

    fun preparedOrders(): MutableLiveData<CookNewOrdersGet> {
        return ordersDomain.getPreparedOrders()
    }

    fun acceptRejectOrder(
        orderId: OrderId,
        orderAccepted: Boolean
    ): LiveData<PostResponse> {
        return ordersDomain.acceptRejectOrder(orderId, orderAccepted)
    }

    fun orderPrepared(orderId: OrderId): LiveData<PostResponse> {
        return ordersDomain.orderPrepared(orderId)
    }

    fun verifyOtp(otpVerification: CookPreparedOrdersFragment.OtpVerification): LiveData<PostResponse> {
        return ordersDomain.verifyOtp(otpVerification)
    }


}