package com.yachaesori.yachaesori_seller.ui.order

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yachaesori.yachaesori_seller.MainActivity
import com.yachaesori.yachaesori_seller.R
import com.yachaesori.yachaesori_seller.data.model.Order
import com.yachaesori.yachaesori_seller.data.model.getOrderState
import com.yachaesori.yachaesori_seller.databinding.RowOrderBinding
import java.text.DecimalFormat

class OrderListAdapter :
    ListAdapter<Order, OrderListAdapter.OrderListViewHolder>(OrderDiffCallBack()) {

    class OrderListViewHolder(
        private val rowOrderBinding: RowOrderBinding
    ) : RecyclerView.ViewHolder(rowOrderBinding.root) {

        fun bind(item: Order, position: Int) {
            rowOrderBinding.run {
                textViewOrderState.text = getOrderState(item.status).str
                textViewOrderNum.text = "No.${item.orderId}"
                textViewOrderDate.text = item.orderDate
                textViewTotalOrderPrice.text = DecimalFormat("#,###").format(item.totalPrice) + "원"
                textViewOrderCustomerID.text = item.userId
                textViewOrderProducts.text =
                    "${item.itemList[0].product.name} 외 ${item.itemList.size - 1}건"

                root.setOnClickListener {
                    navigateToOrderDetail(position, it)
                }
            }
        }

        private fun navigateToOrderDetail(position: Int, it: View) {
            val action = OrderManageFragmentDirections.actionItemManageOrderToItemOrderDetail(position)
            it.findNavController().navigate(action)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderListViewHolder {
        return OrderListViewHolder(
            RowOrderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: OrderListViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }
}

private class OrderDiffCallBack() : DiffUtil.ItemCallback<Order>() {
    override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
        return oldItem == newItem
    }
}