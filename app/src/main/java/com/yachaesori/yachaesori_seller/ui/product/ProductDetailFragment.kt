package com.yachaesori.yachaesori_seller.ui.product

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.yachaesori.yachaesori_seller.R
import com.yachaesori.yachaesori_seller.data.model.Product
import com.yachaesori.yachaesori_seller.databinding.FragmentProductDetailBinding
import java.text.NumberFormat
import java.util.Locale

class ProductDetailFragment : Fragment() {
    private val productViewModel: ProductViewModel by activityViewModels { ProductViewModelFactory() }

    private var _fragmentProductDetailBinding: FragmentProductDetailBinding? = null
    private val fragmentProductDetailBinding get() = _fragmentProductDetailBinding!!

    // 추가할 탭 TabItem
    private val tabNameArray = arrayOf("상품상세", "상품후기", "Q&A")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentProductDetailBinding = FragmentProductDetailBinding.inflate(inflater)

        return fragmentProductDetailBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentProductDetailBinding.run {
            // Toolbar 백버튼
            toolbarProductDetail.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            //수정하기 버튼
            toolbarProductDetail.setOnMenuItemClickListener {
                productViewModel.isEditMode.value = true
                findNavController().navigate(R.id.action_item_product_detail_to_item_product_add)
                true
            }

            fabProductDetail.setOnClickListener {
                // 스크롤 맨 위로 이동
                // nestedScrollViewProductDetail.smoothScrollTo(0, 0)
                appbarProductMain.setExpanded(true)
            }

        }

        // Observer
        productViewModel.selectedProduct.observe(viewLifecycleOwner) { product ->
            fragmentProductDetailBinding.run {

                textViewProductName.text = product.name
                textViewProductPrice.text = "${
                    NumberFormat.getNumberInstance(Locale.getDefault()).format(product.price)
                }원"
                // 해시태그 추가
                addOption(product.options, chipGroupProductOption)
                // 리뷰 개수
                // TabLayout 세팅
                tabLayoutViewPagerSetting(product)

                // 메인 이미지
                setMainImage(product)

            }
        }

    }

    private fun tabLayoutViewPagerSetting(product: Product) {
        fragmentProductDetailBinding.run {
            // ViewPager2 어댑터 등록
            viewPagerContent.adapter = ProductDetailTabLayoutAdapter(
                this@ProductDetailFragment,
                product
            )

            val tabItemProductInfo: TabLayout.Tab = tabLayout.newTab()
            tabItemProductInfo.text = tabNameArray[0]
            tabLayout.addTab(tabItemProductInfo)

            val tabItemProductReview: TabLayout.Tab = tabLayout.newTab()
            tabItemProductReview.text = tabNameArray[1]
            tabLayout.addTab(tabItemProductReview)

            val tabItemProductQnA: TabLayout.Tab = tabLayout.newTab()
            tabItemProductQnA.text = tabNameArray[2]
            tabLayout.addTab(tabItemProductQnA)

            // TabLayout, ViewPager2 연동
            TabLayoutMediator(tabLayout, viewPagerContent) { tab: TabLayout.Tab, position: Int ->
                tab.text = tabNameArray[position]
            }.attach()

            // TabLayout 화면 전환 리스너
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    nestedScrollViewContent.scrollTo(0, 0)
                    viewPagerContent.currentItem = tab!!.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {}
                override fun onTabReselected(tab: TabLayout.Tab?) {}
            })
        }
    }

    private fun addOption(optionList: List<String>, chipGroup: ChipGroup) {
        for (option in optionList) {
            val chip = layoutInflater.inflate(R.layout.item_chip, chipGroup, false) as Chip
            chip.text = option
            chipGroup.addView(chip)
        }
    }

    private fun setMainImage(product: Product) {
        // 메인 이미지 다운 받아서 표시
        val imageStoragePath = product.mainImageUrl
        Log.d("ProductDetailFragment", product.toString())
        if (imageStoragePath.isNotEmpty()) {
            productViewModel.loadAndDisplayImage(
                imageStoragePath,
                fragmentProductDetailBinding.imageViewMainImage
            )
        }
    }
}

