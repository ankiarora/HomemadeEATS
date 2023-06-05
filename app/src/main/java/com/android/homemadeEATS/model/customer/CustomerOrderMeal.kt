package com.android.homemadeEATS.model.customer

import com.android.homemadeEATS.model.common.MealAmount
import com.android.homemadeEATS.model.common.Address

data class CustomerOrderMeal(
    val mealDetails: List<MealAmount>,
    val foodPrice: Int,
    val deliveryPrice: Int,
    val taxPrice: Int,
    val cookId: String,
    val customerAddress: Address
)