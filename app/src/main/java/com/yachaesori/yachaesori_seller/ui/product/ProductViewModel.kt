package com.yachaesori.yachaesori_seller.ui.product

import android.net.Uri
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.yachaesori.yachaesori_seller.R
import com.yachaesori.yachaesori_seller.data.model.Image
import com.yachaesori.yachaesori_seller.data.model.Product
import com.yachaesori.yachaesori_seller.data.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductViewModel(private val productRepository: ProductRepository) : ViewModel() {
    private val _productList = MutableLiveData<List<Product>>()
    val productList: LiveData<List<Product>> = _productList

    private val _productCount = MutableLiveData<Long>()
    val productCount: LiveData<Long> = _productCount

    var selectedProduct: MutableLiveData<Product> = MutableLiveData()
    var isEditMode: MutableLiveData<Boolean> = MutableLiveData()


    init {
        _productList.value = mutableListOf<Product>()
        _productCount.value = 0L
        selectedProduct.value = Product("", "", 0L, "", "", "")
        isEditMode.value = false
    }

    fun setSelectedProduct(product: Product) {
        selectedProduct.value = product
    }

    // 판매자가 등록한 상품 리스트 가져오기 (상품 목록용)
    fun getAllProduct() {
        productRepository.getAllProduct { task ->
            val tempProductList = mutableListOf<Product>()

            for (productSnapshot in task.result.children) {
                val product = productSnapshot.getValue(Product::class.java)
                if (product != null) {
                    tempProductList.add(product)
                }
            }
            _productList.value = tempProductList.sortedByDescending { it.regDate }  // 기존 최신순 정렬
        }
    }

    // 로그인 판매자가 등록한 상품 건 수 가져오기 (빠른 카운팅용)
    fun getProductCount() {
        productRepository.getAllProduct {
            Log.d("what allProduct", it.result.value.toString())
            _productCount.value = it.result.childrenCount
        }
    }

    fun saveOrUpdateProduct(product: Product) {
        if (isEditMode.value!!) {
            viewModelScope.launch {
                productRepository.updateProduct(selectedProduct.value!!, product)
            }

        } else {
            viewModelScope.launch {
                productRepository.addProduct(product) {}
            }
        }

    }

    // Storage 이미지 업로드
    fun uploadImage(image: Image) {
        val uri = Uri.parse(image.uriString)
        productRepository.uploadImage(uri, image.fileName!!) {}
    }

    // Storage 이미지 여러 개 업로드
    fun uploadImageList(images: ArrayList<Image>) {
        for (image in images) {
            val uri = Uri.parse(image.uriString)
            productRepository.uploadImage(uri, image.fileName!!) {}
        }
    }

    // Storage 이미지 ImageView에 매칭 (Glide 라이브러리)
    fun loadImage(storagePath: String, onImageLoaded: (Uri) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val uri = productRepository.downloadImage(storagePath)

            launch(Dispatchers.Main) {
                onImageLoaded(uri)
            }
        }
    }
}