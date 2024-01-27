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
import org.apache.poi.ss.usermodel.WorkbookFactory
import java.io.FileOutputStream
import java.io.IOException

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

            toolbarManageOrder.setOnMenuItemClickListener {
                // 엑셀 생성 및 내보내기
                val filePath = "${getExternalFilesDir(null).toString().absolutePath}/example.xlsx"
                exportToExcel(filePath)
                true
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

    private fun exportToExcel(filePath: String) {
        val workbook = WorkbookFactory.create(true)
        val sheet = workbook.createSheet("주문내역")

        // 데이터 입력 (예시)
        val headerRow = sheet.createRow(0)
        val headerCell = headerRow.createCell(0)
        headerCell.setCellValue("Header")

        val dataRow = sheet.createRow(1)
        val dataCell = dataRow.createCell(0)
        dataCell.setCellValue("Data")

        // 파일에 쓰기
        try {
            val fos = FileOutputStream(filePath)
            workbook.write(fos)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // 메모리에서 workbook 해제
        try {
            workbook.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


}