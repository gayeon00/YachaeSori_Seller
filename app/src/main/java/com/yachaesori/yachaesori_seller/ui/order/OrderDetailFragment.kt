package com.yachaesori.yachaesori_seller.ui.order

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.yachaesori.yachaesori_seller.data.model.Order
import com.yachaesori.yachaesori_seller.data.model.getOrderState
import com.yachaesori.yachaesori_seller.databinding.FragmentOrderDetailBinding
import com.yachaesori.yachaesori_seller.databinding.RowOrderItemBinding
import java.text.DecimalFormat

class OrderDetailFragment : Fragment() {
    private lateinit var orderViewModel: OrderViewModel
    private var _fragmentOrderDetailBinding: FragmentOrderDetailBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val fragmentOrderDetailBinding get() = _fragmentOrderDetailBinding!!
    private val args: OrderDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _fragmentOrderDetailBinding = FragmentOrderDetailBinding.inflate(layoutInflater)


        orderViewModel =
            ViewModelProvider(this, OrderViewModelFactory())[OrderViewModel::class.java]
        Log.d("orderViewModel", orderViewModel.toString())


        return fragmentOrderDetailBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orderViewModel.orderList.observe(viewLifecycleOwner) {
            orderViewModel.setOrder(it[args.position])
        }

        fragmentOrderDetailBinding.run {
            reyclerViewOrderProductList.run {
                adapter = OrderItemRecyclerViewAdapter()
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(
                    MaterialDividerItemDecoration(
                        context,
                        MaterialDividerItemDecoration.VERTICAL
                    )
                )
            }
            toolbarOrderDetail.setNavigationOnClickListener {
                findNavController().popBackStack()
            }


        }

        orderViewModel.run {

            order.observe(viewLifecycleOwner) {
                fragmentOrderDetailBinding.run {
                    textViewOrderState2.text = getOrderState(it.status).str
                    textViewOrderUid.text = it.orderId
                    textViewOrderDate.text = it.orderDate

                    textViewOrderTotalCount.text = "${it.itemList.size}개"
                    textViewOrderTotalPrice.text = DecimalFormat("#,###").format(it.totalPrice)+"원"

                    textViewOrderShippingAddress1.text = it.address
                    textViewOrderShippingMessage.text = it.msg

                    textViewOrderUserContact.text = it.phone
                    textViewOrderReceiver.text = it.name
                    textViewOrderReceiverContact.text = it.phone
                    textViewOrderUserId.text =it.userId
                }
            }
        }

    }
    //TODO: ListAdapter로 수정하기
    inner class OrderItemRecyclerViewAdapter :
        Adapter<OrderItemRecyclerViewAdapter.OrderItemViewHolder>() {
        inner class OrderItemViewHolder(rowOrderItemBinding: RowOrderItemBinding) :
            ViewHolder(rowOrderItemBinding.root) {
            val textViewOrderProductId = rowOrderItemBinding.textViewOrderProductId
            val textViewOrderProductCount = rowOrderItemBinding.textViewOrderProductCount
            val textViewOrderProductName = rowOrderItemBinding.textViewOrderProductName
            val textViewOrderProductOption = rowOrderItemBinding.textViewOrderProductOption
            val textViewOrderProductPrice = rowOrderItemBinding.textViewOrderProductPrice
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
            val rowOrderItemBinding = RowOrderItemBinding.inflate(layoutInflater)
            rowOrderItemBinding.root.layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
            )
            return OrderItemViewHolder(rowOrderItemBinding)
        }

        override fun getItemCount(): Int {
            return orderViewModel.order.value?.itemList?.size ?: 0
        }

        override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
            val item = orderViewModel.order.value!!.itemList[position]
            holder.textViewOrderProductId.text =
                item.product.productId
            holder.textViewOrderProductCount.text =
                "${item.quantity}개"
            holder.textViewOrderProductName.text =
                item.product.name
            holder.textViewOrderProductOption.text =
                item.selectedOption
            holder.textViewOrderProductPrice.text =
                DecimalFormat("#,###").format(item.product.price)+"원"

        }
    }


}
