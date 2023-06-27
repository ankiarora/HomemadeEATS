package com.android.homemadeEATS.views.fragment.common

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.android.homemadeEATS.R
import com.android.homemadeEATS.databinding.FragmentLoginBinding
import com.android.homemadeEATS.viewmodel.common.LoginViewModel
import com.android.homemadeEATS.views.activity.common.AddressActivity
import com.android.homemadeEATS.views.activity.common.RegistrationActivity
import com.android.homemadeEATS.views.activity.cook.CookNavActivity
import com.android.homemadeEATS.views.activity.customer.CustomerNavActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import kotlinx.android.synthetic.main.fragment_login.*


open class LoginFragment : BaseFragment() {
    private var userType: Int? = -1
    private var sharedPref: SharedPreferences? = null
    private val RC_SIGN_IN: Int = 1
    private val TAG = "LoginFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        val binding = DataBindingUtil.inflate<FragmentLoginBinding>(
            inflater,
            R.layout.fragment_login,
            container,
            false
        ).apply {
            viewModel = this@LoginFragment.viewModel as LoginViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPref = activity?.getSharedPreferences(
            getString(R.string.pref_file_key), Context.MODE_PRIVATE
        )
        googleLoginSetup()
        loginUser()
    }

    private fun loginUser() {
        btn_login.setOnClickListener {
            if ((viewModel as LoginViewModel).validateFields()) {
                showValidationErrorDialog(getString(R.string.field_empty_message))
            } else {
                openAddressScreen()
            }
        }
    }

    private fun openAddressScreen() {
        (viewModel as LoginViewModel).loginUser().observe(
            viewLifecycleOwner
        ) { activeData ->
            if (activeData?.message != null) {
                this.userType = activeData.user?.userType
                Toast.makeText(requireContext(), activeData.message, Toast.LENGTH_LONG)
                    .show()
                with(sharedPref?.edit()) {
                    this?.putBoolean(getString(R.string.pref_login_status), true)
                    activeData.user?.userType?.let {
                        this?.putInt(
                            getString(R.string.pref_user_type),
                            it
                        )
                    }
                    this?.putString(
                        getString(R.string.pref_user_token),
                        activeData.user?.token
                    )
                    this?.apply()
                }
                (viewModel as LoginViewModel).setAccessToken(
                    sharedPref?.getString(
                        getString(R.string.pref_user_token),
                        ""
                    )
                )
                isAddressAdded()
            } else {
                Toast.makeText(requireContext(), activeData?.error, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun showValidationErrorDialog(message: String) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Validation Error")
            .setMessage(message)
            .setPositiveButton("Ok") { _: DialogInterface, _: Int ->
                //do nothing
            }
        builder.setCancelable(false);
        builder.show()
    }

    private fun googleLoginSetup() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.server_client_id))
            .requestProfile()
            .build()
        tv_register.setOnClickListener {
            val intent = Intent(requireContext(), RegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(requireActivity())
        val isLoggedIn = sharedPref?.getBoolean(getString(R.string.pref_login_status), false)
        this.userType = sharedPref?.getInt(getString(R.string.pref_user_type), -1)

        if (isLoggedIn == true || account != null) {
            (viewModel as LoginViewModel).setAccessToken(
                sharedPref?.getString(
                    getString(R.string.pref_user_token),
                    ""
                )
            )
            isAddressAdded()
        }
    }

    private fun isAddressAdded() {
        (viewModel as LoginViewModel).isAddressAdded().observe(
            viewLifecycleOwner
        ) { response ->
            when {
                response.equals("false") -> {
                    val intent = Intent(requireContext(), AddressActivity::class.java)
                    intent.putExtra("isSelected", true)
                    intent.putExtra("userType", userType)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }

                response.equals("true") -> {
                    Toast.makeText(
                        requireContext(),
                        "address already added",
                        Toast.LENGTH_LONG
                    )
                        .show()
//                    getUserProfile()

                    val intent = Intent(requireContext(), AddressActivity::class.java)
                    intent.putExtra("isSelected", true)
                    intent.putExtra("userType", userType)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }

                else -> {
                    with(sharedPref?.edit()) {
                        this?.putString(
                            getString(R.string.pref_user_token),
                            ""
                        )
                        this?.apply()
                    }
                    Toast.makeText(requireContext(), response, Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun getUserProfile() {
        (viewModel as LoginViewModel).getUserProfile().observe(
            viewLifecycleOwner
        ) { activeData ->
            if (activeData.userProfile == null)
                Toast.makeText(context, activeData.error, Toast.LENGTH_LONG).show()
            else {
                openHomeScreen()
            }
        }
    }

    private fun openHomeScreen() {
        when (userType) {
            0 -> {
                val intent = Intent(requireContext(), CustomerNavActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }

            1 -> {
                val intent = Intent(requireContext(), CookNavActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                requireActivity().finish()
            }

            2 -> {
                //TODO
                //call the screen for delivery user
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val idToken = account!!.idToken
            if (idToken != null) {
                sendTokenToServer(idToken)
            }
            // Signed in successfully, show authenticated UI.
            openAddressScreen()
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
        }
    }

    private fun sendTokenToServer(idToken: String) {
    }
}