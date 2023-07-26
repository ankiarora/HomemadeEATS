package com.android.homemadeEATS.services

import com.android.homemadeEATS.model.common.*
import com.android.homemadeEATS.model.cook.OrderId
import com.android.homemadeEATS.model.cook.CookNewOrder
import com.android.homemadeEATS.model.customer.CustomerBookedMeal
import com.android.homemadeEATS.model.customer.CustomerMeal
import com.android.homemadeEATS.model.customer.CustomerOrderMeal
import com.android.homemadeEATS.views.fragment.cook.CookPreparedOrdersFragment
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*


interface ApiService {
    @Multipart
    @POST("/auth/signup")
    suspend fun signUp(
        @Part certificate: MultipartBody.Part?,
        @Part email: MultipartBody.Part,
        @Part password: MultipartBody.Part,
        @Part firstName: MultipartBody.Part,
        @Part lastName: MultipartBody.Part,
        @Part phoneNumber: MultipartBody.Part,
        @Part userType: MultipartBody.Part
    ): Response<SignupResponse>

    @POST("/auth/signin")
    suspend fun signIn(@Body signinRequest: SigninRequest): Response<SigninResponse>

    @POST("/address")
    suspend fun setAddress(
        @Body address: Address
    ): Response<PostResponse>

    @GET("/address")
    suspend fun getAddress(): Response<List<Address>>

    @Multipart
    @POST("/meal")
    suspend fun setFoodMenu(
        @Part mealType: MultipartBody.Part,
        @Part meals: MultipartBody.Part,
        @Part price: MultipartBody.Part,
        @Part images: ArrayList<MultipartBody.Part?>
    ): Response<ResponseBody>

    @GET("/meal")
    suspend fun getCustomerAllMeals(
        @Query("search") search: String,
        @Query("skip") skip: Int,
        @Query("take") take: Int
    ): Response<List<CustomerMeal>>

    @GET("user")
    suspend fun getUserProfile(): Response<GetUserProfile>

    @GET("/order/cook/pending")
    suspend fun getCookPendingOrders(): Response<List<CookNewOrder>>

    @GET("/order/cook/booked")
    suspend fun getCookBookedOrders(): Response<List<CookNewOrder>>

    @GET("/order/cook/prepared")
    suspend fun getCookPreparedOrders(): Response<List<CookNewOrder>>

    @GET("/order/pastOrders")
    suspend fun getCookPastOrders(): Response<List<CookNewOrder>>

    @POST("/order/cook/prepared")
    suspend fun orderPrepared(@Body orderId: OrderId): Response<PostResponse>

    @POST("/order/cook/verifyOtp")
    suspend fun verifyOtp(@Body otpVerification: CookPreparedOrdersFragment.OtpVerification): Response<PostResponse>

    @POST("/order/cook/approve")
    suspend fun cookAcceptOrder(@Body orderId: OrderId): Response<PostResponse>

    @POST("/order/cook/reject")
    suspend fun cookRejectOrder(@Body orderId: OrderId): Response<PostResponse>

    @POST("/user/fcmToken")
    suspend fun setFcmToken(@Body fcmToken: FCMToken): Response<PostResponse>

    @POST("/order")
    suspend fun customerOrderItems(
        @Body customerOrderMeal: CustomerOrderMeal
    ): Response<PostResponse>

    @GET("/order/customer/booked")
    suspend fun getCustomerBookedOrders(): Response<List<CustomerBookedMeal>>

    @POST("/user/update")
    suspend fun saveUserProfile(
        @Body userProfile: PostUserProfile
    ): Response<PostResponse>
}