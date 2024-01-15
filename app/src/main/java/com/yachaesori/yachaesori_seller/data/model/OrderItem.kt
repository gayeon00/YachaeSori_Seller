package com.yachaesori.yachaesori_seller.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class OrderItem(
    val product: Product = Product(),
    val selectedOption: String = "",
    val quantity: Int = 0,
    val status: Long = 0L,
)
