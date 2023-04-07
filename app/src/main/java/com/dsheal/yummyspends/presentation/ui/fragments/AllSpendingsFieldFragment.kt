package com.dsheal.yummyspends.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dsheal.yummyspends.R
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dsheal.yummyspends.databinding.FragmentAllSpendsBinding
import com.dsheal.yummyspends.domain.models.spendings.SingleSpendingModel
import com.dsheal.yummyspends.presentation.adapters.SpendingsListAdapter
import com.dsheal.yummyspends.presentation.viewmodels.AllSpendingsFieldViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllSpendingsFieldFragment : BaseFragment() {

    private var mBinding: FragmentAllSpendsBinding? = null
    private val binding get() = mBinding!!

    private val allSpendingsFieldViewModel: AllSpendingsFieldViewModel by viewModels()
    private val args: AllSpendingsFieldFragmentArgs? by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
//        allSpendingsFieldViewModel.listenAllSpendingsFromDb()
//        allSpendingsFieldViewModel.spending.observe(viewLifecycleOwner) { state ->
//            when (state) {
//                is BaseViewModel.State.Loading -> {}
//                is BaseViewModel.State.Failure -> {
//                    showAlert(state.error.message)
//                }
//                is BaseViewModel.State.Success -> {
//                    onDataFetched(state.data)
//                }
//            }
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentAllSpendsBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun initViews() {
        binding.rvSpendingsList.apply {
            adapter = SpendingsListAdapter()
            layoutManager = LinearLayoutManager(requireContext())
        }
        if (args?.spending != null) {
            binding.tlTableHeadingLayout.visibility = View.VISIBLE
            binding.tvPleaseAddHere.visibility = View.GONE
            binding.tvTotalForSpendList.visibility = View.VISIBLE
            binding.btnClearTable.visibility = View.VISIBLE

            (binding.rvSpendingsList.adapter as SpendingsListAdapter).updateList(listOf(args!!.spending!!))
            val allPricesList = mutableListOf<Int>()
            allPricesList.add(args!!.spending!!.spendingPrice)
            binding.tvTotalForSpendList.text = getString(R.string.total_spend, allPricesList.sum())
        } else {
            binding.tlTableHeadingLayout.visibility = View.GONE
            binding.tvPleaseAddHere.visibility = View.VISIBLE
            binding.tvTotalForSpendList.visibility = View.GONE
            binding.btnClearTable.visibility = View.GONE
        }
//        binding.btnAddNewSpending.setOnClickListener {
////            allSpendingsFieldViewModel.onAddNewSpendingClick()
//            findNavController().navigate(AllSpendingsFieldFragmentDirections.toAddNewSpending())
//
//        }

        binding.btnAddSpendingsToHistory.setOnClickListener {
//            allSpendingsFieldViewModel.onAddNewSpendingClick()
            (binding.rvSpendingsList.adapter as SpendingsListAdapter).updateList(emptyList())
            findNavController().navigate(AllSpendingsFieldFragmentDirections.actionHomeFragmentToHistoryFragment())

        }

        binding.btnClearTable.setOnClickListener {
            allSpendingsFieldViewModel.deleteAllSpendingsFromDb()
        }

    }

    fun onDataFetched(data: List<SingleSpendingModel>) {
        if (data.isEmpty()) {
            binding.tlTableHeadingLayout.visibility = View.GONE
            binding.tvPleaseAddHere.visibility = View.VISIBLE
            binding.tvTotalForSpendList.visibility = View.GONE
            binding.btnClearTable.visibility = View.GONE
        } else {
            binding.tlTableHeadingLayout.visibility = View.VISIBLE
            binding.tvPleaseAddHere.visibility = View.GONE
            binding.tvTotalForSpendList.visibility = View.VISIBLE
            binding.btnClearTable.visibility = View.VISIBLE
        }
        (binding.rvSpendingsList.adapter as SpendingsListAdapter).updateList(data)
        val allPricesList = mutableListOf<Int>()
        data.forEach { spend ->
            allPricesList.add(spend.spendingPrice)
        }
        binding.tvTotalForSpendList.text = getString(R.string.total_spend, allPricesList.sum())


        val spendsArrayList = data as ArrayList
//        (binding.rvSpendingsList.adapter as SpendingsListAdapter).notifyDataSetChanged()
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val deletedItem: SingleSpendingModel =
                    spendsArrayList[viewHolder.absoluteAdapterPosition]
                val position = viewHolder.absoluteAdapterPosition
                spendsArrayList.removeAt(viewHolder.absoluteAdapterPosition)
                (binding.rvSpendingsList.adapter as SpendingsListAdapter).notifyItemRemoved(
                    viewHolder.absoluteAdapterPosition
                )
                (binding.rvSpendingsList.adapter as SpendingsListAdapter).notifyItemRangeChanged(
                    position,
                    spendsArrayList.size
                )
                view
                Snackbar.make(
                    binding.rvSpendingsList,
                    "Deleted " + deletedItem.spendingName,
                    Snackbar.LENGTH_LONG
                )
                    .setAction("Undo", View.OnClickListener {
                        spendsArrayList.add(position, deletedItem)
                        (binding.rvSpendingsList.adapter as SpendingsListAdapter).notifyItemInserted(
                            position
                        )
                    }).show()
            }
        }).attachToRecyclerView(binding.rvSpendingsList)

    }
}
