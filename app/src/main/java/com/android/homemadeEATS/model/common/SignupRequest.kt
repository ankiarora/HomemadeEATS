package com.android.homemadeEATS.model.common

import com.squareup.moshi.Json

data class SignupRequest(
    @field:Json(name = "email") val email: String?,
    @field:Json(name = "password") val password: String?,
    @field:Json(name = "firstName") val firstName: String?,
    @field:Json(name = "lastName") val lastName: String?,
    @field:Json(name = "phoneNumber") val phoneNumber: String?,
    @field:Json(name = "userType") val userType: Int?,
)