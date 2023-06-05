package com.android.homemadeEATS.viewHolder

import android.graphics.Typeface
import android.net.Uri
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.homemadeEATS.AppConstants
import com.android.homemadeEATS.R
import com.android.homemadeEATS.databinding.CookOrderDetailItemBinding
import com.android.homemadeEATS.model.common.MealDetail
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel


class CookOrderDetailViewHolder(private val binding: CookOrderDetailItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: MealDetail?) {
        binding.apply {
            mealDetail = item
            executePendingBindings()
        }

        setImages(item?.images)
        val context = binding.root.context
        val tl = binding.tlDishItems
        val param = TableRow.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            1.0f
        )

        var tr = TableRow(context)
        var tvName = TextView(context)
        tvName.layoutParams = param
        tvName.textSize = 19.0f
        tvName.text = "DISH"
        tvName.typeface = Typeface.DEFAULT_BOLD
        tvName.gravity = Gravity.CENTER
        tvName.background =
            ContextCompat.getDrawable(context, R.drawable.black_border_rounder_corner)
        tr.addView(tvName)

        var tvDescription = TextView(context)
        tvDescription.textSize = 19.0f
        tvDescription.text = "DESCRIPTION"
        tvDescription.typeface = Typeface.DEFAULT_BOLD
        tvDescription.gravity = Gravity.CENTER
        tr.addView(tvDescription)
        tvDescription.layoutParams = param
        tvDescription.background =
            ContextCompat.getDrawable(context, R.drawable.black_border_rounder_corner)

        tr.setBackgroundColor(ContextCompat.getColor(context, R.color.lemon))
        tl.addView(tr)

        for (dish in item?.dishes!!) {

            tr = TableRow(context)
            tvName = TextView(context)

            tvName.layoutParams = param
            tvName.textSize = 17.0f
            tvName.text = dish.name
            tvName.gravity = Gravity.CENTER
            tvName.background =
                ContextCompat.getDrawable(context, R.drawable.black_border_rounder_corner)
            tr.addView(tvName)

            tvDescription = TextView(context)
            tvDescription.textSize = 17.0f
            tvDescription.text = dish.description
            tr.addView(tvDescription)
            tvDescription.gravity = Gravity.CENTER
            tvDescription.layoutParams = param
            tvDescription.background =
                ContextCompat.getDrawable(context, R.drawable.black_border_rounder_corner)
            tl.addView(tr)
        }
    }

    private fun setImages(images: List<String>?) {
        if (images?.isEmpty() == false) {
            binding.imageSlider.visibility = View.VISIBLE
        } else {
            binding.imageSlider.visibility = View.GONE
        }

        val imagesList: ArrayList<Uri> = ArrayList()
        for (i in images!!) {
            val imageUrl =
                AppConstants.BASE_URL + "/image?name=" + images[images.indexOf(i)]
            imagesList.add(Uri.parse(imageUrl))
        }

        val imageListSlideModel = ArrayList<SlideModel>()
        if (!imagesList.isNullOrEmpty()) {
            for (i in imagesList) {
                imageListSlideModel.add(SlideModel(i.toString(), ScaleTypes.CENTER_CROP))
            }
        }

        binding.imageSlider.setImageList(imageListSlideModel)
    }
}