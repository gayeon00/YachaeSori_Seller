package com.yachaesori.yachaesori_seller.ui.setting

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yachaesori.yachaesori_seller.data.model.Banner
import com.yachaesori.yachaesori_seller.data.repository.BannerRepository
import kotlinx.coroutines.launch

class BannerViewModel(
    private val repository: BannerRepository
) : ViewModel() {

    private val _bannerList = MutableLiveData<List<Banner>>()
    val bannerList: LiveData<List<Banner>> = _bannerList

    init {
        getBanners()
    }
    fun uploadBanner(uri: Uri) {
        viewModelScope.launch {
            repository.uploadBannerImage(uri)
            repository.addBanner()
        }
    }

    private fun getBanners() {
        viewModelScope.launch {
            _bannerList.value = repository.getBanners()
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