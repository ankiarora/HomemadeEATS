package com.android.homemadeEATS.viewmodel.common

import android.app.Application
import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.android.homemadeEATS.domain.common.AddressDomain
import com.android.homemadeEATS.model.common.Address
import com.android.homemadeEATS.model.common.AddressResponseGet
import com.android.homemadeEATS.model.common.PostResponse

class AddressViewModel(application: Application) : BaseViewModel(application) {
    private val addressDomain = AddressDomain()
    var address = MutableLiveData<String>()
    var landmark = MutableLiveData<String>()
    var saveAs = MutableLiveData<String>()

    private val allAddress by lazy {
        return@lazy addressDomain.getAddresses()
    }

    fun getAllAddresses(): MutableLiveData<AddressResponseGet> {
        return allAddress
    }

    companion object {
        var isSelected: Boolean? = true
        val latitude = MutableLiveData<String>()
        val longitude = MutableLiveData<String>()
        val mapsSelectedAddress = MutableLiveData<String>()
        var streetOrBuildingName = MutableLiveData<String>()
        var city = MutableLiveData<String>()
        var state = MutableLiveData<String>()
        var pincode = MutableLiveData<String>()
    }

    fun isFieldsValidated(): Boolean {
        return TextUtils.isEmpty(address.value)
                || TextUtils.isEmpty(city.value)
                || TextUtils.isEmpty(state.value)
                || TextUtils.isEmpty(landmark.value)
                || TextUtils.isEmpty(pincode.value)
                || TextUtils.isEmpty(saveAs.value)
    }

    fun setAddress(): MutableLiveData<PostResponse> {
        return addressDomain.setAddress(getAddressRequest())
    }

    private fun getAddressRequest(): Address {
        return Address(
            null,
            streetOrBuildingName.value,
            state.value,
            city.value,
            pincode.value,
            address.value,
            landmark.value,
            saveAs.value,
            latitude.value,
            longitude.value,
            mapsSelectedAddress.value,
            isSelected
        )
    }
}