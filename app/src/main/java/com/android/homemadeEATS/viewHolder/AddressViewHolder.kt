package com.android.homemadeEATS.viewHolder

import androidx.recyclerview.widget.RecyclerView
import com.android.homemadeEATS.databinding.SelectAddressItemBinding
import com.android.homemadeEATS.model.common.Address

class AddressViewHolder(
    private val binding: SelectAddressItemBinding,
    private val onItemClickListener: OnItemClickListener
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Address?) {
        binding.apply {
            address = item
            executePendingBindings()
        }
        binding.llAddressItem.setOnClickListener {
            onItemClickListener.openResourceDetails(item)
        }
    }

    interface OnItemClickListener {
        fun openResourceDetails(address: Address?)
    }
}