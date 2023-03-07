package com.dsheal.yummyspends.presentation.adapters

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dsheal.yummyspends.databinding.*
import com.dsheal.yummyspends.domain.models.history.HistoryDataWrapper

class HistoryListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val list = AsyncListDiffer(this, HistoryListCallback())

    class CategoryTitleViewHolder(private val binding: ItemHistoryCategoryTitleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HistoryDataWrapper.CategoryTableTitle) {
            binding.tvHistoryCategoryTitle.text = item.categoryTitle
            binding.tvHistoryCategorySum.text = item.categoryPrice.toString()
        }
    }

    class CategoryItemViewHolder(private val binding: ItemHistoryCategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: HistoryDataWrapper.CategoryTableItem) {
            binding.clHistoryItem.setOnLongClickListener {
                AlertDialog.Builder(binding.root.context)
                    .setPositiveButton("Редактировать") { _, _ ->

                    }
                    .setNegativeButton("Удалить") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
                Toast.makeText(binding.root.context, "AAA", Toast.LENGTH_LONG).show()
                true
            }
            binding.tvHistoryCategoryItemName.text = item.itemName
            binding.tvHistoryCategoryItemPrice.text = item.itemPrice.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_CATEGORY_TABLE_TITLE -> CategoryTitleViewHolder(
                ItemHistoryCategoryTitleBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            TYPE_CATEGORY_TABLE_ITEM -> CategoryItemViewHolder(
                ItemHistoryCategoryItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> {
                CategoryItemViewHolder(
                    ItemHistoryCategoryItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CategoryTitleViewHolder -> {
                holder.bind(list.currentList[position] as HistoryDataWrapper.CategoryTableTitle)
            }
            is CategoryItemViewHolder -> {
                holder.bind(list.currentList[position] as HistoryDataWrapper.CategoryTableItem)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (list.currentList[position]) {
            is HistoryDataWrapper.CategoryTableTitle -> TYPE_CATEGORY_TABLE_TITLE
            is HistoryDataWrapper.CategoryTableItem -> TYPE_CATEGORY_TABLE_ITEM
        }
    }

    override fun getItemCount(): Int {
        return list.currentList.size
    }

    fun updateList(historyItems: List<HistoryDataWrapper>) {
        list.submitList(historyItems)
    }

    class HistoryListCallback : DiffUtil.ItemCallback<HistoryDataWrapper>() {
        override fun areItemsTheSame(
            oldItem: HistoryDataWrapper,
            newItem: HistoryDataWrapper
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: HistoryDataWrapper,
            newItem: HistoryDataWrapper
        ): Boolean {
            return oldItem == newItem
        }

    }

    companion object {
        const val TYPE_CATEGORY_TABLE_TITLE = 0
        const val TYPE_CATEGORY_TABLE_ITEM = 1

    }
}