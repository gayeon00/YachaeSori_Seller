package com.yachaesori.yachaesori_seller.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.yachaesori.yachaesori_seller.R
import com.yachaesori.yachaesori_seller.databinding.FragmentProductDetailBinding
import com.yachaesori.yachaesori_seller.databinding.ItemImageSliderBinding
import java.text.NumberFormat
import java.util.Locale

class ProductDetailFragment : Fragment() {
    private lateinit var productViewModel: ProductViewModel
    
    private var _fragmentProductDetailBinding: FragmentProductDetailBinding? = null
    private val fragmentProductDetailBinding get() = _fragmentProductDetailBinding!!

    // 상품 목록에서 전달받은 상품 식별용 uid
    lateinit var productUid : String

    // 추가할 탭 TabItem
    var tabNameArray = arrayOf("상품상세", "상품후기 (2)", "상품 Q&A")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentProductDetailBinding = FragmentProductDetailBinding.inflate(inflater)

        val args = ProductDetailFragmentArgs.fromBundle(requireArguments())
        productUid = args.productUid

        productViewModel = ViewModelProvider(this, ProductViewModelFactory())[ProductViewModel::class.java]
        productViewModel.getProduct(productUid)

        return fragmentProductDetailBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentProductDetailBinding.run {
            // Toolbar 백버튼
            toolbarProductDetail.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            // 메인 이미지 ViewPager
            viewPagerMainImage.run {
                adapter = ImageSliderAdapter()
                dotsIndicator.setViewPager2(viewPagerMainImage)
                viewPagerMainImage.adapter?.notifyDataSetChanged()
            }

            fabProductDetail.setOnClickListener {
                // 스크롤 맨 위로 이동
                // nestedScrollViewProductDetail.smoothScrollTo(0, 0)
                appbarProductMain.setExpanded(true)
            }

            textViewProductColor.run {
                val colorAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, productViewModel.product.value?.colorList!!)
                setAdapter(colorAdapter)
            }
            textViewProductSize.run {
                val sizeAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, productViewModel.product.value?.sizeList!!)
                setAdapter(sizeAdapter)
            }
        }

        // Observer
        productViewModel.run {
            product.observe(viewLifecycleOwner){ product ->
                fragmentProductDetailBinding.run {
                    if (product?.category != null) {
                        // 텍스트 정보
                        val category = product.category!!
                        textViewProductCategory.text = listOfNotNull(category.main, category.mid, category.sub).joinToString(" > ")
                        textViewProductBarndName.text = product.brandName
                        textViewProductName.text = product.name
                        textViewProductPrice.text = "${NumberFormat.getNumberInstance(Locale.getDefault()).format(product.price)}원"
                        // 해시태그 추가
                        addHashTag(product.hashTag!!, chipGroupProductHashTag)
                        // 색상, 사이즈 리스트
                        val colorAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, product.colorList!!)
                        val sizeAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, product.sizeList!!)
                        textViewProductColor.setAdapter(colorAdapter)
                        textViewProductSize.setAdapter(sizeAdapter)
                        // 리뷰 개수
                        // tabNameArray[1] = "상품후기 (${product.reviewList?.size})"

                        // TabLayout 세팅
                        tabLayoutViewPagerSetting()

                        // 메인 이미지
                        viewPagerMainImage.adapter?.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    private fun tabLayoutViewPagerSetting(){
        fragmentProductDetailBinding.run {
            // ViewPager2 어댑터 등록
            viewPagerContent.adapter = ProductDetailTabLayoutAdapter(this@ProductDetailFragment, productViewModel.product.value!!)

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
            TabLayoutMediator(tabLayout, viewPagerContent){ tab: TabLayout.Tab, position: Int ->
                tab.text = tabNameArray[position]
            }.attach()

            // TabLayout 화면 전환 리스너
            tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    nestedScrollViewContent.scrollTo(0, 0)
                    viewPagerContent.currentItem = tab!!.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) { }
                override fun onTabReselected(tab: TabLayout.Tab?) { }
            })
        }
    }

    private fun addHashTag(hashTagList: List<String>, chipGroup: ChipGroup){
        for(hashTag in hashTagList) {
            val chip = layoutInflater.inflate(R.layout.item_chip, chipGroup, false) as Chip
            chip.text = hashTag
            chipGroup.addView(chip)
        }
    }

    // 이미지의 Storage URL -> ImageView에 표시
    inner class ImageSliderAdapter(): RecyclerView.Adapter<ImageSliderAdapter.ImageSliderViewHolder>() {
        inner class ImageSliderViewHolder(binding: ItemImageSliderBinding): RecyclerView.ViewHolder(binding.root){
            val imageView: ImageView = binding.imageViewSlider
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageSliderViewHolder {
            val binding = ItemImageSliderBinding.inflate(layoutInflater)
            binding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            return ImageSliderViewHolder(binding)
        }

        override fun getItemCount(): Int {
            return productViewModel.product.value?.mainImage!!.size
        }

        override fun onBindViewHolder(holder: ImageSliderViewHolder, position: Int) {
            // 메인 이미지 다운 받아서 표시
            val imageStoragePath = productViewModel.product.value?.mainImage!![position]
            productViewModel.loadAndDisplayImage(imageStoragePath, holder.imageView)
        }
    }
}

