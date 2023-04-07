package com.dsheal.yummyspends.presentation.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.dsheal.yummyspends.R
import com.dsheal.yummyspends.databinding.ActivityMainBinding
import com.dsheal.yummyspends.presentation.viewmodels.MainViewModel
import com.google.firebase.database.IgnoreExtraProperties
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    val binding: ActivityMainBinding
        get() = _binding!!

    companion object {
        private const val TAG = "MAIN_ACTIVITY"
    }

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

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    //TODO think probably it could be useful
    @IgnoreExtraProperties
    data class User(val username: String? = null, val email: String? = null) {
        // Null default values create a no-argument default constructor, which is needed
        // for deserialization from a DataSnapshot.
    }
}