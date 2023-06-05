package com.android.homemadeEATS.viewHolder

import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.android.homemadeEATS.AppConstants
import com.android.homemadeEATS.databinding.CustomerOrderNewItemBinding
import com.android.homemadeEATS.model.customer.CustomerMeal
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel


class CustomerMealViewHolder(val binding: CustomerOrderNewItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: CustomerMeal?, listener: OnItemClickListener) {
        binding.apply {
            customerMeal = item
            executePendingBindings()
        }

        if (item?.images?.isEmpty() == false) {
            binding.imageSlider.visibility = View.VISIBLE
        } else {
            binding.imageSlider.visibility = View.GONE
        }

        val imagesList: ArrayList<Uri> = ArrayList()
        for (i in item?.images!!) {
            val imageUrl =
                AppConstants.BASE_URL + "/image?name=" + item.images[item.images.indexOf(i)]
            imagesList.add(Uri.parse(imageUrl))
        }

        val imageListSlideModel = ArrayList<SlideModel>()
        if (!imagesList.isNullOrEmpty()) {
            for (i in imagesList) {
                imageListSlideModel.add(SlideModel(i.toString(), ScaleTypes.CENTER_CROP))
            }
        }

        binding.imageSlider.setImageList(imageListSlideModel)
        binding.totalQty.text = item.totalCartItems.toString()
        binding.increaseQty.setOnClickListener {
            val totalQty = Integer.parseInt(binding.totalQty.text.toString())
            if (totalQty < 100) {
                binding.totalQty.text = (totalQty + 1).toString()
                item.totalCartItems = totalQty + 1
            }
            listener.updateCartItems(item)
        }

        binding.decreaseQty.setOnClickListener {
            val totalQty = Integer.parseInt(binding.totalQty.text.toString())
            if (totalQty > 0) {
                binding.totalQty.text = (totalQty - 1).toString()
                item.totalCartItems = totalQty - 1
            }
            listener.updateCartItems(item)
        }
    }

    interface OnItemClickListener {
        fun openImages(imagesList: ArrayList<Uri>)
        fun updateCartItems(item: CustomerMeal)
    }
}
