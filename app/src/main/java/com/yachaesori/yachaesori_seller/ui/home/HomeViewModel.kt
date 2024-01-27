package com.yachaesori.yachaesori_seller.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yachaesori.yachaesori_seller.data.repository.OrderRepository
import com.yachaesori.yachaesori_seller.data.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {
    // 주문 상태 건 수
    var orderCount = MutableLiveData<Long>()
    var paymentCount = MutableLiveData<Long>()
    var readyCount = MutableLiveData<Long>()
    var deliveryCount = MutableLiveData<Long>()
    var completeCount = MutableLiveData<Long>()
    var cancelCount = MutableLiveData<Long>()
    var exchangeCount = MutableLiveData<Long>()
    var refundCount = MutableLiveData<Long>()

    init {
        orderCount.value = 0L
        paymentCount.value = 0L
        readyCount.value = 0L
        deliveryCount.value = 0L
        completeCount.value = 0L
        cancelCount.value = 0L
        exchangeCount.value = 0L
        refundCount.value = 0L
    }

    // 등록 상품 건 수
    var productCount = MutableLiveData<Long>()

    // 로그인 판매자가 등록한 상품 건 수 가져오기
    fun getProductCount() {
        productRepository.getAllProduct {
            productCount.value = it.result.childrenCount
        }
    }

    // 로그인 판매자에게 들어온 주문 상태별 건 수 가져오기
    fun getAllOrderCount(sellerUid: String) {
        viewModelScope.launch {
            val orderList = orderRepository.getOrderList()

            withContext(Dispatchers.Main) {
                // 판매자에게 들어온 전체 주문 수
                orderCount.value = orderList.count().toLong()
            }
        }

    }
}

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

