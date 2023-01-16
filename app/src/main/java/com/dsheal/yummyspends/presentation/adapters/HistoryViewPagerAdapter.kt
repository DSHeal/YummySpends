package com.dsheal.yummyspends.presentation.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dsheal.yummyspends.domain.models.spendings.SingleSpendingModel
import com.dsheal.yummyspends.presentation.ui.fragments.HistoryViewPagerContainerFragment
import java.text.SimpleDateFormat
import java.util.*

class HistoryViewPagerAdapter(
    fragment: Fragment, private val spends: List<SingleSpendingModel>
) : FragmentStateAdapter(fragment) {

    val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)
    val calendar = Calendar.getInstance()

    override fun getItemCount(): Int {
        return getDaysInAYear()
    }

    fun getDaysInAYear(): Int {
        calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR))
        calendar.set(Calendar.MONTH, Calendar.DECEMBER)
        calendar.set(Calendar.DATE, 31)
        return calendar.get(Calendar.DAY_OF_YEAR)
    }

    override fun createFragment(position: Int): Fragment {
        val mCalendar = Calendar.getInstance()
        mCalendar.set(Calendar.DAY_OF_YEAR, position)

        return HistoryViewPagerContainerFragment.newInstance(formatter.format(mCalendar.time))
    }
}