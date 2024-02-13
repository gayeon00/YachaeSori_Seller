package com.yachaesori.yachaesori_seller.data.repository

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.yachaesori.yachaesori_seller.data.model.Product
import kotlinx.coroutines.tasks.await

class ProductRepository {

    private val productsRef: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("products")
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    // 전체 상품 가져오기
    fun getAllProduct(callback: (Task<DataSnapshot>) -> Unit) {
        productsRef.orderByChild("productId").get().addOnCompleteListener(callback)
    }

    // 특정 상품 가져오기
    fun getProductByProductId(productId: String, callback: (Task<DataSnapshot>) -> Unit) {
        productsRef.orderByChild("productId").equalTo(productId).get()
            .addOnCompleteListener(callback)
    }

    // 상품 정보 저장
    fun addProduct(product: Product, callback: (Task<Void>) -> Unit) {
        // Product 객체 삽입 후 랜덤 세팅된 key값을 Product객체 내 productId로 저장
        val pushedRef = productsRef.push()
        product.productId = pushedRef.key!!
        pushedRef.setValue(product).addOnCompleteListener(callback)
    }

    // 상품 정보 수정
    fun updateProduct(oldProduct: Product, newProduct: Product) {
        oldProduct.productId.let {
            Log.d("what productId", it)
            newProduct.productId = productsRef.child(it).key!!
            //등록일을 최종 수정일로 업데이트하지 않고 그대로 유지
//            newProduct.regDate = oldProduct.regDate
            productsRef.child(it).setValue(newProduct)
        }

    }

    // 특정 파일명으로 이미지 업로드
    fun uploadImage(
        uploadUri: Uri,
        storagePath: String,
        callback: (Task<UploadTask.TaskSnapshot>) -> Unit
    ) {
        val imageRef = storage.reference.child(storagePath)
        imageRef.putFile(uploadUri).addOnCompleteListener(callback)
    }

    // 특정 파일명의 이미지 다운로드
    suspend fun downloadImage(storagePath: String): Uri {
        return storage.reference.child(storagePath).downloadUrl.await()
    }


}