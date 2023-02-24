package com.dsheal.yummyspends.presentation.ui.fragments

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dsheal.yummyspends.R
import com.dsheal.yummyspends.databinding.FragmentAddNewSpendingBinding
import com.dsheal.yummyspends.domain.models.spendings.Category
import com.dsheal.yummyspends.domain.models.spendings.SingleSpendingModel
import com.dsheal.yummyspends.presentation.adapters.CategorySpinnerAdapter
import com.dsheal.yummyspends.presentation.base.BaseViewModel
import com.dsheal.yummyspends.presentation.ui.activities.MainActivity
import com.dsheal.yummyspends.presentation.viewmodels.AllSpendingsFieldViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.AndroidEntryPoint
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class AddNewSpendingFragment : BaseFragment() {

    private var mBinding: FragmentAddNewSpendingBinding? = null
    private val binding get() = mBinding!!

    private val addNewSpendingViewModel: AllSpendingsFieldViewModel by viewModels()

    var categories: ArrayList<Category> =
        arrayListOf(
            Category("Овощи и фрукты", false), Category("Бакалея", false),
            Category("Молочная продукция", false)
        )

    companion object {
        const val DATE = "DATE"
        fun newInstance(date: String) =
            HistoryViewPagerContainerFragment().apply {
                arguments = Bundle().apply {
                    putString(DATE, date)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userDate = arguments?.getString("DATE") ?: ""
        initViews(userDate)
        addNewSpendingViewModel.spending.observe(viewLifecycleOwner) { state ->
            when (state) {
                is BaseViewModel.State.Loading -> {}
                is BaseViewModel.State.Failure -> {
                    showAlert(state.error?.message)
                }
                is BaseViewModel.State.Success -> {
                    showAlert("Saved")
                    binding.tvSingleSpendingCategory.visibility = View.GONE
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

    private fun initViews(userDate: String) {

        val sharedPreferences =
            (activity as MainActivity).getSharedPreferences("shared preferences", MODE_PRIVATE)

        val gson = Gson()

        val json = sharedPreferences.getString("categories", null)

        val type: Type = object : TypeToken<ArrayList<Category?>?>() {}.type

        if (gson.fromJson<Any>(json, type) == null) {
            categories = ArrayList()
            categories.add(0, (Category("Add your category", false)))
        } else {
            categories =
                gson.fromJson<Any>(json, type) as ArrayList<Category>
            if (categories[0].name == "Add your category") {
                categories.removeAt(0)
            }
        }

        val categoriesSorted = categories.distinct()
            .sortedBy { it.name }

        val arrayAdapter =
            CategorySpinnerAdapter(
                requireContext(),
                R.layout.item_spinner,
                if (categories[0] != Category("Add your category", false))
                    categoriesSorted.toMutableList() + mutableListOf(
                        Category(
                            "Add your category",
                            false
                        )
                    )
                else categoriesSorted.toMutableList()
            )

        val spinner = binding.spCategories
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position == categoriesSorted.lastIndex && categoriesSorted[0] == Category(
                        "Add your category",
                        false
                    )
                ) {
                    binding.tvSingleSpendingCategory.setText("")
                    binding.tvSingleSpendingCategory.visibility =
                        View.VISIBLE

                    spinner.visibility = View.GONE
                } else if (position == categoriesSorted.lastIndex + 1) {
                    binding.tvSingleSpendingCategory.setText("")
                    binding.tvSingleSpendingCategory.visibility =
                        View.VISIBLE

                    spinner.visibility = View.GONE
                } else binding.tvSingleSpendingCategory.setText(categoriesSorted[position].name)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }

        arrayAdapter.setDropDownViewResource(R.layout.item_category_spinner_dropdown)

        with(spinner) {
            adapter = arrayAdapter
            setSelection(0, false)
            gravity = Gravity.CENTER
        }

        binding.tbAddOneSpend.title = "Add new spending"
        binding.tbAddOneSpend.setNavigationOnClickListener {
            (activity as MainActivity).onBackPressed()
        }

        val date = Date()
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)
        val currentDate = formatter.format(date)

        if (userDate.isNotEmpty()) binding.tvSpendingDate.setText(
            userDate
        )

        binding.btnAddSingleNewSpending.setOnClickListener {
            val spendTitle = binding.etSingleSpendingName.text.toString()
            val spendCost = binding.etSingleSpendingPrice.text.toString()
            val spendCategory = binding.tvSingleSpendingCategory.text.toString()
            val customerDate = binding.tvSpendingDate.text.toString()
            if (categories.find { category -> category.name == spendCategory } == null) {
                categories.add(Category(spendCategory, true))
                arrayAdapter.add(Category(spendCategory, true))
                arrayAdapter.remove(Category("Add your category", false))
                arrayAdapter.sort { el1, el2 ->
                    el1.name.compareTo(el2.name)
                }
                arrayAdapter.add(Category("Add your category", false))

                arrayAdapter.notifyDataSetChanged()
                val newCategory = categories.find { category ->
                    category.name == spendCategory
                }
                spinner.setSelection(arrayAdapter.getPosition(newCategory), false)

            }
            val sharedPrefs =
                (activity as MainActivity).getSharedPreferences("shared preferences", MODE_PRIVATE)
            val editor = sharedPrefs.edit()
            val mGson = Gson()

            categories.remove(Category("Add your category", false))
            val categoryNames = categories.distinct()
                .sortedBy { it.name }

            val mJson: String = mGson.toJson(categoryNames)
            editor.putString("categories", mJson)
            editor.apply()
            Toast.makeText(
                requireContext(),
                "Saved Array List to Shared preferences.",
                Toast.LENGTH_SHORT
            )
                .show()

            addNewSpendingViewModel.sendDataToRemoteDb(
                SingleSpendingModel(
                    spendingName = spendTitle,
                    spendingPrice = spendCost.toInt(),
                    spendingCategory = spendCategory,
                    purchaseDate = if (customerDate == "today") currentDate.toString() else customerDate
                )
            )
            binding.etSingleSpendingName.setText("")
            binding.etSingleSpendingPrice.setText("")

            spinner.visibility = View.VISIBLE

        }

        binding.btnEverythingIsAdded.setOnClickListener {
            val customerDate = binding.tvSpendingDate.text.toString()
            findNavController().navigate(
                AddNewSpendingFragmentDirections.toHistoryFragment(
                    customerDate
                )
            )
        }
    }
}
