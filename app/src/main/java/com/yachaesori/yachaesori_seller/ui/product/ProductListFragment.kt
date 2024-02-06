package com.yachaesori.yachaesori_seller.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.yachaesori.yachaesori_seller.R
import com.yachaesori.yachaesori_seller.data.model.Product
import com.yachaesori.yachaesori_seller.databinding.FragmentProductListBinding
import com.yachaesori.yachaesori_seller.databinding.ItemProductListBinding
import java.text.NumberFormat
import java.util.Locale

class ProductListFragment : Fragment() {
    private val productViewModel: ProductViewModel by activityViewModels { ProductViewModelFactory() }
    private var _fragmentProductListBinding: FragmentProductListBinding? = null

    private val fragmentProductListBinding get() = _fragmentProductListBinding!!

    //    lateinit var filterArray: Array<String>
    private lateinit var sortArray: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentProductListBinding = FragmentProductListBinding.inflate(inflater)

        productViewModel.getAllProduct()

//        fragmentProductListBinding.run {
//            if (productViewModel.productList.value!!.isEmpty()) {
//                linearLayoutNoProduct.visibility = View.VISIBLE
//                recyclerViewProductList.visibility = View.GONE
//            } else {
//                linearLayoutNoProduct.visibility = View.GONE
//                recyclerViewProductList.visibility = View.VISIBLE
//            }
//        }

        return fragmentProductListBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 드롭다운 데이터셋
//        filterArray = resources.getStringArray(R.array.productFilter)
        sortArray = resources.getStringArray(R.array.productSort)

        productViewModel.productList.observe(viewLifecycleOwner) {
            fragmentProductListBinding.run {
                progressBarProductList.visibility = View.GONE
                if (it.isEmpty()) {
                    linearLayoutNoProduct.visibility = View.VISIBLE
                    recyclerViewProductList.visibility = View.GONE
                } else {
                    linearLayoutNoProduct.visibility = View.GONE
                    recyclerViewProductList.visibility = View.VISIBLE

                    // productList 데이터가 변경되면 RecyclerView 베이스 데이터 새로 세팅
                    recyclerViewProductList.run {
                        adapter = ProductRecyclerViewAdapter(it)
                        layoutManager = LinearLayoutManager(requireContext())
                        addItemDecoration(
                            MaterialDividerItemDecoration(
                                context,
                                MaterialDividerItemDecoration.VERTICAL
                            )
                        )

                        // 필터링, 정렬 리스트 유지
//                            autoTextViewFilter.setSimpleItems(filterArray)
                        autoTextViewSort.setSimpleItems(sortArray)

                        // 상품 상세에서 백버튼으로 돌아올 때 필터링, 정렬 유지
//                            onFilterTextChanged()
                        onSortTextChanged()
                    }
                }
            }

        }

        fragmentProductListBinding.run {
            toolbarProductList.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            fabProductList.setOnClickListener {
                // RecyclerView 0번째 항목으로 이동
                recyclerViewProductList.smoothScrollToPosition(0)
            }

            recyclerViewProductList.run {
                adapter = ProductRecyclerViewAdapter(productViewModel.productList.value!!)
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(
                    MaterialDividerItemDecoration(
                        context,
                        MaterialDividerItemDecoration.VERTICAL
                    )
                )
            }

//            // 필터링
//            autoTextViewFilter.run {
//                autoTextViewFilter.setSimpleItems(filterArray)
//                addTextChangedListener {
//                    onFilterTextChanged()
//                    // 필터링으로 인해 바뀐 데이터 셋에 대해 기존 정렬 기준 다시 적용
//                    onSortTextChanged()
//                }
//            }

            // 정렬
            autoTextViewSort.run {
                autoTextViewSort.setSimpleItems(sortArray)
                addTextChangedListener { onSortTextChanged() }
            }
        }
    }

