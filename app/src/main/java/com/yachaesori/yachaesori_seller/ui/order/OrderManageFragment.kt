package com.yachaesori.yachaesori_seller.ui.order

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.LayoutParams
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.yachaesori.yachaesori_seller.R
import com.yachaesori.yachaesori_seller.data.model.OrderState
import com.yachaesori.yachaesori_seller.data.model.getOrderState
import com.yachaesori.yachaesori_seller.databinding.FragmentOrderManageBinding
import com.yachaesori.yachaesori_seller.databinding.RowOrderBinding

class OrderManageFragment : Fragment() {
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

        //이 판매자가 올린 상품에 대한 주문 목록만 세팅하기
        orderViewModel.getOrder()

        fragmentOrderManageBinding.run {
            if (orderViewModel.orderList.value!!.isEmpty()) {
                linearLayoutNoOrder.visibility = View.VISIBLE
                linearLayoutRecyclerView.visibility = View.GONE
            } else {
                linearLayoutNoOrder.visibility = View.GONE
                linearLayoutRecyclerView.visibility = View.VISIBLE
            }
        }

        return fragmentOrderManageBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        fragmentOrderManageBinding.run {
            recyclerViewOrder.run {
                adapter = OrderRecyclerViewAdapter()
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

                    recyclerViewOrder.adapter?.notifyDataSetChanged()

                    textViewOrderPaymentCount.text = it.filter {
                        it.state == OrderState.PAYMENT.code
                    }.size.toString()

                    textViewOrderReadyCount.text = it.filter {
                        it.state == OrderState.READY.code
                    }.size.toString()

                    textViewOrderProcessCount.text = it.filter {
                        it.state == OrderState.DELIVERY.code
                    }.size.toString()

                    textViewOrderCompleteCount.text = it.filter {
                        it.state == OrderState.COMPLETE.code
                    }.size.toString()
                }
            }
        }
    }

    inner class OrderRecyclerViewAdapter :
        Adapter<OrderRecyclerViewAdapter.OrderRecyclerViewHolder>() {

        inner class OrderRecyclerViewHolder(rowOrderBinding: RowOrderBinding) :
            ViewHolder(rowOrderBinding.root) {
            val textViewOrderState = rowOrderBinding.textViewOrderState
            val textViewOrderNum = rowOrderBinding.textViewOrderNum
            val textViewOrderDate = rowOrderBinding.textViewOrderDate
            val textViewTotalOrderPrice = rowOrderBinding.textViewTotalOrderPrice
            val textViewOrderCustomerID = rowOrderBinding.textViewOrderCustomerID
            val textViewOrderProducts = rowOrderBinding.textViewOrderProducts

            init {
                rowOrderBinding.root.setOnClickListener {
                    //해당 주문 상세 페이지로 이동
                    //order객체 넘겨줌
                    val bundle = bundleOf()
                    bundle.putParcelable(
                        "order",
                        orderViewModel.orderList.value?.get(adapterPosition)
                    )
                    findNavController().navigate(
                        R.id.action_item_manage_order_to_item_order_detail,
                        bundle
                    )
                }
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderRecyclerViewHolder {
            val rowOrderBinding = RowOrderBinding.inflate(layoutInflater)
            rowOrderBinding.root.layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )
            return OrderRecyclerViewHolder(rowOrderBinding)
        }

        override fun getItemCount(): Int {
            return orderViewModel.orderList.value?.size!!
        }

        override fun onBindViewHolder(holder: OrderRecyclerViewHolder, position: Int) {
            //TODO : LONG(enum)이라 text로 바꿔줘야함
            holder.textViewOrderState.text =
                getOrderState(orderViewModel.orderList.value!![position].state).str
            holder.textViewOrderNum.text =
                "No. ${orderViewModel.orderList.value!![position].orderUid}"
            holder.textViewOrderDate.text = orderViewModel.orderList.value!![position].orderDate
            holder.textViewTotalOrderPrice.text =
                orderViewModel.orderList.value!![position].totalPrice.toString()
            //TODO: userUid도 필요함
//            holder.textViewOrderCustomerID.text = orderViewModel.orderList.value!![position].userUid
            holder.textViewOrderProducts.text =
                "${orderViewModel.orderList.value!![position].itemList[0].name} 외 ${orderViewModel.orderList.value!![position].itemList.size - 1}건"

        }
    }

}