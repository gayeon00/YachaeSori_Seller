package com.yachaesori.yachaesori_seller.ui.setting

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yachaesori.yachaesori_seller.data.repository.BannerRepository
import com.yachaesori.yachaesori_seller.data.repository.ProductRepository
import com.yachaesori.yachaesori_seller.ui.product.ProductViewModel
import kotlinx.coroutines.launch

class BannerViewModel(
    private val repository: BannerRepository
) : ViewModel() {

    private val _downloadUrl = MutableLiveData<String>()
    val downloadUrl: LiveData<String> = _downloadUrl

    private val _saveResult = MutableLiveData<Boolean>()
    val saveResult: LiveData<Boolean> = _saveResult

    fun uploadBanner(uri: Uri) {
        viewModelScope.launch {
            val imageUrl = repository.uploadBannerImage(uri)
            repository.saveBanner(imageUrl)
        }
    }
}

class BannerViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BannerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return BannerViewModel(BannerRepository()) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}