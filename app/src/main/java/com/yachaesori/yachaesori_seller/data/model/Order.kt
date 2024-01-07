package com.yachaesori.yachaesori_seller.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Order(
    var orderUid: String,
    var orderDate: String,
    var message: String,
    var state: Long,
    //TODO : 나중에 Address로 변경해야함
    var address: String,
//    var address:Address,
    var itemList: List<Item> = listOf(),
    var couponUid: String,
    var usePoint: Long,
    var payMethod: Long,
    var totalPrice: Long,
    var userUid: String
) : Parcelable

