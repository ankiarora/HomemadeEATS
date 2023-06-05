package com.android.homemadeEATS.model.common

import com.squareup.moshi.Json

data class SigninResponse(
    @field:Json(name = "message") val message: String?,
    @field:Json(name = "user") val user: User?,
    val error: String?
)