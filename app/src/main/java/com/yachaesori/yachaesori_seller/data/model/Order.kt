package com.yachaesori.yachaesori_seller.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class Order(
    var orderId: String? = "",
    // 주문 완료 상태로 전송
    // Long -> String 변경
    var status: Long = 0L,
    //주문자 아이디
    var userId: String = "",
    var orderDate: String = "",
    val place: String = "",
    val postcode: String = "",
    var address: String = "",
    var name: String = "",
    var phone: String = "",
    var msg: String = "",
    // productUid를 통해 받아온다면 안넘겨줘도 되는 데이터?
    var itemList: List<OrderItem> = listOf(),
    var totalPrice: Long = 0L,
)

