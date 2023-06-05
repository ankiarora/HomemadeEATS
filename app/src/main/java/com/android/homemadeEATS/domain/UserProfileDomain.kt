package com.android.homemadeEATS.domain

import androidx.lifecycle.MutableLiveData
import com.android.homemadeEATS.model.common.PostResponse
import com.android.homemadeEATS.model.common.PostUserProfile
import com.android.homemadeEATS.repository.common.UserProfileRepository

class UserProfileDomain {

    fun getUserFirstName(): String {
        return UserProfileRepository.userProfile?.firstName!!
    }

    fun getUserLastName(): String {
        return UserProfileRepository.userProfile?.lastName!!
    }

    fun getEmail(): String? {
        return UserProfileRepository.userProfile?.email
    }

    fun getUserType(): Int? {
        return UserProfileRepository.userProfile?.userType
    }

    fun getContactNumber(): String? {
        return UserProfileRepository.userProfile?.phoneNumber
    }

    fun getAddress(): String? {
        return UserProfileRepository.userProfile?.address?.mapsAddress
    }

    fun saveUserProfile(userProfile: PostUserProfile): MutableLiveData<PostResponse> {
        return UserProfileRepository.saveUserProfileInfo(userProfile)
    }
}