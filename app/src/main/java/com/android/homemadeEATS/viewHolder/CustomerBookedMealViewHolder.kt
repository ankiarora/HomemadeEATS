package com.android.homemadeEATS.viewHolder

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.homemadeEATS.databinding.CustomerBookedMealItemBinding
import com.android.homemadeEATS.model.common.Address
import com.android.homemadeEATS.model.customer.CustomerBookedMeal


class CustomerBookedMealViewHolder(val binding: CustomerBookedMealItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: CustomerBookedMeal?, listener: OnItemClickListener) {
        binding.apply {
            customerMeal = item
            executePendingBindings()
        }
        binding.cvCustomerBookedItem.setOnClickListener {
            listener.openMealDetailsDialog(item)
        }

        binding.customerOtp.setOnClickListener {
            val builder = AlertDialog.Builder(binding.root.context)
            builder
                .setMessage("Your OTP is ${item?.otp}.\n Please provide this OTP to your cook while pick-up.")
                .setPositiveButton("Ok") { dialogInterface: DialogInterface, i: Int ->
                    //make an api call
                    listener.refreshCustomerItemList()
                }
            builder.setCancelable(false);
            builder.show()
        }

        binding.llContact.setOnClickListener {
            listener.callCook(item?.cookPhoneNumber)
        }

        binding.btnDirections.setOnClickListener {
            showMap(item!!.customerAddress, item.cookAddress)
        }


        when {
            item?.isOrderPrepared == true -> {
                binding.customerOtp.visibility = View.VISIBLE
                binding.orderStatus.text = "ORDER PREPARED"
            }
            item?.isOrderConfirmedByCook == true -> {
                binding.customerOtp.visibility = View.GONE
                binding.orderStatus.text = "ORDER CONFIRMED"
            }
            item?.isOrderRejectedByCook == true -> {
                binding.customerOtp.visibility = View.GONE
                binding.orderStatus.text = "ORDER REJECTED"
                binding.orderStatus.setTextColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        android.R.color.holo_red_light
                    )
                )
            }
            else -> {
                binding.customerOtp.visibility = View.GONE
                binding.orderStatus.text = "WAITING..."
            }
        }
    }

    private fun showMap(customerAddress: Address, cookAddress: Address) {
        val lat1 = customerAddress.latitude
        val lon1 = customerAddress.longitude

        val lat2 = cookAddress.latitude
        val lon2 = cookAddress.longitude
        val gmmIntentUri =
            Uri.parse("https://www.google.co.in/maps/dir/$lat1,$lon1/$lat2,$lon2")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        binding.root.context.startActivity(mapIntent)
    }

    interface OnItemClickListener {
        fun openMealDetailsDialog(item: CustomerBookedMeal?)
        fun refreshCustomerItemList()
        fun callCook(cookPhoneNumber: String?)
    }
}
