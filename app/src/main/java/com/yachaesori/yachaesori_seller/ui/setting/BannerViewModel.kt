package com.yachaesori.yachaesori_seller.ui.setting

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yachaesori.yachaesori_seller.data.model.Banner
import com.yachaesori.yachaesori_seller.data.repository.BannerRepository
import kotlinx.coroutines.launch

class BannerViewModel(
    private val repository: BannerRepository
) : ViewModel() {

    private val _bannerList = MutableLiveData<List<Banner>>()
    val bannerList: LiveData<List<Banner>> = _bannerList

    private val databaseRef = FirebaseDatabase.getInstance().reference.child("banners")

    //realtimedatabase에서 데이터가 변경되면 observe해서 bannerlist에 반영
    private val valueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            val banners = dataSnapshot.children.mapNotNull { it.getValue(Banner::class.java) }
            _bannerList.value = banners
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // 오류 처리
             Log.e("FirebaseBanner", "Error: ${databaseError.message}")
        }
    }
    init {
        // 초기화 시에 데이터베이스에 ValueEventListener을 등록합니다.
        databaseRef.orderByChild("order").addValueEventListener(valueEventListener)
    }

    // ViewModel이 소멸될 때 ValueEventListener을 제거합니다.
    override fun onCleared() {
        super.onCleared()
        databaseRef.removeEventListener(valueEventListener)
    }

    fun uploadBanner(uri: Uri) {
        viewModelScope.launch {
            repository.uploadBannerImage(uri)
            repository.addBanner()
        }
    }

    fun updateBannerOrder(bannerList: List<Banner>) {
        for (i in bannerList.indices) {
            if(bannerList[i].order != i+1) {
                bannerList[i].order = i+1
            }
        }
        viewModelScope.launch {
            repository.updateBannerOrder(bannerList)
        }
    }

    fun deleteBanner(it: Banner) {
        repository.deleteBanner(it)
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