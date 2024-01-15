package com.yachaesori.yachaesori_seller.ui.order

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
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.yachaesori.yachaesori_seller.data.model.getOrderState
import com.yachaesori.yachaesori_seller.databinding.FragmentOrderDetailBinding
import java.text.DecimalFormat

class OrderDetailFragment : Fragment() {
    private val itemListAdapter = ItemListAdapter()
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
                adapter = itemListAdapter
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

        orderViewModel.order.observe(viewLifecycleOwner) {
            fragmentOrderDetailBinding.run {
                textViewOrderState2.text = getOrderState(it.status).str
                textViewOrderUid.text = it.orderId
                textViewOrderDate.text = it.orderDate

                textViewOrderTotalPrice.text = DecimalFormat("#,###").format(it.totalPrice) + "원"

                itemListAdapter.submitList(it.itemList)

                textViewOrderShippingAddress1.text = it.address
                textViewOrderShippingMessage.text = it.msg

                textViewOrderUserContact.text = it.phone
                textViewOrderReceiver.text = it.name
                textViewOrderReceiverContact.text = it.phone
                textViewOrderUserId.text = it.userId
            }
        }


    }
}
