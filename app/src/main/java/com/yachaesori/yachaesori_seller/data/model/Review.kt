package com.yachaesori.yachaesori_seller.data.model

import android.os.Parcel
import android.os.Parcelable

// 리뷰
data class Review(
    var reviewUid: String? = null,
    var customerUid: String? = null,
    var height: Long? = null,
    var weight: Long? = null,
    var rating: Double = 0.0,
    var date: String? = null,
    var content: String? = null,
    var Image: String? = null,
    var size: String? = null,
    var color: String? = null
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readDouble(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(reviewUid)
        parcel.writeString(customerUid)
        parcel.writeValue(height)
        parcel.writeValue(weight)
        parcel.writeDouble(rating)
        parcel.writeString(date)
        parcel.writeString(content)
        parcel.writeString(Image)
        parcel.writeString(size)
        parcel.writeString(color)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Review> {
        override fun createFromParcel(parcel: Parcel): Review {
            return Review(parcel)
        }

        override fun newArray(size: Int): Array<Review?> {
            return arrayOfNulls(size)
        }
    }
}