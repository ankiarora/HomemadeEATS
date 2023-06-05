package com.android.homemadeEATS.views.fragment.common

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.android.homemadeEATS.R
import com.android.homemadeEATS.databinding.FragmentImageDisplayBinding
import com.android.homemadeEATS.viewmodel.common.ImageDisplayViewModel
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel

class ImageDisplayFragment : DialogFragment() {
    lateinit var viewModel: ImageDisplayViewModel
    lateinit var activity: Context
    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.activity = context
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        viewModel = ViewModelProvider(this).get(ImageDisplayViewModel::class.java)
        val binding = DataBindingUtil.inflate<FragmentImageDisplayBinding>(
            requireActivity().layoutInflater,
            R.layout.fragment_image_display,
            null,
            false
        ).apply {
            this.viewModel = this@ImageDisplayFragment.viewModel
        }

        val imageSlider: ImageSlider? = binding.root.findViewById(R.id.imageSlider)
        val images: ArrayList<Uri> = arguments?.getSerializable("images") as ArrayList<Uri>

        val imageList = ArrayList<SlideModel>()
        if(!images.isNullOrEmpty()) {
            for (i in images) {
                imageList.add(SlideModel(i.toString(), ""))
            }
        }

        imageSlider?.setImageList(imageList)
        builder.setNegativeButton("close") { dialog, _ ->
            dismiss()
        }
        builder.setCancelable(false);
        builder.setView(binding.root)
        return builder.create()
    }
}
