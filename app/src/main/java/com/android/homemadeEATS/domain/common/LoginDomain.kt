package com.android.homemadeEATS.domain.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.android.homemadeEATS.model.common.*
import com.android.homemadeEATS.repository.common.AddressRepository
import com.android.homemadeEATS.repository.common.LoginRepository
import com.android.homemadeEATS.repository.common.UserProfileRepository

class LoginDomain {

    fun signUp(signUpRequest: SignupRequest): LiveData<SignupResponse> {
        return LoginRepository.signUp(signUpRequest)
    }

    fun signIn(signInRequest: SigninRequest): LiveData<SigninResponse> {
        return LoginRepository.signIn(signInRequest)
    }

    fun setAccessToken(token: String?) {
        LoginRepository.accessToken = token
    }

    fun getAddresses(): MutableLiveData<AddressResponseGet> {
        return AddressRepository.getAddresses()
    }

    fun isAddressAdded(): LiveData<String> {
        return Transformations.map(getAddresses()) {
            if (!it.error.isNullOrEmpty()) {
                it.error
            } else {
                if (it.addressList?.isNotEmpty() == true) {
                    "true"
                } else {
                    "false"
                }
            }
        }
    }

    fun getUserProfile(): MutableLiveData<UserProfileResponse> {
        return UserProfileRepository.getUserProfileInfo()
    }
}