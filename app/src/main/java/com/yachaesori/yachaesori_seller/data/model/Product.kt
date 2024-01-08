package com.yachaesori.yachaesori_seller.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties

// 상품 클래스
@IgnoreExtraProperties
data class Product(
    var productId: String = "",
    var name: String = "",
    var price: Long = 0L,
    var mainImageUrl: String = "",
    var detailImageUrl: String = "",
    var regDate: String = "",
    var orderCount: Long = 0L,
    val options: List<String> = listOf()
)

// Fragment 간의 전달용 이미지 정보 클래스
data class Image(
    var uriString: String? = null,
    var fileName: String? = null
)