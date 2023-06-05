package com.android.homemadeEATS.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.android.homemadeEATS.databinding.CookOrderDetailItemBinding
import com.android.homemadeEATS.model.common.MealDetail
import com.android.homemadeEATS.viewHolder.CookOrderDetailViewHolder


class OrderDetailAdapter :
    ListAdapter<MealDetail, CookOrderDetailViewHolder>(CookOrderDetailCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CookOrderDetailViewHolder {
        return CookOrderDetailViewHolder(
            CookOrderDetailItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CookOrderDetailViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

private class CookOrderDetailCallback : DiffUtil.ItemCallback<MealDetail>() {

    override fun areItemsTheSame(
        oldItem: MealDetail,
        newItem: MealDetail
    ): Boolean {
        return oldItem.equals(newItem)
    }

    override fun areContentsTheSame(
        oldItem: MealDetail,
        newItem: MealDetail
    ): Boolean {
        return oldItem.equals(newItem)
    }
}
