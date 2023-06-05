package com.android.homemadeEATS.views.fragment.common

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.android.homemadeEATS.R
import com.android.homemadeEATS.viewmodel.common.BaseViewModel

open class BaseFragment : Fragment() {
    private lateinit var progress: ProgressDialog
    protected lateinit var viewModel: BaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_base, container, false)
    }

    fun showProgressBar() {
        progress = ProgressDialog(context)
        progress.setTitle("Loading")
        progress.setMessage("Wait while loading...")
        progress.setCancelable(false) // disable dismiss by tapping outside of the dialog
        progress.show()
    }

    fun hideProgressBar() {
        progress.hide()
    }
}