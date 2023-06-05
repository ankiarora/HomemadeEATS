package com.android.homemadeEATS.views.fragment.common

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Paint
import android.os.Bundle
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.android.homemadeEATS.R
import com.android.homemadeEATS.databinding.FragmentViewProfileBinding
import com.android.homemadeEATS.model.common.PostUserProfile
import com.android.homemadeEATS.viewmodel.common.UserProfileViewModel
import com.android.homemadeEATS.views.activity.common.ViewAddressActivity

class UserProfileFragment : BaseDialogFragment() {
    private var sharedPref: SharedPreferences? = null
    lateinit var activity: Context
    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.activity = context
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = androidx.appcompat.app.AlertDialog.Builder(activity)
        sharedPref = activity.getSharedPreferences(
            getString(R.string.pref_file_key), Context.MODE_PRIVATE
        )
        viewModel = ViewModelProvider(this).get(UserProfileViewModel::class.java)
        val binding = DataBindingUtil.inflate<FragmentViewProfileBinding>(
            requireActivity().layoutInflater,
            R.layout.fragment_view_profile,
            null,
            false
        ).apply {
            this.viewModel = this@UserProfileFragment.viewModel as UserProfileViewModel
        }
        builder.setCancelable(false);
        builder.setView(binding.root)
        val cancelProfile: Button = binding.root.findViewById(R.id.cancelProfile)
        val saveProfile: Button = binding.root.findViewById(R.id.saveProfile)

        val emailID: EditText = binding.root.findViewById(R.id.emailId)
        val phoneNumber: EditText = binding.root.findViewById(R.id.phoneNumber)
        val firstName: EditText = binding.root.findViewById(R.id.userFirstName)
        val lastName: EditText = binding.root.findViewById(R.id.userLastName)
        val userAddress: TextView = binding.root.findViewById(R.id.userAddress)

        val dialog = builder.create()
        cancelProfile.setOnClickListener {
            dialog.dismiss()
        }
        saveProfile.setOnClickListener {
            val userProfile = PostUserProfile(
                emailID.text.toString(),
                phoneNumber.text.toString(),
                firstName.text.toString(),
                lastName.text.toString()
            )
            (viewModel as UserProfileViewModel).saveUserProfile(userProfile)
                .observe(this, { activeData ->
                    Toast.makeText(requireContext(), activeData?.message, Toast.LENGTH_LONG)
                        .show()
                    if (activeData?.message != null) {
                        //clear are cached data
                        with(sharedPref?.edit()) {
                            this?.putString(
                                getString(R.string.pref_user_token),
                                ""
                            )
                            this?.apply()
                        }
                        viewModel.clearAllData()
                    }
                })
        }

        userAddress.paintFlags = userAddress.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        (viewModel as UserProfileViewModel)._selectedLocation.observe(
            this,
            {
                val address = it
                userAddress.setOnClickListener {
                    val intent = Intent(requireContext(), ViewAddressActivity::class.java)
                    intent.putExtra("Selected Address", address)
                    startActivity(intent)
                }
            }
        )

        return dialog
    }
}