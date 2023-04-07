package com.dsheal.yummyspends.presentation.ui.fragments

import android.app.AlertDialog
import androidx.fragment.app.Fragment

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
}