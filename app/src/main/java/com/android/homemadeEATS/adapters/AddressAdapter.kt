package com.android.homemadeEATS.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.android.homemadeEATS.databinding.SelectAddressItemBinding
import com.android.homemadeEATS.model.common.Address
import com.android.homemadeEATS.viewHolder.AddressViewHolder

class AddressAdapter(val onItemClickListener: OnItemClickListener) :
    ListAdapter<Address, AddressViewHolder>(AddressCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        return AddressViewHolder(
            SelectAddressItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            obj
        )
    }

    var obj = object : AddressViewHolder.OnItemClickListener {
        override fun openResourceDetails(address: Address?) {
            onItemClickListener.openResourceDetails(address)
        }

    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    interface OnItemClickListener {
        fun openResourceDetails(address: Address?)
    }
}

private class AddressCallback : DiffUtil.ItemCallback<Address>() {

    override fun areItemsTheSame(
        oldItem: Address,
        newItem: Address
    ): Boolean {
        return oldItem.equals(newItem)
    }

    override fun areContentsTheSame(
        oldItem: Address,
        newItem: Address
    ): Boolean {
        return oldItem.equals(newItem)
    }
}