package com.dsheal.yummyspends.presentation.ui.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dsheal.yummyspends.R
import com.dsheal.yummyspends.databinding.CustomEditTextDialogBinding
import com.dsheal.yummyspends.databinding.FragmentUserCategoriesBinding
import com.dsheal.yummyspends.presentation.adapters.CategoriesListAdapter
import com.dsheal.yummyspends.presentation.base.BaseViewModel
import com.dsheal.yummyspends.presentation.ui.activities.MainActivity
import com.dsheal.yummyspends.presentation.viewmodels.CategoriesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserCategoriesFragment : BaseFragment() {

    private var mBinding: FragmentUserCategoriesBinding? = null
    private val binding get() = mBinding!!

    private val categoriesViewModel: CategoriesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentUserCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbarMenu()
        initRecycler()
        initViews()
        categoriesViewModel.categories.observe(viewLifecycleOwner) { state ->
            when (state) {
                is BaseViewModel.State.Loading -> {}
                is BaseViewModel.State.Failure -> {
                    showAlert(state.error?.message)
                }
                is BaseViewModel.State.Success -> {
                    (binding.rvCategoriesList.adapter as CategoriesListAdapter).updateList(state.data)
                }
            }
        }
    }

    private fun initViews() {
        binding.btnAddCategory.setOnClickListener {
            val editDialogBinding: CustomEditTextDialogBinding =
                CustomEditTextDialogBinding.inflate(
                    LayoutInflater.from(binding.root.context)
                )

            val dialog = AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.add_category_title))
                .setView(editDialogBinding.root)
                .setCancelable(true)
                .create()

            editDialogBinding.ivEditDialogClose.visibility = View.GONE

            val cancelBtn = editDialogBinding.btnEditCategoryDelete
            cancelBtn.apply {
                text = getString(R.string.cancel)
                setOnClickListener {
                    dialog.dismiss()
                }
            }
            val editText = editDialogBinding.etEditCategory
            val saveBtn = editDialogBinding.btnEditCategorySave
            saveBtn.apply {
                text = "Добавить"
                setOnClickListener {
                    dialog.dismiss()
                    (binding.rvCategoriesList.adapter as CategoriesListAdapter).categoriesList.add(
                        editText.text.toString()
                    )
                    Toast.makeText(
                        requireContext(),
                        "Added: ${editText.text.toString()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    val updatedList =
                        (binding.rvCategoriesList.adapter as CategoriesListAdapter).categoriesList
                    (binding.rvCategoriesList.adapter as CategoriesListAdapter).data.submitList(
                        updatedList
                    )
                    Log.i(
                        "CATEGORIES_LIST",
                        (binding.rvCategoriesList.adapter as CategoriesListAdapter).categoriesList.toString()
                    )
                }
            }
            dialog.show()

        }
    }

    private fun initRecycler() {
        binding.rvCategoriesList.apply {
            adapter = CategoriesListAdapter()
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onStop() {
        super.onStop()
        val categoriesAfterEdit =
            (binding.rvCategoriesList.adapter as CategoriesListAdapter).categoriesList
        Log.i("CATEGORIES_FROM_ADAPTER", categoriesAfterEdit.toString())
        categoriesViewModel.sendCategoriesListToRemoteDb(categoriesAfterEdit)
    }

    fun initToolbarMenu() {
        binding.tbMyCategories.inflateMenu(R.menu.edit_category_menu)
        binding.tbMyCategories.setOnMenuItemClickListener { menuItem ->
            val itemId: Int = menuItem.itemId
            if (itemId == R.id.item_close) {
                (activity as MainActivity).onBackPressed()
                return@setOnMenuItemClickListener true
            } else {
                return@setOnMenuItemClickListener false
            }
        }
    }
}