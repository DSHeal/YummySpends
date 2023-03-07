package com.dsheal.yummyspends.presentation.adapters

import android.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dsheal.yummyspends.R
import com.dsheal.yummyspends.databinding.CustomEditTextDialogBinding
import com.dsheal.yummyspends.databinding.ItemCategoriesRecyclerBinding

class CategoriesListAdapter :
    RecyclerView.Adapter<CategoriesListAdapter.CategoriesViewHolder>() {

    val data = AsyncListDiffer(this, CategoryItemCallback())
    val categoriesList = ArrayList<String>()

    class CategoriesViewHolder(
        private val binding: ItemCategoriesRecyclerBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: String, adapter: CategoriesListAdapter, position: Int) {
            binding.tvCategoryTitle.setText(category)

            binding.ivEditCategory.setOnClickListener {
                val categoryBinding: CustomEditTextDialogBinding =
                    CustomEditTextDialogBinding.inflate(
                        LayoutInflater.from(binding.root.context)
                    )

                val dialogBuilder = AlertDialog.Builder(binding.root.context)
                    .setView(categoryBinding.root)
                    .setCancelable(true)
                    .create()

                val editText = categoryBinding.etEditCategory
                editText.setText(category)

                val closeBtn = categoryBinding.ivEditDialogClose
                closeBtn.setOnClickListener {
                    dialogBuilder.dismiss()
                }

                val saveBtn = categoryBinding.btnEditCategorySave
                saveBtn.setOnClickListener {
                    binding.tvCategoryTitle.text = categoryBinding.etEditCategory.text
                    adapter.categoriesList.removeAt(position)
                    adapter.categoriesList.add(
                        position,
                        categoryBinding.etEditCategory.text.toString()
                    )
                    Log.i("ADDED", "AT: $position")
                    Log.i("CATEGORIES_LIST", adapter.categoriesList.toString())
                    adapter.data.submitList(adapter.categoriesList)
                    dialogBuilder.dismiss()
                }

                val deleteBtn = categoryBinding.btnEditCategoryDelete
                deleteBtn.setOnClickListener {
                    val confirmationDialog = AlertDialog.Builder(binding.root.context)
                        .setCancelable(true)
                        .setTitle(R.string.delete_confirmation_title)
                        .setPositiveButton(R.string.delete_agree) { dialog, _ ->
                            val itemToDelete = adapter.categoriesList[position]
                            adapter.categoriesList.removeAt(position)
                            val newListAfterDeletion = adapter.categoriesList
                            adapter.notifyItemRemoved(position)
                            adapter.data.submitList(newListAfterDeletion)
                            Log.i("LIST_AFTER_DELETION", newListAfterDeletion.toString())
                            dialog.dismiss()
                            dialogBuilder.dismiss()
                        }
                        .setNegativeButton(R.string.cancel) { dialog, _ ->
                            dialog.dismiss()
                            dialogBuilder.dismiss()
                        }
                        .create()
                    confirmationDialog.show()
                }
                dialogBuilder.show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        return CategoriesViewHolder(
            ItemCategoriesRecyclerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        holder.bind(data.currentList[position], this, position)
    }

    override fun getItemCount(): Int {
        return data.currentList.size
    }

    fun updateList(categories: List<String>) {
        data.submitList(categories)
        categoriesList.addAll(categories)
    }

    class CategoryItemCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }
    }
}