package com.yachaesori.yachaesori_seller.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.yachaesori.yachaesori_seller.R
import com.yachaesori.yachaesori_seller.databinding.FragmentHomeBinding
import com.yachaesori.yachaesori_seller.ui.order.OrderViewModel
import com.yachaesori.yachaesori_seller.ui.order.OrderViewModelFactory
import com.yachaesori.yachaesori_seller.ui.product.ProductViewModel
import com.yachaesori.yachaesori_seller.ui.product.ProductViewModelFactory

class HomeFragment : Fragment() {
    private lateinit var productViewModel: ProductViewModel
    private lateinit var orderViewModel: OrderViewModel

    private var lastBackPressedTime: Long = 0

    private lateinit var _fragmentHomeBinding: FragmentHomeBinding
    private val fragmentHomeBinding get() = _fragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentHomeBinding = FragmentHomeBinding.inflate(inflater)

        handleBackPress()

        // ViewModel
        productViewModel =
            ViewModelProvider(this, ProductViewModelFactory())[ProductViewModel::class.java]
        orderViewModel =
            ViewModelProvider(this, OrderViewModelFactory())[OrderViewModel::class.java]

        // Observer
        productViewModel.run {
            productCount.observe(viewLifecycleOwner) {
                fragmentHomeBinding.textViewProductCount.text = "등록된 상품이 ${it}건 있습니다."
            }
        }

        orderViewModel.run {
            orderList.observe(viewLifecycleOwner) {
                fragmentHomeBinding.run {
                    // 주문 상태 건 수 표시
                    textViewPaymentCount.text = it.filter {
                        it.state == OrderState.PAYMENT.code
                    }.size.toString()

                    textViewReadyCount.text = it.filter {
                        it.state == OrderState.READY.code
                    }.size.toString()

                    textViewProcessCount.text = it.filter {
                        it.state == OrderState.DELIVERY.code
                    }.size.toString()

                    textViewCompleteCount.text = it.filter {
                        it.state == OrderState.COMPLETE.code
                    }.size.toString()

                    textViewExchangeCount.text = it.filter {
                        it.state == OrderState.EXCHANGE.code
                    }.size.toString()

                    textViewCancelCount.text = it.filter {
                        it.state == OrderState.CANCEL.code
                    }.size.toString()

                    textViewRefundCount.text = it.filter {
                        it.state == OrderState.REFUND.code
                    }.size.toString()
                }
            }
        }

        fragmentHomeBinding.run {
            toolbarHome.run {
                // 툴바 타이틀 폰트 설정
                setTitleTextAppearance(requireContext(), R.style.HsbombaramTextAppearance)
            }

            buttonRegProduct.setOnClickListener {
                // 상품 등록 화면으로 이동
                it.findNavController().navigate(R.id.action_item_home_to_item_product_add)
            }

            linearGuide.setOnClickListener {
                // 판매자 가이드 화면으로 이동

                findNavController().navigate(R.id.action_item_home_to_item_guide)
            }
        }

        // 로그인 판매자가 관련된 주문들 건 수 표시
        orderViewModel.getOrder()

        // 로그인 판매자가 등록한 상품 개수 표시
        productViewModel.getProductCount()

        return fragmentHomeBinding.root
    }

    private fun handleBackPress() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (System.currentTimeMillis() - lastBackPressedTime < 2000) {
                    requireActivity().finish()
                } else {
                    Toast.makeText(context, "뒤로 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                    lastBackPressedTime = System.currentTimeMillis()
                }
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

}

