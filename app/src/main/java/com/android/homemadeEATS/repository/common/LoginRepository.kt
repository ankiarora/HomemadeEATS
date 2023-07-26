package com.android.homemadeEATS.repository.common

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.homemadeEATS.AppConstants
import com.android.homemadeEATS.model.common.*
import com.android.homemadeEATS.network.ApiFactory
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


object LoginRepository {
    var accessToken: String? = null
    var user: User? = null

    private fun getPathFromURI(context: Context?, uri: Uri): String? {
        var res: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = context?.contentResolver?.query(uri, proj, null, null, null)
        if (cursor != null) {
            cursor.moveToFirst()
            res =
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
            cursor.close()
        }
        return res
    }

    fun signUp(
        context: Context?,
        request: SignupRequest,
        selectedPdfUri: Uri
    ): MutableLiveData<SignupResponse> {
        val job: CompletableJob = Job()
        return object : MutableLiveData<SignupResponse>() {
            override fun onActive() {
                job.let { job ->
                    CoroutineScope(Dispatchers.IO + job).launch {

                        var pdfFilePart: MultipartBody.Part? = null
                        if (selectedPdfUri != Uri.EMPTY) {
                            val pdfFile = File(getPathFromURI(context, selectedPdfUri))
                            // Create a file part from the selected PDF file
                            val filePart = RequestBody.create(
                                "multipart/form-data".toMediaTypeOrNull(),
                                pdfFile
                            )
                            pdfFilePart =
                                MultipartBody.Part.createFormData(
                                    "certificate",
                                    pdfFile.getName(),
                                    filePart
                                )
                        }

                        val email: MultipartBody.Part =
                            MultipartBody.Part.createFormData(
                                "email",
                                request.email!!
                            )

                        val password: MultipartBody.Part =
                            MultipartBody.Part.createFormData(
                                "password",
                                request.password!!
                            )
                        val firstName: MultipartBody.Part =
                            MultipartBody.Part.createFormData(
                                "firstName",
                                request.firstName!!
                            )
                        val lastName: MultipartBody.Part =
                            MultipartBody.Part.createFormData(
                                "lastName",
                                request.lastName!!
                            )
                        val phoneNumber: MultipartBody.Part =
                            MultipartBody.Part.createFormData(
                                "phoneNumber",
                                request.phoneNumber!!
                            )
                        val userType: MultipartBody.Part =
                            MultipartBody.Part.createFormData(
                                "userType",
                                request.userType.toString()
                            )

                        val response = ApiFactory.apiWithoutToken.signUp(
                            pdfFilePart,
                            email,
                            password,
                            firstName,
                            lastName,
                            phoneNumber,
                            userType
                        )
                        withContext(Dispatchers.Main) {
                            value = if (response.code() == AppConstants.SUCCESS) {
                                SignupResponse(response.body()?.message, null)
                            } else if (response.code() == AppConstants.CONFLICT) {
                                SignupResponse(null, response.errorBody()!!.string())
                            } else if (response.code() == AppConstants.UNPROCESSABLE_ENTITY && response.errorBody() != null) {
                                val errorBody = Gson().fromJson(
                                    response.errorBody()!!.string(),
                                    ErrorBody::class.java
                                )
                                SignupResponse(null, errorBody.errors?.get(0)?.msg)
                            } else {
                                SignupResponse(
                                    null,
                                    "Unexpected error occurred. Please try again later."
                                )
                            }
                            job.complete()
                        }
                    }
                }
            }
        }
    }

    fun signIn(request: SigninRequest): MutableLiveData<SigninResponse> {
        val job: CompletableJob = Job()
        return object : MutableLiveData<SigninResponse>() {
            override fun onActive() {
                if (accessToken.isNullOrEmpty()) {
                    job.let { job ->
                        CoroutineScope(Dispatchers.IO + job).launch {
                            val response = ApiFactory.apiWithoutToken.signIn(request)
                            withContext(Dispatchers.Main) {
                                if (response.code() == AppConstants.SUCCESS) {
                                    accessToken = response.body()?.user?.token
                                    user = response.body()?.user
                                    value = SigninResponse(
                                        response.body()!!.message,
                                        response.body()!!.user,
                                        null
                                    )
                                } else if (response.code() == AppConstants.CONFLICT) {
                                    value = SigninResponse(
                                        null,
                                        null,
                                        response.body()!!.message
                                    )
                                } else if (response.code() == AppConstants.UNPROCESSABLE_ENTITY && response.errorBody() != null) {
                                    val errorBody = Gson().fromJson(
                                        response.errorBody()!!.string(),
                                        ErrorBody::class.java
                                    )
                                    value =
                                        SigninResponse(null, null, errorBody.errors?.get(0)?.msg)
                                } else if (response.code() == AppConstants.UNAUTHORIZED) {
                                    value = SigninResponse(
                                        null,
                                        null,
                                        "Please enter correct username and password.\n Or your certificate is not yet verified."
                                    )
                                } else {
                                    value = SigninResponse(
                                        null,
                                        null,
                                        "Unexpected error occurred. Please try again later."
                                    )
                                }
                                job.complete()
                            }
                        }
                    }
                }
            }
        }
    }

    fun setFCMToken(fcmToken: FCMToken?): LiveData<PostResponse> {
        val job: CompletableJob = Job()
        return object : MutableLiveData<PostResponse>() {
            override fun onActive() {
                job.let { job ->
                    CoroutineScope(Dispatchers.IO + job).launch {
                        val response = ApiFactory.apiWithToken.setFcmToken(fcmToken!!)
                        withContext(Dispatchers.Main) {
                            if (response.code() == AppConstants.SUCCESS)
                                value = PostResponse(response.body()!!.message, null)
                            else if (response.code() == AppConstants.CONFLICT) {
                                val errorBody = Gson().fromJson(
                                    response.errorBody()!!.string(),
                                    PostResponse::class.java
                                )
                                value = PostResponse("", errorBody.message)
                            } else if (response.code() == AppConstants.UNPROCESSABLE_ENTITY && response.errorBody() != null) {
                                val errorBody = Gson().fromJson(
                                    response.errorBody()!!.string(),
                                    ErrorBody::class.java
                                )
                                value = PostResponse("", errorBody.errors?.get(0)?.msg)
                            } else {
                                value = PostResponse(
                                    "",
                                    "Unexpected error occurred. Please try again later."
                                )
                            }
                            job.complete()
                        }
                    }
                }
            }
        }
    }
}