//    private fun onFilterTextChanged() {
//        fragmentProductListBinding.run {
//            val selectedFilterItem = autoTextViewFilter.text.toString()
//            val recyclerViewAdapter = recyclerViewProductList.adapter as ProductRecyclerViewAdapter
//            recyclerViewAdapter.filter(selectedFilterItem)
//        }
//    }

    private fun onSortTextChanged() {
        fragmentProductListBinding.run {
            val selectedSortItem = autoTextViewSort.text.toString()
            val recyclerViewAdapter = recyclerViewProductList.adapter as ProductRecyclerViewAdapter
            when (selectedSortItem) {
                sortArray[0] -> {
                    recyclerViewAdapter.sort("regDate", true)
                }

                sortArray[1] -> {
                    recyclerViewAdapter.sort("regDate", false)
                }

                sortArray[2] -> {
                    recyclerViewAdapter.sort("name", false)
                }

                sortArray[3] -> {
                    recyclerViewAdapter.sort("price", false)
                }

                sortArray[4] -> {
                    recyclerViewAdapter.sort("price", true)
                }
            }
        }
    }

    inner class ProductRecyclerViewAdapter(private var productList: List<Product>) :
        Adapter<ProductRecyclerViewAdapter.ProductViewHolder>() {
        // 필터링된 상품 리스트를 저장할 변수 (default : 전체, 최신순)
        private var filteredProductList = productList.sortedByDescending { it.regDate }

        inner class ProductViewHolder(rowProductBinding: ItemProductListBinding) :
            ViewHolder(rowProductBinding.root) {
            val imageViewProduct = rowProductBinding.imageViewProduct
            val textViewName = rowProductBinding.textViewName

            //            val textViewCategory = rowProductBinding.textViewCategory
            val textViewRegDate = rowProductBinding.textViewRegDate

            //            val textViewHashTag = rowProductBinding.textViewHashTag
            val textViewPrice = rowProductBinding.textViewPrice

            init {
                // 상품 상세 화면으로 이동
                rowProductBinding.root.setOnClickListener {
                    productViewModel.setSelectedProduct(filteredProductList[adapterPosition])
                    findNavController().navigate(R.id.action_item_product_list_to_item_product_detail)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            val rowProductBinding = ItemProductListBinding.inflate(layoutInflater)
            rowProductBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            return ProductViewHolder(rowProductBinding)
        }

        override fun getItemCount(): Int {
            return filteredProductList.size
        }

        override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
            val product = filteredProductList[position]
//            val category = product.category!!
            // 다음 화면으로 넘겨줄 uid
            // holder.productUid = product.productUid.toString()

            // 서버로 부터 이미지를 내려받아 ImageView에 표시
            productViewModel.loadImage(product.mainImageUrl) {
                Glide.with(holder.imageViewProduct.context)
                    .load(it)
                    .placeholder(R.drawable.loading_placeholder)
                    .fitCenter()
                    .into(holder.imageViewProduct)

            }


            holder.textViewName.text = product.name
//            // 카테고리, 태그 분리 후 구분자 넣어서 결합
//            holder.textViewCategory.text =
//                listOfNotNull(category.main, category.mid, category.sub).joinToString(" > ")
//            holder.textViewHashTag.text = product.hashTag?.joinToString(" ") { "#$it" }
            // 가격 천의 자리에 쉼표 찍기
            holder.textViewPrice.text =
                "${NumberFormat.getNumberInstance(Locale.getDefault()).format(product.price)}원"
            holder.textViewRegDate.text = "${product.regDate}"
        }

        // RecyclerView 데이터 필터링
//        fun filter(category: String) {
//            filteredProductList = if (category != "전체") {
//                productList.filter { it.category?.main == category }
//            } else {
//                productList
//            }
//            notifyDataSetChanged()
//        }

        // RecyclerView 데이터 정렬 (정렬 기준 키, 내림차순 여부)
        fun sort(sortKey: String, isDescending: Boolean) {
            filteredProductList = when (sortKey) {
                "regDate" -> {
                    if (isDescending) {
                        filteredProductList.sortedByDescending { it.regDate }
                    } else {
                        filteredProductList.sortedBy { it.regDate }
                    }
                }

                "name" -> {
                    filteredProductList.sortedBy { it.name }
                }

                "price" -> {
                    if (isDescending) {
                        filteredProductList.sortedByDescending { it.price }
                    } else {
                        filteredProductList.sortedBy { it.price }
                    }
                }

                else -> {
                    filteredProductList
                }
            }
            notifyDataSetChanged()
        }
    }
}