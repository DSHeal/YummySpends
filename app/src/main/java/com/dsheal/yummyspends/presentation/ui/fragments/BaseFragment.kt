package com.dsheal.yummyspends.presentation.ui.fragments

import android.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController

abstract class BaseFragment : Fragment() {

    fun showAlert(message: String?) {
        AlertDialog.Builder(context)
            .setMessage(message)
            .setCancelable(true)
            .setPositiveButton("Ok"){ dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    protected open fun getNavController(): NavController = findNavController()

}