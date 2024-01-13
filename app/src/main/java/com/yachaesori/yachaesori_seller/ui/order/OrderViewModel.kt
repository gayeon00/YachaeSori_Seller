package com.yachaesori.yachaesori_seller.ui.order

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yachaesori.yachaesori_seller.data.model.Order
import com.yachaesori.yachaesori_seller.data.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderViewModel(
    private val orderRepository: OrderRepository
) : ViewModel() {
    private val _orderList = MutableLiveData<List<Order>>()
    val orderList: LiveData<List<Order>> = _orderList

    private val _order = MutableLiveData<Order>()
    val order: LiveData<Order> = _order

    init {
        getOrderList()
    }

    fun getOrderList() {
        viewModelScope.launch {
            val orderList = orderRepository.getOrderList()
            Log.d("order 받아온 orders", orderList.toString())

            withContext(Dispatchers.Main) {
                _orderList.postValue(orderList)
            }
        }

    }


    fun setOrder(order: Order) {
        _order.postValue(order)
    }
}
