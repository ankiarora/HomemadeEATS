package com.android.homemadeEATS.domain.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.android.homemadeEATS.model.common.Address
import com.android.homemadeEATS.model.common.AddressResponseGet
import com.android.homemadeEATS.model.common.PostResponse
import com.android.homemadeEATS.repository.common.AddressRepository

class AddressDomain {
    fun setAddress(address: Address): MutableLiveData<PostResponse> {
        return AddressRepository.setAddress(address)
    }

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
}