package com.yachaesori.yachaesori_seller.data.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.yachaesori.yachaesori_seller.data.model.Banner
import kotlinx.coroutines.tasks.await

class BannerRepository {
    private val reference = FirebaseDatabase.getInstance().reference.child("banners")

    suspend fun getBanners(): List<Banner> {
        return reference.get().await().children.mapNotNull { it.getValue(Banner::class.java) }
    }

    fun addBanner(banner: Banner, callback: (Task<Void>) -> Unit) {
        val bannerId = reference.push().key
        bannerId?.let { id ->
            banner.id = id
            reference.child(id).setValue(banner).addOnCompleteListener {
                callback(it)
            }
        }
    }

    fun updateBanner(banner: Banner, callback: (Task<Void>) -> Unit) {
        reference.child(banner.id).setValue(banner).addOnCompleteListener {
            callback(it)
        }
    }

    fun deleteBanner(banner: Banner, callback: (Task<Void>) -> Unit) {
        reference.child(banner.id).removeValue().addOnCompleteListener {
            callback(it)
        }
    }
}