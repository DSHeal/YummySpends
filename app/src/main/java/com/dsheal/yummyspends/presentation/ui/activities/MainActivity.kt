package com.dsheal.yummyspends.presentation.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.dsheal.yummyspends.R
import com.dsheal.yummyspends.databinding.ActivityMainBinding
import com.dsheal.yummyspends.presentation.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    val binding: ActivityMainBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        NavigationUI.setupWithNavController(
            binding.bottomNavView,
            (supportFragmentManager.findFragmentById(R.id.fr_container_view) as NavHostFragment).navController
        )
        val mainViewModel = MainViewModel()
        setContentView(binding.root)
    }

    private fun setBottomNavigation() {
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        val profileDeepLink = intent?.data?.getQueryParameter("p") ?: ""

        if (intent?.action != null && profileDeepLink.isNotEmpty()) {
        } else {
            //analytics here
        }

    }
}