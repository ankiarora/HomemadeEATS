package com.android.homemadeEATS.model.cook

import com.android.homemadeEATS.model.common.Address
import com.android.homemadeEATS.model.common.MealDetail

data class CookNewOrder(
    val mealDetails: List<MealDetail>,
    val cookId: String,
    val foodPrice: Int,
    val deliveryPrice: Int,
    val taxPrice: Int,
    val totalPrice: Int,
    val orderTime: String,
    val customerFirstName: String,
    val customerLastName: String,
    val customerId: String,
    val orderId: String,
    val isBillPaid: Boolean,
    val customerPhoneNumber: String,
    val cookAddress: Address,
    val customerAddress: Address
) {
    fun getTotalMealItems(): String {
        return "Total meal Items: ${mealDetails.size}"
    }

    fun getPricePayInfo(): String {
        if (isBillPaid) {
            return "Prepaid Order."
        } else {
            return "Payment Pending."
        }
    }

    fun distance(): String {
        val lat1 = customerAddress.latitude!!.toDouble()
        val lon1 = customerAddress.longitude!!.toDouble()

        val lat2 = cookAddress.latitude!!.toDouble()
        val lon2 = cookAddress.longitude!!.toDouble()
        val theta = lon1 - lon2
        var dist = (Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
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

    fun getAddress(): String {
        val s: String =
            cookAddress.address + ", " + cookAddress.streetOrBuildingName + ", " + cookAddress.city + ", " + cookAddress.state +
                    ", " + cookAddress.landmark
        return s
    }
}
