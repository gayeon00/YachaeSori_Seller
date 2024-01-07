package com.yachaesori.yachaesori_seller.ui.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.yachaesori.yachaesori_seller.MainActivity
import com.yachaesori.yachaesori_seller.R
import com.yachaesori.yachaesori_seller.databinding.FragmentMypageBinding

class MypageFragment : Fragment() {

    private var _fragmentMypageBinding: FragmentMypageBinding? = null
    private lateinit var mainActivity: MainActivity

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val fragmentMypageBinding get() = _fragmentMypageBinding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragmentMypageBinding = FragmentMypageBinding.inflate(layoutInflater)
        mainActivity = activity as MainActivity
        return fragmentMypageBinding.root
    }


}