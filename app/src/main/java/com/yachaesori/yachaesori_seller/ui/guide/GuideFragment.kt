package com.yachaesori.yachaesori_seller.ui.guide

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.yachaesori.yachaesori_seller.databinding.FragmentGuideBinding

class GuideFragment : Fragment() {
    lateinit var fragmentGuideBinding: FragmentGuideBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentGuideBinding = FragmentGuideBinding.inflate(inflater)
        return fragmentGuideBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fragmentGuideBinding.run {
            toolbarGuide.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            fabGuide.setOnClickListener {
                scrollViewGuide.smoothScrollTo(0, 0)
            }
        }
    }
}