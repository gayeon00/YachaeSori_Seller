package com.yachaesori.yachaesori_seller.ui.product

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.yachaesori.yachaesori_seller.R
import com.yachaesori.yachaesori_seller.data.model.Image
import com.yachaesori.yachaesori_seller.data.model.Product
import com.yachaesori.yachaesori_seller.databinding.FragmentProductOptionBottomSheetBinding
import com.yachaesori.yachaesori_seller.databinding.FragmentProductOptionBinding
import com.yachaesori.yachaesori_seller.databinding.ItemProductOptionBinding

class ProductOptionFragment : Fragment() {
    private lateinit var productViewModel: ProductViewModel

    private var _fragmentProductOptionBinding: FragmentProductOptionBinding? = null
    private val fragmentProductOptionBinding get() = _fragmentProductOptionBinding!!

    lateinit var product: Product
    private lateinit var imageArray: Array<Image>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentProductOptionBinding = FragmentProductOptionBinding.inflate(inflater)
        productViewModel = ViewModelProvider(this, ProductViewModelFactory())[ProductViewModel::class.java]

        // 이전 프래그먼트에서 전달받은 값
        val args = ProductOptionFragmentArgs.fromBundle(requireArguments())
        product = args.product
        imageArray = args.imageArray

        fragmentProductOptionBinding.run {
            textViewOptionGuide.visibility = View.VISIBLE
            linearLayoutOption.visibility = View.INVISIBLE
        }

