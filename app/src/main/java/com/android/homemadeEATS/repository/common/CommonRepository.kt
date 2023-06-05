package com.android.homemadeEATS.repository.common

object CommonRepository {
    fun clearData(){
        AddressRepository.addressList = emptyList()
        LoginRepository.accessToken = null
    }
}