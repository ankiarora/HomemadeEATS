package com.android.homemadeEATS.model.common

import com.squareup.moshi.Json

data class SignupResponse(
    @field:Json(name = "message") var message: String?,
    var error: String?
)