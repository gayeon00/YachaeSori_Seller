package com.yachaesori.yachaesori_seller.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yachaesori.yachaesori_seller.data.repository.ProductRepository

class ProductViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductViewModel(ProductRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
