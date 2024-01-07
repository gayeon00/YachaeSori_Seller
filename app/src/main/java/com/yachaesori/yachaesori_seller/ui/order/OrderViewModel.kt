package com.yachaesori.yachaesori_seller.ui.order

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yachaesori.yachaesori_seller.data.model.Order
import com.yachaesori.yachaesori_seller.data.repository.OrderRepository

class OrderViewModel(private val orderRepository: OrderRepository) : ViewModel() {
    private val _orderList = MutableLiveData<List<Order>>()
    val orderList: LiveData<List<Order>> = _orderList

    private val _order = MutableLiveData<Order>()
    val order: LiveData<Order> = _order

    private val _customer = MutableLiveData<Customer>()
    val customer: LiveData<Customer> = _customer

    init {
        _orderList.value = mutableListOf()
    }

    fun getOrderDetailByIdx(orderIdx: Long) {

    }

    fun getOrder() {
        orderRepository.getOrder {
            Log.d("order 받아온 orders", it.toString())
            _orderList.postValue(it)
        }
    }

    fun setOrderWithIdx(orderIdx: Int) {
        _order.postValue(orderList.value!![orderIdx])
    }

    fun setOrder(order: Order) {
        _order.postValue(order)
    }

    fun getCustomerByUid(uid: String){
        orderRepository.getCustomerByUid(uid) {
            _customer.postValue(it)
        }
    }
}

data class Customer(
    var email: String,
    var name: String,
    var contact: String,
)