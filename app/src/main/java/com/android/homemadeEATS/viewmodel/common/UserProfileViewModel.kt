package com.android.homemadeEATS.viewmodel.common

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.homemadeEATS.domain.UserProfileDomain
import com.android.homemadeEATS.domain.common.AddressDomain
import com.android.homemadeEATS.model.common.Address
import com.android.homemadeEATS.model.common.PostResponse
import com.android.homemadeEATS.model.common.PostUserProfile

class UserProfileViewModel(application: Application) : BaseViewModel(application) {
    private val userProfileDomain = UserProfileDomain()
    private val addressDomain = AddressDomain()

    private val allAddress by lazy {
        return@lazy addressDomain.getAddresses()
    }

    private fun getSelectedLocation(): LiveData<Address?> {
        return addressDomain.getSelectedLocation(allAddress)
    }

    val _selectedLocation = getSelectedLocation()
    private fun getFirstName(): String {
        return userProfileDomain.getUserFirstName()
    }

    fun getLastName(): String {
        return userProfileDomain.getUserLastName()
    }

    val _firstName = getFirstName()
    val _lastName = getLastName()

    private fun getEmail(): String? {
        return userProfileDomain.getEmail()
    }


    val _email = getEmail()

    fun getUserType(): String {
        when (userProfileDomain.getUserType()) {
            0 -> return "Customer"
            1 -> return "Cook"
            2 -> return "Delivery"
        }
        return ""
    }

    val _userType = getUserType()

    private fun getContactNumber(): String? {
        return userProfileDomain.getContactNumber()
    }

    val _contactNo = getContactNumber()

    fun getAddress(): String? {
        return userProfileDomain.getAddress()
    }

    fun saveUserProfile(userProfile: PostUserProfile): MutableLiveData<PostResponse> {
        return userProfileDomain.saveUserProfile(userProfile)
    }

    val _address = getAddress()

}