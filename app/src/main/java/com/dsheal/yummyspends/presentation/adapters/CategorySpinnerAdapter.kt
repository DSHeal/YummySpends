package com.dsheal.yummyspends.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.dsheal.yummyspends.R
import com.dsheal.yummyspends.domain.models.spendings.Category

class CategorySpinnerAdapter(
    context: Context,
    private val layoutId: Int,
    private val spinnerItems: List<Category>,
) : ArrayAdapter<Category>(
    context,
    layoutId,
    spinnerItems
) {
    override fun getItem(position: Int) = spinnerItems[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view =
            convertView ?: LayoutInflater.from(context).inflate(layoutId, parent, false) as TextView
        val parentSpinner = parent as? Spinner

        return if (parentSpinner != null) bindSelectedData(
            getItem(parent.selectedItemPosition).name,
            view as TextView
        )
        else bindSelectedData(getItem(position).name, view as TextView)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view =
            convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.item_category_spinner_dropdown, parent, false)
        return bindData(getItem(position).name, view as TextView)
    }

    private fun bindData(value: String, view: TextView): TextView {
        view.text = value
        return view
    }

    private fun bindSelectedData(value: String, view: TextView): TextView {
        val bindedView = bindData(value, view)
        bindedView.setPadding(0, 0, 0, 0)
        return bindedView
    }
}