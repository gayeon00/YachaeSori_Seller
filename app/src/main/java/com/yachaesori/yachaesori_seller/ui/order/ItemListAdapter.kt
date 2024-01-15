package com.yachaesori.yachaesori_seller.ui.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yachaesori.yachaesori_seller.data.model.OrderItem
import com.yachaesori.yachaesori_seller.databinding.RowOrderItemBinding
import java.text.DecimalFormat

class ItemListAdapter :
    ListAdapter<OrderItem, ItemListAdapter.OrderItemViewHolder>(OrderItemDiffCallBack()) {
    class OrderItemViewHolder(
        private val binding: RowOrderItemBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind(item: OrderItem) {
                binding.textViewOrderProductId.text =
                    item.product.productId
                binding.textViewOrderProductCount.text =
                    "${item.quantity}개"
                binding.textViewOrderProductName.text =
                    item.product.name
                binding.textViewOrderProductOption.text =
                    item.selectedOption
                binding.textViewOrderProductPrice.text =
                    DecimalFormat("#,###").format(item.product.price)+"원"
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        return OrderItemViewHolder(
            RowOrderItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

private class OrderItemDiffCallBack : DiffUtil.ItemCallback<OrderItem>() {
    override fun areItemsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: OrderItem, newItem: OrderItem): Boolean {
        return oldItem == newItem
    }
}