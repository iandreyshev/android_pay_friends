package ru.iandreyshev.payfriends.ui.computation

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ComputationViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = ITEMS_COUNT
    override fun createFragment(position: Int) = ComputationResultFragment.newInstance(isResult = position == 0)

    companion object {
        private const val ITEMS_COUNT = 2
    }
}
