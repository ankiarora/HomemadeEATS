package com.android.homemadeEATS.views.fragment.common

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.android.homemadeEATS.R
import com.android.homemadeEATS.databinding.FragmentRegistrationBinding
import com.android.homemadeEATS.viewmodel.common.RegistrationViewModel
import com.android.homemadeEATS.views.activity.common.RegistrationActivity
import com.google.android.material.chip.Chip
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import kotlinx.android.synthetic.main.fragment_registration.*
import java.util.concurrent.TimeUnit


class RegistrationFragment : BaseFragment() {

    private lateinit var verificationId: String
    private lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(RegistrationViewModel::class.java)
        val binding = DataBindingUtil.inflate<FragmentRegistrationBinding>(
            inflater,
            R.layout.fragment_registration,
            container,
            false
        ).apply {
            viewModel = this@RegistrationFragment.viewModel as RegistrationViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAuth = FirebaseAuth.getInstance();
        observeRegistration()
    }

    private fun observeRegistration() {

        btn_register.setOnClickListener {
            if ((viewModel as RegistrationViewModel).isFieldEmpty()) {
                showValidationErrorDialog(
                    getString(R.string.validation_error_title),
                    getString(R.string.field_empty_message)
                )
            } else if (!(viewModel as RegistrationViewModel).passwordMatch()) {
                showValidationErrorDialog(
                    getString(R.string.validation_error_title),
                    getString(R.string.password_not_match_message)
                )
            } else if (chip_group.checkedChipId == -1 || !chip_group.findViewById<Chip>(chip_group.checkedChipId).isChecked) {
                showValidationErrorDialog(
                    getString(R.string.validation_error_title),
                    getString(R.string.field_empty_message)
                )
            } else {
                when {
                    chip1.isChecked -> (viewModel as RegistrationViewModel).spinnerSelectedItem = 0
                    chip2.isChecked -> (viewModel as RegistrationViewModel).spinnerSelectedItem = 1
                    else -> (viewModel as RegistrationViewModel).spinnerSelectedItem = 2
                }
                (viewModel as RegistrationViewModel).getRegistrationResponse()
                    .observe(
                        viewLifecycleOwner,
                        { activeData ->
                            if (activeData?.message != null) {
                                Toast.makeText(
                                    requireContext(),
                                    activeData.message,
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                                if (activeData.message.equals("Signup Successfull"))
                                    requireActivity().onBackPressed()
                            } else
                                Toast.makeText(
                                    requireContext(),
                                    activeData?.error,
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                        })
            }
        }

        chip_group
    }

    private fun showValidationErrorDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ok") { dialogInterface: DialogInterface, i: Int ->
                //do nothing
            }
        builder.setCancelable(false);
        builder.show()
    }
}