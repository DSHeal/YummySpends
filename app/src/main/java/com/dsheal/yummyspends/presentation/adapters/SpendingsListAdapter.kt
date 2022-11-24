package com.dsheal.yummyspends.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dsheal.yummyspends.databinding.SingleSpendingRecyclerItemBinding
import com.dsheal.yummyspends.domain.models.spendings.SingleSpendingModel
import javax.inject.Inject

class SpendingsListAdapter @Inject constructor() :
    RecyclerView.Adapter<SpendingsListAdapter.SpendingsListViewHolder>() {

//    private var spendings: List<SingleSpendingModel> = emptyList()

    private val differ = AsyncListDiffer(this, SpendingItemCallback())

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SpendingsListViewHolder {
        return SpendingsListViewHolder(
            SingleSpendingRecyclerItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: SpendingsListAdapter.SpendingsListViewHolder,
        position: Int
    ) = holder.bind(differ.currentList[position])


    override fun getItemCount(): Int = differ.currentList.size

    class SpendingsListViewHolder(private val binding: SingleSpendingRecyclerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SingleSpendingModel) {
            with(binding) {
                tvSingleSpendingName.text = item.spendingName
                tvSingleSpendingPrice.text = item.spendingPrice.toString()
                tvSingleSpendingCategory.text = item.spendingCategory
            }

        }
    }

    fun updateList(reviews: List<SingleSpendingModel>) {
        differ.submitList(reviews)
    }

    class SpendingItemCallback : DiffUtil.ItemCallback<SingleSpendingModel>() {
        override fun areItemsTheSame(
            oldItem: SingleSpendingModel,
            newItem: SingleSpendingModel
        ): Boolean {
            return oldItem.purchaseDate == newItem.purchaseDate
        }

        override fun areContentsTheSame(
            oldItem: SingleSpendingModel,
            newItem: SingleSpendingModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}