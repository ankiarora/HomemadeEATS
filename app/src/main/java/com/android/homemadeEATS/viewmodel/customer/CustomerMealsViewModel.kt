package com.android.homemadeEATS.viewmodel.customer

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.homemadeEATS.domain.common.AddressDomain
import com.android.homemadeEATS.domain.customer.CustomerMealsDomain
import com.android.homemadeEATS.model.common.Address
import com.android.homemadeEATS.model.common.PostResponse
import com.android.homemadeEATS.model.customer.*
import com.android.homemadeEATS.viewmodel.common.BaseViewModel

class CustomerMealsViewModel(application: Application) : BaseViewModel(application) {
    private val customerMealDomain = CustomerMealsDomain()
    var totalPrice = MutableLiveData<String>()


    private val addressDomain = AddressDomain()
    private val allAddress by lazy {
        return@lazy addressDomain.getAddresses()
    }

    fun getSelectedLocation(): LiveData<Address?> {
        return addressDomain.getSelectedLocation(allAddress)
    }

    val _selectedLocation = getSelectedLocation()


    fun getAllMeals(skip: Int, search: String): LiveData<CustomerMealGet> {
        return customerMealDomain.getAllCustomerMeals(skip, search)
    }

    fun setCurrentSetCustomerMealsList(
        currentCustomerMealList: List<CustomerMeal>,
        cartItemMealArray: ArrayList<CustomerMeal>
    ) {
        customerMealDomain.setCurrentSetCustomerMealsList(
            currentCustomerMealList,
            cartItemMealArray
        )
    }

    fun customerOrderItems(customerOrderMeal: CustomerOrderMeal): LiveData<PostResponse> {
        return customerMealDomain.customerOrderItems(customerOrderMeal)
    }

    fun bookedOrders(): MutableLiveData<CustomerBookedMealGet> {
        return customerMealDomain.getBookedOrders()
    }

}