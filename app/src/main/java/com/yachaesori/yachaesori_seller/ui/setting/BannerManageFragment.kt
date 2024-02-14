package com.yachaesori.yachaesori_seller.ui.setting

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yachaesori.yachaesori_seller.databinding.FragmentBannerManageBinding

class BannerManageFragment : Fragment() {
    private lateinit var binding: FragmentBannerManageBinding
    private val bannerViewModel: BannerViewModel by viewModels { BannerViewModelFactory() }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK && result.data != null) {
                val uri = result.data?.data

                uri?.let {
                    uploadBanner(uri)
                }
            }
        }

    /**
     * uri로 storage에 업로드 하고, 그 downloadUrl로 Banner 객체 만들어서 realtime에 넣기
     */
    private fun uploadBanner(uri: Uri) {
        bannerViewModel.uploadBanner(uri)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBannerManageBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setNavigationIcon()
        setAddBanner()

    }

    /**
     * 배너 추가 버튼 클릭 시
     *
     * 갤러리로 가서 이미지 선택하기
     * 맨 마지막 순서로 추가되기
     */
    private fun setAddBanner() {
        binding.toolbarManageBanner.setOnMenuItemClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                    type = "image/*"
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
                }

            galleryLauncher.launch(galleryIntent)
            true
        }
    }

    /**
     * 뒤로가기 클릭 시
     */
    private fun setNavigationIcon() {
        binding.toolbarManageBanner.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

    }
}