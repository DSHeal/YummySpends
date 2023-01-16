package com.dsheal.yummyspends.presentation.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dsheal.yummyspends.databinding.FragmentAddNewSpendingBinding
import com.dsheal.yummyspends.presentation.base.BaseViewModel
import com.dsheal.yummyspends.presentation.viewmodels.AllSpendingsFieldViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddNewSpendingFragment : BaseFragment() {

    private var mBinding: FragmentAddNewSpendingBinding? = null
    private val binding get() = mBinding!!

    private val addNewSpendingViewModel: AllSpendingsFieldViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        addNewSpendingViewModel.spending.observe(viewLifecycleOwner) { state ->
            when (state) {
                is BaseViewModel.State.Loading -> {}
                is BaseViewModel.State.Failure -> {
                    showAlert(state.error.message)
                }
                is BaseViewModel.State.Success -> {
                    showAlert("Saved")
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentAddNewSpendingBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun initViews() {
        binding.tbAddOneSpend.title = "Add new spending"
//
//        binding.etSingleSpendingName.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//
//            override fun afterTextChanged(s: Editable?) {
//
//            }
//        })


        val date = Date()
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)
        val currentDate = formatter.format(date)

        binding.btnAddSingleNewSpending.setOnClickListener {
            val spendTitle = binding.etSingleSpendingName.text.toString()
            val spendCost = binding.etSingleSpendingPrice.text.toString()
            val spendCategory = binding.tvSingleSpendingCategory.text.toString()
            val customerDate = binding.tvSpendingDate.text.toString()
            addNewSpendingViewModel.saveSpendingInDb(
                spendTitle,
                spendCost.toInt(),
                spendCategory,
                if (customerDate == "today") currentDate.toString() else customerDate
            )

            findNavController().popBackStack()
        }
    }
}
