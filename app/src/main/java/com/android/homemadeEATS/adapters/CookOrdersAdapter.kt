package com.android.homemadeEATS.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.android.homemadeEATS.databinding.OrdersListItemBinding
import com.android.homemadeEATS.model.cook.CookNewOrder
import com.android.homemadeEATS.viewHolder.CookOrderViewHolder

class CookOrdersAdapter(
    private val context: Context?,
    private val orderType: String?,
    val onItemClickListener: OnItemClickListenerAdaper
) :
    ListAdapter<CookNewOrder, CookOrderViewHolder>(ResourceAllocateDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CookOrderViewHolder {
        return CookOrderViewHolder(
            OrdersListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            obj
        )
    }

    var obj = object : CookOrderViewHolder.OnItemClickListenerViewHolder {
        override fun openMealDetailsDialog(item: CookNewOrder?) {
            onItemClickListener.openMealDetails(item)
        }

        override fun acceptRejectOrder(item: CookNewOrder?, orderAccepted: Boolean) {
            onItemClickListener.acceptRejectOrder(item, orderAccepted)
        }

        override fun orderPrepared(orderId: String?) {
            onItemClickListener.orderPrepared(orderId)
        }

        override fun orderReceived(orderId: String?, otp: String) {
            onItemClickListener.orderReceived(orderId, otp)
        }

        override fun callCustomer(customerPhoneNumber: String?) {
            onItemClickListener.callCustomer(customerPhoneNumber)
        }
    }

    override fun onBindViewHolder(holderCook: CookOrderViewHolder, position: Int) {
        holderCook.bind(getItem(position), orderType)
    }

    interface OnItemClickListenerAdaper {
        fun openMealDetails(mealDetails: CookNewOrder?)
        fun acceptRejectOrder(item: CookNewOrder?, orderAccepted: Boolean)
        fun orderPrepared(orderId: String?)
        fun orderReceived(orderId: String?, otp: String)
        fun callCustomer(customerPhoneNumber: String?)
    }
}

class ResourceAllocateDiffCallback : DiffUtil.ItemCallback<CookNewOrder>() {

    override fun areItemsTheSame(
        oldItem: CookNewOrder,
        newItem: CookNewOrder
    ): Boolean {
        return oldItem.orderId == newItem.orderId
    }

    override fun areContentsTheSame(
        oldItem: CookNewOrder,
        newItem: CookNewOrder
    ): Boolean {
        return oldItem == newItem
    }
}