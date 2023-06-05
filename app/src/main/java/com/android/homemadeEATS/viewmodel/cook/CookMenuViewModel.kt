package com.android.homemadeEATS.viewmodel.cook

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.homemadeEATS.domain.common.AddressDomain
import com.android.homemadeEATS.domain.cook.CookMenuDomain
import com.android.homemadeEATS.model.common.Address
import com.android.homemadeEATS.model.common.PostResponse
import com.android.homemadeEATS.viewmodel.common.BaseViewModel
import okhttp3.MultipartBody


class CookMenuViewModel(application: Application) : BaseViewModel(application) {
    private val foodDomain = CookMenuDomain()
    private val addressDomain = AddressDomain()
    private val allAddress by lazy {
        return@lazy addressDomain.getAddresses()
    }

    fun getSelectedLocation(): LiveData<Address?> {
        return addressDomain.getSelectedLocation(allAddress)
    }

    val _selectedLocation = getSelectedLocation()

    fun setFoodMenu(
        mealType: MultipartBody.Part,
        meals: MultipartBody.Part,
        price: MultipartBody.Part,
        images: ArrayList<MultipartBody.Part?>
    ): MutableLiveData<PostResponse> {
        return foodDomain.setFoodMenu(mealType, meals, price, images)
    }

}