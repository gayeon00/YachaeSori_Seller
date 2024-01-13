package com.yachaesori.yachaesori_seller.ui.order

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.yachaesori.yachaesori_seller.data.model.OrderState
import com.yachaesori.yachaesori_seller.databinding.FragmentOrderManageBinding

class OrderManageFragment : Fragment() {
    private val orderListAdapter = OrderListAdapter()
    private lateinit var orderViewModel: OrderViewModel
    private var _fragmentOrderManageBinding: FragmentOrderManageBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val fragmentOrderManageBinding get() = _fragmentOrderManageBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentOrderManageBinding = FragmentOrderManageBinding.inflate(layoutInflater)
        orderViewModel =
            ViewModelProvider(this, OrderViewModelFactory())[OrderViewModel::class.java]
        Log.d("orderViewModel", orderViewModel.toString())


//        fragmentOrderManageBinding.run {
//            if (orderViewModel.orderList.value!!.isEmpty()) {
//                linearLayoutNoOrder.visibility = View.VISIBLE
//                linearLayoutRecyclerView.visibility = View.GONE
//            } else {
//                linearLayoutNoOrder.visibility = View.GONE
//                linearLayoutRecyclerView.visibility = View.VISIBLE
//            }
//        }

        return fragmentOrderManageBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        fragmentOrderManageBinding.run {
            recyclerViewOrder.run {
                adapter = orderListAdapter
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(
                    MaterialDividerItemDecoration(
                        context,
                        MaterialDividerItemDecoration.VERTICAL
                    )
                )
            }

            toolbarManageOrder.setNavigationOnClickListener {
                findNavController().popBackStack()
            }


        }

        orderViewModel.run {
            orderList.observe(viewLifecycleOwner) {
                fragmentOrderManageBinding.run {
                    if (it.isEmpty()) {
                        linearLayoutNoOrder.visibility = View.VISIBLE
                        linearLayoutRecyclerView.visibility = View.GONE
                    } else {
                        linearLayoutNoOrder.visibility = View.GONE
                        linearLayoutRecyclerView.visibility = View.VISIBLE
                    }

                    orderListAdapter.submitList(it)

                    textViewOrderPaymentCount.text = it.filter {
                        it.status == OrderState.PAYMENT.code
                    }.size.toString()

                    textViewOrderReadyCount.text = it.filter {
                        it.status == OrderState.READY.code
                    }.size.toString()

                    textViewOrderProcessCount.text = it.filter {
                        it.status == OrderState.DELIVERY.code
                    }.size.toString()

                    textViewOrderCompleteCount.text = it.filter {
                        it.status == OrderState.COMPLETE.code
                    }.size.toString()
                }
            }
        }
    }



}