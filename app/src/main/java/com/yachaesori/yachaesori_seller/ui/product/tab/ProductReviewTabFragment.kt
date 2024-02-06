package com.yachaesori.yachaesori_seller.ui.product.tab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.yachaesori.yachaesori_seller.R
import com.yachaesori.yachaesori_seller.data.model.Product
import com.yachaesori.yachaesori_seller.data.model.Review
import com.yachaesori.yachaesori_seller.databinding.FragmentProductReviewTabBinding
import com.yachaesori.yachaesori_seller.databinding.FragmentProductReviewTabItemBinding
import com.yachaesori.yachaesori_seller.ui.product.ProductViewModel
import com.yachaesori.yachaesori_seller.ui.product.ProductViewModelFactory

class ProductReviewTabFragment(product: Product) : Fragment() {
    private lateinit var productViewModel: ProductViewModel

    private var _fragmentProductReviewTabBinding: FragmentProductReviewTabBinding? = null
    private val fragmentProductReviewTabBinding get() = _fragmentProductReviewTabBinding!!

    val reviewList = mutableListOf<Review>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentProductReviewTabBinding = FragmentProductReviewTabBinding.inflate(inflater)
        productViewModel =
            ViewModelProvider(this, ProductViewModelFactory())[ProductViewModel::class.java]
        return fragmentProductReviewTabBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setSampleData()

        // TODO 상품에 등록된 리뷰 정보 가져오기

        // 클릭한 Chip 버튼에 따라 BottomSheetDialog 표시
        fragmentProductReviewTabBinding.run {
            // 평점 분포 변경 터치 막기 (단순 표시용)
            seekBarVeryGood.setOnTouchListener { _, _ -> true }
            seekBarLike.setOnTouchListener { _, _ -> true }
            seekBarSoso.setOnTouchListener { _, _ -> true }
            seekBarJustOk.setOnTouchListener { _, _ -> true }
            seekBarNotMuch.setOnTouchListener { _, _ -> true }

            val reviewCount = reviewList.count()
            val countVeryGood = reviewList.count { it.rating == 5.0 }
            val countLike = reviewList.count { it.rating >= 4.0 && it.rating < 5.0 }
            val countSoso = reviewList.count { it.rating >= 3.0 && it.rating < 4.0 }
            val countJustOk = reviewList.count { it.rating >= 2.0 && it.rating < 3.0 }
            val countNotMuch = reviewList.count { it.rating >= 1.0 && it.rating < 2.0 }

            textViewVeryGoodCount.text = countVeryGood.toString()
            textViewLikeCount.text = countLike.toString()
            textViewSosoCount.text = countSoso.toString()
            textViewJuskOkCount.text = countJustOk.toString()
            textViewNotMuchCount.text = countNotMuch.toString()

            seekBarVeryGood.progress = ((countVeryGood / reviewCount.toDouble()) * 100).toInt()
            seekBarLike.progress = ((countLike / reviewCount.toDouble()) * 100).toInt()
            seekBarSoso.progress = ((countSoso / reviewCount.toDouble()) * 100).toInt()
            seekBarJustOk.progress = ((countJustOk / reviewCount.toDouble()) * 100).toInt()
            seekBarNotMuch.progress = ((countNotMuch / reviewCount.toDouble()) * 100).toInt()

            textViewReviewCount.text = "(${reviewList.size})"

            textViewReviewAvg.text = reviewList.map { it.rating }.average().toString()

            chipStarRating.setOnClickListener {
                showBottomSheet(R.layout.fragment_product_review_tab_bottom_sheet_star)
            }
            chipHeight.setOnClickListener {
                showBottomSheet(R.layout.fragment_product_review_tab_bottom_sheet_height)
            }
            chipWeight.setOnClickListener {
                showBottomSheet(R.layout.fragment_product_review_tab_bottom_sheet_weight)
            }
            chipDefaultSize.setOnClickListener {
                showBottomSheet(R.layout.fragment_product_review_tab_bottom_sheet_size)
            }

            recyclerViewReview.run {
                adapter = ReviewRecyclerViewAdapter()
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(
                    MaterialDividerItemDecoration(
                        requireContext(),
                        MaterialDividerItemDecoration.VERTICAL
                    )
                )
            }
        }
    }

    private fun setSampleData() {
        // TODO 리뷰 기능 구현시 작성자 이름 대신 cutomerUid 값으로 데이터 읽기
        reviewList.add(
            Review(
                "1", "사자", 180L, 90L, 4.0, "2023.08.25",
                "몸에 딱 맞아요. 잘 산 것 같습니다!", "image/review_sample_image.jpg", "XL", "블랙"
            )
        )
        reviewList.add(
            Review(
                "2", "이멋사", 163L, 60L, 3.0, "2023.08.25",
                "품질이 최고입니다ㅎㅎ", "image/review_sample_image2.jpg", "M", "그린"
            )
        )
    }

    // 리뷰 필터링 BottomSheet 표시
    private fun showBottomSheet(layout: Int) {
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(layout)
        bottomSheetDialog.show()
    }

    inner class ReviewRecyclerViewAdapter :
        RecyclerView.Adapter<ReviewRecyclerViewAdapter.ReviewViewHolder>() {
        inner class ReviewViewHolder(private val binding: FragmentProductReviewTabItemBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(review: Review) {
                // TODO UserViewModel 사용해서 리뷰 작성자 uid로 고객 정보 얻기
                binding.textViewReviewNickname.text = review.customerUid
                binding.textViewReviewDate.text = review.date
                binding.ratingBarReview.rating = review.rating.toFloat()
                productViewModel.loadImage(review.Image!!) {
                    Glide.with(binding.imageViewReview.context)
                        .load(it)
                        .placeholder(R.drawable.loading_placeholder)
                        .fitCenter()
                        .into(binding.imageViewReview)
                }
                binding.textViewReviewInfo.text = "${review.height}cm · ${review.weight}kg"
                binding.textViewReviewOption.text = "${review.color} [${review.size}]"
                binding.textViewReviewContent.text = review.content
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
            val binding = FragmentProductReviewTabItemBinding.inflate(layoutInflater)
            binding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            return ReviewViewHolder(binding)
        }

        override fun getItemCount(): Int {
            return reviewList.size
        }

        override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
            val review = reviewList[position]
            holder.bind(review)
        }
    }
}