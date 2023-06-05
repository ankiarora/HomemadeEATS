package com.android.homemadeEATS.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.android.homemadeEATS.databinding.CustomerBookedMealItemBinding
import com.android.homemadeEATS.model.customer.CustomerBookedMeal
import com.android.homemadeEATS.viewHolder.CustomerBookedMealViewHolder

class CustomerBookedMealItemsAdapter(val onItemClickListener: OnItemClickListener) :
    ListAdapter<CustomerBookedMeal, CustomerBookedMealViewHolder>(CustomerBookedMealCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomerBookedMealViewHolder {
        return CustomerBookedMealViewHolder(
            CustomerBookedMealItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    private val obj = object : CustomerBookedMealViewHolder.OnItemClickListener {
        override fun openMealDetailsDialog(item: CustomerBookedMeal?) {
            onItemClickListener.openMealDetails(item)
        }

        override fun refreshCustomerItemList() {
            onItemClickListener.refreshCustomerItemList()
        }

        override fun callCook(cookPhoneNumber: String?) {
            onItemClickListener.callCook(cookPhoneNumber)
        }
    }

    override fun onBindViewHolder(holder: CustomerBookedMealViewHolder, position: Int) {
        holder.bind(getItem(position), obj)
    }

    interface OnItemClickListener {
        fun openMealDetails(item: CustomerBookedMeal?)
        fun refreshCustomerItemList()
        fun callCook(cookPhoneNumber: String?)
    }
}


private class CustomerBookedMealCallback : DiffUtil.ItemCallback<CustomerBookedMeal>() {

    override fun areItemsTheSame(
        oldItem: CustomerBookedMeal,
        newItem: CustomerBookedMeal
    ): Boolean {
        return oldItem.equals(newItem)
    }

    override fun areContentsTheSame(
        oldItem: CustomerBookedMeal,
        newItem: CustomerBookedMeal
    ): Boolean {
        return oldItem.equals(newItem)
    }

}