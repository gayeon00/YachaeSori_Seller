package com.yachaesori.yachaesori_seller.data.repository

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.yachaesori.yachaesori_seller.data.model.Banner
import kotlinx.coroutines.tasks.await

class BannerRepository {

    private val databaseRef = FirebaseDatabase.getInstance().reference.child("banners")
    private val storageRef =
        FirebaseStorage.getInstance().reference.child("image/banner_${System.currentTimeMillis()}.jpg")


    suspend fun getBanners(): List<Banner> {
        return databaseRef.get()
            .await().children.mapNotNull { it.getValue(Banner::class.java) }
    }

    fun addBanner(banner: Banner, callback: () -> () -> Unit) {
        val bannerId = databaseRef.push().key
        bannerId?.let { id ->
            banner.id = id
            databaseRef.child(id).setValue(banner).addOnSuccessListener {
                callback()
            }
        }
    }

    fun updateBanner(banner: Banner, callback: (Task<Void>) -> Unit) {
        databaseRef.child(banner.id).setValue(banner).addOnCompleteListener {
            callback(it)
        }
    }

    fun deleteBanner(banner: Banner, callback: (Task<Void>) -> Unit) {
        databaseRef.child(banner.id).removeValue().addOnCompleteListener {
            callback(it)
        }
    }

    suspend fun uploadBannerImage(uri: Uri): String {
        storageRef.putFile(uri).await() // 업로드 완료 대기

        // 업로드된 이미지의 download URL 가져오기
        return getImageDownloadUrl(storageRef)
    }

    private suspend fun getImageDownloadUrl(storageRef: StorageReference): String {
        return try {
            val downloadUrl = storageRef.downloadUrl.await()
            downloadUrl.toString()
        } catch (e: Exception) {
            // download URL 가져오기 실패 시 예외 처리
            // 필요에 따라 로깅 또는 다른 처리를 추가할 수 있습니다.
            ""
        }
    }

    private fun getBannerCount(callback: (Long) -> Unit) {
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val childrenCount = dataSnapshot.childrenCount

                // Callback으로 전달
                callback(childrenCount)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseBanner", "Error: ${databaseError.message}")
            }
        })
    }

    /**
     * imageUrl로 Banner 객체 만들어 db에 저장
     */
    fun saveBanner(imageUrl: String) {
        val bannerId = databaseRef.push().key!!
        getBannerCount {
            val banner = Banner(bannerId, imageUrl, (it+1).toInt())
            databaseRef.child(bannerId).setValue(banner)
        }
    }
}