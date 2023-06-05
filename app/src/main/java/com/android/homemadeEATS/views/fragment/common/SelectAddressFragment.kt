package com.android.homemadeEATS.views.fragment.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.homemadeEATS.R
import com.android.homemadeEATS.adapters.AddressAdapter
import com.android.homemadeEATS.databinding.FragmentSelectAddressBinding
import com.android.homemadeEATS.model.common.Address
import com.android.homemadeEATS.viewmodel.common.AddressViewModel
import com.android.homemadeEATS.views.activity.common.ViewAddressActivity
import kotlinx.android.synthetic.main.fragment_select_address.*

class SelectAddressFragment : BaseFragment() {
    private lateinit var viewManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(AddressViewModel::class.java)
        val binding = DataBindingUtil.inflate<FragmentSelectAddressBinding>(
            inflater,
            R.layout.fragment_select_address,
            container,
            false
        ).apply {
            viewModel = this@SelectAddressFragment.viewModel as AddressViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    private fun subscribeUi(adapter: AddressAdapter) {
        showProgressBar()
        (viewModel as AddressViewModel).getAllAddresses()
            .observe(
                viewLifecycleOwner,
                { activeData ->
                    hideProgressBar()
                    if (activeData.addressList?.isEmpty() == false)
                        adapter.submitList(activeData.addressList)
                    else
                        Toast.makeText(context, activeData.error, Toast.LENGTH_LONG).show()
                })
    }

    private val obj = object : AddressAdapter.OnItemClickListener {
        override fun openResourceDetails(address: Address?) {
            (requireActivity() as ViewAddressActivity).addFragment(
                ViewAddressFragment(
                    false,
                    address
                )
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = AddressAdapter(obj)
        rvSelectOrdersList.adapter = adapter
        viewManager = LinearLayoutManager(activity)
        rvSelectOrdersList.apply { layoutManager = viewManager }
        subscribeUi(adapter)
    }
}