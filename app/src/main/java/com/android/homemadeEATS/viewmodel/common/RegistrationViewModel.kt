package com.android.homemadeEATS.viewmodel.common

import android.app.Application
import android.content.Context
import android.net.Uri
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.homemadeEATS.domain.common.LoginDomain
import com.android.homemadeEATS.model.common.SignupRequest
import com.android.homemadeEATS.model.common.SignupResponse

open class RegistrationViewModel(application: Application) : BaseViewModel(application) {
    var spinnerSelectedItem: Int = 0
    var firstName = MutableLiveData<String>()
    var lastName = MutableLiveData<String>()
    var emailID = MutableLiveData<String>()
    var phoneNumber = MutableLiveData<String>()
    var password = MutableLiveData<String>()
    var reenterPassword = MutableLiveData<String>()

    var loginDomain: LoginDomain = LoginDomain()
    fun getRegistrationResponse(context: Context?, selectedPdfUri: Uri): LiveData<SignupResponse> {
        return loginDomain.signUp(context, getSignupRequest(), selectedPdfUri)
    }

    private fun getSignupRequest(): SignupRequest {
        return SignupRequest(
            emailID.value,
            password.value,
            firstName.value,
            lastName.value,
            phoneNumber.value,
            spinnerSelectedItem
        )
    }

    fun isFieldEmpty(): Boolean {
        return TextUtils.isEmpty(firstName.value)
                || TextUtils.isEmpty(lastName.value)
                || TextUtils.isEmpty(emailID.value)
                || TextUtils.isEmpty(phoneNumber.value)
                || TextUtils.isEmpty(password.value)
                || TextUtils.isEmpty(reenterPassword.value)

    }

    fun passwordMatch(): Boolean {
        return password.value.equals(reenterPassword.value)
    }
}