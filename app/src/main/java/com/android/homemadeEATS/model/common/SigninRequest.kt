package com.android.homemadeEATS.model.common

import com.squareup.moshi.Json

data class SigninRequest(
    @field:Json(name = "email") val email: String?,
    @field:Json(name = "password") val password: String?
)