package com.yachaesori.yachaesori_seller.ui.product

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.yachaesori.yachaesori_seller.R
import com.yachaesori.yachaesori_seller.data.model.Product
import com.yachaesori.yachaesori_seller.databinding.FragmentProductDetailBinding
import com.yachaesori.yachaesori_seller.ui.order.OrderManageFragmentDirections
import java.text.NumberFormat
import java.util.Locale

class ProductDetailFragment : Fragment() {
    private val productViewModel: ProductViewModel by activityViewModels {ProductViewModelFactory()}

    private var _fragmentProductDetailBinding: FragmentProductDetailBinding? = null
    private val fragmentProductDetailBinding get() = _fragmentProductDetailBinding!!

    // 상품 목록에서 전달받은 상품 식별용 uid
    lateinit var productId: String

    // 추가할 탭 TabItem
    var tabNameArray = arrayOf("상품상세", "상품후기", "Q&A")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentProductDetailBinding = FragmentProductDetailBinding.inflate(inflater)

        val args = ProductDetailFragmentArgs.fromBundle(requireArguments())
        productId = args.productId

        if(productViewModel.product.value == Product("", "", 0L, "", "", "")) {
            productViewModel.getProduct(productId)
        }

        Log.d("ProductDetailFragment", productId)

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
                val action = ProductDetailFragmentDirections.actionItemProductDetailToItemProductAdd(true)
                findNavController().navigate(action)
                true
            }

            fabProductDetail.setOnClickListener {
                // 스크롤 맨 위로 이동
                // nestedScrollViewProductDetail.smoothScrollTo(0, 0)
                appbarProductMain.setExpanded(true)
            }

        }

        // Observer
        productViewModel.product.observe(viewLifecycleOwner) { product ->
            fragmentProductDetailBinding.run {


                textViewProductName.text = product.name
                textViewProductPrice.text = "${
                    NumberFormat.getNumberInstance(Locale.getDefault()).format(product.price)
                }원"
                // 해시태그 추가
                addOption(product.options, chipGroupProductOption)
                // 리뷰 개수
                // tabNameArray[1] = "상품후기 (${product.reviewList?.size})"

                Log.d("ProductDetailFragment", productViewModel.product.value.toString())
                if (productViewModel.product.value != Product("", "", 0L, "", "", "")) {
                    // TabLayout 세팅
                    tabLayoutViewPagerSetting()

                    // 메인 이미지
                    setMainImage()
                }

            }
        }

    }

    private fun tabLayoutViewPagerSetting() {
        fragmentProductDetailBinding.run {
            // ViewPager2 어댑터 등록
            viewPagerContent.adapter = ProductDetailTabLayoutAdapter(
                this@ProductDetailFragment,
                productViewModel.product.value!!
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

    private fun setMainImage() {
        // 메인 이미지 다운 받아서 표시
        val imageStoragePath = productViewModel.product.value?.mainImageUrl
        Log.d("ProductDetailFragment", productViewModel.product.value.toString())
        if (imageStoragePath != null) {
            productViewModel.loadAndDisplayImage(
                imageStoragePath,
                fragmentProductDetailBinding.imageViewMainImage
            )
        }
    }
}

