package com.android.homemadeEATS.model.common

import android.os.Parcel
import android.os.Parcelable

data class Address(
    val _id: String?,
    val streetOrBuildingName: String?,
    val state: String?,
    val city: String?,
    val pincode: String?,
    val address: String?,
    val landmark: String?,
    val saveAs: String?,
    val latitude: String?,
    val longitude: String?,
    val mapsAddress: String?,
    val isSelected: Boolean?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(_id)
        parcel.writeString(streetOrBuildingName)
        parcel.writeString(state)
        parcel.writeString(city)
        parcel.writeString(pincode)
        parcel.writeString(address)
        parcel.writeString(landmark)
        parcel.writeString(saveAs)
        parcel.writeString(latitude)
        parcel.writeString(longitude)
        parcel.writeString(mapsAddress)
        parcel.writeValue(isSelected)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Address> {
        override fun createFromParcel(parcel: Parcel): Address {
            return Address(parcel)
        }

        override fun newArray(size: Int): Array<Address?> {
            return arrayOfNulls(size)
        }
    }

}