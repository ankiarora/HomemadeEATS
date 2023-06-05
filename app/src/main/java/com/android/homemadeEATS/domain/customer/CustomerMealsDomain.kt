package com.android.homemadeEATS.domain.customer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.homemadeEATS.model.customer.CustomerMeal
import com.android.homemadeEATS.model.customer.CustomerMealGet
import com.android.homemadeEATS.model.customer.CustomerOrderMeal
import com.android.homemadeEATS.model.common.PostResponse
import com.android.homemadeEATS.model.customer.CustomerBookedMealGet
import com.android.homemadeEATS.repository.customer.CustomerMealsRepository

class CustomerMealsDomain {
    fun getAllCustomerMeals(skip: Int, search: String): MutableLiveData<CustomerMealGet> {
        return CustomerMealsRepository.getAllMeals(skip, search)
    }

    fun setCurrentSetCustomerMealsList(
        currentCustomerMealList: List<CustomerMeal>,
        cartItemMealArray: ArrayList<CustomerMeal>
    ) {
        CustomerMealsRepository.currentItemList = currentCustomerMealList
        CustomerMealsRepository.cartItems = cartItemMealArray
    }

    fun customerOrderItems(customerOrderMeal: CustomerOrderMeal): LiveData<PostResponse> {
        return CustomerMealsRepository.customerOrderItems(customerOrderMeal)
    }

    fun getBookedOrders(): MutableLiveData<CustomerBookedMealGet> {
        return CustomerMealsRepository.getBookedOrders()
    }
}