package com.yachaesori.yachaesori_seller.data.model

import com.yachaesori.yachaesori_seller.data.model.OrderState.*

// 주문상태
enum class OrderState(val code: Long, val str: String) {
    PAYMENT(1, "주문완료"),
    READY(2, "배송준비"),
    DELIVERY(3, "배송중"),
    COMPLETE(4, "배송완료"),
    CANCEL(5, "취소"),
    EXCHANGE(6, "교환"),
    REFUND(7, "환불")
}

fun getOrderState(code: Long? = null, str: String? = null): OrderState {
    if (code != null) {
        when (code) {
            1L -> return PAYMENT
            2L -> return READY
            3L -> return DELIVERY
            4L -> return COMPLETE
            5L -> return CANCEL
            6L -> return EXCHANGE
            7L -> return REFUND
        }
    } else if (str != null) {
        when (str) {
            "주문완료" -> return PAYMENT
            "배송준비" -> return READY
            "배송중" -> return DELIVERY
            "배송완료" -> return COMPLETE
            "취소" -> return CANCEL
            "교환" -> return EXCHANGE
            "환불" -> return REFUND
        }
    }
    return CANCEL
}