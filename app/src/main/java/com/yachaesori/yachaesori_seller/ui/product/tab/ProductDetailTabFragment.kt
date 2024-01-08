package com.yachaesori.yachaesori_seller.ui.product.tab

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.yachaesori.yachaesori_seller.data.model.Product
import com.yachaesori.yachaesori_seller.databinding.FragmentProductDetailTabBinding
import com.yachaesori.yachaesori_seller.ui.product.ProductViewModel
import com.yachaesori.yachaesori_seller.ui.product.ProductViewModelFactory

class ProductDetailTabFragment(val product: Product) : Fragment() {
    private lateinit var productViewModel: ProductViewModel

    private var _fragmentProductDetailTabBinding: FragmentProductDetailTabBinding? = null
    private val fragmentProductDetailTabBinding get() = _fragmentProductDetailTabBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentProductDetailTabBinding = FragmentProductDetailTabBinding.inflate(inflater)
        productViewModel = ViewModelProvider(this, ProductViewModelFactory())[ProductViewModel::class.java]

        return fragmentProductDetailTabBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentProductDetailTabBinding.run {
            productViewModel.loadAndDisplayImage(product.detailImageUrl!!, imageViewProductDescription)
        }
    }

}