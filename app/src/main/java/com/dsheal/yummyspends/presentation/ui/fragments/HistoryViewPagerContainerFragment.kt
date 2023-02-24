package com.dsheal.yummyspends.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dsheal.yummyspends.R
import com.dsheal.yummyspends.databinding.FragmentHistoryViewPagerContainerBinding
import com.dsheal.yummyspends.domain.models.history.HistoryDataWrapper
import com.dsheal.yummyspends.domain.models.spendings.SingleSpendingModel
import com.dsheal.yummyspends.presentation.adapters.HistoryListAdapter
import com.dsheal.yummyspends.presentation.base.BaseViewModel
import com.dsheal.yummyspends.presentation.viewmodels.AllSpendingsFieldViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryViewPagerContainerFragment : BaseFragment() {

    private var mBinding: FragmentHistoryViewPagerContainerBinding? = null
    private val binding get() = mBinding!!

    private var spendingLiveData = MutableLiveData<BaseViewModel.State<List<SingleSpendingModel>>>()

    val spending = spendingLiveData

    private val viewModel: AllSpendingsFieldViewModel by viewModels()

    var dateForHeader: String? = null

    companion object {
        const val DATE = "DATE"
        fun newInstance(date: String) =
            HistoryViewPagerContainerFragment().apply {
                arguments = Bundle().apply {
                    putString(DATE, date)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentHistoryViewPagerContainerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            dateForHeader = arguments?.getString(DATE) ?: ""
            tbHistoryCurrentDate.title = dateForHeader
            tbHistoryCurrentDate.setTitleMargin(250, 20, 20, 20)

            btnAddNewSpending.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("DATE", dateForHeader)
                findNavController().navigate(R.id.addNewSpending, bundle)

            }

            val recycler = rvHistoryList
            recycler.apply {
                adapter = HistoryListAdapter()
                layoutManager = LinearLayoutManager(requireContext())
            }

            viewModel.listenSpendingsByDate(dateForHeader!!)
            viewModel.spending.observe(viewLifecycleOwner) { state ->
                when (state) {
                    is BaseViewModel.State.Loading -> {}
                    is BaseViewModel.State.Failure -> {
                        showAlert(state.error?.message)
                    }
                    is BaseViewModel.State.Success -> {
                        onDataFetched(state.data)
                    }
                }
            }
        }
    }

    fun onDataFetched(data: List<SingleSpendingModel>) {
        val totalSumCalculated = data.sumOf { spend ->
            spend.spendingPrice
        }
        binding.tvTotalSumForDay.text =
            getString(R.string.history_total_for_the_day, totalSumCalculated)

        (binding.rvHistoryList.adapter as HistoryListAdapter).updateList(
            mapSingleSpendingModelToHistoryDataWrapper(data)
        )
    }

    fun mapSingleSpendingModelToHistoryDataWrapper(data: List<SingleSpendingModel>): List<HistoryDataWrapper> {
        val finalList: MutableList<HistoryDataWrapper> = mutableListOf()
        var categoryPrice = 0

        val groupedList: Map<String, List<SingleSpendingModel>> = data.groupBy { spending ->
            spending.spendingCategory
        }

        for (i in groupedList.keys) {
            for (v in groupedList.getValue(i)) {
                categoryPrice += v.spendingPrice
            }
            finalList.add(HistoryDataWrapper.CategoryTableTitle(i, categoryPrice))
            for (v in groupedList.getValue(i)) {
                finalList.add(HistoryDataWrapper.CategoryTableItem(v.spendingName, v.spendingPrice))
            }
            categoryPrice = 0
        }
        return finalList
    }
}