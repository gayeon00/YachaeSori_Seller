package com.yachaesori.yachaesori_seller.ui.order

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.storage.StorageManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.yachaesori.yachaesori_seller.MainActivity
import com.yachaesori.yachaesori_seller.data.model.Order
import com.yachaesori.yachaesori_seller.data.model.OrderState
import com.yachaesori.yachaesori_seller.databinding.FragmentOrderManageBinding
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date

// Request code for creating a PDF document.
const val CREATE_FILE = 1

class OrderManageFragment : Fragment() {

    lateinit var writeActivityLauncher: ActivityResultLauncher<Intent>

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

        //쓰기용 런처
        val contract1 = ActivityResultContracts.StartActivityForResult()
        writeActivityLauncher = registerForActivityResult(contract1) {
            //사용자가 저장할 파일을 선택하고 돌아오면 Resultcode는 RESULT_OK가 들어온다
            if (it.resultCode == AppCompatActivity.RESULT_OK) {
                //사용자가 선택한 파일의 정보를 가지고 있는 Intent로 파일 정보를 가져온다
                if (it.data != null) {
                    try {
                        //저장할 파일에 접근할 수 있는 객체로부터 파일 정보를 가져온다
                        //w: 쓰기, r: 읽기, a: 이어쓰기
                        val des1 = requireActivity().contentResolver?.openFileDescriptor(
                            it.data?.data!!,
                            "w"
                        )
                        //스트림 생성
                        val fos = FileOutputStream(des1?.fileDescriptor)
                        // Create a new workbook
                        val xssfWorkbook = XSSFWorkbook()
                        // Create a new sheet
                        val xssfSheet = xssfWorkbook.createSheet("주문내역")

                        // Create header row
                        createHeaderRow(xssfSheet)

                        // Add data rows
                        for (order in orderViewModel.orderList.value!!) {
                            createDataRow(xssfSheet, order)
                        }


                        xssfWorkbook.write(fos)
                        fos.close()

                        xssfWorkbook.close()

                        (requireActivity() as MainActivity).showSnackBar("Documents 폴더에 저장됐습니다.")
                    } catch (e: Exception) {
                        (requireActivity() as MainActivity).showSnackBar("오류가 발생했습니다.")
                        throw RuntimeException(e)
                    }

                }
            }
        }

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
                //파일 관리 앱의 액티비티 실행
                val fileIntent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                fileIntent.addCategory(Intent.CATEGORY_OPENABLE)
                //MimeType을 설정
                //파일에 저장돼있는 데이터의양식이 뭔지 나타내는 문자열
                //파일 확장자를 바꾼다고 해서 그게 정말 jpg가 되지 않는 이유가 얘 때문!
                //https://developer.mozilla.org/ko/docs/Web/HTTP/Basics_of_HTTP/MIME_types
                fileIntent.type = "*/*"
                fileIntent.putExtra(Intent.EXTRA_TITLE, "${today()}주문내역.xlsx")
                writeActivityLauncher.launch(fileIntent)


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

    private fun createDataRow(xssfSheet: XSSFSheet, order: Order) {
        val dataRow = xssfSheet.createRow(xssfSheet.lastRowNum + 1)

        dataRow.createCell(0).setCellValue(order.postcode)
        dataRow.createCell(1).setCellValue(order.address)
        dataRow.createCell(2).setCellValue(order.name)
        dataRow.createCell(3).setCellValue(order.phone)
    }

    private fun createHeaderRow(sheet: XSSFSheet) {
        val headerRow = sheet.createRow(0)
        val headers = arrayOf("우편번호", "주소", "이름", "휴대폰 번호")
        for ((index, header) in headers.withIndex()) {
            val cell = headerRow.createCell(index)
            cell.setCellValue(header)
        }
    }

    private fun today(): String {
        val now = System.currentTimeMillis()
        val date = Date(now)
        val sdf = SimpleDateFormat("yyyyMMdd")
        return sdf.format(date)
    }


}




