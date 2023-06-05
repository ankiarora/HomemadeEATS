package com.android.homemadeEATS.views.fragment.common

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.homemadeEATS.R
import com.android.homemadeEATS.adapters.OrderDetailAdapter
import com.android.homemadeEATS.databinding.FragmentCookExpandOrderBinding
import com.android.homemadeEATS.model.common.MealDetail
import com.android.homemadeEATS.viewmodel.common.OrdersViewModel
import kotlinx.android.synthetic.main.fragment_cook_expand_order.*

class ExpandOrderFragment(
    val mealDetails: List<MealDetail>?
) : BaseFragment() {
    private lateinit var activity: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.activity = context;
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(this).get(OrdersViewModel::class.java)
        val binding = DataBindingUtil.inflate<FragmentCookExpandOrderBinding>(
            inflater,
            R.layout.fragment_cook_expand_order,
            container,
            false
        ).apply {
            viewModel = this@ExpandOrderFragment.viewModel as OrdersViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = OrderDetailAdapter()
        rvOrderDetails.adapter = adapter
        val viewManager = LinearLayoutManager(activity)
        rvOrderDetails.apply { layoutManager = viewManager }
        if (mealDetails?.isEmpty() == false)
            adapter.submitList(mealDetails)

    }
}