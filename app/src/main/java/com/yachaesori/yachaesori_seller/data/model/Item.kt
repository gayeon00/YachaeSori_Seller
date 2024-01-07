package com.yachaesori.yachaesori_seller.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Item(
    var productUid: String,
    //TODO Long으로 바꾸기
    var size: String,
    //TODO Long으로 바꾸기
    var color: String,
    var quantity: Long,
    var sellerUid: String,
    var name: String,
    var mainImage: String,
    var price: Long,
) : Parcelable
