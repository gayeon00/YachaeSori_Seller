package com.yachaesori.yachaesori_seller.ui.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.yachaesori.yachaesori_seller.R
import com.yachaesori.yachaesori_seller.databinding.FragmentBannerManageBinding

class BannerManageFragment : Fragment() {
    private lateinit var binding: FragmentBannerManageBinding

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK && result.data != null) {
                val uri = result.data?.data

                uri?.let {
                    //배너 업로드 하기
                }
            }
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