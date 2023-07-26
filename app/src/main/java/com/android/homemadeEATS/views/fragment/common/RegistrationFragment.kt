package com.android.homemadeEATS.views.fragment.common

import android.app.Activity
import android.app.AlertDialog
import android.content.ClipData
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.android.homemadeEATS.R
import com.android.homemadeEATS.databinding.FragmentRegistrationBinding
import com.android.homemadeEATS.viewmodel.common.RegistrationViewModel
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_cook_food_menu.ll_breakfast_images
import kotlinx.android.synthetic.main.fragment_cook_food_menu.outerLlBreakfastImages
import kotlinx.android.synthetic.main.fragment_registration.btn_register
import kotlinx.android.synthetic.main.fragment_registration.btn_uploadFile
import kotlinx.android.synthetic.main.fragment_registration.chip1
import kotlinx.android.synthetic.main.fragment_registration.chip2
import kotlinx.android.synthetic.main.fragment_registration.chip_group
import kotlinx.android.synthetic.main.fragment_registration.ll_licence
import kotlinx.android.synthetic.main.fragment_registration.tv_filename
import kotlinx.android.synthetic.main.fragment_view_profile.userType


class RegistrationFragment : BaseFragment() {

    private var selectedPdfUri: Uri = Uri.EMPTY
    private lateinit var verificationId: String
    private lateinit var mAuth: FirebaseAuth
    private val PICK_PDF_FILE_REQUEST_CODE = 101

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
        mAuth = FirebaseAuth.getInstance()
        observeRegistration()
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val resultCode = result?.resultCode
            val intent: Intent? = result.data
            val selectedImages: ClipData = intent?.clipData!!
            if (resultCode == Activity.RESULT_OK) {
                selectedPdfUri = selectedImages.getItemAt(0).uri
                tv_filename.text = "licence added"
            } else {
                Toast.makeText(
                    context,
                    "Sorry! unable to upload image/images",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private fun observeRegistration() {

        chip2.setOnClickListener {
            if (chip2.isChecked)
                ll_licence.visibility = View.VISIBLE
            else
                ll_licence.visibility = View.GONE
        }
        btn_uploadFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            Intent.createChooser(intent, "Select Picture")
            resultLauncher.launch(intent)
        }
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
                (viewModel as RegistrationViewModel).getRegistrationResponse(
                    context,
                    selectedPdfUri
                )
                    .observe(
                        viewLifecycleOwner
                    ) { activeData ->
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
                    }
            }
        }

        chip_group
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PDF_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                // Get the selected PDF file URI
                selectedPdfUri = data.data!!
                tv_filename.text = "licence added"
                // Do something with the selected PDF file URI, such as displaying it or processing it.
                // For example, you can display the PDF using a PDF viewer library or send the URI to a PDF processing function.
            }
        }
    }

    private fun showValidationErrorDialog(title: String, message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Ok") { dialogInterface: DialogInterface, i: Int ->
                //do nothing
            }
        builder.setCancelable(false)
        builder.show()
    }
}