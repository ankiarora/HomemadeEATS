package com.android.homemadeEATS.viewmodel.common

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.homemadeEATS.domain.common.LoginDomain
import com.android.homemadeEATS.model.common.*

class LoginViewModel(application: Application) : BaseViewModel(application) {
    val emailAddress = MutableLiveData<String>()
    val password = MutableLiveData<String>()

    var loginDomain = LoginDomain()
    fun validateFields(): Boolean {
        return TextUtils.isEmpty(emailAddress.value) || TextUtils.isEmpty(password.value)
    }

    fun loginUser(): LiveData<SigninResponse> {
        return loginDomain.signIn(getSignInRequest())
    }

    private fun getSignInRequest(): SigninRequest {
        return SigninRequest(emailAddress.value, password.value)
    }

    fun setAccessToken(token: String?) {
        loginDomain.setAccessToken(token)
    }

    fun isAddressAdded(): LiveData<String> {
        return loginDomain.isAddressAdded()
    }

    fun getUserProfile(): MutableLiveData<UserProfileResponse> {
        return loginDomain.getUserProfile()
    }
}