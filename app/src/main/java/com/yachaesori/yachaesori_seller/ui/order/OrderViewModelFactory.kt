package com.yachaesori.yachaesori_seller.ui.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yachaesori.yachaesori_seller.data.repository.OrderRepository


class OrderViewModelFactory : ViewModelProvider.Factory {

    //Unchecked cast 경고 무시
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrderViewModel::class.java)) {
            return OrderViewModel(OrderRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}