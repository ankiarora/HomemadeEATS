package com.android.homemadeEATS.views.fragment.common

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.android.homemadeEATS.R
import com.android.homemadeEATS.databinding.FragmentAddressBinding
import com.android.homemadeEATS.viewmodel.common.AddressViewModel
import com.android.homemadeEATS.views.activity.cook.CookNavActivity
import com.android.homemadeEATS.views.activity.customer.CustomerNavActivity
import kotlinx.android.synthetic.main.fragment_address.*

class AddressFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(AddressViewModel::class.java)
        val binding = DataBindingUtil.inflate<FragmentAddressBinding>(
            inflater,
            R.layout.fragment_address,
            container,
            false
        ).apply {
            viewModel = this@AddressFragment.viewModel as AddressViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnSaveAddress.setOnClickListener {
            if ((viewModel as AddressViewModel).isFieldsValidated()) {
                showValidationErrorDialog(
                    getString(R.string.validation_error_title),
                    getString(R.string.field_empty_message)
                )
            } else {
                AddressViewModel.isSelected = arguments?.getBoolean("isSelected")
                (viewModel as AddressViewModel).setAddress().observe(
                    viewLifecycleOwner,
                    { activeData ->
                        Toast.makeText(requireContext(), activeData?.message, Toast.LENGTH_LONG)
                            .show()
                        if (activeData?.message != null) {
                            openNavActivity();
                        }
                    }
                )
            }
        }
    }

    private fun openNavActivity() {
        when (arguments?.getInt("userType")) {
            0 -> {
                val intent = Intent(requireContext(), CustomerNavActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            1 -> {
                val intent = Intent(requireContext(), CookNavActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    private fun showValidationErrorDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ok") { _: DialogInterface, i: Int ->
                //do nothing
            }
        builder.setCancelable(false);
        builder.show()
    }
}