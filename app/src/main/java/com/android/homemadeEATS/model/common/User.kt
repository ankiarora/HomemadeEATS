package com.android.homemadeEATS.model.common

data class User(
    val token: String?,
    val userType: Int?,
    val email: String?,
    val firstName: String?,
    val lastName: String?,
    val address: Address?,
    val phoneNumber: String?
)