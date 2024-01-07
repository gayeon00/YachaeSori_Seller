package com.yachaesori.yachaesori_seller.ui.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.yachaesori.yachaesori_seller.R
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.yachaesori.yachaesori_seller.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private lateinit var fragmentMainBinding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMainBinding = FragmentMainBinding.inflate(inflater)

        Log.d("user", "mainFragment onCreate")

        // navigation
        val navHostFragment = childFragmentManager.findFragmentById(R.id.fragmentMainContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        // 특정 화면에서 BottomNavigationBar 숨기기
        navController.addOnDestinationChangedListener { _: NavController, navDestination: NavDestination, _: Bundle? ->
            if (navDestination.id == R.id.item_home || navDestination.id == R.id.item_menu || navDestination.id == R.id.item_mypage ) {
                fragmentMainBinding.bottomNavigation.visibility = View.VISIBLE
            } else {
                fragmentMainBinding.bottomNavigation.visibility = View.GONE
            }
        }

        fragmentMainBinding.run {
            bottomNavigation.run {
                setupWithNavController(navController)
            }
        }
        return fragmentMainBinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("user", "mainFragment onDestroy")
    }
}