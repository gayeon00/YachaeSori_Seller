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

    val code = System.currentTimeMillis()

    private val databaseRef = FirebaseDatabase.getInstance().reference.child("banners")
    private val storageRef =
        FirebaseStorage.getInstance().reference.child("image/banner_${code}.jpg")


    suspend fun getBanners(): List<Banner> {
        return databaseRef.get()
            .await().children.mapNotNull { it.getValue(Banner::class.java) }
    }

    fun addBanner(banner: Banner, callback: () -> Unit) {
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

    fun deleteBanner(banner: Banner) {
        databaseRef.child(banner.id).removeValue()
        //순서 재조정
        updateBannerOrderAfterDeletion(banner.order)

    }

    // 삭제된 Banner 이후의 모든 Banner를 가져와 순서를 갱신
    private fun updateBannerOrderAfterDeletion(deletedOrder: Int) {
        databaseRef.orderByChild("order").startAt(deletedOrder.toDouble()).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (childSnapshot in dataSnapshot.children) {
                        val banner = childSnapshot.getValue(Banner::class.java)
                        banner?.let {
                            // 현재 순서를 1 감소시킴
                            it.order = it.order - 1
                            // 변경된 Banner를 다시 저장
                            databaseRef.child(childSnapshot.key!!).setValue(it)
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("YourRepository", "Error: ${databaseError.message}")
                }
            })
    }

    suspend fun uploadBannerImage(uri: Uri){
        storageRef.putFile(uri).await() // 업로드 완료 대기
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
    fun addBanner() {
        val bannerId = databaseRef.push().key!!
        getBannerCount {
            val banner = Banner(bannerId, "image/banner_${code}.jpg", (it+1).toInt())
            databaseRef.child(bannerId).setValue(banner)
        }
    }

    fun updateBannerOrder(bannerList: List<Banner>) {
        val databaseBanners = mutableMapOf<String, Banner>()

        // 데이터베이스에서 가져온 banners를 Map으로 변환
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val banner = snapshot.getValue(Banner::class.java)
                    banner?.let { databaseBanners[snapshot.key!!] = it }
                }

                // bannerList와 데이터베이스의 order를 비교하고 업데이트
                for (localBanner in bannerList) {
                    val databaseBanner = databaseBanners[localBanner.id]
                    if (databaseBanner != null && localBanner.order != databaseBanner.order) {
                        // order가 다르면 업데이트
                        updateOrder(localBanner.id, localBanner.order)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // 오류 처리
                 Log.e("FirebaseBanner", "Error: ${databaseError.message}")
            }
        })
    }

    private fun updateOrder(bannerId: String, newOrder: Int) {
        databaseRef.child(bannerId).child("order").setValue(newOrder)
    }
}