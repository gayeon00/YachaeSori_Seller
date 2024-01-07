package com.yachaesori.yachaesori_seller.ui.product

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.yachaesori.yachaesori_seller.data.model.Product
import com.yachaesori.yachaesori_seller.ui.product.tab.ProductDetailTabFragment
import com.yachaesori.yachaesori_seller.ui.product.tab.ProductQnaTabFragment
import com.yachaesori.yachaesori_seller.ui.product.tab.ProductReviewTabFragment

private const val NUM_PAGES = 3

class ProductDetailTabLayoutAdapter(fragment: Fragment, val product: Product) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = NUM_PAGES

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ProductDetailTabFragment(product)
            1 -> ProductReviewTabFragment(product)
            2 -> ProductQnaTabFragment()
            else -> ProductDetailTabFragment(product)
        }
    }
}