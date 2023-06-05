package com.android.homemadeEATS.views.fragment.common

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.android.homemadeEATS.viewmodel.common.BaseViewModel

open class BaseDialogFragment : DialogFragment() {
    protected lateinit var viewModel: BaseViewModel

    companion object {
        fun newInstance() = BaseFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
}