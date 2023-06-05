package com.android.homemadeEATS.model.customer

import com.android.homemadeEATS.model.common.Meal
import com.android.homemadeEATS.model.common.Address
import java.util.*
import kotlin.math.sin

data class CustomerMeal(
    val _id: String,
    val images: List<String>?,
    val dishes: List<Meal>?,
    val price: Int?,
    val mealType: String?,
    val cookId: String?,
    val cookFirstName: String?,
    val cookLastName: String?,
    var totalCartItems: Int = 0,
    val cookAddress: Address,
    val customerAddress: Address
) {
    fun getDishesString(): String {
        var dishString = "";
        for (dish: Int in 0 until dishes?.size!! - 1) {
            dishString = dishString + dishes[dish].name.toString()
                .toUpperCase(Locale.ROOT) + " : " + dishes[dish].description + "\n";
        }
        dishString =
            dishString + dishes[dishes.size - 1].name.toString()
                .toUpperCase(Locale.ROOT) + " : " + dishes[dishes.size - 1].description;
        return dishString
    }

    fun distance(): String {
        val lat1 = customerAddress.latitude!!.toDouble()
        val lon1 = customerAddress.longitude!!.toDouble()

        val lat2 = cookAddress.latitude!!.toDouble()
        val lon2 = cookAddress.longitude!!.toDouble()
        val theta = lon1 - lon2
        var dist = (sin(deg2rad(lat1))
                * sin(deg2rad(lat2))
                + (Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta))))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist *= 60 * 1.1515
        return "Distance: " + String.format("%.2f", dist) + " miles"
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }
}