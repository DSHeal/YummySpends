package com.dsheal.yummyspends.presentation.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dsheal.yummyspends.domain.models.spendings.SingleSpendingModel
import com.dsheal.yummyspends.presentation.ui.fragments.HistoryViewPagerContainerFragment

class HistoryViewPagerAdapter(
    fragment: Fragment, private val spends: List<SingleSpendingModel>
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        when (position) {

//            0 -> { }
            1 -> return HistoryViewPagerContainerFragment.newInstance(spends[0].purchaseDate)
//            2 -> {}
        }
        return HistoryViewPagerContainerFragment.newInstance(if (spends.isEmpty()) "" else spends[0].purchaseDate)
    }

}