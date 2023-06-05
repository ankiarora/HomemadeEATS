package com.android.homemadeEATS.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.android.homemadeEATS.databinding.CustomerOrderNewItemBinding
import com.android.homemadeEATS.model.customer.CustomerMeal
import com.android.homemadeEATS.viewHolder.CustomerMealViewHolder

class CustomerMealsAdapter(val listener: OnItemClickListener) :
    ListAdapter<CustomerMeal, CustomerMealViewHolder>(CustomerMealCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerMealViewHolder {
        return CustomerMealViewHolder(
            CustomerOrderNewItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    private val obj = object : CustomerMealViewHolder.OnItemClickListener {
        override fun openImages(imagesList: ArrayList<Uri>) {
            listener.openImages(imagesList)
        }

        override fun updateCartItems(item: CustomerMeal) {
            listener.updateCartItems(item)
        }

    }

    override fun onBindViewHolder(holder: CustomerMealViewHolder, position: Int) {
        holder.bind(getItem(position), obj)
    }

    interface OnItemClickListener {
        fun openImages(imagesList: ArrayList<Uri>)
        fun updateCartItems(item: CustomerMeal)
    }
}


private class CustomerMealCallback : DiffUtil.ItemCallback<CustomerMeal>() {

    override fun areItemsTheSame(
        oldItem: CustomerMeal,
        newItem: CustomerMeal
    ): Boolean {
        return oldItem.equals(newItem)
    }

    override fun areContentsTheSame(
        oldItem: CustomerMeal,
        newItem: CustomerMeal
    ): Boolean {
        return oldItem.equals(newItem)
    }
}