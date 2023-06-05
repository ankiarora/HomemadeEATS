package com.android.homemadeEATS.model.common

import android.os.Parcel
import android.os.Parcelable

data class MealDetail(
    val dishes: List<Meal>?,
    val images: List<String>?,
    val mealType: String?,
    val price: Int?,
    val quantity: Int?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readArrayList(Meal::class.java.getClassLoader()) as List<Meal>,
        parcel.createStringArrayList(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeList(dishes)
        parcel.writeStringList(images)
        parcel.writeString(mealType)
        parcel.writeValue(price)
        parcel.writeValue(quantity)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MealDetail> {
        override fun createFromParcel(parcel: Parcel): MealDetail {
            return MealDetail(parcel)
        }

        override fun newArray(size: Int): Array<MealDetail?> {
            return arrayOfNulls(size)
        }
    }
}