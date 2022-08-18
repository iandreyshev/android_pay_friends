package ru.iandreyshev.stale.ui.payment

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class PaymentViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = ITEMS_COUNT
    override fun createFragment(position: Int) = PaymentResultFragment.newInstance(isResult = position == 0)

    companion object {
        private const val ITEMS_COUNT = 2
    }
}
