package com.dsheal.yummyspends.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.dsheal.yummyspends.R
import com.dsheal.yummyspends.databinding.FragmentAllSpendsBinding
import com.dsheal.yummyspends.databinding.FragmentHistoryBinding
import com.dsheal.yummyspends.domain.models.spendings.SingleSpendingModel
import com.dsheal.yummyspends.presentation.adapters.SpendingsListAdapter
import com.dsheal.yummyspends.presentation.base.BaseViewModel
import com.dsheal.yummyspends.presentation.viewmodels.AllSpendingsFieldViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SpendingsHistoryFragment : BaseFragment()  {

    private var mBinding: FragmentHistoryBinding? = null
    private val binding get() = mBinding!!

    private var spendingLiveData = MutableLiveData<BaseViewModel.State<List<SingleSpendingModel>>>()

    val spending = spendingLiveData

    private val viewModel: AllSpendingsFieldViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.listenAllSpendingsFromDb()

        initViews()
        viewModel.spending.observe(viewLifecycleOwner) { state ->
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

    fun initViews() {
        binding.rvHistorySpends.apply {
            adapter = SpendingsListAdapter()
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    fun onDataFetched(data: List<SingleSpendingModel>) {
        (binding.rvHistorySpends.adapter as SpendingsListAdapter).updateList(data)
    }
}