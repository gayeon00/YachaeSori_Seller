package com.yachaesori.yachaesori_seller.data.repository

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.yachaesori.yachaesori_seller.data.model.Product

class ProductRepository {

    private val productsRef : DatabaseReference = FirebaseDatabase.getInstance().getReference("products")
    private val storage : FirebaseStorage = FirebaseStorage.getInstance()

    // 전체 상품 가져오기
    fun getAllProduct(callback: (Task<DataSnapshot>) -> Unit){
        productsRef.orderByChild("productId").get().addOnCompleteListener(callback)
    }

    // 특정 상품 가져오기
    fun getProductByProductId(productId: String, callback: (Task<DataSnapshot>) -> Unit){
        productsRef.orderByChild("productId").equalTo(productId).get().addOnCompleteListener(callback)
    }

    // 상품 정보 저장
    fun addProduct(product: Product, callback: (Task<Void>) -> Unit) {
        // Product 객체 삽입 후 랜덤 세팅된 key값을 Product객체 내 productId로 저장
        val pushedRef = productsRef.push()
        product.productId = pushedRef.key!!
        pushedRef.setValue(product).addOnCompleteListener(callback)
    }

    // 상품 정보 수정
    fun modifyProduct(product: Product, isNewImage: Boolean, callback: (Task<Void>) -> Unit) {
        productsRef.orderByChild("productId").equalTo(product.productId).get()
            .addOnCompleteListener {
                for (dataSnapshot in it.result.children) {
                    // 수정 가능한 항목만 값 수정
                    // code, sellerIdx, orderCount, regDate 등은 고정 값 / reviewList는 판매자가 조작할 수 없는 값
                    if (isNewImage) {
                        dataSnapshot.ref.child("mainImage").setValue(product.mainImageUrl)
                        dataSnapshot.ref.child("descriptionImage")
                            .setValue(product.detailImageUrl)
                    }
                    dataSnapshot.ref.child("name").setValue(product.name)
                    dataSnapshot.ref.child("price").setValue(product.price)
                    dataSnapshot.ref.child("options").setValue(product.options).addOnCompleteListener(callback)
                }
            }
    }

    // 상품 정보 삭제
    fun deleteProduct(productId: String, callback: (Task<Void>) -> Unit) {
        productsRef.orderByChild("productId").equalTo(productId).get()
            .addOnCompleteListener {
                for (dataSnapshot in it.result.children) {
                    dataSnapshot.ref.removeValue().addOnCompleteListener(callback)
                }
            }
    }

    // 특정 파일명으로 이미지 업로드
    fun uploadImage(uploadUri: Uri, storagePath: String, callback: (Task<UploadTask.TaskSnapshot>) -> Unit) {
        val imageRef = storage.reference.child(storagePath)
        imageRef.putFile(uploadUri).addOnCompleteListener(callback)
    }

    // 특정 파일명의 이미지 다운로드
    fun downloadImage(storagePath: String, callback: (Task<Uri>) -> Unit) {
        storage.reference.child(storagePath).downloadUrl.addOnCompleteListener(callback)
    }
}