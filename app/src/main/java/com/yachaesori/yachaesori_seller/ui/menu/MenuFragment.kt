package com.yachaesori.yachaesori_seller.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.yachaesori.yachaesori_seller.R
import com.yachaesori.yachaesori_seller.databinding.FragmentMenuBinding
import com.yachaesori.yachaesori_seller.ui.product.ProductViewModel
import com.yachaesori.yachaesori_seller.ui.product.ProductViewModelFactory

class MenuFragment : Fragment() {
    private val productViewModel: ProductViewModel by activityViewModels { ProductViewModelFactory() }
    private var _fragmentMenuBinding: FragmentMenuBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val fragmentMenuBinding get() = _fragmentMenuBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentMenuBinding = FragmentMenuBinding.inflate(layoutInflater)
        return fragmentMenuBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var selectedItem: MenuItem? = null

        fragmentMenuBinding.run {
            navigationView.setNavigationItemSelectedListener { item ->
                selectedItem?.isChecked = false // 이전 선택 항목의 isChecked 상태를 false로 변경

                when (item.itemId) {
                    R.id.item_product -> {
                        item.isChecked = true
                        linearLayoutProductManage.visibility = View.VISIBLE
                        linearLayoutOrderMagage.visibility = View.GONE
                        linearLayoutCS.visibility = View.GONE
                        linearLayoutSetting.visibility = View.GONE
                    }

                    R.id.item_order -> {
                        item.isChecked = true
                        //주문관리 페이지로 이동
                        linearLayoutProductManage.visibility = View.GONE
                        linearLayoutOrderMagage.visibility = View.VISIBLE
                        linearLayoutCS.visibility = View.GONE
                        linearLayoutSetting.visibility = View.GONE

                    }

                    R.id.item_cs -> {
                        item.isChecked = true
                        linearLayoutProductManage.visibility = View.GONE
                        linearLayoutOrderMagage.visibility = View.GONE
                        linearLayoutCS.visibility = View.VISIBLE
                        linearLayoutSetting.visibility = View.GONE
                    }

                    R.id.item_setting -> {
                        item.isChecked = true
                        linearLayoutProductManage.visibility = View.GONE
                        linearLayoutOrderMagage.visibility = View.GONE
                        linearLayoutCS.visibility = View.GONE
                        linearLayoutSetting.visibility = View.VISIBLE
                    }
                }
                selectedItem = item
                true
            }

            buttonGoToAddProduct.setOnClickListener {
                productViewModel.isEditMode.value = false
                //상품 등록 페이지로 이동
                findNavController().navigate(R.id.action_item_menu_to_item_product_add)
            }

            buttonGoToModifyProduct.setOnClickListener {
                //상품 조회/수정 페이지로 이동
                findNavController().navigate(R.id.action_item_menu_to_item_product_list)
            }

            buttonGoToManageOrder.setOnClickListener {
                //주문 배송관리 페이지로 이동
                findNavController().navigate(R.id.action_item_menu_to_item_manage_order)
            }
            buttonGoToManageNotice.setOnClickListener {
                //공지관리 페이지로 이동
            }
            buttonGoToManageInq.setOnClickListener {
                //문의 관리 페이지로 이동
            }
            buttonGoToSetting.setOnClickListener {
                //설정 페이지로 이동
            }
        }
    }
}