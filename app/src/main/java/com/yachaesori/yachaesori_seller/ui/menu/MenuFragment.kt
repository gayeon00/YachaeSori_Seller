package com.yachaesori.yachaesori_seller.ui.menu

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.yachaesori.yachaesori_seller.MainActivity
import com.yachaesori.yachaesori_seller.R
import com.yachaesori.yachaesori_seller.databinding.FragmentMenuBinding
import com.yachaesori.yachaesori_seller.ui.product.ProductViewModel
import com.yachaesori.yachaesori_seller.ui.product.ProductViewModelFactory
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MenuFragment : Fragment() {
    private val productViewModel: ProductViewModel by activityViewModels { ProductViewModelFactory() }
    private var _fragmentMenuBinding: FragmentMenuBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val fragmentMenuBinding get() = _fragmentMenuBinding!!
    private var selectedButton: Int = -1

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK && result.data != null) {
                val uri = result.data?.data

                uri?.let {
                    when (selectedButton) {
                        R.id.buttonGoToIntroImageSetting -> uploadImage(it, "intro")
                        R.id.buttonGoToPrepImageSetting -> uploadImage(it, "guide")
                    }
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _fragmentMenuBinding = FragmentMenuBinding.inflate(layoutInflater)
        return fragmentMenuBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var selectedItem: MenuItem? = null

        fragmentMenuBinding.run {
            navigationView.setNavigationItemSelectedListener { item ->
                selectedItem?.isChecked = false // 이전 선택 항목의 isChecked 상태를 false로 변경

                when (item.itemId) {
                    R.id.item_product -> {
                        item.isChecked = true
                        linearLayoutProductManage.visibility = View.VISIBLE
                        linearLayoutOrderMagage.visibility = View.GONE
                        linearLayoutSetting.visibility = View.GONE
                    }

                    R.id.item_order -> {
                        item.isChecked = true
                        //주문관리 페이지로 이동
                        linearLayoutProductManage.visibility = View.GONE
                        linearLayoutOrderMagage.visibility = View.VISIBLE
                        linearLayoutSetting.visibility = View.GONE

                    }

                    R.id.item_setting -> {
                        item.isChecked = true
                        linearLayoutProductManage.visibility = View.GONE
                        linearLayoutOrderMagage.visibility = View.GONE
                        linearLayoutSetting.visibility = View.VISIBLE
                    }
                }
                selectedItem = item
                true
            }

            buttonGoToAddProduct.setOnClickListener {
                productViewModel.isEditMode.value = false
                //상품 등록 페이지로 이동
                findNavController().navigate(R.id.action_item_menu_to_item_product_add)
            }

            buttonGoToModifyProduct.setOnClickListener {
                //상품 조회/수정 페이지로 이동
                findNavController().navigate(R.id.action_item_menu_to_item_product_list)
            }

            buttonGoToManageOrder.setOnClickListener {
                //주문 배송관리 페이지로 이동
                findNavController().navigate(R.id.action_item_menu_to_item_manage_order)
            }
            buttonGoToIntroImageSetting.setOnClickListener {
                //회사소개이미지 업로드 하도록 갤러리 사진선택으로 이동
                selectedButton = R.id.buttonGoToIntroImageSetting
                openGallery()
            }

            buttonGoToPrepImageSetting.setOnClickListener {
                //손질법이미지 업로드 하도록 갤러리 사진선택으로 이동
                selectedButton = R.id.buttonGoToPrepImageSetting
                openGallery()
            }

            buttonGoToBannerSetting.setOnClickListener {
                //배너관리 페이지로 이동
                findNavController().navigate(R.id.action_item_menu_to_item_banner_manage)
            }
        }
    }

    private fun openGallery() {
        val galleryIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
            }

        galleryLauncher.launch(galleryIntent)
    }

    private fun uploadImage(imageUri: Uri, path: String) {
        val imageRef =
            FirebaseStorage.getInstance().reference.child("image/yachae_${path}.jpg")

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                //firebase storage에 이미지 업로드
                imageRef.putFile(imageUri).await()
                Log.d("deleteImageAndUploadStorage", "storage upload 성공")
                // 이미지 경로 저장 성공에 대한 처리
                (activity as MainActivity).showSnackBar("성공적으로 변경됐습니다.")

            } catch (e: Exception) {
                Log.d("deleteImageAndUploadStorage", "업로드 및 데이터베이스 업데이트 실패: $e")
                throw e
            }
        }
    }

}