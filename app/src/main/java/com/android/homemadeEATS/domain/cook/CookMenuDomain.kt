package com.android.homemadeEATS.domain.cook

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.android.homemadeEATS.model.common.Address
import com.android.homemadeEATS.model.common.AddressResponseGet
import com.android.homemadeEATS.model.common.PostResponse
import com.android.homemadeEATS.repository.common.AddressRepository
import com.android.homemadeEATS.repository.cook.CookMenuRepository
import okhttp3.MultipartBody

class CookMenuDomain {
    fun getAddresses(): MutableLiveData<AddressResponseGet> {
        return AddressRepository.getAddresses()
    }

    fun getSelectedLocation(allAddress: MutableLiveData<AddressResponseGet>): LiveData<Address?> {
        val data = Transformations.map(allAddress) { it ->
            if (it?.addressList?.isEmpty() == false)
                it.addressList.filter { it.isSelected == true }.get(0)
            else
                null
        }
        return data
    }

    fun setFoodMenu(
        mealType: MultipartBody.Part,
        meals: MultipartBody.Part,
        price: MultipartBody.Part,
        images: ArrayList<MultipartBody.Part?>
    ): MutableLiveData<PostResponse> {
        return CookMenuRepository.setFoodMenu(mealType, meals, price, images)
    }
}