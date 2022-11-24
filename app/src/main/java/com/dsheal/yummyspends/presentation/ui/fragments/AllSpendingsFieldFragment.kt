package com.dsheal.yummyspends.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dsheal.yummyspends.R
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.dsheal.yummyspends.databinding.FragmentAllSpendsBinding
import com.dsheal.yummyspends.domain.models.spendings.SingleSpendingModel
import com.dsheal.yummyspends.presentation.adapters.SpendingsListAdapter
import com.dsheal.yummyspends.presentation.base.BaseViewModel
import com.dsheal.yummyspends.presentation.viewmodels.AllSpendingsFieldViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllSpendingsFieldFragment : BaseFragment() {

    private var mBinding: FragmentAllSpendsBinding? = null
    private val binding get() = mBinding!!

    private val allSpendingsFieldViewModel: AllSpendingsFieldViewModel by viewModels()
    private val args: AllSpendingsFieldFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        observe(allSpendingsFieldViewModel.events, ::onEvent)

        initViews()
        allSpendingsFieldViewModel.listenAllSpendingsFromDb()
        allSpendingsFieldViewModel.spending.observe(viewLifecycleOwner) { state ->
            when (state) {
                is BaseViewModel.State.Loading -> {}
                is BaseViewModel.State.Failure -> {
                    showAlert(state.error.message)
                }
                is BaseViewModel.State.Success -> {
                    onDataFetched(state.data)
                }
            }
        }
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
        binding.btnAddNewSpending.setOnClickListener {
//            allSpendingsFieldViewModel.onAddNewSpendingClick()
            findNavController().navigate(AllSpendingsFieldFragmentDirections.toAddNewSpending())

        }

        binding.btnAddSpendingsToHistory.setOnClickListener {
//            allSpendingsFieldViewModel.onAddNewSpendingClick()
            findNavController().navigate(AllSpendingsFieldFragmentDirections.actionHomeFragmentToHistoryFragment())

        }
        binding.rvSpendingsList.apply {
            adapter = SpendingsListAdapter()
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    fun onDataFetched(data: List<SingleSpendingModel>) {
        if (data.isEmpty()) {
            binding.clTitlesContainer.visibility = View.GONE
            binding.tvPleaseAddHere.visibility = View.VISIBLE
        } else {
            binding.clTitlesContainer.visibility = View.VISIBLE
            binding.tvPleaseAddHere.visibility = View.GONE
        }
        (binding.rvSpendingsList.adapter as SpendingsListAdapter).updateList(data)
        val allPricesList = mutableListOf<Int>()
            data.forEach { spend ->
            allPricesList.add(spend.spendingPrice)
        }
        binding.tvTotalForSpendList.text = getString(R.string.total_spend, allPricesList.sum())
    }
}
