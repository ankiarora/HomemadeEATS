package com.android.homemadeEATS.viewHolder

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.android.homemadeEATS.databinding.OrdersListItemBinding
import com.android.homemadeEATS.model.cook.CookNewOrder
import com.android.homemadeEATS.views.fragment.common.PastOrdersFragment
import com.android.homemadeEATS.views.fragment.cook.CookBookedOrdersFragment
import com.android.homemadeEATS.views.fragment.cook.CookPendingOrdersFragment

class CookOrderViewHolder(
    private val binding: OrdersListItemBinding,
    val listener: OnItemClickListenerViewHolder
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: CookNewOrder?, orderType: String?) {
        binding.apply {
            order = item
            executePendingBindings()
        }
        when {
            orderType.equals(CookPendingOrdersFragment::class.simpleName) -> {
                binding.acceptRejectLayout.visibility = View.VISIBLE
                binding.orderPrepared.visibility = View.GONE;
                binding.tvEnterOtp.visibility = View.GONE
            }
            orderType.equals(CookBookedOrdersFragment::class.simpleName) -> {
                binding.acceptRejectLayout.visibility = View.GONE
                binding.orderPrepared.visibility = View.VISIBLE;
                binding.tvEnterOtp.visibility = View.GONE
            }
            orderType.equals(PastOrdersFragment::class.simpleName) -> {
                binding.acceptRejectLayout.visibility = View.GONE
                binding.orderPrepared.visibility = View.GONE;
                binding.tvEnterOtp.visibility = View.GONE
                binding.llContact.visibility = View.GONE
                binding.tvAmountPaid.visibility = View.GONE
            }
            else -> {
                binding.acceptRejectLayout.visibility = View.GONE
                binding.orderPrepared.visibility = View.GONE;
                binding.tvEnterOtp.visibility = View.VISIBLE
            }
        }


        binding.cardViewOrderItem.setOnClickListener {
            listener.openMealDetailsDialog(item)
        }

        binding.orderPrepared.setOnClickListener {
            listener.orderPrepared(item?.orderId)
        }

        binding.acceptOrder.setOnClickListener {
            listener.acceptRejectOrder(item, true)
        }

        binding.rejectOrder.setOnClickListener {
            listener.acceptRejectOrder(item, false)
        }

        binding.tvEnterOtp.setOnClickListener {
            if (binding.llLayout.isVisible) {
                binding.llLayout.visibility = View.GONE
            } else {
                binding.llLayout.visibility = View.VISIBLE
            }
        }

        binding.submitOtp.setOnClickListener {
            listener.orderReceived(item?.orderId, binding.cookOtp.text.toString())
        }

        binding.llContact.setOnClickListener {
            listener.callCustomer(item?.customerPhoneNumber)
        }

    }

    interface OnItemClickListenerViewHolder {
        fun openMealDetailsDialog(item: CookNewOrder?)
        fun acceptRejectOrder(item: CookNewOrder?, orderAccept: Boolean)
        fun orderPrepared(orderId: String?)
        fun orderReceived(orderId: String?, otp: String)
        fun callCustomer(customerPhoneNumber: String?)
    }
}