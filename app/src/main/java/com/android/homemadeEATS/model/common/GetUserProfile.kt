package com.android.homemadeEATS.model.common

data class GetUserProfile(
    val email: String?,
    val phoneNumber: String?,
    val firstName: String?,
    val lastName: String?,
    val userType: Int?,
    val address: Address?
)

data class PostUserProfile(
    val email: String?,
    val phoneNumber: String?,
    val firstName: String?,
    val lastName: String?,
)