        return fragmentProductOptionBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentProductOptionBinding.run {
            toolbarOption.run {
                setNavigationOnClickListener {
                    findNavController().popBackStack()
                }
            }

            productViewModel.run {
                // 추가된 색상, 사이즈 옵션이 없을 경우 안내 문구 표시
                colorList.observe(viewLifecycleOwner){
                    if (productViewModel.sizeList.value!!.isEmpty() && productViewModel.colorList.value!!.isEmpty()) {
                        textViewOptionGuide.visibility = View.VISIBLE
                        linearLayoutOption.visibility =View.INVISIBLE
                    } else {
                        textViewOptionGuide.visibility = View.GONE
                        linearLayoutOption.visibility =View.VISIBLE
                    }
                    recyclerViewColor.adapter?.notifyDataSetChanged()
                }
                sizeList.observe(viewLifecycleOwner){
                    if (productViewModel.sizeList.value!!.isEmpty() && productViewModel.colorList.value!!.isEmpty()) {
                        textViewOptionGuide.visibility = View.VISIBLE
                        linearLayoutOption.visibility =View.INVISIBLE
                    } else {
                        textViewOptionGuide.visibility = View.GONE
                        linearLayoutOption.visibility =View.VISIBLE
                    }
                    recyclerViewSize.adapter?.notifyDataSetChanged()
                }
            }

            buttonShowBottomSheet.setOnClickListener {
                // ModalBottomSheet에서 추가 버튼 클릭시 처리 리스너 등록
                val modalBottomSheetProduct = ModalBottomSheetProduct()
                modalBottomSheetProduct.setOnOptionSelectionListener(object : ModalBottomSheetProduct.OnOptionSelectedListener {
                    override fun onOptionSelected(colorList: List<Int>, sizeList: List<Int>) {
                        // 색상, 사이즈 리스트 저장
                        productViewModel.addColorAndSizeOption(colorList, sizeList)
                    }
                })

                modalBottomSheetProduct.show(requireActivity().supportFragmentManager, modalBottomSheetProduct.tag)
            }

            recyclerViewColor.run {
                adapter = ColorRecyclerViewAdapter()
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            }

            recyclerViewSize.run {
                adapter = SizeRecyclerViewAdapter()
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            }

            // 상품 등록
            buttonRegisterProduct.setOnClickListener {
                progressBarOption.visibility = View.VISIBLE

                // 전달받은 product 객체에 colorList, sizeList 정보 세팅
                product.colorList = productViewModel.colorList.value?.map { input ->
                    ProductColor.values().firstOrNull { it.code == input }?.str ?: ""
                }
                product.sizeList = productViewModel.sizeList.value?.map { input ->
                    ProductSize.values().firstOrNull { it.code == input }?.str ?: ""
                }

                // 상품 정보 DB 저장
                productViewModel.addProduct(product)
                // 이미지 업로드
                productViewModel.uploadImageList(imageArray)

                Toast.makeText(context, "상품이 등록 되었습니다", Toast.LENGTH_LONG).show()
                findNavController().popBackStack(R.id.item_home, false)
            }
        }

    }

    // 선택한 색상, 사이즈 목록 표시
    // 색상 목록 RecyclerView Adapter
    inner class ColorRecyclerViewAdapter() : RecyclerView.Adapter<ColorRecyclerViewAdapter.ColorViewHolder>() {
        inner class ColorViewHolder(itemProductOptionBinding:ItemProductOptionBinding): RecyclerView.ViewHolder(itemProductOptionBinding.root){
            val textViewOption = itemProductOptionBinding.textViewOption
            private val buttonDeleteOption = itemProductOptionBinding.buttonDeleteOption

            init {
                buttonDeleteOption.setOnClickListener {
                    // 색상 삭제
                    productViewModel.deleteColorOption(adapterPosition)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
            val rowOptionBinding = ItemProductOptionBinding.inflate(layoutInflater)
            val colorViewHolder = ColorViewHolder(rowOptionBinding)
            rowOptionBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            return colorViewHolder
        }

        override fun getItemCount(): Int {
            return productViewModel.colorList.value!!.size
        }

        override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
            val currentCode = productViewModel.colorList.value!![position]
            holder.textViewOption.text = ProductColor.values().firstOrNull { it.code == currentCode }?.str
        }
    }

    // 사이즈 목록 RecyclerView Adapter
    inner class SizeRecyclerViewAdapter() : RecyclerView.Adapter<SizeRecyclerViewAdapter.SizeViewHolder>() {
        inner class SizeViewHolder(rowOptionBinding:ItemProductOptionBinding): RecyclerView.ViewHolder(rowOptionBinding.root){
            val textViewOption = rowOptionBinding.textViewOption
            val buttonDeleteOption = rowOptionBinding.buttonDeleteOption
            init {
                buttonDeleteOption.setOnClickListener {
                    // 색상 삭제
                    productViewModel.deleteSizeOption(adapterPosition)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeViewHolder {
            val rowOptionBinding = ItemProductOptionBinding.inflate(layoutInflater)
            val sizeViewHolder = SizeViewHolder(rowOptionBinding)
            rowOptionBinding.root.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            return sizeViewHolder
        }

        override fun getItemCount(): Int {
            return productViewModel.sizeList.value!!.size
        }

        override fun onBindViewHolder(holder: SizeViewHolder, position: Int) {
            val currentCode: Int = productViewModel.sizeList.value!![position]
            holder.textViewOption.text = ProductSize.values().firstOrNull { it.code == currentCode }?.str
        }
    }
}

// 색상, 사이즈 선택 Modal BottomSheet
class ModalBottomSheetProduct: BottomSheetDialogFragment(){
    private lateinit var modalBottomSheetBinding: FragmentProductOptionBottomSheetBinding
    private lateinit var listener : OnOptionSelectedListener

    fun setOnOptionSelectionListener(listener: OnOptionSelectedListener){
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        modalBottomSheetBinding = FragmentProductOptionBottomSheetBinding.inflate(inflater, container, false)
        return modalBottomSheetBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 옵션 추가 이벤트 리스너
        modalBottomSheetBinding.run {
            buttonAddOption.setOnClickListener {
                // 체크된 색상 이름 추출
                val checkedColorList: List<Int> = chipGroupColor.children
                    .filterIsInstance<Chip>()
                    .filter { it.isChecked }
                    .map { it.contentDescription.toString().toInt() }
                    .toList()

                // 체크된 사이즈 이름 추출
                val checkedSizeList: List<Int> = chipGroupSize.children
                    .filterIsInstance<Chip>()
                    .filter { it.isChecked }
                    .map { it.contentDescription.toString().toInt() }
                    .toList()

                // ProductOptionFragment에서 등록된 리스너 실행
                listener.onOptionSelected(checkedColorList, checkedSizeList)

                // 닫기
                dismiss()
            }
        }
    }

    // ModalBottomSheetProduct 에서 입력된 데이터를 ParentFragment로 전달하기 위한 인터페이스
    interface OnOptionSelectedListener {
        fun onOptionSelected(colorList: List<Int>, sizeList : List<Int>)
    }
}

