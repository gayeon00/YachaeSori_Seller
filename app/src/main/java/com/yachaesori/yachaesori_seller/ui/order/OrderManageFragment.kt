package com.yachaesori.yachaesori_seller.ui.order

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.storage.StorageManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.yachaesori.yachaesori_seller.MainActivity
import com.yachaesori.yachaesori_seller.data.model.OrderState
import com.yachaesori.yachaesori_seller.databinding.FragmentOrderManageBinding
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.lang.RuntimeException
import java.text.SimpleDateFormat
import java.util.Date

// Request code for creating a PDF document.
const val CREATE_FILE = 1

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

    @RequiresApi(Build.VERSION_CODES.R)
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
                exportToExcel()
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

    @RequiresApi(Build.VERSION_CODES.R)
    private fun exportToExcel() {
        val xssfWorkbook = XSSFWorkbook()
        val xssfSheet = xssfWorkbook.createSheet("주문내역")

        val xssfRow = xssfSheet.createRow(0)
        val xssfCell = xssfRow.createCell(0)
        xssfCell.setCellValue("hello")

        saveWorkBook(xssfWorkbook)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun saveWorkBook(xssfWorkbook: XSSFWorkbook) {
        val storageManager =
            requireActivity().getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val storageVolume = storageManager.storageVolumes[0] //internal storage


        try {
            val file = File(storageVolume.directory!!.path + "/Download/${today()}주문내역.xlsx")
            val fos = FileOutputStream(file)

            xssfWorkbook.write(fos)
            fos.close()
            xssfWorkbook.close()

            (requireActivity() as MainActivity).showSnackBar("Download 폴더에 저장됐습니다.")
        } catch (e: Exception) {
            (requireActivity() as MainActivity).showSnackBar("오류가 발생했습니다.")
            throw RuntimeException(e)
        }



    }

    private fun today(): String {
        val now = System.currentTimeMillis()
        val date = Date(now)
        val sdf = SimpleDateFormat("yyyyMMdd")
        return sdf.format(date)
    }


}




